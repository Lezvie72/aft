package pages.core.actions

import io.qameta.allure.Step
import models.CoinType
import models.CoinType.*
import models.user.classes.DefaultUser
import models.user.classes.MainWallet
import models.user.classes.UserWithMainWalletAndOtf
import models.user.interfaces.HasOtfWallet
import models.user.interfaces.SimpleWallet
import models.user.interfaces.User
import org.openqa.selenium.WebDriver
import pages.BasePage
import pages.atm.*
import pages.atm.AtmIssuancesPage.StatusType.APPROVE
import pages.core.annotations.Action
import utils.helpers.FileHelper
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

class PrerequisiteActions<T : WebDriver>(page: BasePage, driver: T) : BaseActions<BasePage, T>(page, driver) {

    //add balance
    fun presetForOTF(
        user: DefaultUser,
        amount: String,
        amountCC: String,
        amountVT: String,
        amountMoveCC: String,
        wallet: MainWallet
    ) {
        val alias = openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
        openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }.addPayment(alias, amount)

        openPage<AtmMarketplacePage>(driver) { submit(user) }.buyOrReceiveToken(CC, amountCC, user, wallet)
        openPage<AtmMarketplacePage>(driver) { submit(user) }.buyOrReceiveToken(VT, amountVT, user, wallet)

        openPage<AtmWalletPage>(driver) { submit(user) }.moveToOTFWalletNew(
            amountMoveCC,
            CC, user, wallet
        )
        openPage<AtmWalletPage>(driver) { submit(user) }.moveToOTFWalletNew(amountVT, VT, user, wallet)
    }

    //add balances to users list
    fun initBalances() {

        val amount = "3000"
        val amountCC = "200"
        val amountVT = "100"
        val amountMoveCC = "100"

        val users: List<UserWithMainWalletAndOtf> = listOf(
            Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET,
            Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET,
            Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF,
            Users.ATM_USER_2FA_OTF_OPERATION,
            Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA,
            Users.ATM_USER_2FA_OTF_OPERATION_SECOND,
            Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02,
            Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04,
            Users.ATM_USER_2FA_OTF_OPERATION_EIGHTH,
            Users.ATM_USER_2FA_OTF_OPERATION_SEVENTH,
            Users.ATM_USER_2FA_OTF_OPERATION_SIXTH,
            Users.ATM_USER_2FA_OTF_OPERATION_FIFTH,
            Users.ATM_USER_2FA_OTF_OPERATION_FORTH,
            Users.ATM_USER_2FA_OTF_OPERATION_THIRD
        )

        openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }.changeFeeForToken(CC, CC, "0", "1", "1")
        openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }.changeFeeForToken(VT, VT, "0", "1", "1")

        users.forEach { user ->
            try {
                val wallet = user.mainWallet
                presetForOTF(user, amount, amountCC, amountVT, amountMoveCC, wallet)
            } catch (e: Exception) {
            } finally {
                openPage<AtmProfilePage>(driver).logout()
            }
        }
    }

    fun prerequisitesRfq(tokenName: CoinType, secondTokenName: CoinType) {
        openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }.addTokenIfNotPresented(tokenName)
        openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }.changeFeeSettingsForToken(
            tokenName,
            secondTokenName
        )
    }

    fun prerequisitesStreaming(
        baseInputValue: CoinType,
        quoteValue: CoinType,
        availableAmountValue: String,
        feePlaceAmountValue: String,
        feeAcceptAmountValue: String,
        feePlaceModeValue: String,
        feeAcceptModeValue: String,
        available: Boolean
    ) {
        openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }.addTradingPairIfNotPresented(
            baseInputValue.tokenSymbol, quoteValue.tokenSymbol, "", availableAmountValue, feePlaceAmountValue,
            feeAcceptAmountValue, feePlaceModeValue, feeAcceptModeValue, available
        )
        openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }.changeFeeSettingsForTokenStreaming(
            baseInputValue.tokenSymbol,
            quoteValue.tokenSymbol, "1", "1"
        )
    }

    fun prerequisitesBlocktrade(
        tokenName: String,
        availableCheckbox: Boolean,
        feePlacingAmountValue: String,
        feePlacingAssetValue: String,
        feePlacingModeValue: String,
        feeAcceptingAssetValue: String,
        feeAcceptingAmountValue: String,
        feeAcceptingModeValue: String,
        baseToken: CoinType, quoteToken: CoinType
    ) {
        openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }.addTokenIfNotPresented(
            tokenName,
            true,
            feePlacingAmountValue,
            feePlacingAssetValue,
            feePlacingModeValue,
            feeAcceptingAssetValue,
            feeAcceptingAmountValue,
            feeAcceptingModeValue
        )
        openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }.changeFeeSettingsForTokenBlocktrade(
            baseToken, quoteToken
        )
    }

    //add balance to wallet
    //TODO: should fix from step() {} to annotation
    //@Step("Precondition. Add balance to main wallet")
    fun addCurrencyCoinToWallet(user: DefaultUser, amount: String, wallet: SimpleWallet) {
        step("Precondition. Add balance to main wallet") {
            val alias = openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
            openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }.addPayment(alias, amount)
            openPage<AtmMarketplacePage>(driver) { submit(user) }.buyOrReceiveToken(CC, amount, user, wallet)
        }
    }

    //move form main to otf
    //TODO: add coin parameter
    fun moveCurrencyCoinFromMainToOTFWallet(user: DefaultUser, amount: String, wallet: MainWallet) {
        step("Precondition. Move balance from MAIN wallet to OTF") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                moveToOTFWallet(amount, user, wallet)
            }
        }
    }

    //check what there is approval for user (by email) in submitted status and declined it, or create new approval and reject it
    fun checkAndRejectEmployeeAdding(user: User, email: String) {
        step("Precondition. Create approval with status rejected") {
            with(openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }) {
                if (!inviteTable.find(findApprovalWithEmailAndStatus(email, "submitted")).isNullOrEmpty()) {
                    rejectUserWithEmail(email)
                } else if (inviteTable.find(findApprovalWithEmailAndStatus(email, "rejected")).isNullOrEmpty()) {
                    openPage<AtmEmployeesPage>(driver) { submit(user) }.addAndRejectEmployee(
                        email,
                        AtmEmployeesPage.Roles.ADMIN
                    )
                }
                0
            }
        }
    }

    //check what bank accounts list is empty and delete all banks accounts if not empty
    fun bankAccountsListShouldBeEmpty(user: User) {
        step("Precondition. Delete all bank account list if it's not empty") {
            with(openPage<AtmBankAccountsPage>(driver) { submit(user) }) {
                if (usdPanel.getAttribute("aria-expanded") == "false") {
                    e {
                        click(usdPanel)
                    }
                    if (bankAccountsList.isNotEmpty()) {
                        bankAccountsList.forEach { ba ->
                            ba.select()
                            ba.deleteWithConfirm()
                        }
                    }
                }

            }
        }
    }

    //set controller for wallet
    fun setControllerStateForWallet(walletName: String, admin: User, employee: User, state: Boolean) {
        step("Set controller state for wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(admin) }) {
                chooseWallet(walletName)
                e {
                    click(assign)
                }
                findEmployeeAndSetControllerCheckBox(employee.email, state, admin)
            }
            openPage<AtmProfilePage>(driver).logout()
        }
    }

    //create P2P offer
    fun createP2P(
        walletID: String,
        companyName: String,
        coinToSend: CoinType,
        amountSend: String,
        coinToReceive: CoinType,
        amountReceive: String,
        expiryType: AtmP2PPage.ExpireType,
        user: HasOtfWallet
    ) {
        step("Create P2P offer") {
            openPage<AtmProfilePage>(driver).logout()
            with(openPage<AtmP2PPage>(driver) { submit(user) }) {
                createP2PwithoutSign(
                    walletID,
                    companyName,
                    coinToSend,
                    amountSend,
                    coinToReceive,
                    amountReceive,
                    expiryType
                ).also {
                    signAndSubmitMessage(user, user.otfWallet.secretKey)
                }
                //TODO: add assert for successful creating offer
            }
        }
    }

    //accept  P2P offer
    fun acceptP2P(user: HasOtfWallet, amount: BigDecimal) {
        step("Accept incoming P2P offer") {
            with(openPage<AtmP2PPage>(driver) { submit(user) }) {
                findIncomingP2P(amount)
                wait(15L) {
                    until("Couldn't load fee") {
                        offerFee.text.isNotEmpty()
                    }
                    offerFee.amount
                }

                wait(15L) {
                    until("Couldn't load wallet") {
                        fromWalletText.text != " No wallet "
                    }
                }

                e {
                    click(acceptFromDetails)
                    signAndSubmitMessage(user, user.otfWallet.secretKey)
                }
            }
            openPage<AtmProfilePage>(driver).logout()
        }
    }

    fun addITToken(
        user: DefaultUser,
        user1: DefaultUser,
        wallet: MainWallet,
        walletForAccept: MainWallet,
        amountForIT: BigDecimal,
        maturityDate: String
    ) {
        placeAndProceedTokenRequest(
            IT, wallet, walletForAccept,
            amountForIT,
            APPROVE, user, user1, maturityDate
        )
    }

    //подходит для для любого токена который необходимо подверждать на странице Issuance
    @Step("buy token")
    @Action("User place order and set status")
    fun placeAndProceedTokenRequest(
        coinType: CoinType,
        wallet: MainWallet,
        walletAccept: MainWallet,
        amount: BigDecimal,
        statusType: AtmIssuancesPage.StatusType,
        user: DefaultUser,
        user1: DefaultUser,
        maturityDate: String = ""
    ) {
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyOrReceiveToken(coinType, amount.toString(), user, wallet, maturityDate)
        }
        AtmProfilePage(driver).logout()

        with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
            changeStatusForOfferWithAmount(coinType, amount, statusType, user1, walletAccept)
        }

    }

    data class Result(
        val amountRedemption: String,
        val amountRedemption1: String,
        val amountRedemption2: String,
        val barNo: String,
        val barNo1: String,
        val barNo2: String
    )

    @Step("upload nomenclature and transfer ETC token")
    @Action("Upload nomenclature and transfer ETC token")
    fun prerequisiteForEtc(
        etcWallet: MainWallet,
        wallet: MainWallet,
        fileName: String,
        user: DefaultUser
    ): Result {

        val (amountRedemption, amountRedemption1, amountRedemption2,
            barNo, barNo1, barNo2) = FileHelper.createNomenclature(fileName)

        with(openPage<AtmIssuancesPage>(driver) { submit(user) }) {
            chooseToken(ETC)
            e {
                click(manageVolume)
                click(addVolume)
            }
            uploadNomenclature(uploadNomenclature1, "${fileName}.csv", user, etcWallet)
        }
        FileHelper.deleteFile("${fileName}.csv")

        AtmIssuancesPage(driver)
        val amountEtcRedemption = amountRedemption.toBigDecimal() * BigDecimal(1000)

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            transferFromWalletToWallet(ETC, etcWallet, wallet, amountEtcRedemption.toString(), "", "etc", user)
        }
        return Result(amountRedemption, amountRedemption1, amountRedemption2, barNo, barNo1, barNo2)
    }
}