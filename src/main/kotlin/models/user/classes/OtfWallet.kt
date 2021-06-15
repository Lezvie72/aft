package models.user.classes

import models.user.interfaces.SimpleWallet

class OtfWallet(
    override val name: String = "OTF 1",
    override val secretKey: String = "",
    override val publicKey: String = "",
    override val walletId: String = ""
) : SimpleWallet