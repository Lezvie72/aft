package frontend.atm.otс.blocktrade

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmAdminBlocktradeSettingsPage
import pages.atm.AtmP2PPage
import pages.atm.AtmP2PPage.ExpireType.GOOD_TILL_CANCELLED
import pages.atm.AtmP2PPage.ExpireType.TEMPORARY
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag("OTC"), Tag("Blocktrade"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("P2P Blocktrade. Placing new offer to conclude a deal")
class PlacingNewOfferToConcludeDeal : BaseTest() {

    @TmsLink("ATMCH-826")
    @Test
    @DisplayName("Place P2P offer. Interface")
    fun placeP2POfferInterface() {
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {

            e {
                click(createBlockTrade)
            }
            assert {
                elementWithTextPresentedIgnoreCase("New Blocktrade")
                elementWithTextPresentedIgnoreCase("Deal source")
                elementWithTextPresentedIgnoreCase("AVAILABLE BALANCE")
                elementWithTextPresentedIgnoreCase("Fee option")
                elementWithTextPresentedIgnoreCase("Transaction fee")
                elementWithTextPresentedIgnoreCase("AMOUNT TO RECEIVE")
                elementWithTextPresentedIgnoreCase("AMOUNT TO SEND")
                elementPresented(toWallet)
                elementPresented(assetToSend)
                elementPresented(amountToReceive)
                elementPresented(expiresIn)
                elementPresented(createDeal)
                elementPresented(cancel)
                elementPresented(goodTillCancelled)
                elementPresented(limitedTimeOffer)
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-832")
    @Test
    @DisplayName("Place P2P offer. Wrong signature")
    fun placeP2PwrongSignature() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")

        val user1 = Users.ATM_USER_2FA_OTF_OPERATION
        val user2 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET
        val baseAsset = CC
        val quoteAsset = VT

        prerequisite {
            prerequisitesBlocktrade(
                baseAsset.tokenSymbol,
                true,
                "1",
                baseAsset.tokenSymbol,
                "FIXED",
                baseAsset.tokenSymbol,
                "1",
                "FIXED",
                baseAsset,quoteAsset
            )
        }

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()

        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2PwithoutSign(
                walletID,
                companyName,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                TEMPORARY
            )
            e {
                signMessage(user2.otfWallet.secretKey)
                enterConfirmationCode(user1.oAuthSecret)
            }
            assert { elementContainingTextPresented(" Invalid key ") }
            e {
                click(cancelOfferSignatureDialog)
                click(myP2PFromCreate)
            }
            driver.navigate().refresh()
            val cancelledOffer = outgoingOffers.find {
                it.amountToReceive == amount
            }
            assertThat(
                "Offer with amount $amount should have been not created",
                cancelledOffer,
                nullValue()
            )
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-831")
    @Test
    @DisplayName("Place P2P offer. Temporary offer")
    fun placeP2PtemporaryOffer() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT

        val user1 = Users.ATM_USER_2FA_OTF_OPERATION
        val user2 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            changeFeeSettingsForTokenBlocktrade(CC, VT)
        }

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()
        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            val fee = createP2P(
                walletID,
                companyName,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                TEMPORARY,
                user1
            )
            e {
                click(createFromMyBlockTrade)
            }
            assert {
                urlEndsWith("/trading/p2p/outgoing/create")
            }
            e {
                click(myP2PFromCreate)
            }
            e {
                wait {
                    until("wait for loading list of open transactions", 15) {
                        check {
                            isElementPresented(openOutgoingP2P)
                        }
                    }
                }
            }
            val myOffer = outgoingOffers.find {
                it.amountToReceive == amount
            } ?: error("Couldn't find offer with amount $amount")

            assertThat(
                "Amount to receive: expected: $amount, was ${myOffer.amountToReceive}",
                myOffer.amountToReceive,
                Matchers.equalTo(amount)
            )
            assertThat(
                "Amount to send: expected: ${amount.plus(fee)}, was ${myOffer.amountToSend}",
                myOffer.amountToSend,
                Matchers.equalTo(amount.plus(fee))
            )
            assertThat(
                "Coin to receive: expected: $quoteAsset, was ${myOffer.currencyToReceive}",
                myOffer.currencyToReceive,
                Matchers.equalTo(quoteAsset.tokenSymbol)
            )
            assertThat(
                "Coin to send: expected: $baseAsset, was ${myOffer.currencyToSend}",
                myOffer.currencyToSend,
                Matchers.equalTo(baseAsset.tokenSymbol)
            )
        }
    }

    //    @Disabled("ATMCH-4009")
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA)
    )
    @TmsLink("ATMCH-829")
    @Test
    @DisplayName("Place P2P offer. Offer Good till cancel")
    fun placeP2PGoodTillCancelOffer() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            changeFeeSettingsForTokenBlocktrade(CC, VT)
        }

        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()
        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()

        val firstWallet = user1.otfWallet
        val secondWallet = user2.otfWallet

        val (baseBefore2, quoteBefore2) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
            val base = getBalance(baseAsset, secondWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, secondWallet.name)
            base to quote
        }

        openPage<AtmWalletPage>(driver).logout()

        val (baseBefore1, quoteBefore1) = with(openPage<AtmWalletPage>(driver) { submit(user1) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }
        val fee1 = with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(
                walletID,
                companyName,
                CC,
                amount.toString(),
                VT,
                amount.toString(),
                GOOD_TILL_CANCELLED,
                user1
            )
        }
        openPage<AtmWalletPage>(driver).logout()
        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            val fee2 = acceptP2P(user2, amount)
            val (baseAfter2, quoteAfter2) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
                val base = getBalance(baseAsset, secondWallet.name)
                openPage<AtmWalletPage>(driver)
                val quote = getBalance(quoteAsset, secondWallet.name)
                base to quote
            }
            assertThat(
                baseBefore2 + (amount - fee2),
                Matchers.closeTo(baseAfter2, BigDecimal("0.1"))
            )
            assertThat(
                quoteBefore2 - amount,
                Matchers.closeTo(quoteAfter2, BigDecimal("0.1"))
            )
            openPage<AtmWalletPage>(driver).logout()

            val (baseAfter1, quoteAfter1) = with(openPage<AtmWalletPage>(driver) { submit(user1) }) {
                val base = getBalance(baseAsset, firstWallet.name)
                openPage<AtmWalletPage>(driver)
                val quote = getBalance(quoteAsset, firstWallet.name)
                base to quote
            }
            assertThat(
                baseBefore1 - (amount + fee1),
                Matchers.closeTo(baseAfter1, BigDecimal("0.1"))
            )
            assertThat(
                quoteBefore1 + amount,
                Matchers.closeTo(quoteAfter1, BigDecimal("0.1"))
            )
        }
    }

    @ResourceLock(Constants.ROLE_USER_OTF_FOR_OTF)
    @TmsLink("ATMCH-828")
    @Test
    @DisplayName("Place P2P offer. Wrong 2FA Code")
    fun placeP2Pwrong2fa() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT

        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF
        val user2 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET
        val user3 = Users.ATM_USER_WITH_BLOCK_WALLET

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            changeFeeSettingsForTokenBlocktrade(CC, VT)
        }

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()
        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2PwithoutSign(
                walletID,
                companyName,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                TEMPORARY
            )
            e {
                signMessage(user1.otfWallet.secretKey)
                submitConfirmationCode(user3.oAuthSecret)
            }
            assert { elementContainingTextPresented("WRONG CODE") }
            e {
                submitConfirmationCode(user3.oAuthSecret)
            }
            assert { elementContainingTextPresented("WRONG CODE") }
            e {
                click(cancel2fa)
                click(createDeal)
                signMessage(user1.otfWallet.secretKey)
                submitConfirmationCode(user3.oAuthSecret)
            }
            assert { elementContainingTextPresented("WRONG CODE") }
            e {
                submitConfirmationCode(user3.oAuthSecret)
            }
            assert { elementContainingTextPresented("WRONG CODE") }
            e {
                submitConfirmationCode(user1.oAuthSecret)
                click(createFromMyBlockTrade)
            }
            assert {
                urlEndsWith("/trading/p2p/outgoing/create")
            }
            e {
                click(myP2PFromCreate)
            }
            e {
                wait {
                    until("wait for loading list of open transactions", 15) {
                        check {
                            isElementPresented(openOutgoingP2P)
                        }
                    }
                }
            }
            val myOffer = outgoingOffers.find {
                it.amountToReceive == amount
            } ?: error("Couldn't find offer with amount $amount")
            assert { elementPresented(myOffer) }
        }
    }

    @Disabled("не ясен функционал")
    @ResourceLock(Constants.USER_BALANCE_LOCK)
    @TmsLink("ATMCH-827")
    @Test
    @DisplayName("Place P2P offer. Insufficient funds")
    fun placeP2PofferInsufficientFunds() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val amount1 = BigDecimal("3.90000000")
        val baseAsset = CC
        val quoteAsset = VT

        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET
        val user3 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()
        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(
                walletID,
                companyName,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                TEMPORARY,
                user1
            )
        }
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user3) }) {
            createP2PwithoutSign(
                walletID,
                companyName,
                baseAsset,
                amount1.toString(),
                quoteAsset,
                amount.toString(),
                GOOD_TILL_CANCELLED
            )
            val p2pPage = driver.windowHandle
            driver.openTab()
            openPage<AtmP2PPage>(driver)
            createP2P(
                walletID,
                companyName,
                baseAsset,
                amount1.toString(),
                quoteAsset,
                amount.toString(),
                GOOD_TILL_CANCELLED,
                user3
            )
            driver.switchTo().window(p2pPage)
            //по тесту первая вкладка должна оставаться нетронутой, и должно появиться сообщение
            //по факту первая вкладка при работе со второй переходит на home
//            signAndSubmitMessage(user3)
//            assert {
//                elementContainingTextPresented("Insufficient funds")
//            }
        }
    }


}


