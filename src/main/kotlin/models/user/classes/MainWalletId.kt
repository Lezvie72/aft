package models.user.classes

import models.user.interfaces.SimpleWallet

class MainWalletId(
    override val name: String = "Main 1",
    override val secretKey: String = "",
    override val publicKey: String = "",
    override val walletId: String = ""
) : SimpleWallet