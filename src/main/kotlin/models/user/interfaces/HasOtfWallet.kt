package models.user.interfaces

import models.user.classes.OtfWallet


interface HasOtfWallet : User {
    val otfWallet: OtfWallet
}