package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.IT
import org.apache.commons.lang.RandomStringUtils.randomNumeric
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmIssuancesPage.StatusType.APPROVE
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Display Industrial Tokens In Wallet")
class DisplayIndustrialTokensInWallet : BaseTest() {

    private val maturityDate = IT.maturityDateMonthNumber


    @TmsLink("ATMCH-2780")
    @Test
    @DisplayName("Display industrial tokens in wallet. Interface.")
    fun displayIndustrialTokensInWalletInterface() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2MAIN_WALLET
        val mainWallet = user.walletList[0]

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        val balance = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
        }
        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0") {
            if (balance == BigDecimal.ZERO) {
                prerequisite { addITToken(user, user1, mainWallet, wallet, amount, maturityDate) }
                AtmProfilePage(driver).logout()
            }
        }

        step("User check element in IT token page") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet(mainWallet.name)
                setDisplayZeroBalance(true)
                assert {
                    elementWithTextPresented(" BALANCE ")
                    elementWithTextPresented(" AVAILABLE ")
                    elementWithTextPresented(" HELD IN ORDERS ")
                    elementPresented(maturityDates)
                }
                e {
                    click(maturityDates)
                }
                assert {
                    elementPresented(issueList)
                    elementPresented(issuanceInfo)
                    elementWithTextPresented(" MATURITY DATE ")
                    elementWithTextPresented(" AVAILABLE AMOUNT ")
                }
//            e {
//                click(issuanceInfo)
//            }
//            assert {
//                elementWithTextPresented("No parameters added yet")
//            }
            }
        }
    }

    @TmsLink("ATMCH-2779")
    @Test
    @DisplayName("Display industrial tokens in wallet. Functional.")
    fun displayIndustrialTokensInWalletFunctional() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        val balance = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
        }

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0") {
            if (balance == BigDecimal.ZERO) {
                prerequisite {
                    placeAndProceedTokenRequest(
                        IT, mainWallet, wallet, amount,
                        APPROVE, user, user1
                    )
                }
                AtmProfilePage(driver).logout()
            }
        }
        AtmProfilePage(driver).logout()

        step("User check element in IT token page") {

            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet(mainWallet.name)
                setDisplayZeroBalance(true)
                assert {
                    elementWithTextPresentedIgnoreCase("BALANCE")
                    elementWithTextPresentedIgnoreCase("AVAILABLE")
                    elementWithTextPresentedIgnoreCase("HELD IN ORDERS")
                    elementPresented(maturityDates)
                }
                e {
                    click(maturityDates)
                }
                assert {
                    elementPresented(issueList)
                    elementPresented(issuanceInfo)
                    elementWithTextPresentedIgnoreCase("MATURITY DATE")
                    elementWithTextPresentedIgnoreCase("AVAILABLE AMOUNT")
                }
                e {
                    click(issuanceInfo)
                }
                assert {
                    elementWithTextPresentedIgnoreCase("No parameters added yet")
                }
            }
        }
    }
}
