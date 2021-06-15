package frontend.atm.otс.streaming

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
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
import pages.atm.AtmAdminCompaniesPage
import pages.atm.AtmProfilePage
import pages.atm.AtmStreamingPage
import pages.htmlelements.blocks.atm.streaming.StreamingOfferItem
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.to
import utils.isChecked
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.STREAMING.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("Implementation of anonymous mode")
class ImplementationOfAnonymousMode : BaseTest() {

    // the same company (TestCompany01)
    private val user1 = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val user2 = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE03

    // the other company
    private val user3 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET
    private val shortNameCompany = "TestCompany01"

    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val amountValue = OtfAmounts.AMOUNT_10.amount

    private val SHOW = "Show"
    private val elementShow = By.xpath("//atm-span[contains(text(), ' Show ')]")
    private val counterparty =
        By.xpath("//span[contains(text(), 'Counterparty')]/ancestor::atm-property-value//atm-counterparty//atm-span")

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3992")
    @Test
    @DisplayName("Anonymous mode ON")
    fun anonymousModeON() {
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            setFlagShowOnUtf(shortNameCompany, false)
        }
        openPage<AtmAdminCompaniesPage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(user1) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$quoteAsset/$baseAsset",
                "$amountValue $quoteAsset",
                unitPrice.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                user1
            )
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(user2) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$quoteAsset/$baseAsset",
                "$amountValue $quoteAsset",
                unitPrice.toString(),
                AtmStreamingPage.ExpireType.TEMPORARY,
                user2
            )
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(overview)
                click(showBuyOnly)
                click(dateFrom)
                click(today)
                var item: StreamingOfferItem = findOfferBy(unitPrice, overviewOffersList)
                assert {
                    elementContainsText(item, SHOW)
                }
                click(item)
                wait(10L) {
                    untilPresented(acceptOffer)
                }
                val showBtInOffer = wait {
                    untilPresented<WebElement>(elementShow)
                }.to<Button>("Show text as button")
                assert {
                    elementContainsText(showBtInOffer, SHOW)
                }
                click(wait {
                    untilPresented<WebElement>(elementShow)
                }.to<Button>("Show text as button"))
                page.navigate().refresh()
                wait(10L) {
                    untilPresented<WebElement>(acceptOffer)
                }
                val newTextField = wait {
                    untilPresented<WebElement>(counterparty)
                }.to<TextBlock>("Field counterparty with text")
                assertThat("Text field not contains Show", !newTextField.text.contains("Show"))
            }
        }
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3993")
    @Test
    @DisplayName("Anonymous mode OFF")
    fun anonymousModeOFF() {
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            setFlagShowOnUtf(shortNameCompany, true)
        }
        openPage<AtmAdminCompaniesPage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(user1) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$quoteAsset/$baseAsset",
                "$amountValue $quoteAsset",
                unitPrice.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                user1
            )
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(user2) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$quoteAsset/$baseAsset",
                "$amountValue $quoteAsset",
                unitPrice.toString(),
                AtmStreamingPage.ExpireType.TEMPORARY,
                user2
            )
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(overview)
                click(showBuyOnly)
                click(dateFrom)
                click(today)
                var item: StreamingOfferItem = findOfferBy(unitPrice, overviewOffersList)
                assert {
                    elementNotContainsText(item, SHOW)
                }
                item.open()
                assert {
                    elementNotContainsText(item, SHOW)
                }
            }
        }
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3990")
    @Test
    @DisplayName("Anonymous mode: UI and usability")
    fun anonymousModeUiAndUsability() {
        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                sendKeys(search, shortNameCompany)
                pressEnter(search)
                companiesTable.find {
                    it[AtmAdminCompaniesPage.SHORT_NAME]?.text == shortNameCompany
                }?.let { row ->
                    row[AtmAdminCompaniesPage.SHORT_NAME]?.let {
                        click(it)
                    }
                } ?: error("Can't find row with short name '$shortNameCompany'")
                click(editButton)
                wait {
                    until("Edit company form shoukd be visible") {
                        untilPresentedAnyWithText<TextBlock>("Edit Company", "Edit company label")
                    }
                }
                wait {
                    untilPresented(showOnOtfCheckbox)
                }
                assertThat("Show on OTF checkbox should be enabled", showOnOtfCheckbox.isEnabled)
                setCheckbox(showOnOtfCheckbox, false)
                click(saveInEditForm)
                wait(5L) {
                    until("Save button should be disappeared") {
                        !saveInEditForm.exists()
                    }
                }
                companiesTable.find {
                    it[AtmAdminCompaniesPage.SHORT_NAME]?.text == shortNameCompany
                }?.let { row ->
                    row[AtmAdminCompaniesPage.SHORT_NAME]?.let {
                        click(it)
                    }
                } ?: error("Can't find row with short name '$shortNameCompany'")
                click(editButton)
                assertThat("Show on OTF checkbox should be False", !showOnOtfCheckbox.isChecked())
                setCheckbox(showOnOtfCheckbox, true)
                click(saveInEditForm)
                wait(5L) {
                    until("Save button should be disappeared") {
                        !saveInEditForm.exists()
                    }
                }
                companiesTable.find {
                    it[AtmAdminCompaniesPage.SHORT_NAME]?.text == shortNameCompany
                }?.let { row ->
                    row[AtmAdminCompaniesPage.SHORT_NAME]?.let {
                        click(it)
                    }
                } ?: error("Can't find row with short name '$shortNameCompany'")
                click(editButton)
                assertThat("Show on OTF checkbox should be True", showOnOtfCheckbox.isChecked())
            }
        }
    }
}