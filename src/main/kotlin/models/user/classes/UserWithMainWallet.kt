package models.user.classes

import models.user.interfaces.HasMainWallet
import models.user.interfaces.auth.HasDefaultAuth
import utils.Constants

open class UserWithMainWallet(
    override val email: String,
    override val mainWallet: MainWallet
) : DefaultUser(email, password = Constants.DEFAULT_PASSWORD), HasMainWallet, HasDefaultAuth