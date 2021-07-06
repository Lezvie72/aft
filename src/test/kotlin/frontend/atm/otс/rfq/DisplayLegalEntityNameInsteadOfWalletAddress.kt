package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.apache.commons.lang.RandomStringUtils
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.atm.AtmRFQPage
import pages.atm.AtmWalletPage
import ru.yandex.qatools.htmlelements.element.Button
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.to
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.DEBUG), Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Display legal entity name instead of wallet address")
class DisplayLegalEntityNameInsteadOfWalletAddress : BaseTest() {

    private val amount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
    private val dealAmount = BigDecimal("3.0000${RandomStringUtils.randomNumeric(4)}")

    private val baseAsset = CC
    private val quoteAsset = VT
    private val user2 = Users.ATM_USER_2FA_OTF_OPERATION_FIFTH
    private val user1 = Users.ATM_USER_2FA_OTF_OPERATION_SIXTH

    private val counterpartyName = "ATMUSERCOMPLIANCEMANAGER"
    private val counterpartyNameInvalid = "ATMUSERCOMPLIANCEMANAGER12314"

    private val counterpartyValueUser2 = "ATMUSER2FAOTFOPERATIONFIFTH"
    private val counterpartyValueUser1 = "ATMUSER2FAOTFOPERATIONSIXTH"


    @TmsLink("ATMCH-2973")
    @Test
    @Story("Positive test")
    @DisplayName("RFQ. Checking field Counterparty while creating a request")
    fun rfqCheckingFieldCounterpartyWhileCreatingRequest() {

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            e {
                click(createRequest)
                click(onlySelectedCounterparties)
            }
            assert {
                elementContainingTextPresented(" Will not receive request ")
                elementContainingTextPresented(" Click item to add ")
                elementContainingTextPresented(" Will receive request ")
                elementContainingTextPresented(" Click item to remove ")
            }
            e {
                sendKeys(counterpartyInput, counterpartyNameInvalid)


                val counterpartyValue = check {
                    isElementPresented(By.xpath(".//atm-rfq-selected-companies//div[contains(text(),'${counterpartyNameInvalid}')]"))
                }
                Assert.assertFalse("Counterparty '$counterpartyNameInvalid' is found", counterpartyValue)

                deleteData(counterpartyInput)
                sendKeys(counterpartyInput, counterpartyName)

                val counterpartyItemAdd = wait {
                    untilPresented<WebElement>(By.xpath(".//atm-rfq-selected-companies//div[contains(text(),'${counterpartyName}')]"))
                }.to<Button>("Counterparty '$counterpartyName'")

                assert { elementPresented(counterpartyItemAdd) }

                click(counterpartyItemAdd)

                val counterpartyItemRemove = wait {
                    untilPresented<WebElement>(By.xpath(".//atm-rfq-selected-companies//div//h3[contains(text(),'Will receive request')]//ancestor::div//div[contains(text(),'${counterpartyName}')]"))
                }.to<Button>("Counterparty '$counterpartyName'")

                assert {
                    elementPresented(counterpartyItemRemove)
                }

                click(counterpartyItemRemove)

                assert {
                    elementNotPresented(counterpartyItemRemove)
                    elementDisabled(createRequestFromForm1)
                }

                sendKeys(counterpartyInput, counterpartyName)

                val counterpartyItemAdd1 = wait {
                    untilPresented<WebElement>(By.xpath(".//atm-rfq-selected-companies//div[contains(text(),'${counterpartyName}')]"))
                }.to<Button>("Counterparty '$counterpartyName'")

                assert { elementPresented(counterpartyItemAdd1) }

                click(counterpartyItemAdd1)

                select(assetToSend, baseAsset.tokenSymbol)
                select(assetToReceive, quoteAsset.tokenSymbol)
                sendKeys(amountToSend, amount.toString())
                deleteData(expiresIn)
                sendKeys(expiresIn, "1")
                click(createRequestFromForm)

                assert { elementPresented(privateKey) }
            }

        }

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_FIFTH),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SIXTH)
    )
    @TmsLink("ATMCH-3113")
    @Test
    @Story("Positive test")
    @DisplayName("RFQ. Checking field Counterparty in incoming requests, trade history")
    fun rfqCheckingFieldCounterpartyInIncomingRequestsTradeHistory() {

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(AtmRFQPage.OperationType.BUY, baseAsset, quoteAsset, amount, "1", user1)
        }

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            e {
                click(viewRequest)
            }

            val myOffer = incomingOffers.find {
                it.baseAmount == amount
            } ?: error("Can't find offer with base amount '$amount'")

            checkCounterparty(counterpartyValueUser1)

            myOffer.open()

            checkCounterparty(counterpartyValueUser1)

            e {
                sendKeys(totalOfferAmount, dealAmount.toString())
                sendKeys(expiryDealTime, "1")
                Thread.sleep(2000)
                click(makeOffer)
            }
            signAndSubmitMessage(user2, user2.otfWallet.secretKey)

        }

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            acceptOffer(amount, dealAmount, user1)

            e {
                click(viewRequest)
                click(tradeHistory)
                click(showBuyOnly)

                val myOffer = historyOffers.find {
                    it.paidAmount == dealAmount
                } ?: error("Can't find offer with paid amount '$dealAmount'")

                checkCounterparty(counterpartyValueUser2)

                myOffer.open()

                checkCounterparty(counterpartyValueUser2)

            }

        }

    }
}
