package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.CoinType.IT
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminCustodianFeePage
import ru.yandex.qatools.htmlelements.element.Button
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to
import java.math.BigDecimal

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Administrator panel.Change In Custodian Fee For Token")
class ChangeInCustodianFeeForToken : BaseTest() {

    private val token = CoinType.ETC

    @ResourceLock(Constants.ROLE_ADMIN)
    @TmsLink("ATMCH-5661")
    @Test
    @DisplayName("Administrator panel. Change in custodian fee for token")
    fun administratorPanelChangeInCustodianFeeForToken() {
        val amount = BigDecimal("1${RandomStringUtils.randomNumeric(2)}")
        step("User set custodian fee and value for token") {
            with(openPage<AtmAdminCustodianFeePage>(driver) { submit(Users.ATM_ADMIN) }) {
                setFee(token, amount)
                checkFeeValueForToken(token, amount)
                assert { elementNotPresented(custodianFeeInput) }
            }
        }
        step("User set custodian fee to zero for token") {
            with(openPage<AtmAdminCustodianFeePage>(driver) { submit(Users.ATM_ADMIN) }) {
                setFee(token, BigDecimal.ZERO)
            }
        }
    }

    @ResourceLock(Constants.ROLE_ADMIN)
    @TmsLink("ATMCH-5662")
    @Test
    @DisplayName("Administrator panel.Custodian fee. Cancel Change In")
    fun administratorPanelCustodianFeeCancelChangeIn() {
        val amount = BigDecimal("1${RandomStringUtils.randomNumeric(2)}")
        step("User get custodian fee value,cancel change value and get value after cancel") {
            with(openPage<AtmAdminCustodianFeePage>(driver) { submit(Users.ATM_ADMIN) }) {
                val fee = getFeeValueForToken(token)
                e {
                    sendKeys(search, token.tokenName)
                    pressEnter(search)
                }
                val row = custodianFeeTable.find {
                    it[AtmAdminCustodianFeePage.TOKEN_NAME]?.text == token.tokenName
                }?.get(AtmAdminCustodianFeePage.TOKEN_NAME)?.to<Button>("Ticker symbol ${token.tokenName}")
                    ?: error("Row with Ticker symbol ${token.tokenName} not found in table")
                e {
                    click(row)
                    click(setCustodianFee)
                    sendKeys(custodianFeeInput, amount.toString())
                    click(cancelButton)
                }
                val feeAfterCancel = getFeeValueForToken(token)
                assert { elementNotPresented(custodianFeeInput) }
                Assertions.assertTrue(fee == feeAfterCancel, "Expected $fee equal to $feeAfterCancel")
            }
        }
    }

}