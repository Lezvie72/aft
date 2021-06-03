package models.user.classes

import models.user.interfaces.auth.HasTwoFA

class UserWithMainWalletAndOtf2FA(
    override val email: String = "",
    override var oAuthSecret: String,
    override val mainWallet: MainWallet,
    override val otfWallet: OtfWallet,
    val castodian: String = ""
) : UserWithMainWalletAndOtf(email, mainWallet, otfWallet), HasTwoFA