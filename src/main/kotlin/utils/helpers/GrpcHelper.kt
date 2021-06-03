package utils.helpers

import com.google.protobuf.GeneratedMessageV3
import io.grpc.Metadata
import io.grpc.StatusRuntimeException
import io.grpc.stub.AbstractStub
import io.grpc.stub.MetadataUtils
import io.grpc.stub.StreamObserver
import io.qameta.allure.Allure
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.support.ui.FluentWait
import utils.Environment
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KFunction1

/**
 * Class is used to create handlers to work with gRPC requests, responses and exceptions during runtime
 *
 * response - field to represent response from gRPC method
 * headerCapturer - contains all information about headers
 * trailCapturer - contains all information about trails
 * exception - field to represent exception, received from gRPC method
 * exceptionHandlers - additional ways to manage specific exception during runtime. e.g. if message is UNAVAILABLE - do A, else - do B
 */
class MessageWithCapturers<T>(
    val response: T? = null,
    val headerCapturer: AtomicReference<Metadata>,
    val trailCapturer: AtomicReference<Metadata>,
    val exception: Exception? = null,
    val exceptionHandlers: MutableMap<String, MessageWithCapturers<T>.() -> T> = mutableMapOf()
) {
    fun isExists(): Boolean {
        return response != null
    }
}

/**
 * method to add exception handler to gRPC request
 */
fun <T : GeneratedMessageV3> MessageWithCapturers<T>.onError(
    exceptionMessage: String,
    exceptionHandler: MessageWithCapturers<T>.() -> T
): MessageWithCapturers<T> {
    this.exceptionHandlers[exceptionMessage] = exceptionHandler
    return this
}

/**
 * method to add general exception handler, which returns some value instead in case of an error
 */
inline fun <reified T : GeneratedMessageV3> MessageWithCapturers<T>.onError(block: MessageWithCapturers<T>.() -> T): T {
    return if (this.isExists()) {
        this.response!!
    } else {
        block()
    }
}

/**
 * method to get response from gRPC, or, in case of exception, handle that exception and, if no handler for specific exception
 * was found - throw that exception
 */
inline fun <reified T> MessageWithCapturers<T>.getOrThrow(): T {
    return if (this.isExists())
        this.response!!
    else {
        this.exceptionHandlers[this.exception?.message]?.invoke(this) ?: throw this.exception!!
    }
}

inline fun <reified T : AbstractStub<T>, R : GeneratedMessageV3> T.perform(block: T.() -> R): R {
    val result = this.tryPerform(block)
    if (result.isExists()) {
        return result.response!!
    } else {
        throw result.exception!!
    }
}

inline fun <reified T : AbstractStub<T>, R, E> T.tryPerform(
    request: E,
    method: KFunction1<E, R>
): MessageWithCapturers<R> {
    return step("Try perform '$method'") {
        Allure.addAttachment("request", request.toString())
        this.tryPerform {
            method(request)
        }
    }
}

inline fun <reified T : AbstractStub<T>, R> T.tryPerform(block: T.() -> R): MessageWithCapturers<R> {
    val (stub, hc, tc) = this.withCapture()
    return try {
        val t = block(stub)
        Allure.addAttachment("response", t.toString())
        MessageWithCapturers(response = t, headerCapturer = hc, trailCapturer = tc)
    } catch (e: StatusRuntimeException) {
        Allure.addAttachment("error", "${e.message} : ${e.status} - ${e.trailers}")
        val errorTc = if (e.trailers == null) tc else AtomicReference(e.trailers!!)
        MessageWithCapturers(headerCapturer = hc, trailCapturer = errorTc, exception = e)
    } finally {
        attachCapturerMetadata("Headers", hc)
        attachCapturerMetadata("Trails", tc)
    }
}

inline fun <reified T : AbstractStub<T>> T.getMetadata(): Metadata {
    return this.withCapture().second.get()
}

inline fun <reified T : AbstractStub<T>> T.getToken(): String {
    return this.getMetadata().get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)) ?: ""
}

inline fun <reified T : AbstractStub<T>> T.applyMetadata(vararg metadata: Pair<String, String>): T {
    return applyMetadata(metadata.toMap())
}

inline fun <reified T : AbstractStub<T>> T.withToken(token: String): T {
    return this.applyMetadata(
        mapOf(
            "Authorization" to token
        )
    )
}

inline fun <reified T : AbstractStub<T>> T.applyMetadata(metadata: Map<String, String> = mapOf()): T {
    val mt = Metadata()
    val headers = mutableMapOf(
        "referer" to "https://${Environment.atm_front_base_url}",
        "content-type" to "application/grpc-web+proto",
        "origin" to "https://${Environment.atm_front_base_url}",
        "device" to "c35eb5889a9d70233d8fc196b05aa0e3" + Thread.currentThread().id,
        "user-agent" to
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36"
    )
    metadata.forEach {
        headers[it.key.toLowerCase()] = it.value
    }

    mt.apply {
        headers.forEach {
            val headerKey = Metadata.Key.of(it.key, Metadata.ASCII_STRING_MARSHALLER)
            this.put(headerKey, it.value)
        }
    }
    return MetadataUtils.attachHeaders(this, mt)
}

inline fun <reified T : AbstractStub<T>> T.withCapture(): Triple<T, AtomicReference<Metadata>, AtomicReference<Metadata>> {
    val headerCapture = AtomicReference<Metadata>()
    val trailerCapture = AtomicReference<Metadata>()
    return Triple(
        MetadataUtils.captureMetadata(this, headerCapture, trailerCapture),
        headerCapture,
        trailerCapture
    )
}

inline fun <reified T : AbstractStub<T>, E : Any> T.subscribe(body: (T, StreamObserver<E>) -> Unit): LinkedList<E> {
    val result = LinkedList<E>()
    val observer = object : StreamObserver<E> {
        override fun onNext(value: E) {
            value.let {
                result.add(it)
            }
        }

        override fun onError(t: Throwable?) {
            t?.let {
                throw it
            }
        }

        override fun onCompleted() {
            TODO("Not yet implemented")
        }

    }

    this.tryPerform {
        body(this, observer)
    }

    return result
}

fun <T> LinkedList<T>.wait(
    timeoutInSeconds: Long,
    message: () -> String = { "Expected condition failed. " }
): FluentWait<LinkedList<T>> {
    return FluentWait(this)
        .withTimeout(Duration.ofSeconds(timeoutInSeconds))
        .pollingEvery(Duration.ofMillis(100))
        .withMessage(message)
}

fun <T> LinkedList<T>.clearLinkedList(): LinkedList<T> {
    try {
        while (true) {
            val wait = FluentWait(this)
                .pollingEvery(Duration.ofMillis(250))
                .withTimeout(Duration.ofSeconds(3))
            wait.until { it.count() > 0 }
            this.clear()
        }
    } catch (e: TimeoutException) {
        return this
    }
}