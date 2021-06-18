package pages.atm

import org.openqa.selenium.WebDriver
import pages.BasePage
import pages.core.annotations.PageDomain
import pages.core.annotations.PageUrl

@PageDomain(
    baseUrlProperty = "{тута должен быть базовый урл}.url",
    baseUrlLoginProperty = "{тута должен быть базовый урл}.login",
    baseUrlPasswordProperty = "{тута должен быть базовый урл}.password",
    authProvider = AtmAdminLoginPage::class
)

@PageUrl("а вот тута примочка")
class AtmAdminKibanaPage(driver: WebDriver): BasePage(driver) {

}
