package frontend.e2e

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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.atm.AtmRFQPage
import pages.atm.AtmWalletPage
import ru.yandex.qatools.htmlelements.element.Button
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to
import java.math.BigDecimal

@Tag("SmokeE2E")
@Epic("Frontend")
@Feature("E2E")
@Story("RFQ")
class SmokeRFQE2E : BaseTest() {

    private val baseAsset = CC
    private val quoteAsset = VT

    private val textUserMaker = "Message for User Taker"
    private val textUserTaker = "Message for User Maker"

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_FORTH)
    @TmsLink("ATMCH-5990")
    @Test
    @DisplayName("Cancel RFQ Offer")
    fun cancelRFQOffer() {

        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val user = Users.ATM_USER_2FA_OTF_OPERATION_FORTH

        prerequisite {
            prerequisitesRfq(
                baseAsset, quoteAsset
            )
        }

        with(openPage<AtmRFQPage>(driver) { submit(user) }) {
            step("${user.email} create RFQ offer with $amount") {
                createRFQ(AtmRFQPage.OperationType.BUY, baseAsset, quoteAsset, amount, "1", user)
            }
            step("${user.email} cancel RFQ offer with $amount") {
                e {
                    click(myRequest)
                }
                val myOffer = outgoingOffers.find {
                    it.baseAmount == amount
                } ?: error("Can't find offer with base amount '$amount'")

                myOffer.cancelRfqOffer(user)

                driver.navigate().refresh()

                val cancelledOffer = outgoingOffers.find {
                    it.baseAmount == amount
                }
                assertThat(
                    "Offer with amount $amount should have been be cancelled",
                    cancelledOffer,
                    Matchers.nullValue()
                )
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_EIGHTH),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SEVENTH)
    )
    @TmsLink("ATMCH-1037")
    @Test
    @Story("Positive test")
    @DisplayName("RFQ. Acceptance of the offer by the participant and transaction")
    fun rfqAcceptanceOfTheOfferByTheParticipantAndTransaction() {

        val amount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val dealAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val newDealAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_EIGHTH
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION_SEVENTH

        prerequisite {
            prerequisitesRfq(
                baseAsset, quoteAsset
            )
        }
        step("${user1.email} create RFQ offer with $amount") {
            with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
                createRFQ(AtmRFQPage.OperationType.BUY, baseAsset, quoteAsset, amount, "1", user1)
            }
        }

        openPage<AtmWalletPage>(driver).logout()

        step("${user2.email} create deal $dealAmount for RFQ offer with $amount") {
            with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
                createDeal(amount, dealAmount, "1", user2)
            }
        }
        step("${user2.email} change deal $dealAmount to $newDealAmount for RFQ offer with $amount and send message") {
            with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
                e {
                    click(viewRequest)
                }

                val myOffer = incomingOffersWithDeal.find {
                    it.baseAmount == amount
                } ?: error("Can't find offer with base amount '$amount'")

                myOffer.goToChat()

                e {
                    click(changeOffer)
                    deleteData(totalOfferAmount).also {
                        sendKeys(totalOfferAmount, "0")
                        sendKeys(totalOfferAmount, newDealAmount.toString())
                        Thread.sleep(1000)
                    }
                    click(goodTillCancelled)

                    sendKeys(chatInput, textUserTaker)
                    click(chatSendButton)
                    Thread.sleep(2000)
                    click(saveChanges)
                }
                signAndSubmitMessage(user2, user2.otfWallet.secretKey)
            }
        }
        openPage<AtmWalletPage>(driver).logout()

        step("${user1.email} open RFQ offer with  deal amount  $newDealAmount check and send another message") {
            with(openPage<AtmRFQPage>(driver) { submit(user1) }) {

                e {
                    click(myRequest)
                }
                setDisplayRequestsWithOffers(true)

                val myOffer = outgoingOffers.find {
                    it.baseAmount == amount
                } ?: error("Can't find offer with base amount '$amount'")

                myOffer.viewOffer()

                val openChat = wait {
                    untilPresented<WebElement>(By.xpath("(//atm-rfq-item-outgoing-offers//atm-rfq-item-outgoing-offer//div//span[contains(text(),'OPEN CHAT')])[2]"))
                }.to<Button>("openChat")

                e { click(openChat) }
                checkTheMessageFromChat(textUserTaker)

                e {
                    sendKeys(chatInput, textUserMaker)
                    click(chatSendButton)
                }

            }
        }
        openPage<AtmWalletPage>(driver).logout()

        step("${user2.email} open RFQ offer and check message") {

            with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
                e {
                    click(viewRequest)
                }
                val myOffer = incomingOffersWithDeal.find {
                    it.baseAmount == amount
                } ?: error("Can't find offer with base amount '$amount'")
                myOffer.goToChat()
                checkTheMessageFromChat(textUserMaker)
            }
        }

        openPage<AtmWalletPage>(driver).logout()

        step("${user1.email} accept RFQ offer") {
            with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
                e {
                    click(myRequest)
                }
                setDisplayRequestsWithOffers(true)

                val myOffer = outgoingOffers.find {
                    it.baseAmount == amount
                } ?: error("Can't find offer with base amount '$amount'")

                myOffer.viewOffer()

                val openChat = wait {
                    untilPresented<WebElement>(By.xpath("(//atm-rfq-item-outgoing-offers//atm-rfq-item-outgoing-offer//div//span[contains(text(),'OPEN CHAT')])[2]"))
                }.to<Button>("openChat")

                e { click(openChat) }

                val fee = wait(15L) {
                    until("Couldn't load fee") {
                        offerFee.text.isNotEmpty()
                    }
                    offerFee.amount
                }

                e {
                    click(acceptOffer)
                }
                signAndSubmitMessage(user1, user1.otfWallet.secretKey)

                findOfferInHistory(newDealAmount + fee)
            }

        }

    }
}