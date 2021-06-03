package pages.core.actions

import org.openqa.selenium.WebDriver
import pages.BasePage

open class BaseActions<T1 : BasePage, T2 : WebDriver>(val page: T1, val driver: T2)