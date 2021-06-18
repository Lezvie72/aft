package models.user.classes

import models.user.interfaces.User
import utils.Constants

open class KibanaUser(
    override val email: String = "",
    override var password: String = Constants.KIBANA_PASSWORD
) : User