package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmMarketplacePage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.MARKETPLACE.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("Update marketplace information in accordance with metainfo from smartcontract")
class UpdateMarketplaceInformation : BaseTest() {
    private val user = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02

    @ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02)
    @TmsLink("ATMCH-4649")
    @Test
    @DisplayName("Marketplace. Industrial token information")
    fun marketplaceIndustrialTokenInformation() {
        // wanted value
        val foundTokenName = "INDUSTRIAL TOKEN"

        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            assert {
                elementWithTextPresented(foundTokenName)
            }
            e {
                val row = tokensCards.findListTable {
                    val name = it.tokenName.text
                    println(name)
                    it.tokenName.text == foundTokenName
                } ?: error("Token card name $foundTokenName not found")
                assert {
                    elementPresented(row.tokenType)
                    elementPresented(row.underlyingAsset)
                    elementPresented(row.issuer)
                }
                click(row.tokenName)

                softAssert { elementContainingTextPresented("DELIVERY FORM") }
                softAssert { elementContainingTextPresented("PRICING MECHANISM") }
                softAssert { elementContainingTextPresented("1 TOKEN EQUALS") }
                softAssert { elementContainingTextPresented("TOTAL SUPPLY") }
                softAssert { elementContainingTextPresented("ON SALE") }
                softAssert { elementContainingTextPresented("IN CIRCULATION") }
                softAssert { elementContainingTextPresented("MATURITY DATE") }
                softAssert { elementContainingTextPresented("SUPPLY AMOUNT") }
                softAssert { elementContainingTextPresented("ON SALE") }

                click(detailsButton)
                softAssert { elementContainingTextPresented("UNIT OF MEASURE OF METAL") }
                softAssert { elementContainingTextPresented("PRICE") }
                softAssert { elementContainingTextPresented("PARAMETER NAME") }
                click(detailsButton)

                softAssert { elementContainingTextPresented("TRANSFER FEE") }
                softAssert { elementContainingTextPresented("CHARGED IN") }
                softAssert { elementContainingTextPresented("FEE RATE (%)") }
                softAssert { elementContainingTextPresented("FLOOR") }
                softAssert { elementContainingTextPresented("CAP") }

                softAssert { elementContainingTextPresented("ATTACHMENTS") }
                softAssert { elementPresented(datesRadio) }
            }
        }
    }

    @TmsLink("ATMCH-5245")
    @Test
    @DisplayName("IT. Checking the metadata of tokens in the Marketplace section")
    fun marketplaceCheckingMetadata() {
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            e{
                chooseToken(CoinType.GF28ILN060A)
            }
            assert {
                elementContainingTextPresented("Nickel Full Plate Cathodes, LME deliverable")
                elementContainingTextPresented("Floating")
                elementContainingTextPresented("Non-prepaid")
                elementContainingTextPresented("1")
            }
            openPage<AtmMarketplacePage>(driver)
            e{
                chooseToken(CoinType.GF28ILN060B)
            }
            assert {
                elementContainingTextPresented("Nickel Full Plate Cathodes, LME deliverable")
                elementContainingTextPresented("Floating")
                elementContainingTextPresented("Non-prepaid")
                elementContainingTextPresented("1")
            }
            openPage<AtmMarketplacePage>(driver)
            e{
                chooseToken(CoinType.GF29ILN037C)
            }
            assert {
                elementContainingTextPresented("Copper NORILSK Full Plate")
                elementContainingTextPresented("Floating")
                elementContainingTextPresented("Non-prepaid")
                elementContainingTextPresented("1")
            }
            openPage<AtmMarketplacePage>(driver)
            e{
                chooseToken(CoinType.GF29ILN037D)
            }
            assert {
                elementContainingTextPresented("Copper NORILSK Full Plate")
                elementContainingTextPresented("Floating")
                elementContainingTextPresented("Non-prepaid")
                elementContainingTextPresented("1")
            }
        }
    }
}