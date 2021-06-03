package models.user.classes

import models.user.interfaces.User
import utils.Constants

open class DefaultUser(
    override val email: String = "",
    override var password: String = Constants.DEFAULT_PASSWORD,
    override val project: Int = 0
) : User