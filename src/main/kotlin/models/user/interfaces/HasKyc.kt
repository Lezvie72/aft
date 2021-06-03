package models.user.interfaces

interface HasKyc: User {
    var kyc: Boolean
}