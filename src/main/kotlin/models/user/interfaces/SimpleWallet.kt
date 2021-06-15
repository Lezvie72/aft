package models.user.interfaces

interface SimpleWallet {
    val name: String
    val secretKey: String
    val publicKey: String
    val walletId: String
}