package frontend.atm.etc

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.ETC
import models.CoinType.VT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import pages.atm.AtmIssuancesPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ETC.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Feature("ETC")
@Story("ETC Automatic Token Linkage")
class ETCAutomaticTokenLinkage : BaseTest() {

    @TmsLink("ATMCH-4037")
    @Test
    @DisplayName("ETC. Issuer has 2 key")
    fun etcIssuerHas2Key() {
        val user = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_THIRD

        step("User go to Issuance and check data") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user) }) {

                chooseToken(ETC)

                assert {
                    elementContainingTextPresented("You are allowed to partially manage the smart contract")
                }
            }
        }

    }

    @TmsLink("ATMCH-4036")
    @Test
    @DisplayName("ETC. Issuer has 1 key")
    fun etcIssuerHas1Key() {
        val user = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS

        step("User go to Issuance and check data") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user) }) {

                chooseToken(VT)

                assert {
                    elementContainingTextPresented("TOKEN TYPE")
                    elementContainingTextPresented("VALIDATION TOKEN")
                    elementContainingTextPresented("UNDERLYING ASSET")
                    elementContainingTextPresented("ISSUER")
                }
            }
        }

    }

    @TmsLink("ATMCH-4035")
    @Test
    @DisplayName("User go to Issuance and check data")
    fun etcIssuerDoesntHaveKeys() {
        val user = Users.ATM_USER_EMPLOYEE_ADMIN_ROLE

        step("User go to Trading, check available operation") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user) }) {
//                assert {
//                    elementContainingTextPresented("You do not have suitable issuer wallet keys. Please  an issuing wallet. It is also possible that your smart contract has not yet been deployed in our network, then you need to wait, or contact support")
//                }
                e {
                    click(linkRegisterWallet)
                }
                assert {
                    urlEndsWith("/wallets/new")
                }
            }
        }

    }
}

