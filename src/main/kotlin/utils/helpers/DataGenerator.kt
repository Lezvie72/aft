package utils.helpers

import io.qameta.allure.Step
import org.apache.commons.lang3.RandomStringUtils
import org.bitcoinj.core.Base58
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.Security
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec


object DataGenerator {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    fun generateBTCAddress(): String {

        val sha = MessageDigest.getInstance("SHA-256")
        val rmd = MessageDigest.getInstance("RipeMD160", "BC")

        val kg = KeyPairGenerator.getInstance("EC")
        val eSpec = ECGenParameterSpec("secp256k1")
        kg.initialize(eSpec)
        val pbKey = kg.generateKeyPair().public

        val pt = (pbKey as ECPublicKey).w
        val sx: String = adjustTo64(pt.affineX.toString(16))?.toUpperCase() ?: ""
        val sy: String = adjustTo64(pt.affineY.toString(16))?.toUpperCase() ?: ""
        val bcPub = "04$sx$sy".toByteArray(Charsets.UTF_8)
        val s1 = sha.digest(bcPub)
        val r1 = rmd.digest(s1)
        val r2 = ByteArray(r1.size + 1)
        r2[0] = 0
        for (i in r1.indices) r2[i + 1] = r1[i]
        val s2 = sha.digest(r2)
        val s3 = sha.digest(s2)
        val a1 = ByteArray(25)
        for (i in r2.indices) a1[i] = r2[i]
        for (i in 0..3) a1[21 + i] = s3[i]
        return Base58.encode(a1)
    }
}

fun String.encodeSHA_512(): String {
    val md = MessageDigest.getInstance("SHA-512")
    val passwordBytes = md.digest(this.toByteArray())

    val sb = StringBuilder()
    for (element in passwordBytes) {
        sb.append(String.format("%02x", element))
    }
    return sb.toString()
}

fun adjustTo64(s: String): String? {
    return when (s.length) {
        62 -> "00$s"
        63 -> "0$s"
        64 -> s
        else -> throw IllegalArgumentException("not a valid key: $s")
    }
}

@Step("Generate new available email")
fun generateEmail(): String {
    return "aft.uat.sdex+${RandomStringUtils.random(11, true, true)}@gmail.com".toLowerCase()
}