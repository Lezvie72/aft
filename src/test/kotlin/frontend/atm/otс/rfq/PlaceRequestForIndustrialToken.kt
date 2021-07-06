package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmRFQPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.step

@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Place request for Industrial token. 3196")
class PlaceRequestForIndustrialToken : BaseTest() {
    private val userOneIndustrial = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val industrialToken = CoinType.IT


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3669")
    @Test
    @DisplayName("RFQ - IT. Request placing - IT as base and quote asset")
    fun requestPlacingItAsBaseAndQuoteAsset() {
        val checkedValue = "MATURITY DATE"

        with(utils.helpers.openPage<AtmRFQPage>(driver) { submit(userOneIndustrial) }) {
            e {
                step("Step 1. Go to Trading > RFQ and click on the Create a request button") {
                    click(createRequest)
                    assert { elementContainingTextPresented("Create new request") }
                }

                step("Step 2. Select I want to sell asset option") {
                    click(iWantToSellAsset)
                    assertThat("Element I want to sell asset should be selected", iWantToSellAsset.isSelected)
                }

                step("Step 3-4. Check $checkedValue and balance field") {
                    select(assetToSend, industrialToken.tokenSymbol)
                    select(assetToReceive, baseAsset.tokenSymbol)
                    assert {
                        elementContainingTextPresented(checkedValue)
                        elementPresented(availableBalance)
                    }
                }

                step("Step 5. Select other value from Select base asset dropdown list") {
                    select(assetToSend, quoteAsset.tokenSymbol)
                    assert {
                        elementContainingTextNotPresented(checkedValue)
                    }
                }

                step("Step 6. Select IT as Select quote asset value") {
                    select(assetToReceive, industrialToken.tokenSymbol)
                    assert {
                        elementContainingTextPresented(checkedValue)
                    }
                }

                step("Step 7. Select other value from Select quote asset dropdown list") {
                    select(assetToReceive, baseAsset.tokenSymbol)
                    assert {
                        elementContainingTextNotPresented(checkedValue)
                    }
                }

                step("Step 8. Select I want to buy asset option") {
                    click(iWantToBuyAsset)
                    assertThat("Element I want to buy asset should be selected", iWantToBuyAsset.isSelected)
                }

                step("Step 9. Select IT as Select base asset value") {
                    select(assetToSend, industrialToken.tokenSymbol)
                    assert {
                        elementContainingTextPresented(checkedValue)
                    }
                }

                step("Step 10. Select other value from Select base asset dropdown list") {
                    select(assetToSend, quoteAsset.tokenSymbol)
                    assert {
                        elementContainingTextNotPresented(checkedValue)
                    }
                }

                step("Step 11. Select IT as Select quote asset value") {
                    select(assetToReceive, industrialToken.tokenSymbol)
                    assert {
                        elementContainingTextPresented(checkedValue)
                    }
                }

                step("Step 12. Select other value from Select quote asset dropdown list") {
                    select(assetToReceive, baseAsset.tokenSymbol)
                    assert {
                        elementContainingTextNotPresented(checkedValue)
                    }
                }
            }
        }
    }
}