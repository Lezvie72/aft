package models.user.classes

import utils.Constants

class DefaultUserWithCustomPassword(
    override val email: String = "",
    override var password: String
) : DefaultUser(email, password = Constants.DEFAULT_PASSWORD)