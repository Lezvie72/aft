package frontend.atm.otс.blocktrade

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType
import models.CoinType.CC
import models.CoinType.VT
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmP2PPage
import pages.atm.AtmP2PPage.ExpireType
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal


@Tags(Tag("OTC"), Tag("Blocktrade"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Acceptance of an offer and performance of a transaction")
class AcceptanceOfAnOfferAndPerformanceOfTransaction : BaseTest() {

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_EIGHTH),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SEVENTH)
    )
    @TmsLink("ATMCH-913")
    @Test
    @DisplayName("Accept P2P offer. Offer cancelled in time accept process")
    fun p2pOfferCancelledWhileSigned() {

        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT

        val user1 = Users.ATM_USER_2FA_OTF_OPERATION_EIGHTH
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SEVENTH

        val user1Browser = driver
        val user2Browser = createDriver()

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
                baseAsset, quoteAsset
            )
        }

        val (walletID, companyName) = step("GIVEN user1 walletID is known") {
            val companyName = openPage<AtmProfilePage>(user1Browser) { submit(user1) }.getCompanyName()
            val walletID = openPage<AtmWalletPage>(user1Browser) { submit(user1) }.takeWalletID()

            walletID to companyName
        }

        step("AND user2 has offer to user1 $amount@$baseAsset -> $amount$quoteAsset") {
            openPage<AtmP2PPage>(user2Browser) { submit(user2) }.also {
                it.createP2P(
                    walletID,
                    companyName,
                    baseAsset,
                    amount.toString(),
                    quoteAsset,
                    amount.toString(),
                    ExpireType.GOOD_TILL_CANCELLED,
                    user2
                )
            }
        }

        val page = openPage<AtmP2PPage>(user1Browser)
        step("WHEN user1 opens incoming P2P offers") {
            with(page) {
                e {
                    click(viewIncomingP2P)
                }
            }
        }

        val offer = step("THEN offer created by user2 is displayed") {
            with(page) {
                wait {
                    until("Couldn't find offer with amount '$amount'", 15L) {
                        incomingOffers.find {
                            it.amountToReceive == amount
                        }
                    }
                } ?: error("Couldn't find offer with amount '$amount'")
            }
        }

        step("WHEN user1 opens this offer") {
            with(page) {
                e {
                    click(offer)
                    wait(15L) {
                        until("Couldn't load fee") {
                            offerFee.text.isNotEmpty()
                        }
                    }
                    wait(15L) {
                        until("Couldn't load wallet") {
                            fromWalletText.text != " No wallet "
                        }
                    }
                    click(acceptFromDetails)
                }
            }
        }

        step("AND user2 cancel offer") {
            with(openPage<AtmP2PPage>(user2Browser) { submit(user2) }) {
                cancelOffer(amount, user2)
            }
        }

        step("AND user1 sign message with his private key") {
            with(page) {
                wait {
                    until("Signature is displayed", 20) {
                        check {
                            isElementWithTextPresented("Accept trade")
                        }
                    }
                }
                e {
                    sendKeys(privateKey, user1.otfWallet.secretKey)
                    click(confirmPrivateKeyButton)
                }
                enterConfirmationCode(user1.oAuthSecret)
            }
        }

        step("THEN error should be presented on page") {
            with(page) {
                assert {
                    elementWithTextPresented("Offer already closed")
                }
            }
        }
    }

    @Issue("IT токен отключен для ОТС")
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_OTF), ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-914")
    @Test
    @DisplayName("Accept P2P offer. Insufficient funds")
    fun acceptP2POfferInsufficientFunds() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = CoinType.IT

        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

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
                baseAsset, quoteAsset
            )
        }

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()
        val walletID = openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(
                walletID,
                companyName,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                ExpireType.GOOD_TILL_CANCELLED,
                user1
            )
        }
        openPage<AtmProfilePage>(driver).logout()
        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            findIncomingP2P(amount)
            assert {
//                elementDisabled(acceptFromDetails)//на кнопку можно нажать но но дальше ничего не происходит
                // по процессу вроде как верно так как баланс валюты нулевой
                elementNotPresented(privateKey)
            }
        }
    }

    //    @Disabled("ATMCH-4009")
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_THIRD),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_FORTH)
    )
    @TmsLink("ATMCH-856")
    @Test
    @DisplayName("Accept P2P offer. User have 2FA")
    fun acceptP2POfferUserHave2FA() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION_THIRD
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_FORTH

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
                baseAsset, quoteAsset
            )
        }

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()

        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()


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
            createP2P(walletID, companyName, CC, amount.toString(), VT, amount.toString(), ExpireType.TEMPORARY, user1)
        }

        openPage<AtmWalletPage>(driver).logout()

        val fee2 = with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            acceptP2P(user2, amount)
        }

        val (baseAfter2, quoteAfter2) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
            val base = getBalance(baseAsset, secondWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, secondWallet.name)
            base to quote
        }

        assertThat(
            baseBefore2 + (amount - fee2),
            closeTo(baseAfter2, BigDecimal("0.1"))
        )
        assertThat(
            quoteBefore2 - amount,
            closeTo(quoteAfter2, BigDecimal("0.1"))
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
            closeTo(baseAfter1, BigDecimal("0.1"))
        )
        assertThat(
            quoteBefore1 + amount,
            closeTo(quoteAfter1, BigDecimal("0.1"))
        )

    }

    //    @Disabled("ATMCH-4009")
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_FIFTH),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SIXTH)
    )
    @TmsLink("ATMCH-912")
    @Test
    @DisplayName("Accept P2P offer. User haven't 2fa")
    fun acceptP2POfferUserHaveNot2fa() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION_FIFTH
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SIXTH

        val firstWallet = user1.otfWallet
        val secondWallet = user2.otfWallet

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
                baseAsset, quoteAsset
            )
        }

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()

        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

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
            createP2P(walletID, companyName, CC, amount.toString(), VT, amount.toString(), ExpireType.TEMPORARY, user1)
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
                closeTo(baseAfter2, BigDecimal("0.1"))
            )
            assertThat(
                quoteBefore2 - amount,
                closeTo(quoteAfter2, BigDecimal("0.1"))
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
                closeTo(baseAfter1, BigDecimal("0.1"))
            )
            assertThat(
                quoteBefore1 + amount,
                closeTo(quoteAfter1, BigDecimal("0.1"))
            )
        }
    }

}


