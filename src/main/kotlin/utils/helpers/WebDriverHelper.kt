@file:Suppress("UNCHECKED_CAST")

package utils.helpers

import io.qameta.allure.Attachment
import io.restassured.path.json.JsonPath
import org.openqa.selenium.*
import org.openqa.selenium.html5.LocalStorage
import org.openqa.selenium.html5.SessionStorage
import org.openqa.selenium.html5.WebStorage
import org.openqa.selenium.logging.LogEntry
import org.openqa.selenium.logging.LogType
import pages.AuthorizationProvider
import pages.BasePage
import pages.core.annotations.PageDomain
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.HtmlElement
import ru.yandex.qatools.htmlelements.element.TypifiedElement
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader
import utils.Environment
import java.lang.reflect.Constructor
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor


/**
 * method creates instance of class <T> and open url by @PageUrl annotation on this class
 */
fun <T : BasePage> getPage(
    driver: WebDriver,
    pageUrlAnnotation: PageUrl,
    pageDomainAnnotation: PageDomain,
    pageName: String,
    constructor: Constructor<T>
): T {
    val uuid = openStep("User opens page '$pageName'")
    if (pageDomainAnnotation.baseUrlProperty.isNotEmpty()) {
        val url = Environment.getProperty(pageDomainAnnotation.baseUrlProperty)
        val usernameProperty = pageDomainAnnotation.baseUrlLoginProperty
        val passwordProperty = pageDomainAnnotation.baseUrlPasswordProperty
        if (usernameProperty.isNotEmpty() && passwordProperty.isNotEmpty()) {
            val username = Environment.getProperty(usernameProperty)
            val password = Environment.getProperty(passwordProperty)
            val finalUrl = "https://$url${pageUrlAnnotation.url}"
            if (!driver.isAuthorized("p_$url")) {
                driver.navigate().to("https://$username:$password@$url${pageUrlAnnotation.url}")
                driver.authorize("p_$url")
            }
            driver.navigate().to(finalUrl)
        } else {
            driver.get("https://$url${pageUrlAnnotation.url}")
        }
    } else {
        Environment.atm_front_base_url
    }
    closeStep(uuid)
    return constructor.newInstance(driver)
}

inline fun <reified T : BasePage> openPage(
    driver: WebDriver,
    noinline auth: AuthorizationProvider<out BasePage>.() -> Unit = {
        submit(Users.USER)
    }
): T {
    val domainAnnotation =
        T::class.findAnnotationWithInheritance<PageDomain>() ?: error("Can't open page ${T::class.simpleName}")
    val urlAnnotation = T::class.findAnnotation<PageUrl>() ?: error("Can't open page ${T::class.simpleName}")
    val pageName = T::class.findAnnotation<PageName>()?.name ?: T::class.toString()
    val constructor = T::class.java.getConstructor(WebDriver::class.java)
    return openPage(driver, domainAnnotation, urlAnnotation, pageName, constructor, auth)
}

/**
 * method checks whether authorization is required for page <T> and perform authorization if it is
 */
@Suppress("UNCHECKED_CAST")
fun <T : BasePage> openPage(
    driver: WebDriver,
    domainAnnotation: PageDomain,
    urlAnnotation: PageUrl,
    pageName: String,
    constructor: Constructor<T>,
    auth: AuthorizationProvider<out BasePage>.() -> Unit = {
        submit(Users.USER)
    }
): T {
    val baseUrl = Environment.getProperty(domainAnnotation.baseUrlProperty, "")

    if (!driver.currentUrl.contains(baseUrl)) {
        driver.get("https://$baseUrl")
    }
    val authRequired = !driver.isAuthorized(baseUrl) && urlAnnotation.authRequired

    val authProvider = if (authRequired) {
        val authPageConstructor = domainAnnotation.authProvider
            .primaryConstructor as (WebDriver) -> AuthorizationProvider<out BasePage>
        authPageConstructor(driver)
    } else null

    authProvider?.let {
        auth(driver, it, auth, baseUrl)
    }

    var page = getPage(driver, urlAnnotation, domainAnnotation, pageName, constructor)

    page = if (authRequired) {
        val authUrl = authProvider?.getAuthUrl()
        authUrl?.let {
            page.nonCriticalWait(3L) {
                until("") {
                    driver.currentUrl.matches(".*$it$".toRegex())
                }
            }
            if (driver.currentUrl.endsWith(authUrl)) {
                auth(driver, authProvider, auth, baseUrl)
                getPage(driver, urlAnnotation, domainAnnotation, pageName, constructor)
            } else {
                page
            }
        } ?: page
    } else page

    return page
}

/**
 * method returns value for key 'token' from local storage
 */
fun <T : WebDriver> T.getToken(): String? {
    return try {
        this.getLocalStorage().getItem("token")
            ?: this.getLocalStorage().getItem("G1JWiY8HtGJv")
            ?: this.getLocalStorage().getItem("wd5tnbG2jPH2")
    } catch (e: Exception) {
        null
    }
}

fun <T : WebDriver> T.setToken(token: String): Unit {
    try {
        this.getLocalStorage().setItem("token", token)
        this.getLocalStorage().setItem("G1JWiY8HtGJv", token)
        this.getLocalStorage().setItem("wd5tnbG2jPH2", token)
    } catch (e: Exception) {
    }
}

fun <T : WebDriver> T.isAuthorized(service: String): Boolean {
    return this.manage().getCookieNamed("auth_$service") != null
}

fun <T : WebDriver> T.authorize(service: String): Boolean {
    this.manage().addCookie(Cookie("auth_$service", "true"))
    return this.manage().getCookieNamed("auth_$service") != null
}

fun <T : WebDriver> T.logout(service: String): Boolean {
    if (this.manage().getCookieNamed("auth_$service") != null) {
        this.manage().deleteCookieNamed("auth_$service")
    }
    return this.manage().getCookieNamed("auth_$service") == null
}


/**
 * method performs authorization to Sdex.
 * If error appears during first try, second try will be performed with attempt to verify all user's devices
 */
inline fun <reified T : AuthorizationProvider<out BasePage>> auth(
    driver: WebDriver,
    authPage: T,
    auth: T.() -> Unit,
    service: String
) {
    try {
        authPage.openLoginPage(driver)
        authPage.run(auth)
        driver.authorize(service)
    } catch (e: TimeoutException) {
        authPage.openLoginPage(driver)
        authPage.run(auth)
        driver.authorize(service)
    }
}

fun <T : WebDriver> T.getLocalStorage(): LocalStorage {
    return when (this) {
        is WebStorage -> this.localStorage
        else -> error("Can't get local storage of this driver")
    }

}

@SuppressWarnings
fun <T : WebDriver> T.getSessionStorage(): SessionStorage {
    return when (this) {
        is WebStorage -> this.sessionStorage
        else -> error("Can't get local storage of this driver")
    }
}

fun containsIgnoreCaseXpath(obj: String, attr: String, value: String): By {
    return By.xpath(".//$obj[contains(translate($attr, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ_', 'abcdefghijklmnopqrstuvwxyz '), \"${value.toLowerCase()}\")]")
}

fun equalsIgnoreCaseXpath(obj: String, attr: String, value: String): By {
    return By.xpath(
        ".//$obj[translate($attr, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ_', 'abcdefghijklmnopqrstuvwxyz ') = \"${value.toLowerCase()}\"]" +
                " | " +
                ".//$obj[translate($attr, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ_', 'abcdefghijklmnopqrstuvwxyz ') = \" ${value.toLowerCase()} \"]"
    )
}

inline fun <reified T : Any> T.getName(): String {
    val cls = this::class
    return when {
        cls.isSubclassOf(TypifiedElement::class) -> {
            (this as TypifiedElement).name
        }
        cls.isSubclassOf(HtmlElement::class) -> {
            (this as HtmlElement).name
        }
        cls.isSubclassOf(WebElement::class) -> {
            cls.findAnnotation<Name>()?.value ?: "Undefined"
        }
        cls.isSubclassOf(BasePage::class) -> {
            cls.findAnnotation<PageName>()?.name ?: "Undefined"
        }
        else -> {
            "Undefined"
        }
    }
}


fun getJSExecutor(_driver: WebDriver): JavascriptExecutor {
    return when (_driver) {
        is JavascriptExecutor -> _driver
        else -> error("Can't get JS executor of this driver")
    }
}

inline fun <reified T : WebElement> T.scrollIntoView(driver: WebDriver): T {
    getJSExecutor(driver).executeScript("arguments[0].scrollIntoView(true);", this)
    return this
}

fun WebElement.highlight(_driver: WebDriver) {
    getJSExecutor(_driver).executeScript("arguments[0].style.border='3px solid red'", this)
}

inline fun <reified T : TypifiedElement> WebElement.to(name: String = "Element"): T {
    return HtmlElementLoader.createTypifiedElement(T::class.java, this, name)
}

inline fun <reified R : Annotation> KClass<out Any>.findAnnotationWithInheritance(): R? {
    val annotation = this.findAnnotation<R>()
    return if (annotation == null) {
        val a = this.supertypes
        a.map { m ->
            (m.classifier as KAnnotatedElement).findAnnotation<R>()
        }.find {
            it != null
        }
    } else {
        annotation
    }
}

@Attachment("Screenshot")
fun WebDriver.takeScreenshot(): ByteArray {
    return (this as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
}

fun LogEntry.getValueFromMap(path: String): String? =
    JsonPath.from(this.message).getMap<String, String>(path).entries.joinToString(separator = "\n") {
        "\"${it.key}\" : \"${it.value}\""
    }

fun LogEntry.getValue(path: String): String? = JsonPath.from(this.message).getString(path)

fun List<LogEntry>.getByMethod(method: String) =
    this.find { JsonPath.from(it.message).getString("message.method") == method }

fun WebDriver.getPerformance(): List<Pair<String, String>> {
    val REQUEST_ID = "message.params.requestId"
    val entries = try {
        this.manage().logs().get(LogType.PERFORMANCE).all
    } catch (e: InvalidArgumentException) {
        return listOf()
    }
    val responses = entries.filter {
        JsonPath.from(it.message).setRoot("message.params.response.headers").getString("grpc-message") != null
    }
    val responseIds = responses.map {
        JsonPath.from(it.message).getString(REQUEST_ID)
    }

    val result = mutableMapOf<String, MutableList<LogEntry>>()
    entries.filter {
        JsonPath.from(it.message).getString(REQUEST_ID) in responseIds
    }.sortedByDescending {
        it.timestamp
    }.forEach {
        val key = JsonPath.from(it.message).getString(REQUEST_ID)
        if (result[key] == null) result[key] = mutableListOf()
        result[key]?.add(it)
    }

    return result.values.map { log ->
        try {
            val reqHeaders =
                log.getByMethod("Network.requestWillBeSentExtraInfo")?.getValueFromMap("message.params.headers")
            val resHeaders =
                log.getByMethod("Network.responseReceived")?.getValueFromMap("message.params.response.headers")
            val url =
                log.getByMethod("Network.responseReceived")?.getValue("message.params.response.url") ?: "undefined"

            url to """
            URL: $url \n\n
            requestHeaders: \n\n $reqHeaders \n\n
            responseHeaders: \n\n $resHeaders" \n\n
            """
        } catch (e: Exception) {
            "error" to "couldn't get data due some error"
        }
    }
}