package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmWalletPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Fiat depositing - reviewing the bank details of the Atomize")
class FiatDepositingReviewingTheBankDetailsOfTheAtomize : BaseTest() {

    @TmsLink("ATMCH-1724")
    @Test
    @DisplayName("FIAT type is doesn't show for OTF wallet requisites")
    fun fiatTypeDoesntShowForOtfWalletRequisites() {
        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(otfWalletTicker)
            }
            assert {
                elementNotPresented(depositDetails)
            }
        }
    }

    @TmsLink("ATMCH-618")
    @Test
    @DisplayName("Main wallet. Fiat requisites (Fiat deposit details)")
    fun mainWalletFiatRequisitesFiatDepositDetails() {
        val user = Users.ATM_USER_2MAIN_WALLET
        val wallet = user.mainWallet

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(wallet.name)
            assert {
                elementIsDisplayed("Create wallet button")
                elementIsDisplayed("Assign button")
                elementIsDisplayed("Show zero balance button")
            }
            e {
                click(depositDetails)
            }
            assert {
//                elementIsDisplayed("Refresh wallet data")
                elementPresented(close)
//                elementIsDisplayed("Show zero balance button")
            }
            e {
                select(selectCurrency, "USD")
                select(selectBicType, "BIC")
            }
            assert {
                elementContainingTextPresented("Reference number")
                elementContainingTextPresented("Recipient name")
                elementContainingTextPresented("Recipient address")
                elementContainingTextPresented("Bank name")
                elementContainingTextPresented("Bank address")
                elementContainingTextPresented("BIC")
                elementContainingTextPresented("Account number")
            }
            checkReferenceValue()
        }
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(wallet.name)
            e {
                click(depositDetails)
//                click(referenceNumberHistory)
            }
//            assert {
//                elementIsDisplayed("Reference history")
//            }
//            e {
//                click(refreshReference)
//                wait {
//                    until("Reference number didn't show up") {
//                        referenceNumber.text != ""
//                    }
//                }
//            }
//
//            val alias = referenceNumber.text
//            checkNewAliasInHistory(alias)
            e {
                click(close)
            }
            assert { elementIsDisplayed("Fiat deposit details") }
        }

    }

    @TmsLink("ATMCH-2093")
    @Test
    @DisplayName("Fiat deposit details. Select bic type=Payment system")
    fun fiatDepositDetailsSelectBicTypePaymentSystem() {
        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_2MAIN_WALLET) }) {
            chooseWallet("Main 1")
            e {
                click(depositDetails)
                select(selectCurrency, "USD")
                select(selectBicType, "BIC")
            }
            checkReferenceValue()
        }
    }

}