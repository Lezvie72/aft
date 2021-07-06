package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmProfilePage
import pages.atm.AtmRFQPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Place offer for the request with Industrial Token. 3202")
class PlaceOfferForTheRequestWithIndustrialToken : BaseTest() {
    private val userOneIndustrial = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwoIndustrial = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val userNonIndustrial = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val industrialToken = CoinType.IT
    private val maturityDateInnerDate = industrialToken.date
    private val maturityDateShortWrite = "09.22.2020"


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3964")
    @Test
    @DisplayName("RFQ. IT selected as base and quoted asset")
    fun ItSelectedAsBaseAndQuotedAsset() {
        val amountToBuy = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val amountToSell = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val checkedValue = "MATURITY DATE"

        step("Preconditions - send offer from ${userOneIndustrial.email} to ${userTwoIndustrial.email} to buy and sell") {
            val companyName =
                openPage<AtmProfilePage>(driver) { submit(userTwoIndustrial) }.companyName.text

            openPage<AtmWalletPage>(driver).logout()

            with(openPage<AtmRFQPage>(driver) { submit(userOneIndustrial) }) {
                createRFQ(
                    AtmRFQPage.OperationType.BUY,
                    baseAsset,
                    industrialToken,
                    amountToBuy,
                    "1",
                    userOneIndustrial,
                    userTwoIndustrial,
                    companyName,
                    maturityDateInnerDate
                )
            }
            with(openPage<AtmRFQPage>(driver)) {
                createRFQ(
                    AtmRFQPage.OperationType.SELL,
                    industrialToken,
                    baseAsset,
                    amountToSell,
                    "1",
                    userOneIndustrial,
                    userTwoIndustrial,
                    companyName,
                    maturityDateInnerDate
                )
            }

            openPage<AtmWalletPage>(driver).logout()
        }

        step("Step 1-6. Check value $checkedValue on the page for Buy request") {
            with(openPage<AtmRFQPage>(driver) { submit(userTwoIndustrial) }) {
                e {
                    findIncomingRFQ(amountToBuy)
                    assert {
                        elementContainingTextPresented(checkedValue)
                        elementContainingTextPresented(maturityDateShortWrite)
                    }
                }
            }
        }

        step("Step 7-12. Check value $checkedValue on the page for Sell request") {
            with(openPage<AtmRFQPage>(driver)) {
                e {
                    findIncomingRFQ(amountToSell)
                    assert {
                        elementContainingTextPresented(checkedValue)
                        elementContainingTextPresented(maturityDateShortWrite)
                    }
                }
            }
        }
    }


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3965")
    @Test
    @DisplayName("RFQ. Base asset is not IT and quoted asset is not IT")
    fun baseAssetIsNotItAndQuotedAssetIsNotIt() {
        val amountToBuy = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val amountToSell = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val checkedValue = "MATURITY DATE"

        step("Preconditions - send offer from ${userOneIndustrial.email} to ${userTwoIndustrial.email} to buy and sell") {
            val companyName =
                openPage<AtmProfilePage>(driver) { submit(userTwoIndustrial) }.companyName.text

            openPage<AtmWalletPage>(driver).logout()

            with(openPage<AtmRFQPage>(driver) { submit(userOneIndustrial) }) {
                createRFQ(
                    AtmRFQPage.OperationType.BUY,
                    baseAsset,
                    quoteAsset,
                    amountToBuy,
                    "1",
                    userOneIndustrial,
                    userTwoIndustrial,
                    companyName
                )
            }
            with(openPage<AtmRFQPage>(driver)) {
                createRFQ(
                    AtmRFQPage.OperationType.SELL,
                    baseAsset,
                    quoteAsset,
                    amountToSell,
                    "1",
                    userOneIndustrial,
                    userTwoIndustrial,
                    companyName
                )
            }

            openPage<AtmWalletPage>(driver).logout()
        }

        step("Step 1-6. Check value $checkedValue on the page for Buy request") {
            with(openPage<AtmRFQPage>(driver) { submit(userTwoIndustrial) }) {
                e {
                    findIncomingRFQ(amountToBuy)
                    assert {
                        elementContainingTextNotPresented(checkedValue)
                    }
                }
            }
        }

        step("Step 7-12. Check value $checkedValue on the page for Sell request") {
            with(openPage<AtmRFQPage>(driver)) {
                e {
                    findIncomingRFQ(amountToSell)
                    assert {
                        elementContainingTextNotPresented(checkedValue)
                    }
                }
            }
        }
    }
}