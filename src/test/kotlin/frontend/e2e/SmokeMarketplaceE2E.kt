package frontend.e2e

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminPaymentsPage
import pages.atm.AtmAdminTokensPage
import pages.atm.AtmMarketplacePage
import pages.atm.AtmWalletPage
import ru.yandex.qatools.htmlelements.element.Button
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to
import java.math.BigDecimal

@Tag(TagNames.Flow.SMOKEE2E)
@Epic("Frontend")
@Feature("E2E")
@Story("Marketplace")
class SmokeMarketplaceE2E : BaseTest() {

    @TmsLink("ATMCH-5262")
    @Test
    @DisplayName("Admin panel Tokens steps 1-12")
    fun adminPanelTokens() {
        step("Admin go to Token page and check information") {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                assert {
                    elementPresented(addToken)
                    elementPresented(editToken)
                    elementPresented(usdEquivalentSettings)
                    elementPresented(transferFee)
                    elementPresented(search)

                    elementPresented(tokensTable)

                    //The grid contains following columns:
                    elementContainingTextPresented("Id ")
                    elementContainingTextPresented("Issuer")
                    elementContainingTextPresented("token name")
                    elementContainingTextPresented("ticker symbol")
                    elementContainingTextPresented("Token Type")
                    elementContainingTextPresented("Token Description")
                    elementContainingTextPresented("Underlying asset")
                    elementContainingTextPresented("USD Equivalent")
                    elementContainingTextPresented("Total supply")
                    elementContainingTextPresented("Token quantity per unit/unit")
                    elementContainingTextPresented("Deal type")
                    elementContainingTextPresented("Created")
                    elementContainingTextPresented("Updated")
                }
                e { click(addToken) }
                assert {
                    elementPresented(tokenIssuer)
                    elementPresented(tokenName)
                    elementPresented(tickerSymbol)
                    elementPresented(tokenType)
//                    elementPresented(tokenStatus)
                    elementPresented(tokenDescription)
                    elementPresented(channelName)
                    elementPresented(chaincodeName)
                    elementPresented(underlyingAsset)
                    elementPresented(confirm)
                    elementPresented(cancel)
                }
                e {
                    click(cancel)

//                    sendKeys(search, VT.tokenSymbol)
                    val row = tokensTable.find {
                        it[AtmAdminTokensPage.TICKER_SYMBOL]?.text == VT.tokenSymbol
                    }?.get(AtmAdminTokensPage.TICKER_SYMBOL)?.to<Button>("Ticker symbol VT")
                        ?: error("Row with Ticker symbol VT not found in table")
                    e {
                        click(row)
                    }
                }
                assert { elementEnabled(transferFee) }
                e {
                    click(editToken)
                }
                assert {
                    elementPresented(tokenIssuer)
                    elementPresented(tokenName)
                    elementPresented(tickerSymbol)
//                    elementPresented(tokenStatus)
                    elementPresented(tokenDescription)
                    elementPresented(channelName)
                    elementPresented(chaincodeName)
                    elementPresented(underlyingAsset)
                    elementPresented(confirm)
                    elementPresented(cancel)
                }
                e {
                    click(cancel)
                }
                assert {
                    elementPresented(createDateTo)
                    elementPresented(createDateFrom)
                    elementPresented(updateDateTo)
                    elementPresented(updateDateFrom)
                }
            }
        }

    }

    @TmsLink("ATMCH-5262")
    @Test
    @DisplayName("Admin panel Tokens change fee steps 13-19")
    fun adminPanelTokensChangeFee() {
        val newFeeRate = "100.00000000"
        val newFloor = "1.10000000"
        val newCap = "100.10000000"

        step("Admin go to Token page and changes fee") {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
//                e {
//                    sendKeys(search, CC.tokenSymbol)
//                }
                val row = tokensTable.find {
                    it[AtmAdminTokensPage.TICKER_SYMBOL]?.text == CC.tokenSymbol
                }?.get(AtmAdminTokensPage.TICKER_SYMBOL)?.to<Button>("Ticker symbol $CC")
                    ?: error("Row with Ticker symbol $CC not found in table")
                e {
                    click(row)
                    click(transferFee)
                    wait {
                        until("Table history of fee is displayed", 15) {
                            check {
                                isElementPresented(historyTable)
                            }
                        }
                    }
                }

                e {
                    click(clearButton)
                    chargeIn.sendAndSelect(CC.tokenSymbol, CC.tokenSymbol, this@with)
                    sendKeys(rate, newFeeRate)
                    sendKeys(floor, newFloor)
                    sendKeys(cap, newCap)

                    wait {
                        until("Button Save is presented", 10) {
                            check {
                                isElementPresented(save)
                            }
                        }
                    }
                    click(save)
                    wait {
                        until("Button Save is presented", 15) {
                            check {
                                isElementWithTextPresented("Fee was updated")
                            }
                        }
                    }
                }
            }


        }

        step("User go to Marketplace page and check fee values") {
            with(openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA) }) {
                chooseToken(CC)
                assertThat(
                    "Expected New Token name '${tokenInfo.feeRate.text}' is '$newFeeRate'",
                    tokenInfo.feeRate.amount,
                    Matchers.`is`(BigDecimal(newFeeRate))
                )

                assertThat(
                    "Expected New Token description '${tokenInfo.floor.text}' is '$newFloor'",
                    tokenInfo.floor.amount,
                    Matchers.`is`(BigDecimal(newFloor))
                )

                assertThat(
                    "Expected New Token name '${tokenInfo.cap.name}' is '$newCap'",
                    tokenInfo.cap.amount,
                    Matchers.`is`(BigDecimal(newCap))
                )

            }

        }

        step("Admin go to Token page and changes fee back") {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                changeFeeForToken(CC, CC, "0", "1", "1")
            }

        }
    }

    @TmsLink("ATMCH-5262")
    @Test
    @DisplayName("Admin panel Tokens change status for Token steps 20-31")
    fun adminPanelTokensChangeStatus() {
        //todo вопрос осается в статус архвный ставить сущесвующте токены не рентабельно так как по флоу их нельзя перевести обратно в действущий
//        step("Admin go to Token page and changes status for token") {
//            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
//                val tokenName = "FT"
//                val tokenButton = tokensTable.find {
//                    it[AtmAdminTokensPage.TICKER_SYMBOL]?.text == tokenName
//                }?.get(AtmAdminTokensPage.TICKER_SYMBOL)?.to<Button>("Ticker symbol CC")
//                    ?: error("Row with Ticker symbol CC not found in table")
//                editTokenStatus(tokenButton, "Archived")
//            }
//        }
//        step("User go to Marketplace page and check status Token")
//        {
//            with(openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA) }) {
//                assert {
//                    elementContainingTextNotPresented(FT.tokenName)
//                }
//            }
//
//        }
        step("Admin go to Token page and changes status for token")
        {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                val tokenName = FT.tokenSymbol
                val tokenButton = tokensTable.find {
                    it[AtmAdminTokensPage.TICKER_SYMBOL]?.text == tokenName
                }?.get(AtmAdminTokensPage.TICKER_SYMBOL)?.to<Button>("Ticker symbol $tokenName")
                    ?: error("Row with Ticker symbol $tokenName not found in table")
                editTokenStatus(tokenButton, AtmAdminTokensPage.StatusToken.UNAVAILABLE)
            }

        }
        step("User go to Marketplace page and check status Token")
        {
            with(openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA) }) {
                assert {
                    elementContainingTextNotPresented(FT.tokenName)
                }
            }

        }
        step("Admin go to Token page and changes status for token")
        {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                val tokenName = FT.tokenSymbol
                val tokenButton = tokensTable.find {
                    it[AtmAdminTokensPage.TICKER_SYMBOL]?.text == tokenName
                }?.get(AtmAdminTokensPage.TICKER_SYMBOL)?.to<Button>("Ticker symbol $tokenName")
                    ?: error("Row with Ticker symbol $tokenName not found in table")
                editTokenStatus(tokenButton, AtmAdminTokensPage.StatusToken.AVAILABLE)
            }
        }

        step("User go to Marketplace page and check status Token")
        {
            with(openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA) }) {
                assert {
                    elementContainingTextPresented(FT.tokenName)
                }
            }

        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET)
    @TmsLink("ATMCH-5262")
    @Test
    @DisplayName("Buy CC Token And Check Balance steps 33-43")
    fun buyCCTokenAndCheckBalance() {
        val amount = "10"
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val wallet = user.mainWallet
        step("Precondition") {
            val alias = openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
            openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }.addPayment(alias, amount)
        }

        val balanceBefore = step("User check balance before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(CC, wallet.name)
        }
        step("User buy CC token") {
            openPage<AtmMarketplacePage>(driver) { submit(user) }.buyOrReceiveToken(CC, amount, user, wallet)
        }
        //TODO transaction history
        val balanceAfter = step("User check balance after operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(CC, wallet.name)
        }

        assertThat(
            "Expected base balance: $balanceAfter, was: $balanceBefore",
            balanceAfter,
            Matchers.closeTo(balanceBefore + amount.toBigDecimal(), BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET)
    @TmsLink("ATMCH-5262")
    @Test
    @DisplayName("Buy VT Token And Check Balance steps 44-53")
    fun buyVTTokenAndCheckBalance() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET
        val wallet = user.walletList[0]

//        prerequisite { addCurrencyCoinToWallet(user, amount, wallet) }

        val balanceBefore = step("User check balance before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(VT, wallet.name)
        }
        step("User buy VT token") {
            openPage<AtmMarketplacePage>(driver) { submit(user) }.buyOrReceiveToken(VT, amount, user, wallet)
        }
        //TODO transaction history
        val balanceAfter = step("User check balance after operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(VT, wallet.name)
        }

        assertThat(
            "Expected base balance: $balanceAfter, was: $balanceBefore",
            balanceAfter,
            Matchers.closeTo(balanceBefore + amount.toBigDecimal(), BigDecimal("0.01"))
        )
    }
}


