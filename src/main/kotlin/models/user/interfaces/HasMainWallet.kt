package models.user.interfaces

import models.user.classes.MainWallet


interface HasMainWallet: User {
    val mainWallet: MainWallet
}