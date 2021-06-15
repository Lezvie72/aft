package models.user.interfaces

import models.user.classes.MainWallet


interface HasMultipleMainWallet : HasMainWallet {
    val walletList: List<MainWallet>

    fun getFirst(): MainWallet {
        return walletList.component1()
    }

    fun getSecond(): MainWallet {
        return walletList.component2()
    }

    override val mainWallet: MainWallet
        get() = getFirst()
}