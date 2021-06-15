package models.user.classes

import models.user.interfaces.HasOtfWallet
import utils.Constants


open class UserWithOtfWallet(
    override val email: String = "",
    override val otfWallet: OtfWallet
) : DefaultUser(password = Constants.DEFAULT_PASSWORD), HasOtfWallet