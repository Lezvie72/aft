package models.user.classes

import models.user.interfaces.auth.HasTwoFA
import utils.Constants

open class DefaultUserWith2FA(
    override val email: String = "",
    override var oAuthSecret: String = ""
) : DefaultUser(email, password = Constants.DEFAULT_PASSWORD), HasTwoFA