package utils

import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory
import java.time.Instant

object BackendManager {

    fun nonce(): String {
        val currentTimeMicros = Instant.now().epochSecond * 1_000_000 + Instant.now().nano
        return currentTimeMicros.toString()
    }

}