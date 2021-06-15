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
import pages.atm.AtmP2PPage
import pages.atm.AtmP2PPage.ExpireType
import pages.atm.AtmPage
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.BLOCKTRADE.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Cancellation of a Blocktrade offer to conclude a deal by the recipient of the offer")
class CancellationOfBlocktradeOfferToConcludeDealByTheRecipientOfTheOffer : BaseTest() {
    private val user1 = Users.ATM_USER_2FA_OTF_OPERATION
    private val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
    private val user3 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
    private val user4 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

    private val baseAsset = CC
    private val quoteAsset = VT

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET),
        ResourceLock(Constants.ROLE_USER_MANUAL_SIG_OTF_WALLET_FOR_OTF)
    )
    @TmsLink("ATMCH-919")
    @Test
    @DisplayName("Cancellation P2P offer by receiver. Wrong signature")
    fun cancellationP2POfferByReceiverWrongSignature() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val companyName = openPage<AtmProfilePage>(driver) { submit(user4) }.getCompanyName()

        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user4) }.takeWalletID()
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user3) }) {
            createP2P(walletID, companyName, CC, amount.toString(), VT, amount.toString(), ExpireType.TEMPORARY, user3)
        }
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user4) }) {
            findIncomingP2P(amount)
            e {
                click(reject)
            }
        }
        with(AtmPage(driver)) {
            signAndSubmitMessage(user4, user4.otfWallet.secretKey)
//            assert {
//                elementContainingTextPresented("Invalid signature")
//            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(user4) }) {
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
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

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

    @Tag(TagNames.Flow.DEBUG)
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET),
        ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET)
    )
    @TmsLink("ATMCH-920")
    @Test
    @DisplayName("Cancellation P2P offer by reciever. Success cancellation")
    fun cancelP2OfferByReceiverSuccess() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        val companyName = openPage<AtmProfilePage>(driver) { submit(user4) }.getCompanyName()
        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user4) }.takeWalletID()

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user3) }) {
            createP2P(
                walletID,
                companyName,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                ExpireType.GOOD_TILL_CANCELLED,
                user3
            )
        }
        openPage<AtmWalletPage>(driver).logout()
        with(openPage<AtmP2PPage>(driver) { submit(user4) }) {
            findIncomingP2P(amount)
            rejectP2P(user4)
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


