package frontend.atm.otс.blocktrade

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmP2PPage
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.BLOCKTRADE.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Show OTF balances in Wallets")
class ShowOtfBalanceInWallets : BaseTest() {
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-2560")
    @Test
    @DisplayName("Checking the OTF wallet balance after Blocktrade offer acceptance")
    fun checkingTheOtfWalletBalanceAfterBlocktradeOfferAcceptance() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        var fee = BigDecimal("0.0000")

        val (walletID, companyName) = step("GIVEN $userTwo walletID is known") {
            val companyName = utils.helpers.openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
            val walletID = utils.helpers.openPage<AtmWalletPage>(driver).takeWalletID()

            walletID to companyName
        }

        val (baseBeforeUserTwo, quoteBeforeUserTwo) = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            val base = getBalance(baseAsset, userTwo.otfWallet.name)
            utils.helpers.openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, userTwo.otfWallet.name)
            base to quote
        }

        val (baseBeforeHeldUserTwo, quoteBeforeHeldUserTwo) = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            val base = getHeldInOrders(baseAsset, userOne.otfWallet.name)
            utils.helpers.openPage<AtmWalletPage>(driver)
            val quote = getHeldInOrders(quoteAsset, userOne.otfWallet.name)
            base to quote
        }

        utils.helpers.openPage<AtmProfilePage>(driver).logout()

        val (baseBeforeUserOne, quoteBeforeUserOne) = with(utils.helpers.openPage<AtmWalletPage>(driver) {
            submit(
                userOne
            )
        }) {
            val base = getBalance(baseAsset, userOne.otfWallet.name)
            utils.helpers.openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, userOne.otfWallet.name)
            base to quote
        }

        val (baseBeforeHeldUserOne, quoteBeforeHeldUserOne) = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            val base = getHeldInOrders(baseAsset, userOne.otfWallet.name)
            utils.helpers.openPage<AtmWalletPage>(driver)
            val quote = getHeldInOrders(quoteAsset, userOne.otfWallet.name)
            base to quote
        }

        step("Create offer from ${userOne.email} to ${userTwo.email} and check balance user ${userOne.email}") {
            with(utils.helpers.openPage<AtmP2PPage>(driver)) {
                fee = createP2P(
                    walletID,
                    companyName,
                    baseAsset,
                    amount.toString(),
                    quoteAsset,
                    amount.toString(),
                    AtmP2PPage.ExpireType.TEMPORARY, userOne
                )
            }

            val (baseAfterUserOne, quoteAfterUserOne) = with(utils.helpers.openPage<AtmWalletPage>(driver) {
                submit(
                    userOne
                )
            }) {
                val base = getBalance(baseAsset, userOne.otfWallet.name)
                utils.helpers.openPage<AtmWalletPage>(driver)
                val quote = getBalance(quoteAsset, userOne.otfWallet.name)
                base to quote
            }

            val (baseAfterHeldUserOne, quoteAfterHeldUserOne) = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
                val base = getHeldInOrders(baseAsset, userOne.otfWallet.name)
                utils.helpers.openPage<AtmWalletPage>(driver)
                val quote = getHeldInOrders(quoteAsset, userOne.otfWallet.name)
                base to quote
            }

            val expectedBaseForUserOne = baseBeforeUserOne - amount - fee
            MatcherAssert.assertThat(
                "Expected base balance for user ${userOne.email}: expected $baseBeforeUserOne - $amount - $fee, but was: $baseAfterUserOne",
                baseAfterUserOne,
                Matchers.closeTo(expectedBaseForUserOne, BigDecimal("0.01"))
            )

            val expectedHeldForUserOne = baseBeforeHeldUserOne + amount + fee
            MatcherAssert.assertThat(
                "Expected base balance for user ${userOne.email}: expected $baseBeforeHeldUserOne + $fee + $amount, but was: $baseAfterHeldUserOne",
                baseAfterHeldUserOne,
                Matchers.closeTo(expectedHeldForUserOne, BigDecimal("0.01"))
            )
        }

        utils.helpers.openPage<AtmProfilePage>(driver).logout()

        step("Accept offer and check balance user ${userTwo.email}") {
            val fee2 = with(utils.helpers.openPage<AtmP2PPage>(driver) { submit(userTwo) }) {
                acceptP2P(userTwo, amount)
            }
            val (baseAfterUserTwo, quoteAfterUserTwo) = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
                val base = getBalance(baseAsset, userTwo.otfWallet.name)
                utils.helpers.openPage<AtmWalletPage>(driver)
                val quote = getBalance(quoteAsset, userTwo.otfWallet.name)
                base to quote
            }

            val (baseAfterHeldUserTwo, quoteAfterHeldUserTwo) = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
                val base = getHeldInOrders(baseAsset, userOne.otfWallet.name)
                utils.helpers.openPage<AtmWalletPage>(driver)
                val quote = getHeldInOrders(quoteAsset, userOne.otfWallet.name)
                base to quote
            }

            val expectedQuoteForUserTwo = quoteBeforeUserTwo - amount
            MatcherAssert.assertThat(
                "Expected quote balance for user ${userTwo.email}: expected $quoteAfterUserTwo - $amount, but was: $quoteAfterUserTwo",
                quoteAfterUserTwo,
                Matchers.closeTo(expectedQuoteForUserTwo, BigDecimal("0.01"))
            )

            MatcherAssert.assertThat(
                "Expected base balance for user ${userTwo.email}: expected $baseBeforeHeldUserTwo, but was: $baseAfterUserTwo",
                baseAfterHeldUserTwo,
                Matchers.closeTo(baseAfterHeldUserTwo, BigDecimal("0.01"))
            )
        }
        utils.helpers.openPage<AtmProfilePage>(driver).logout()

        step("Check held state user ${userOne.email} after accept his offer") {
            val (baseAfterHeldUserOneAfterAccept, quoteAfterHeldUserOneAfterAccept) = with(
                utils.helpers.openPage<AtmWalletPage>(
                    driver
                ) { submit(userOne) }) {
                val base = getHeldInOrders(baseAsset, userOne.otfWallet.name)
                utils.helpers.openPage<AtmWalletPage>(driver)
                val quote = getHeldInOrders(quoteAsset, userOne.otfWallet.name)
                base to quote
            }

            MatcherAssert.assertThat(
                "Expected base balance for user ${userOne.email}: expected $baseBeforeHeldUserOne, but was: $baseAfterHeldUserOneAfterAccept",
                baseBeforeHeldUserOne,
                Matchers.closeTo(baseAfterHeldUserOneAfterAccept, BigDecimal("0.01"))
            )
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-2557")
    @Test
    @DisplayName("Checking the OTF wallet balance held by Blocktrade offers")
    fun checkingTheOtfWalletBalanceHeldByBlocktradeOffers() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        var fee = BigDecimal("0.0000")

        val (walletID, companyName) = step("GIVEN $userTwo walletID is known") {
            val companyName = utils.helpers.openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
            val walletID = utils.helpers.openPage<AtmWalletPage>(driver).takeWalletID()
            walletID to companyName
        }
        utils.helpers.openPage<AtmProfilePage>(driver).logout()

        val baseBeforeUserOne = with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(userOne) }) {
            getBalance(baseAsset, userOne.otfWallet.name)
        }
        val baseBeforeHeldUserOne = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            getHeldInOrders(baseAsset, userOne.otfWallet.name)
        }

        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            e {
                step("Go to Wallets > Main wallet from preconditions > Token from preconditions and click on the Move button") {
                    waitWalletsAreDisplayed()
                    chooseWallet(userOne.mainWallet.name)
                    setDisplayZeroBalance(true)
                    chooseToken(baseAsset)
                    click(move)
                    wait {
                        until("Element TRANSFER TO OTF WALLET should be presented") {
                            untilPresentedAnyWithText<TextBlock>(
                                "TRANSFER TO OTF WALLET",
                                "Label with text TRANSFER TO OTF WALLET"
                            )
                        }
                    }
                }

                step("Step 2 Fill in valid value in Amount to transfer field and click on the Submit button") {
                    sendKeys(moveTokenQuantity, amount.toString())
                    sendKeys(transferNote, "test")
                    click(submitButton)
                    assert {
                        elementWithTextPresented("Manual signature")
                    }
                    signAndSubmitMessage(userOne, userOne.mainWallet.secretKey)
                }

                val baseAfterUserOne = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
                    getBalance(baseAsset, userOne.otfWallet.name)
                }
                val baseAfterHeldUserOne = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
                    getHeldInOrders(baseAsset, userOne.otfWallet.name)
                }

                step("Step 4 Go to OTF wallet > Moved Token and check the balance") {

                    val expectedQuoteForUserTwo = baseBeforeUserOne + amount
                    MatcherAssert.assertThat(
                        "Expected ${baseAsset.tokenSymbol} balance for user ${userOne.email}: expected $baseBeforeUserOne + $amount, but was: $baseAfterUserOne",
                        baseAfterUserOne,
                        Matchers.closeTo(expectedQuoteForUserTwo, BigDecimal("0.01"))
                    )
                    MatcherAssert.assertThat(
                        "Expected held ${baseAsset.tokenSymbol} balance for user ${userOne.email}: expected $baseBeforeHeldUserOne, but was: $baseAfterHeldUserOne",
                        baseAfterHeldUserOne,
                        Matchers.closeTo(baseBeforeHeldUserOne, BigDecimal("0.01"))
                    )
                }

                step("Step 5 Go to Trading > Blocktrade and create new offer") {
                    with(utils.helpers.openPage<AtmP2PPage>(driver)) {
                        fee = createP2P(
                            walletID,
                            companyName,
                            baseAsset,
                            amount.toString(),
                            quoteAsset,
                            amount.toString(),
                            AtmP2PPage.ExpireType.GOOD_TILL_CANCELLED, userOne
                        )
                    }
                }

                step("Step 6 Return to OTF wallet > Moved Token and check the balance") {
                    val baseAfterUserOneCreateOffer = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
                        getBalance(baseAsset, userOne.otfWallet.name)
                    }
                    val baseAfterHeldUserOneCreateOffer = with(utils.helpers.openPage<AtmWalletPage>(driver)) {
                        getHeldInOrders(baseAsset, userOne.otfWallet.name)
                    }
                    val expectedBaseForUserOneAfterCreate = baseAfterUserOne - amount - fee
                    MatcherAssert.assertThat(
                        "Expected ${baseAsset.tokenSymbol} balance for user ${userOne.email}: expected $baseAfterUserOne - $amount - $fee, but was: $baseAfterUserOneCreateOffer",
                        baseAfterUserOneCreateOffer,
                        Matchers.closeTo(expectedBaseForUserOneAfterCreate, BigDecimal("0.01"))
                    )
                    val expectedBaseHeldForUserOneAfterCreate = baseAfterHeldUserOne + amount + fee
                    MatcherAssert.assertThat(
                        "Expected held ${baseAsset.tokenSymbol} balance for user ${userOne.email}: expected $baseAfterHeldUserOne + $amount + $fee, but was: $baseAfterHeldUserOne",
                        baseAfterHeldUserOneCreateOffer,
                        Matchers.closeTo(expectedBaseHeldForUserOneAfterCreate, BigDecimal("0.01"))
                    )
                }
            }
        }
    }
}