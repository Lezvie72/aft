package frontend.atm.otс.blocktrade

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
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
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.BLOCKTRADE.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Blocktrade. Hide industrial tokens and offers with industrial tokens from non-industrial participants")
class HideIndustrialTokensAndOffersWithIndustrialTokensFromNonIndustrialParticipants : BaseTest() {
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val nonIndustrialUser = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val industrialToken = CoinType.IT


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4323")
    @Test
    @DisplayName("Blocktade.Selection industrial tokens only by participants having industrial mark in admin panel.")
    fun selectionIndustrialTokensOnlyByParticipantsHavingIndustrialMarkInAdminPanel() {
        val amountOne = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val amountTwo = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        var fee = BigDecimal("0.0000")

        val (walletID, companyName) = step("GIVEN $userTwo walletID is known") {
            val companyName = utils.helpers.openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
            val walletID = utils.helpers.openPage<AtmWalletPage>(driver).takeWalletID()
            walletID to companyName
        }

        utils.helpers.openPage<AtmProfilePage>(driver).logout()

        with(utils.helpers.openPage<AtmP2PPage>(driver) { submit(userOne) }) {
            e {
                softAssert { elementContainingTextPresented("MY BLOCKTRADES") }
                softAssert { elementContainingTextPresented("INCOMING BLOCKTRADES") }
                softAssert { elementContainingTextPresented("TRADE HISTORY") }

                fee = createP2P(
                    walletID,
                    companyName,
                    baseAsset,
                    amountOne.toString(),
                    quoteAsset,
                    amountOne.toString(),
                    AtmP2PPage.ExpireType.TEMPORARY,
                    userOne,
                    manualCompleted = true
                )

                assert {
                    elementContainsText(assetToSend, baseAsset.tokenSymbol)
                }
                e {
                    click(createDeal)
                    signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                }
                assertThat(
                    "Offer $amountOne + $fee should be exist",
                    isOfferExist(amountOne + fee, outgoingOffers)
                )

                fee = with(utils.helpers.openPage<AtmP2PPage>(driver)) {
                    createP2P(
                        walletID,
                        companyName,
                        quoteAsset,
                        amountTwo.toString(),
                        baseAsset,
                        amountTwo.toString(),
                        AtmP2PPage.ExpireType.TEMPORARY,
                        userOne,
                        manualCompleted = true
                    )
                }

                assert {
                    elementContainsText(assetToSend, quoteAsset.tokenSymbol)
                }
                e {
                    click(createDeal)
                    signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                }
                assertThat(
                    "Offer $amountTwo should be exist",
                    isOfferExist(amountTwo, outgoingOffers)
                )
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4326")
    @Test
    @DisplayName("Blocktade.Offers with industrial tokens by industrial participants")
    fun offersWithIndustrialTokensByIndustrialParticipants() {
        val amountOne = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val amountTwo = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        var fee = BigDecimal("0.0000")

        step("Preconditions - create two offers") {
            val (walletID, companyName) = step("GIVEN $userTwo walletID is known") {
                val companyName = utils.helpers.openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
                val walletID = utils.helpers.openPage<AtmWalletPage>(driver).takeWalletID()
                walletID to companyName
            }

            utils.helpers.openPage<AtmProfilePage>(driver).logout()

            with(utils.helpers.openPage<AtmP2PPage>(driver) { submit(userOne) }) {
                e {
                    softAssert { elementContainingTextPresented("MY BLOCKTRADES") }
                    softAssert { elementContainingTextPresented("INCOMING BLOCKTRADES") }
                    softAssert { elementContainingTextPresented("TRADE HISTORY") }


                    fee = createP2P(
                        walletID,
                        companyName,
                        baseAsset,
                        amountOne.toString(),
                        quoteAsset,
                        amountOne.toString(),
                        AtmP2PPage.ExpireType.TEMPORARY,
                        userOne
                    )
                    alert { checkErrorAlert(5L) }
                    signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                    assert {
                        elementShouldBeDisappeared(createDeal, 10L)
                    }


                    fee = with(utils.helpers.openPage<AtmP2PPage>(driver)) {
                        createP2P(
                            walletID,
                            companyName,
                            baseAsset,
                            amountTwo.toString(),
                            quoteAsset,
                            amountTwo.toString(),
                            AtmP2PPage.ExpireType.TEMPORARY,
                            userOne
                        )
                    }
                    alert { checkErrorAlert(5L) }
                    signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                    assert {
                        elementShouldBeDisappeared(createDeal, 10L)
                    }
                }
                utils.helpers.openPage<AtmProfilePage>(driver).logout()
            }
        }

        step("Step 1-5. Accept blocktrade offer with count $amountOne") {
            with(utils.helpers.openPage<AtmP2PPage>(driver) { submit(userTwo) }) {
                acceptP2P(userTwo, amountOne)
            }
        }

        step("Step 6-8. Reject blocktrade offer with count $amountOne") {
            with(utils.helpers.openPage<AtmP2PPage>(driver)) {
                findIncomingP2P(amountTwo)
                rejectP2P(userTwo)
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4328")
    @Test
    @DisplayName("Blocktade.Selection industrial tokens by not an industrial participant.")
    fun selectionIndustrialTokensByNotAnIndustrialParticipant() {
        with(utils.helpers.openPage<AtmP2PPage>(driver) { submit(nonIndustrialUser) }) {
            e {
                softAssert { elementContainingTextPresented("MY BLOCKTRADES") }
                softAssert { elementContainingTextPresented("INCOMING BLOCKTRADES") }
                softAssert { elementContainingTextPresented("TRADE HISTORY") }

                click(createBlockTrade)

                assert {
                    hasNotLeastOneCommonMeaning(
                        assetToSend.getHeadersAsString(page), mutableSetOf(industrialToken.tokenSymbol),
                        "Send token $industrialToken.tokenSymbol was found for non industrial account ${nonIndustrialUser.email}"
                    )
                }

                assert {
                    hasNotLeastOneCommonMeaning(
                        assetToReceive.getHeadersAsString(page), mutableSetOf(industrialToken.tokenSymbol),
                        "Receive token $industrialToken.tokenSymbol was found for non industrial account ${nonIndustrialUser.email}"
                    )
                }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4329")
    @Test
    @DisplayName("Blocktade.Offers with industrial tokens for non-industrial participant.")
    fun offersWithIndustrialTokensForNonIndustrialParticipant() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        var fee = BigDecimal("0.0000")

        val (walletID, companyName) = step("GIVEN $nonIndustrialUser walletID is known") {
            val companyName =
                utils.helpers.openPage<AtmProfilePage>(driver) { submit(nonIndustrialUser) }.getCompanyName()
            val walletID = utils.helpers.openPage<AtmWalletPage>(driver).takeWalletID()
            walletID to companyName
        }

        utils.helpers.openPage<AtmProfilePage>(driver).logout()

        step("Precondition - create offer $amount from ${userOne.email} to ${nonIndustrialUser.email}") {
            with(utils.helpers.openPage<AtmP2PPage>(driver) { submit(userOne) }) {
                e {
                    fee = createP2P(
                        walletID,
                        companyName,
                        industrialToken,
                        amount.toString(),
                        baseAsset,
                        amount.toString(),
                        AtmP2PPage.ExpireType.TEMPORARY,
                        userOne
                    )
                    alert { checkErrorAlert(5L) }
                    signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                    assert {
                        elementShouldBeDisappeared(createDeal, 10L)
                    }

                    isOfferExist(amount, outgoingOffers)
                }
                utils.helpers.openPage<AtmProfilePage>(driver).logout()
            }
        }
        step("User ${nonIndustrialUser.email} check incoming offers") {
            with(utils.helpers.openPage<AtmP2PPage>(driver) { submit(nonIndustrialUser) }) {
                e {
                    click(viewIncomingP2P)
                    assertThat(
                        "Offer with count $amount in blocktrade should not exist for non industrial user ${nonIndustrialUser.email}",
                        !isOfferExist(amount, incomingOffers)
                    )
                }
            }
        }
    }
}