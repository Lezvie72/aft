package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmAdminTokensPage
import pages.atm.AtmAdminTokensPage.Headers.TICKER_SYMBOL
import pages.atm.AtmMarketplacePage
import pages.core.actions.AssertActions
import ru.yandex.qatools.htmlelements.element.Button
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.to
import java.math.BigDecimal

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("Management of token description and token status")
class ManagementOfTokenDescriptionAndTokenStatus : BaseTest() {

    @TmsLink("ATMCH-1355")
    @Test
    @DisplayName("Register/Managing of Tokens. Mandatory fields checking")
    fun managingOfTokens() {
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val token = tokensTable.find {
                it[TICKER_SYMBOL]?.text == "CC"
            }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol CC")
                ?: error("Row with Ticker symbol CC not found in table")
            e {
                click(token)
                click(editToken)

                // TODO: https://sdexnt.atlassian.net/browse/ATMCH-1833
                // TODO: У селектов нет null опции

                tickerSymbol.delete()

                click(confirm)
            }
            val errorText = "Field is required"

            assertThat(
                "Expected ticker symbol error text: $errorText",
                tickerSymbol.errorText == errorText
            )
            e {
                click(cancel)
                sendKeys(search, "CC")
                click(usdEquivalentSettings)
                until("Wait for hidden preloader", 5L) {
                    usdEquivalentOverlay.preloader.getAttribute("style") == "visibility: hidden;"
                }
                usdEquivalentOverlay.usdEquivalent.delete()
            }

            assertThat(
                "Expected usd equivalent error text: $errorText",
                usdEquivalentOverlay.usdEquivalent.errorText == errorText
            )
        }
    }

    @TmsLink("ATMCH-1618")
    @Test
    @Story("Other")
    @DisplayName("Register/Managing of Tokens. Page content checking")
    fun managingOfTokensPageContentCheck() {
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementPresented(editToken)
                elementPresented(addToken)

                elementPresented(search)
                elementPresented(createDateTo)
                elementPresented(createDateFrom)
                elementPresented(updateDateTo)
                elementPresented(updateDateFrom)

                elementPresented(tokensTable)
            }

            assert {
                elementPresented(tokensTable.idSort)
                elementPresented(tokensTable.nameSort)
                elementPresented(tokensTable.symbolSort)
                elementPresented(tokensTable.statusSort)
                elementPresented(tokensTable.descriptionSort)
                elementPresented(tokensTable.issuerNameSort)
                elementPresented(tokensTable.underlyingAssetSort)
                elementPresented(tokensTable.usdEquivalentSort)
                elementPresented(tokensTable.createdSort)
                elementPresented(tokensTable.updatedSort)
            }

            e {
                click(addToken)
            }

            assert {
                elementPresented(tokenIssuer)
                elementPresented(tokenName)
                elementPresented(tickerSymbol)
                elementPresented(tokenType)
//                elementPresented(tokenStatus)
                elementPresented(tokenDescription)
                elementPresented(channelName)
                elementPresented(chaincodeName)
                elementPresented(underlyingAsset)
                //TODO: USD equivalent moved to grid form
                // TODO: Данный элемент отсутствует, но присутствует в кейсе
//                elementPresented(usdEquivalent)

                elementPresented(confirm)
                elementPresented(cancel)
            }

            assert {
                //TODO: Issuer field has auto filling
//                validateFieldSimple(tokenIssuer, AssertActions.STRING_VALIDATION)
                validateFieldSimple(tokenName, AssertActions.STRING_VALIDATION)
                validateFieldSimple(tickerSymbol, AssertActions.STRING_VALIDATION)

                validateFieldSimple(tokenDescription, AssertActions.STRING_VALIDATION)
                validateFieldSimple(channelName, AssertActions.STRING_VALIDATION)
                validateFieldSimple(chaincodeName, AssertActions.STRING_VALIDATION)

                //TODO: USD equivalent moved to grid form
//                validateFieldSimple(usdEquivalent, AssertActions.INT_VALIDATION)
            }

            e {
                click(cancel)
                click(
                    tokensTable.find {
                        it[TICKER_SYMBOL]?.text == "CC"
                    }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol CC")
                        ?: error("Row with Ticker symbol CC not found in table")
                )
            }

            assert {
                elementIsDisplayed(editToken.name)
                elementIsDisplayed(transferFee.name)
            }

            e {
                click(editToken)
            }

            assert {
                elementPresented(tokenIssuer)
                elementPresented(tokenName)
                elementPresented(tickerSymbol)
                elementPresented(tokenStatus)
                elementPresented(tokenDescription)
                elementPresented(channelName)
                elementPresented(chaincodeName)
                elementPresented(underlyingAsset)
                //TODO: USD equivalent moved to grid form
                // TODO: Данный элемент отсутствует, но присутствует в кейсе
//                elementPresented(usdEquivalent)

                elementPresented(confirm)
                elementPresented(cancel)
            }

            assert {
                //TODO: Issuer field has auto filling
//                validateFieldSimple(tokenIssuer, AssertActions.STRING_VALIDATION)
                validateFieldSimple(tokenName, AssertActions.STRING_VALIDATION)
                validateFieldSimple(tickerSymbol, AssertActions.STRING_VALIDATION)

                validateFieldSimple(tokenDescription, AssertActions.STRING_VALIDATION)
                validateFieldSimple(channelName, AssertActions.STRING_VALIDATION)
                validateFieldSimple(chaincodeName, AssertActions.STRING_VALIDATION)

                //TODO: USD equivalent moved to grid form
//                validateFieldSimple(usdEquivalent, AssertActions.INT_VALIDATION)
            }

            e {
                click(cancel)
            }
        }
    }

    @Disabled("Can be reversed but need some time to regulate this question")
    @TmsLink("ATMCH-1898")
    @Test
    @Story("Positive test")
    @DisplayName("Register/Managing of Tokens. Status changing (New <-> Available)")
    fun managingOfTokensStatusChangingNewToAvailable() {
        // TODO: Данный тест переводит из текущего статуса токена в указанный
        // TODO: Можно попробовать указать наименее популярный токен для тестов
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val tokenName = "CC"
            val tokenButton = tokensTable.find {
                it[TICKER_SYMBOL]?.text == tokenName
            }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol CC")
                ?: error("Row with Ticker symbol CC not found in table")

            val prevStatus = getTokenMainInformation(tokenButton).status
            val newStatus = "available"

            try {
                e {
                    editTokenStatus(tokenButton, newStatus)
                }

                e {
                    click(tokenButton)
                    click(editToken)
                }

                assert {
                    elementContainsText(tokenStatus, newStatus)
                }

                e {
                    click(cancel)
                }
            } finally {
                e {
                    editTokenStatus(tokenButton, prevStatus)
                }
            }
        }
    }

    @Disabled("Can be reversed but need some time to regulate this question")
    @TmsLink("ATMCH-1903")
    @Test
    @Story("Positive test")
    @DisplayName("Register/Managing of Tokens. Status changing (New <-> Unavailable)")
    fun managingOfTokensStatusChangingNewToUnavailable() {
        // TODO: Данный тест переводит из текущего статуса токена в указанный
        // TODO: Можно попробовать указать наименее популярный токен для тестов
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val tokenName = CoinType.CC
            val tokenButton = tokensTable.find {
                it[TICKER_SYMBOL]?.text == tokenName.name
            }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol CC")
                ?: error("Row with Ticker symbol CC not found in table")

            val prevStatus = getTokenMainInformation(tokenButton).status
            val newStatus = "unavailable"

            try {
                e {
                    editTokenStatus(tokenButton, newStatus)
                }

                // TODO: Нужна проверка. Опять отключили изменение токена.
                openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET) }.isTokenVisible(
                    tokenName
                )

                e {
                    click(tokenButton)
                    click(editToken)
                }

                assert {
                    elementContainsText(tokenStatus, newStatus)
                }

                e {
                    click(cancel)
                }
            } finally {
                e {
                    editTokenStatus(tokenButton, prevStatus)
                }
            }
        }
    }

    @Disabled("Can be reversed but need some time to regulate this question")
    @TmsLink("ATMCH-1904")
    @Test
    @Story("Positive test")
    @DisplayName("Register/Managing of Tokens. Status changing (New <-> Archived)")
    fun managingOfTokensStatusChangingNewToArchived() {
        // TODO: Данный тест переводит из текущего статуса токена в указанный
        // TODO: Можно попробовать указать наименее популярный токен для тестов
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val tokenName = CoinType.CC
            val tokenButton = tokensTable.find {
                it[TICKER_SYMBOL]?.text == tokenName.name
            }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol CC")
                ?: error("Row with Ticker symbol CC not found in table")

            val newStatus = "archived"

            e {
                editTokenStatus(tokenButton, newStatus)
            }

            // TODO: Нужна проверка. Опять отключили изменение токена.
            openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET) }.isTokenVisible(
                tokenName
            )

            e {
                click(tokenButton)
                click(editToken)
            }

            assert {
                elementContainsText(tokenStatus, newStatus)
            }

            e {
                click(cancel)
            }

            // TODO: Я не знаю какой эксепшн вылетит
            try {
                editTokenStatus(tokenButton, newStatus)
            } catch (e: Exception) {

            }
        }
    }

    @Disabled("Can be reversed but need some time to regulate this question")
    @TmsLink("ATMCH-1916")
    @Test
    @Story("Positive test")
    @DisplayName("Register/Managing of Tokens. Token editing")
    fun managingOfTokensTokenEditing() {
        // TODO: Данный тест переводит из текущего статуса токена в указанный
        // TODO: Можно попробовать указать наименее популярный токен для тестов
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val tokenSymbolName = CoinType.CC
            val tokenButton = tokensTable.find {
                it[TICKER_SYMBOL]?.text == tokenSymbolName.name
            }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol CC")
                ?: error("Row with Ticker symbol CC not found in table")

            val (prevTokenName, prevTokenDescription, status) = getTokenMainInformation(tokenButton)
            val prevEquivalent = getTokenEquivalentInformation(tokenButton)
            val newTokenName = "test"
            val newTokenDescription = "test"
            val newUsdEquivalent = "5.0"

            e {
                click(tokenButton)
                click(editToken)
            }

            assert {
                elementPresented(tokenName)
                elementPresented(tokenDescription)

                elementPresented(confirm)
                elementPresented(cancel)
            }

            try {
                e {
                    sendKeys(tokenName, newTokenName)
                    sendKeys(tokenDescription, newTokenDescription)

                    click(confirm)
                }

                e {
                    click(usdEquivalentSettings)
                    until("Wait for hidden preloader", 5L) {
                        usdEquivalentOverlay.preloader.getAttribute("style") == "visibility: hidden;"
                    }
                    sendKeys(usdEquivalentOverlay.usdEquivalent, newUsdEquivalent)
                    click(confirm)
                }

                // TODO: Не могу отдебажить на текущем состоянии стендов
                with(openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET) }) {
                    chooseToken(tokenSymbolName)
                    assertThat(
                        "Expected New Token name '${tokenInfo.tokenName.text}' is '$newTokenName'",
                        tokenInfo.tokenName.text,
                        Matchers.`is`(newTokenName)
                    )

                    assertThat(
                        "Expected New Token description '${tokenInfo.issuerDescription.text}' is '$newTokenName'",
                        tokenInfo.tokenName.text,
                        Matchers.`is`(newTokenDescription)
                    )

                    assertThat(
                        "Expected New Token name '${tokenInfo.buyValue.name}' is '$newTokenName'",
                        tokenInfo.buyValue.amount,
                        Matchers.`is`(BigDecimal(newUsdEquivalent))
                    )
                }
            } finally {
                with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                    e {
                        click(tokenButton)
                        click(editToken)

                        sendKeys(tokenName, prevTokenName)
                        sendKeys(tokenDescription, prevTokenDescription)

                        click(confirm)
                    }

                    e {
                        click(usdEquivalentSettings)
                        until("Wait for hidden preloader", 5L) {
                            usdEquivalentOverlay.preloader.getAttribute("style") == "visibility: hidden;"
                        }
                        sendKeys(usdEquivalentOverlay.usdEquivalent, prevEquivalent)
                        click(confirm)
                    }

                }
            }
        }
    }

    @Disabled("Can be reversed but need some time to regulate this question")
    @TmsLink("ATMCH-1917")
    @Test
    @Story("Positive test")
    @DisplayName("Register/Managing of Tokens. Changing token transfer fee")
    fun managingOfTokensChangingTokenFee() {
        // TODO: Данный тест переводит из текущего статуса токена в указанный
        // TODO: Можно попробовать указать наименее популярный токен для тестов
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val tokenSymbolName = CoinType.CC
            val tokenButton = tokensTable.find {
                it[TICKER_SYMBOL]?.text == tokenSymbolName.name
            }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol CC")
                ?: error("Row with Ticker symbol CC not found in table")

            val newFeeRate = "1.1"
            val newFloor = "1.1"
            val newCap = "1.1"

            val (prevRate, prevFloor, prevCap) = getTokenFeeInformation(tokenButton)
            try {
                e {
                    click(tokenButton)
                    click(transferFee)
                }
                e {
                    sendKeys(rate, newFeeRate)
                    sendKeys(floor, newFloor)
                    sendKeys(cap, newCap)

                    click(close)
                }

                // TODO: Не могу отдебажить на текущем состоянии стендов
                with(openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET) }) {
                    chooseToken(tokenSymbolName)
                    assertThat(
                        "Expected New Token name '${tokenInfo.feeRate.text}' is '$newFeeRate'",
                        tokenInfo.feeRate.amount,
                        Matchers.`is`(BigDecimal(newFeeRate))
                    )

                    assertThat(
                        "Expected New Token description '${tokenInfo.floor.text}' is '$newFloor'",
                        tokenInfo.floor.amount,
                        Matchers.`is`(BigDecimal(newFloor))
                    )

                    assertThat(
                        "Expected New Token name '${tokenInfo.cap.name}' is '$newCap'",
                        tokenInfo.cap.amount,
                        Matchers.`is`(BigDecimal(newCap))
                    )
                }
            } finally {
                with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                    e {
                        click(tokenButton)
                        click(transferFee)
                    }
                    e {
                        sendKeys(rate, prevRate)
                        sendKeys(floor, prevFloor)
                        sendKeys(cap, prevCap)

                        click(close)
                    }
                }
            }
        }
    }


}
