package utils.gmail

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Base64
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.MessagePartHeader
import io.qameta.allure.Step
import org.openqa.selenium.support.ui.FluentWait
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

object GmailApi {

    private val credentialsPath = "credentials.json"
    private val jacksonFactory = JacksonFactory.getDefaultInstance()
    private val labels = listOf(GmailScopes.GMAIL_READONLY)

    private val credentials by lazy {
        GmailApi::class.java.classLoader.getResourceAsStream(credentialsPath)
            ?: error("Couldn't find file $credentialsPath")
    }

    private val service by lazy {
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        Gmail.Builder(transport, jacksonFactory, getAuthCredentials(transport))
            .build()
    }

    private fun getAuthCredentials(transport: HttpTransport): Credential {
        val secrets = GoogleClientSecrets.load(jacksonFactory, InputStreamReader(credentials))
        val authFlow = GoogleAuthorizationCodeFlow.Builder(transport, jacksonFactory, secrets, labels)
            .setDataStoreFactory(FileDataStoreFactory(File("tokens")))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(authFlow, receiver).authorize("user")
    }

    operator fun List<MessagePartHeader>.get(key: String): MessagePartHeader? {
        return find {
            it.name.toLowerCase() == key.toLowerCase()
        }
    }

    @Step("Get href for new user with email '{email}'")
    fun getHrefForNewUser(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getHref(email, since, "Confirm your Symbridge account")

    @Step("Get href for new atm user with email '{email}'")
    fun getHrefForNewUserATM(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getNewFormatHref(email, since, "Atomyze registration invitation")

    @Step("Get href for password recovery of atm user with email '{email}'")
    fun getHrefPassRecoveryUserATM(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getHref(email, since, "Password reset")

    @Step("Get href for password recovery link of atm admin with email '{email}'")
    fun getHrefPassRecoveryLinkUserADMIN(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getHrefForAdminRecovery(email, since, "Password reset")


    @Step("Get href for registration employee of atm user with email '{email}'")
    fun getHrefATMRegistrationEmployee(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getHref(email, since, "Atomyze registration")

    @Step("Get href for deactivated employee with email '{email}'")
    fun getHrefATMDeactivatedEmployee(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getHref(email, since, "Account deactivated")

    @Step("Get href for blocked employee with email '{email}'")
    fun getHrefATMBlockedEmployee(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getHref(email, since, "Account is blocked")

    @Step("Get href for registration employee with email '{email}'")
    fun getBodyATMRegistrationEmployee(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Atomyze registration", since)

    @Step("Get href message from Issuer'{email}'")
    fun getMessageFromIssuer(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getHrefForOrder(email, since, "You have new message from an issuer")

    @Step("Get message about an order '{email}'")
    fun getNotificationAboutOrder(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getHrefForOrder(email, since, "You have new message about an order")

    @Step("Get href message from Issuer'{email}'")
    fun getTextMessageFromIssuer(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "You have new message from an issuer", since)

    @Step("Get message about an order '{email}'")
    fun getTextNotificationAboutOrder(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "You have new message about an order", since)

    @Step("Get message from Issuer'{email}'")
    fun getTextMessageNodeActivated(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Atomyze – Node activated", since)

    @Step("Get message about an order '{email}'")
    fun getTextMessageNodeCertificateIssued(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Atomyze – Node certificate issued", since)

    @Step("Get body for role changed employee with email '{email}'")
    fun getBodyATMRoleChanged(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Role changed", since)

    @Step("Get body for deactivated employee with email '{email}'")
    fun getBodyATMDeactivatedEmployee(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Account deactivated", since)

    @Step("Get body for activated employee with email '{email}'")
    fun getBodyATMActivatedEmployee(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Account activated", since)

    @Step("Get body for 2FA status changing with email '{email}'")
    fun getBodyChange2FAStatus(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Two-factor authentication is changed", since)

    @Step("Get body for blocked employee with email '{email}'")
    fun getBodyATMBlockedEmployee(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Atomyze profile suspended", since)

    @Step("Get body for rejected employee with email '{email}'")
    fun getBodyATMRejectedEmployee(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Atomyze account registration declined", since)

    @Step("Get body for approved employee with email '{email}'")
    fun getBodyATMApprovedEmployee(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Atomyze account registration approved", since)

    @Step("Get body for approved employee with email '{email}'")
    fun getBodyATMCreatedEmployee(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Employee created", since)

    @Step("Get href for password changed of atm user with email '{email}'")
    fun getBodyPassChangedUserATM(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Your password has been reset", since)

    @Step("Get href for password changed of atm user with email '{email}'")
    fun getMessagePassChangedUserATM(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Password Successfully Changed", since)


    @Step("Get request confirmation code for email '{email}'")
    fun getVerificationCode(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getCode(email, since, "Confirm request")

    @Step("Get href for device verification with email '{email}'")
    fun getHrefForVerification(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getHref(email, since, "Verify a new device")

    @Step("Get message that device removed to the '{email}'")
    fun getTextThatDeviceRemoved(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getEmailBody(email, "Device remove", since)

    @Step("Get href for password recovery for email '{email}'")
    fun getRecoveryForUser(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ): String = getHref(email, since, "Password reset")


    @Step("Get email body for subject {subject} since {since}")
    fun getEmailBody(
        email: String,
        subject: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ) = getLastEmail(email, since, subject)

    private fun getLastEmail(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC),
        subject: String
    ): String {
        val wait = FluentWait(service)
            .withTimeout(Duration.ofSeconds(60))
            .pollingEvery(Duration.ofSeconds(5))
            .ignoring(IllegalStateException::class.java)

        return wait.until {
            val query = "to:$email after:${since.toEpochSecond(ZoneOffset.UTC)} subject:\"$subject\""
            val messages = it.users().messages().list("me").apply {
                q = query
            }.execute().messages
            val messageId = messages.last().id
            val msg = it.users().messages().get("me", messageId).execute().payload.body.data
            Base64.decodeBase64(msg).toString(Charset.forName("UTF-8"))
        }
    }

    private fun getHref(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC),
        subject: String
    ): String {
        val msg = getLastEmail(email, since, subject)
        return "\"(http.*)\"".toRegex().find(msg)?.groups?.last()?.value ?: error("No href found")
    }

    private fun getNewFormatHref(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC),
        subject: String
    ): String {
        val msg = getLastEmail(email, since, subject)
        return "(http.*atm.*front.*)".toRegex().find(msg)?.groups?.first()?.value
            ?.replaceAfter("\"", "")
            ?.replace("\"", "")
            ?: error("No href found")
        /*
        * Регуляка ищет href для регистрации, так как теперь в письме приходят две ссылки одна ведет на gif файл вторая на регистрацию
        * replaceAfter("com", "") - обрезает остатки строки после указанного домена .com так как сам ссылка лежит в верстке
        */
    }

    private fun getHrefForAdminRecovery(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC),
        subject: String
    ): String {
        val msg = getLastEmail(email, since, subject)
        return "(http.*admin.*front.*)".toRegex().find(msg)?.groups?.last()?.value?.replaceAfter(" ", "")
            ?.replace("\"", "")
            ?: error("No href found")
        /*
        * Регулярка ищет href для регистрации, так как теперь в письме приходят две ссылки одна ведет на gif файл вторая на регистрацию
        * replaceAfter("com", "") - обрезает остатки строки после указанного домена .com так как сам ссылка лежит в верстке
        */
    }

    private fun getHrefForOrder(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC),
        subject: String
    ): String {
        val msg = getLastEmail(email, since, subject)
        return "(http.*atm.*front.*)".toRegex().find(msg)?.groups?.first()?.value?.replaceAfter(" ", "")
            ?: error("No href found")
        /*
        * Регуляка ищет href для регистрации, так как теперь в письме приходят две ссылки одна ведет на gif файл вторая на регистрацию
        * replaceAfter("com", "") - обрезает остатки строки после указанного домена .com так как сам ссылка лежит в верстке
        */
    }

    private fun getCode(
        email: String,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC),
        subject: String
    ): String {
        val msg = getLastEmail(email, since, subject)
        return "Confirm your request: (\\d*)".toRegex().find(msg)?.groupValues?.last() ?: error("No code found")
    }
}