package frontend.atm.etc

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
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmMarketplacePage
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.Constants
import utils.TagNames
import utils.helpers.Users

@Tags(Tag(TagNames.Epic.ETC.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Feature("ETC")
@Story("ETC. Update marketplace page for ETC tokens")
class UpdateMarketplacePageForEtcTokens : BaseTest() {
    private val user = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
    private val coinType = CoinType.ETC

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_THIRD)
    @TmsLink("ATMCH-4632")
    @Test
    @DisplayName("ETC. Marketplace. ETC token page")
    fun marketplaceEtcTokenPage() {
        with(utils.helpers.openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            e {
                chooseToken(coinType)
                wait {
                    untilPresentedAnyWithText<TextBlock>("TOTAL SUPPLY", "TOTAL SUPPLY label of ETC token card")
                }

                assert {
                    elementContainingTextPresented(coinType.tokenName)
                    elementContainingTextPresented("TOKEN TYPE")
                    elementContainingTextPresented("UNDERLYING ASSET")
                    elementContainingTextPresented("ISSUER")
                    elementContainingTextPresented("TICKER")

                    elementContainingTextPresented("ISSUER DESCRIPTION")
                    elementContainingTextPresented("DELIVERY FORM")
                    elementContainingTextPresented("1 TOKEN EQUALS")
                    elementContainingTextPresented("TOTAL SUPPLY")
                    elementContainingTextPresented("TRANSFER FEE")
                    elementContainingTextPresented("CHARGED IN")
                    elementContainingTextPresented("FEE RATE")
                    elementContainingTextPresented("FLOOR")
                    elementContainingTextPresented("CAP")
                    elementContainingTextPresented("ATTACHMENTS")
                }
            }
        }
    }
}