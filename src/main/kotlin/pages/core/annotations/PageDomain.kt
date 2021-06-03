package pages.core.annotations

import pages.AuthorizationProvider
import pages.BasePage
import pages.atm.AtmLoginPage
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PageDomain(
    val baseUrlProperty: String = "",
    val baseUrlLoginProperty: String = "",
    val baseUrlPasswordProperty: String = "",
    val authProvider: KClass<out AuthorizationProvider<out BasePage>> = AtmLoginPage::class
)