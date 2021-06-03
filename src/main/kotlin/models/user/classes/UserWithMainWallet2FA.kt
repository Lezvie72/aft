package models.user.classes

import models.user.interfaces.HasMainWallet
import models.user.interfaces.auth.HasTwoFA

class UserWithMainWallet2FA(
    override val email: String = "",
    override var oAuthSecret: String,
    override val mainWallet: MainWallet
) : UserWithMainWallet(email, mainWallet), HasTwoFA, HasMainWallet