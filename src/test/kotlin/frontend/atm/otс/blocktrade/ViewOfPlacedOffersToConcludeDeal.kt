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
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.BLOCKTRADE.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("View of placed offers to conclude a deal")
class ViewOfPlacedOffersToConcludeDeal : BaseTest() {

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND),
        ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET)
    )
    @TmsLink("ATMCH-928")
    @Test
    @DisplayName("P2P. Check outcoming offers. Good till cancel offer")
    fun p2pCheckOutgoingOffersGoodTillCancelOffer() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
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
                baseAsset, quoteAsset
            )
        }

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()
        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(
                walletID,
                companyName,
                CC,
                amount.toString(),
                VT,
                amount.toString(),
                ExpireType.GOOD_TILL_CANCELLED,
                user1
            )
            e {
                wait {
                    until("wait for loading list of open transactions", 15) {
                        check {
                            isElementPresented(openOutgoingP2P)
                        }
                    }
                }
            }
            assert {
                elementWithTextPresentedIgnoreCase("AMOUNT TO RECEIVE")
                elementWithTextPresentedIgnoreCase("AMOUNT TO SEND")
                elementWithTextPresentedIgnoreCase("COUNTERPARTY")
//                нету
//                elementWithTextPresentedIgnoreCase("Token name")
//                нету
//                elementWithTextPresented("Type offer")
                elementIsDisplayed("Cancel P2P Order")
            }
            e {
                click(createFromMyBlockTrade)
            }
            assert {
                urlEndsWith("/trading/p2p/outgoing/create")
            }
            e {
                click(myP2PFromCreate)
            }
            assert {
                elementWithTextPresented("Token received:")
                elementWithTextPresented("Token transferred:")
                elementWithTextPresented("Date from:")
                elementWithTextPresented("Date to:")
                elementWithTextPresented(" Sort by: ")
            }
            val myOffer = outgoingOffers.find {
                it.amountToReceive == amount
            } ?: error("Couldn't find offer with amount $amount")

            myOffer.clickCancelButton()
            assert {
                elementPresented(cancelOfferConfirmationDialog)
            }

        }

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION),
        ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET)
    )
    @TmsLink("ATMCH-929")
    @Test
    @DisplayName("P2P. Check outcoming offers. Temporary offer")
    fun p2pCheckOutgoingOffersTemporaryOffer() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT

        val user1 = Users.ATM_USER_2FA_OTF_OPERATION
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
                ExpireType.TEMPORARY,
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

            assertThat(myOffer.amountToReceive, Matchers.equalTo(amount))
            assertThat(myOffer.amountToSend, Matchers.equalTo(amount.plus(fee)))
            assertThat(myOffer.currencyToReceive, Matchers.equalTo(quoteAsset.tokenSymbol))
            assertThat(myOffer.currencyToSend, Matchers.equalTo(baseAsset.tokenSymbol))

            myOffer.clickCancelButton()
            e {
                click(yesButton)
            }
            assert {
                elementPresented(privateKey)
                elementWithTextPresented("Cancel offer confirmation")
            }
            myOffer.cancelOffer()
            e {
                click(yesButton)
            }
            signAndSubmitMessage(user1, user1.otfWallet.secretKey)
        }
    }
}


