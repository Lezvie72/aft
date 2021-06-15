package utils.helpers

import io.grpc.Metadata
import io.qameta.allure.Allure
import io.qameta.allure.model.Status
import io.qameta.allure.model.StepResult
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicReference

//Allure helper
fun attachCapturerMetadata(name: String, meta: AtomicReference<Metadata>) {
    meta.get()?.let {
        if (meta.get().keys().isNotEmpty()) {
            Allure.addAttachment(name, meta.get().toString())
        }
    }
}

fun attach(name: String, attachment: String) {
    Allure.addAttachment(name, attachment)
}

fun attachCsvFile(name: String, filePath: String) {
    val file = File(filePath).inputStream()
    Allure.addAttachment(name, "file/csv", file, "csv")
}

fun attachScreenshot(name: String, driver: WebDriver) {
    val screenshot = (driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES).inputStream()
    Allure.addAttachment(name, "image/png", screenshot, "png")
}

fun updateStep(body: StepResult.() -> Unit) {
    try {
        val step = Allure.getLifecycle().currentTestCaseOrStep.get()
        Allure.getLifecycle().updateStep(step, body)
    } catch (e: NoSuchElementException) {

    }
}

fun openStep(step: String): String {
    val uuid = UUID.randomUUID().toString()
    Allure.getLifecycle().startStep(uuid, StepResult().apply {
        name = step
        status = Status.PASSED
    })
    return uuid
}

fun closeStep(uuid: String) {
    Allure.getLifecycle().stopStep(uuid)
}

fun <T> step(stepName: String, body: () -> T): T {
    val uuid = UUID.randomUUID().toString()
    Allure.getLifecycle().startStep(uuid, StepResult().apply {
        name = stepName
        status = Status.PASSED
    })
    return try {
        body()
    } catch (e: AssertionError) {
        Allure.getLifecycle().updateStep(uuid) {
            it.status = Status.FAILED
        }
        throw e
    } catch (e: Exception) {
        Allure.getLifecycle().updateStep(uuid) {
            it.status = Status.BROKEN
        }
        throw e
    } finally {
        Allure.getLifecycle().stopStep(uuid)
    }
}

fun Boolean.alsoIf(condition: Boolean, body: () -> Any?): Boolean {
    return this.also {
        if (it == condition) {
            run(body)
        }
    }
}