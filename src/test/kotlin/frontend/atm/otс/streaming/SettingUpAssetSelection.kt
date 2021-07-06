package frontend.atm.otс.streaming

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import pages.atm.AtmAdminStreamingSettingsPage
import pages.atm.AtmStreamingPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.STREAMING.NUMBER))
@Epic("Frontend")
@Feature("Streaming")
@Story("Setting Up Asset Selection")
class SettingUpAssetSelection : BaseTest() {

    private val ccAsset = CoinType.CC
    private val vtAsset = CoinType.VT
    private val itAsset = CoinType.IT

    private val maturityDate202012Admin = "202012"
    private val maturityDate202011Admin = "202011"

    private val maturityDate202012Platform = "December 2020"
    private val maturityDate202011Platform = "November 2020"

    private val maker = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
    private val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
    private val user = Users.ATM_USER_2FA_OTF_OPERATION_SIXTH

    @TmsLink("ATMCH-6226")
    @Test
    @DisplayName("Streaming. Checking the availability of pair according to user's balance")
    fun streamingCheckingTheAvailabilityOfPairAccordingToUserBalance() {

        step("${user.email} check Streaming Sell offer") {
            with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
                e {
                    click(createOffer)
                    click(iWantToSellAsset)

                    val pairList = selectAssetPair.getHeadersAsString(page, true)

                    assertThat("List not equality", pairList, hasItem("${ccAsset.tokenSymbol}/${vtAsset.tokenSymbol}"))
                    click(selectAssetPair)
                }

                assert {
                    elementContainingTextPresented("PAIRS WITH A BASIC ASSET ON THE BALANCE ARE AVAILABLE")
                }
            }
        }
    }

    @TmsLink("ATMCH-6228")
    @Test
    @DisplayName("Streaming. IT - Checking the Maturity date list according to the user's balance ")
    fun itCheckingTheMaturityDateListAccordingToTheUserBalance() {

        step("Precondition") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addTradingPairIfNotPresented(
                    vtAsset.tokenSymbol,
                    itAsset.tokenSymbol + "_" + maturityDate202012Admin,
                    maturityDate202012Admin,
                    "1",
                    "1",
                    "1",
                    "FIXED",
                    "FIXED",
                    true
                )
                openPage<AtmAdminStreamingSettingsPage>(driver)
                addTradingPairIfNotPresented(
                    vtAsset.tokenSymbol,
                    itAsset.tokenSymbol + "_" + maturityDate202012Admin,
                    maturityDate202011Admin, "1",
                    "1", "1",
                    "FIXED", "FIXED",
                    true
                )
                openPage<AtmAdminStreamingSettingsPage>(driver)
                changeAvailableStatus(
                    vtAsset.tokenSymbol,
                    itAsset.tokenSymbol + "_" + maturityDate202012Admin,
                    true
                )
                openPage<AtmAdminStreamingSettingsPage>(driver)
                changeAvailableStatus(
                    vtAsset.tokenSymbol,
                    itAsset.tokenSymbol + "_" + maturityDate202011Admin,
                    true
                )
            }
        }
        step("${maker.email} check Streaming Sell offer") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                e {
                    click(createOffer)
                    click(iWantToSellAsset)
                    select(selectAssetPair, "${vtAsset.tokenSymbol}/${itAsset.tokenSymbol}")
                    click(offerMaturityDate)
                }

                assert {
                    elementContainingTextPresented(maturityDate202012Platform)
                    elementContainingTextPresented(maturityDate202011Platform)
                }
            }
        }

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            changeAvailableStatus(
                vtAsset.tokenSymbol,
                itAsset.tokenSymbol + "_" + maturityDate202012Admin,
                false
            )
        }

        step("${maker.email} check Streaming Sell offer") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                e {
                    click(createOffer)
                    click(iWantToSellAsset)
                    select(selectAssetPair, "${vtAsset.tokenSymbol}/${itAsset.tokenSymbol}")
                    click(offerMaturityDate)
                }

                assert {
                    elementContainingTextNotPresented(maturityDate202012Platform)
                    elementContainingTextPresented(maturityDate202011Platform)
                }
            }
        }

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            changeAvailableStatus(
                vtAsset.tokenSymbol,
                itAsset.tokenSymbol + "_" + maturityDate202011Admin,
                false
            )
        }

        step("${maker.email} check Streaming Sell offer") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                e {
                    click(createOffer)
                    click(iWantToSellAsset)
                    val pairList = selectAssetPair.getHeadersAsString(page, true)

                    assertThat("List not equality", pairList, not(hasItem("${vtAsset.tokenSymbol}/${itAsset.tokenSymbol}")))
                }
            }

            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                changeAvailableStatus(
                    vtAsset.tokenSymbol,
                    itAsset.tokenSymbol + "_" + maturityDate202012Admin,
                    true
                )
                openPage<AtmAdminStreamingSettingsPage>(driver)
                changeAvailableStatus(
                    vtAsset.tokenSymbol,
                    itAsset.tokenSymbol + "_" + maturityDate202011Admin,
                    true
                )
            }
        }
    }
}