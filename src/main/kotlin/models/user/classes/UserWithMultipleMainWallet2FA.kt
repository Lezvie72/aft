package models.user.classes

import models.user.interfaces.HasMultipleMainWallet

class UserWithMultipleMainWallet2FA(
    override val email: String = "",
    override var oAuthSecret: String,
    override val walletList: List<MainWallet>
) : DefaultUserWith2FA(email, oAuthSecret), HasMultipleMainWallet