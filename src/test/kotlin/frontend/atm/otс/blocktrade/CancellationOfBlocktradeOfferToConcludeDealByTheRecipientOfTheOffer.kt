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
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.*
import pages.atm.AtmP2PPage.ExpireType
import utils.Constants
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag("OTC"), Tag("Blocktrade"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Cancellation of a Blocktrade offer to conclude a deal by the recipient of the offer")
class CancellationOfBlocktradeOfferToConcludeDealByTheRecipientOfTheOffer : BaseTest() {

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_OTF), ResourceLock(Constants.ROLE_USER_OTF_FOR_OTF))
    @TmsLink("ATMCH-919")
    @Test
    @DisplayName("Cancellation P2P offer by receiver. Wrong signature")
    fun cancellationP2POfferByReceiverWrongSignature() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

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
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(walletID,companyName, CC, amount.toString(), VT, amount.toString(), ExpireType.TEMPORARY, user1)
        }
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            findIncomingP2P(amount)
            e {
                click(reject)
            }
        }
        with(AtmPage(driver)) {
            signAndSubmitMessage(user2, user2.otfWallet.secretKey)
//            assert {
//                elementContainingTextPresented("Invalid signature")
//            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            e {
                click(viewIncomingP2P)
            }
            val myOffer = incomingOffers.find {
                it.amountToReceive == amount
            } ?: error("Can't find offer with unit price '$amount'")
            assertThat(
                "Offer with amount $amount should be presented",
                myOffer,
                notNullValue()
            )
        }
    }


    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    )
    @TmsLink("ATMCH-918")
    @Test
    @DisplayName("Cancellation P2P offer by reciever. Wrong 2FA Code")
    fun cancellationP2POfferByRecieverWrong2FACode() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND

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

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(walletID,companyName, CC, amount.toString(), VT, amount.toString(), ExpireType.TEMPORARY, user1)
        }
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            findIncomingP2P(amount)
            e {
                click(reject)}
            wait {
                until("Button Yes from Reject dialog is displayed", 15) {
                    check {
                        isElementPresented(yesButton)
                    }
                }
            }
            e{
                click(yesButton)
            }
        }
        with(AtmPage(driver)) {
            e {
                click(privateKey)
                sendKeys(privateKey, user2.otfWallet.secretKey)
                val code = if (OAuth.generateCode(user2.oAuthSecret) == "123456") "123457" else "123456"
                click(confirmPrivateKeyButton)
                sendKeys(atmOtpConfirmationInput, code)
                click(atmOtpConfirmationConfirmButton)
            }
            assert {
                elementContainingTextPresented("Wrong code")
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            e {
                click(viewIncomingP2P)
            }
            val myOffer = incomingOffers.find {
                it.amountToReceive == amount
            } ?: error("Can't find offer with unit price '$amount'")
            assertThat(
                "Offer with amount $amount should be exists",
                myOffer,
                notNullValue()
            )
        }

    }

    //    @Disabled("ATMCH-3802")
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_OTF), ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-920")
    @Test
    @DisplayName("Cancellation P2P offer by reciever. Success cancellation")
    fun cancelP2OfferByReceiverSuccess() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
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

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(walletID,
                companyName,
                CC,
                amount.toString(),
                VT,
                amount.toString(),
                ExpireType.GOOD_TILL_CANCELLED,
                user1
            )
        }
        openPage<AtmWalletPage>(driver).logout()
        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            findIncomingP2P(amount)
            rejectP2P(user2)
            e {
                click(incomingP2PS)
            }
            driver.navigate().refresh()

            val cancelledOffer = incomingOffers.find {
                it.amountToReceive == amount
            }
            assertThat(
                "Offer with amount $amount should have been be cancelled",
                cancelledOffer,
                nullValue()
            )
        }
    }


}


