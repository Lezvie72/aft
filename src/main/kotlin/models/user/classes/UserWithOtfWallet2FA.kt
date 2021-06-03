package models.user.classes

import models.user.interfaces.HasOtfWallet
import models.user.interfaces.auth.HasTwoFA


class UserWithOtfWallet2FA(
    override val email: String = "",
    override var oAuthSecret: String,
    override val otfWallet: OtfWallet
) : UserWithOtfWallet(email, otfWallet), HasTwoFA, HasOtfWallet