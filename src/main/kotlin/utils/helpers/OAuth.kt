package utils.helpers

import com.warrenstrange.googleauth.GoogleAuthenticator
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig

object OAuth {

    private val prevCodeCache = mutableMapOf<String, String>()

    fun generateCode(secret: String): String {
        val config = GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().apply {
            setCodeDigits(6)
        }.build()
        val auth = GoogleAuthenticator(config)
        return synchronized(this) {
            var code: String
            do {
                code = auth.getTotpPassword(secret).toString()
            } while (code == prevCodeCache[secret] || code.length != 6)
            prevCodeCache[secret] = code
            code
        }
    }
}