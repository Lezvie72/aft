package models.user.classes

import models.user.interfaces.HasMainWallet
import models.user.interfaces.HasOtfWallet

open class UserWithMainWalletAndOtf(
    override val email: String = "",
    override val mainWallet: MainWallet,
    override val otfWallet: OtfWallet
) : DefaultUser(email), HasMainWallet, HasOtfWallet