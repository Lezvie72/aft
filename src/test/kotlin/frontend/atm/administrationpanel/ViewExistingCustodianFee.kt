package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmAdminCustodianFeePage
import pages.atm.AtmAdminCustodianFeePage.Headers.FEE_IN
import pages.atm.AtmAdminCustodianFeePage.Headers.ISSUER
import pages.atm.AtmAdminCustodianFeePage.Headers.PRICE
import pages.atm.AtmAdminCustodianFeePage.Headers.TOKEN_DESCRIPTION
import pages.atm.AtmAdminCustodianFeePage.Headers.TOKEN_NAME
import pages.atm.AtmAdminCustodianFeePage.Headers.TOKEN_STATUS
import pages.atm.AtmAdminCustodianFeePage.Headers.UNDERLYING_ASSET
import pages.atm.AtmAdminTokensPage
import ru.yandex.qatools.htmlelements.element.Button
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.to

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Administrator panel. View existing custodian fee")
class ViewExistingCustodianFee : BaseTest() {

    private val coin = CoinType.ETC

    @TmsLink("ATMCH-5649")
    @Test
    @DisplayName("Сustodian fee. Interface checking.")
    fun custodianFeeInterfaceChecking() {
//        val coin = CoinType.values().random()
        with(openPage<AtmAdminCustodianFeePage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementContainingTextPresented("Custodian Fee")
                elementPresented(setCustodianFee)
                elementPresented(search)
                elementPresented(custodianFeeTable)
//                elementPresented(custodianFeeTable.nextPageButton)
//                elementPresented(custodianFeeTable.prevPageButton)
                elementContainingTextPresented("Token name")
                elementContainingTextPresented("Token status")
                elementContainingTextPresented("Token description")
                elementContainingTextPresented("Issuer")
                elementContainingTextPresented("Underlying asset")
                elementContainingTextPresented("Price")
                elementContainingTextPresented("Fee in % of AUM")
            }
        }

        val tokenMainInfo = openPage<AtmAdminTokensPage>(driver).getTokenMainInformation(coin, coin.tokenName)
        openPage<AtmAdminCustodianFeePage>(driver).checkValuesOfColumns(
            tokenMainInfo.tokenName,
            tokenMainInfo.tokenDescription,
            tokenMainInfo.status
        )
    }

    @TmsLink("ATMCH-5652")
    @Test
    @DisplayName("Custodian fee. Search validation")
    fun custodianFeeSearchValidation() {
//        val coin = CoinType.values().random()
        with(openPage<AtmAdminCustodianFeePage>(driver) { submit(Users.ATM_ADMIN) }) {
            val tokenAllInfo = getAllTokenValues(coin.tokenName)
            e {
                sendKeys(search, coin.tokenName)
                pressEnter(search)
            }

            val rowWithTokenName = custodianFeeTable.find {
                it[TOKEN_NAME]?.text == coin.tokenName
            }?.get(TOKEN_NAME)
                ?.to<Button>("Ticker symbol ${coin.tokenName}")
                ?: error("Row with Token name ${coin.tokenName} not found in table")
            assert {
                elementPresented(rowWithTokenName)
            }

            e {
                search.delete()
                sendKeys(search, tokenAllInfo.tokenStatus)
                pressEnter(search)
            }

            val rowWithStatus = custodianFeeTable.find {
                it[TOKEN_STATUS]?.text == tokenAllInfo.tokenStatus
            }?.get(TOKEN_STATUS)
                ?.to<Button>("Ticker symbol ${coin.tokenName}")
                ?: error("Row with status ${tokenAllInfo.tokenStatus} not found in table")
            assert {
                elementPresented(rowWithStatus)
            }

            e {
                search.delete()
                sendKeys(search, tokenAllInfo.tokenDescription)
                pressEnter(search)
            }

            val rowWithDescription = custodianFeeTable.find {
                it[TOKEN_DESCRIPTION]?.text == tokenAllInfo.tokenDescription
            }?.get(TOKEN_DESCRIPTION)
                ?.to<Button>("Ticker symbol ${tokenAllInfo.tokenDescription}")
                ?: error("Row with status ${tokenAllInfo.tokenDescription} not found in table")
            assert {
                elementPresented(rowWithDescription)
            }

            e {
                search.delete()
                sendKeys(search, tokenAllInfo.issuer)
                pressEnter(search)
            }

            val rowWithIssuer = custodianFeeTable.find {
                it[ISSUER]?.text == tokenAllInfo.issuer
            }?.get(ISSUER)?.to<Button>("Ticker symbol ${tokenAllInfo.issuer}")
                ?: error("Row with issuer ${tokenAllInfo.issuer} not found in table")
            assert {
                elementPresented(rowWithIssuer)
            }

            e {
                search.delete()
                sendKeys(search, tokenAllInfo.underlyingAsset)
                pressEnter(search)
            }

            val rowWithAsset = custodianFeeTable.find {
                it[UNDERLYING_ASSET]?.text == tokenAllInfo.underlyingAsset
            }?.get(UNDERLYING_ASSET)
                ?.to<Button>("Ticker symbol ${tokenAllInfo.underlyingAsset}")
                ?: error("Row with asset ${tokenAllInfo.underlyingAsset} not found in table")
            assert {
                elementPresented(rowWithAsset)
            }

            e {
                search.delete()
                sendKeys(search, tokenAllInfo.price)
                pressEnter(search)
            }

            val rowWithPrice = custodianFeeTable.find {
                it[PRICE]?.text == tokenAllInfo.price
            }?.get(PRICE)
                ?.to<Button>("Ticker symbol ${tokenAllInfo.price}")
                ?: error("Row with price ${tokenAllInfo.price} not found in table")
            assert {
                elementPresented(rowWithPrice)
            }

            e {
                search.delete()
                sendKeys(search, tokenAllInfo.fee)
                pressEnter(search)
            }

            val rowWithFee = custodianFeeTable.find {
                it[FEE_IN]?.text == tokenAllInfo.fee
            }?.get(FEE_IN)
                ?.to<Button>("Ticker symbol ${tokenAllInfo.fee}")
                ?: error("Row with fee ${tokenAllInfo.fee} not found in table")
            assert {
                elementPresented(rowWithFee)
            }

//        TODO: при вводе рандомного текста в поле search выдает "Internal Server Error",
//         невозможно проверить последний шаг
        }
    }

    @TmsLink("ATMCH-5655")
    @Test
    @DisplayName("Custodian fee. Roles validation")
    fun custodianFeeRolesValidation() {
        with(openPage<AtmAdminCustodianFeePage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementContainingTextPresented("Custodian Fee")
            }
        }
        openPage<AtmAdminCustodianFeePage>(driver).logout()
        with(openPage<AtmAdminCustodianFeePage>(driver) { submit(Users.ATM_USER_EMPLOYEE_ADMIN_ROLE) }) {
            assert {
                elementContainingTextNotPresented("Custodian Fee")
            }
        }
    }
}