package pages

import models.user.interfaces.User
import org.openqa.selenium.WebDriver


interface AuthorizationProvider<R : BasePage> {

    fun openLoginPage(driver: WebDriver)

//    fun submit(role: String): R = submit(utils.Environment.userForRole(role) ?: error("User with role $role not found"))

//    fun submit(role: Stand): R = submit(role.role)

    fun submit(user: User): R

    fun getAuthUrl(): String = "/login"

}