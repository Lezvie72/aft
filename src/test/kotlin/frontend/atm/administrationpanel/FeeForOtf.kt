package frontend.atm.administrationpanel

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
import pages.atm.AtmAdminBlocktradeSettingsPage
import pages.atm.AtmAdminRfqSettingsPage
import pages.atm.AtmAdminStreamingSettingsPage
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.Constants
import utils.TagNames
import utils.helpers.Users

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.OTCSETTINGS))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Fee for OTF")
class FeeForOtf : BaseTest() {

    private val checkedToken = CoinType.FIAT
    private val changeableToken = CoinType.CC
    private val objectTextOne = "Token is not found"
    private val objectNameOne = "Element with text: Token is not found"
    private val objectTextTwo = "Add trading pair"
    private val objectNameTwo = "Element with text: Add trading pair"
    private val objectTextThree = "Add token"
    private val objectNameThree = "Label: Add trading pair not found"

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-6099")
    @Test
    @DisplayName("Streaming settings. FIAT can't be used in the Secondary market")
    fun streamingSettingsFiatCantBeUsedInTheSecondaryMarket() {
        with(utils.helpers.openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                defaultAsset.delete()
                sendKeys(defaultAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }

                click(add)
                wait(5L) {
                    untilPresentedAnyWithText<TextBlock>(objectTextTwo, objectNameTwo)
                }

                click(baseInputSelect)
                sendKeys(baseInputSelect, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                click(quoteInputSelect)
                sendKeys(quoteInputSelect, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                click(feePlaceAsset)
                sendKeys(feePlaceAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                click(feeAcceptAsset)
                sendKeys(feeAcceptAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-6100")
    @Test
    @DisplayName("Blocktrade settings. FIAT can't be used in the Secondary market")
    fun blocktradeSettingsFiatCantBeUsedInTheSecondaryMarket() {
        with(utils.helpers.openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                defaultAsset.delete()
                sendKeys(defaultAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }

                click(add)
                wait(5L) {
                    untilPresentedAnyWithText<TextBlock>(objectTextThree, objectNameThree)
                }
                click(tokenInput)
                sendKeys(tokenInput, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                click(feePlacingAsset)
                sendKeys(feePlacingAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne,
                        objectNameOne
                    )
                }
                click(feeAcceptingAsset)
                sendKeys(feeAcceptingAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                click(cancelDialog)

                chooseToken(changeableToken.tokenSymbol)
                click(edit)
                tokenInput.delete()
                click(tokenInput)
                sendKeys(tokenInput, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                feePlacingAsset.delete()
                click(feePlacingAsset)
                sendKeys(feePlacingAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                feeAcceptingAsset.delete()
                click(feeAcceptingAsset)
                sendKeys(feeAcceptingAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-6101")
    @Test
    @DisplayName("RFQ settings. FIAT can't be used in the Secondary market")
    fun RfqSettingsFiatCantBeUsedInTheSecondaryMarket() {
        with(utils.helpers.openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                defaultAsset.delete()
                sendKeys(defaultAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }

                click(add)
                wait(5L) {
                    untilPresentedAnyWithText<TextBlock>(objectTextThree, objectNameThree)
                }
                click(tokenInput)
                sendKeys(tokenInput, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                click(feePlacingAsset)
                sendKeys(feePlacingAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                click(feeAcceptingAsset)
                sendKeys(feeAcceptingAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                click(cancelDialog)

                chooseToken(changeableToken.tokenSymbol)
                click(edit)

                feePlacingAsset.delete()
                click(feePlacingAsset)
                sendKeys(feePlacingAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
                feeAcceptingAsset.delete()
                click(feeAcceptingAsset)
                sendKeys(feeAcceptingAsset, checkedToken.tokenSymbol)
                wait(3L) {
                    untilPresentedAnyWithText<TextBlock>(
                        objectTextOne, objectNameOne
                    )
                }
            }
        }
    }
}