package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminTokensPage
import pages.atm.AtmAdminTokensPage.Headers.TICKER_SYMBOL
import pages.atm.AtmMarketplacePage
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import ru.yandex.qatools.htmlelements.element.Button
import utils.Constants
import utils.TagNames
import utils.helpers.Users.Companion.ATM_ADMIN
import utils.helpers.Users.Companion.ATM_USER_FINANCIAL_MANAGER
import utils.helpers.Users.Companion.ATM_USER_FOR_RULES_OF_TOKEN
import utils.helpers.Users.Companion.ATM_USER_FOR_RULES_OF_TOKEN_NON_INDUSTRIAL
import utils.helpers.Users.Companion.ATM_USER_FOR_RULES_OF_TOKEN_SECOND
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.OTCSETTINGS))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Configuring IDTokens Visibility")
class ConfiguringIDTokensVisibility : BaseTest() {

    private val user = ATM_USER_FOR_RULES_OF_TOKEN
    private val wallet = user.mainWallet

    private val user2 = ATM_USER_FOR_RULES_OF_TOKEN_SECOND
    private val wallet2 = user2.mainWallet

    private val userNotIndustrial = ATM_USER_FOR_RULES_OF_TOKEN_NON_INDUSTRIAL
    private val walletNotIndustrial = user.mainWallet

    private val financialManager = ATM_USER_FINANCIAL_MANAGER

    private val companyNameValueList: MutableList<String> =
        mutableListOf("ATMUSERFORRULESOFTOKEN", "ATMUSERFORRULESOFTOKENSECOND")

    private val token = CoinType.GF29ILN037B

    @ResourceLock(Constants.ROLE_USER_FOR_RULES_OF_TOKEN)
    @TmsLink("ATMCH-6132")
    @Test
    @DisplayName("Configuring tokens visibility. UI checking")
    fun configuringTokensVisibilityUIchecking() {

        with(openPage<AtmAdminTokensPage>(driver) { submit(financialManager) }) {
            tokensTable.waitUntilReady()

            val tokenRow = tokensTable.find {
                it[TICKER_SYMBOL]?.text == token.tokenSymbol
            }?.get(TICKER_SYMBOL)?.to<Button>(token.tokenSymbol)
                ?: error("Row with $token not found in table")

            e {
                click(tokenRow)
                click(visibilityRules)
            }

            assert {
                elementContainingTextPresented(token.tokenSymbol)
                elementPresented(standardVisibilityRules)
                elementPresented(customVisibilityRules)
                elementNotPresented(companyName)
                elementPresented(close)
            }

            e {
                click(customVisibilityRules)
            }

            assert {
                elementPresented(companyName)
                elementPresented(add)
                elementPresented(close)
            }

            e {
                companyName.sendAndSelect(companyNameValueList[0], companyNameValueList[0], this@with)
                click(add)
            }

            val companyRow = visibilityRulesTable.find {
                it[AtmAdminTokensPage.COMPANY]?.text == companyNameValueList[0]
            }?.get(AtmAdminTokensPage.COMPANY)?.to<Button>("Company ${companyNameValueList[0]}")
                ?: error("Row with Ticker symbol ${companyNameValueList[0]} not found in table")

            assertThat("No row found with company '$companyNameValueList[0]'", companyRow, notNullValue())

            assert {
                elementContainingTextPresented("Company")
                elementContainingTextPresented("Visible")
                elementContainingTextPresented("User")
                elementContainingTextPresented("Date of change")
            }

            e {
                click(companyRow)
            }
            assert {
                elementEnabled(delete)
            }
            e {
                click(close)
            }

            deleteCompanyFromRules(token, companyNameValueList[0])

        }
    }

    @ResourceLock(Constants.ROLE_USER_FOR_RULES_OF_TOKEN)
    @TmsLink("ATMCH-6136")
    @Test
    @DisplayName("Configuring tokens visibility. New rule adding and checking")
    fun configuringTokensVisibilityNewRuleAddingAndChecking() {

        with(openPage<AtmAdminTokensPage>(driver) { submit(financialManager) }) {

            tokensTable.waitUntilReady()

            val tokenRow = tokensTable.find {
                it[TICKER_SYMBOL]?.text == token.tokenSymbol
            }?.get(TICKER_SYMBOL)?.to<Button>(token.tokenSymbol)
                ?: error("Row with $token not found in table")

            e {
                click(tokenRow)
                click(visibilityRules)
                click(customVisibilityRules)
                assert {
                    elementPresented(tableGrid)
                }
                wait {
                    untilPresented(companyName)
                }
                companyName.sendAndSelect(companyNameValueList[0], companyNameValueList[0], this@with)
                click(add)
                click(close)
            }

        }
        with(openPage<AtmAdminTokensPage>(driver) { }) {

            checkDataForRules(token, companyNameValueList[0], "true", financialManager)

        }
        step("User go to wallet, check token") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(wallet.name)
                    setDisplayZeroBalance(true)
                }
                assert {
                    elementContainingTextPresented(token.tokenName)
                }
                openPage<AtmWalletPage>(driver)
                with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                    assert {
                        elementContainingTextPresented(token.tokenName)
                    }
                }
            }
        }

        with(openPage<AtmAdminTokensPage>(driver) { submit(ATM_ADMIN) }) {

            deleteCompanyFromRules(token, companyNameValueList[0])

        }

    }

    @ResourceLock(Constants.ROLE_USER_FOR_RULES_OF_TOKEN)
    @TmsLink("ATMCH-6141")
    @Test
    @DisplayName("Configuring token visibility. Checking the company that is out of the rule")
    fun configuringTokenVisibilityCheckingTheCompanyThatIsOutOfTheRule() {

        with(openPage<AtmAdminTokensPage>(driver) { submit(financialManager) }) {
            e {
                tokensTable.waitUntilReady()

                val tokenRow = tokensTable.find {
                    it[TICKER_SYMBOL]?.text == token.tokenSymbol
                }?.get(TICKER_SYMBOL)?.to<Button>(token.tokenSymbol)
                    ?: error("Row with $token not found in table")

                e {
                    click(tokenRow)
                    click(visibilityRules)
                    click(customVisibilityRules)
                    assert {
                        elementPresented(tableGrid)
                    }
                    wait {
                        untilPresented(companyName)
                    }
                    companyName.sendAndSelect(companyNameValueList[0], companyNameValueList[0], this@with)
                    click(add)
                    click(close)
                }
            }
        }
        with(openPage<AtmAdminTokensPage>(driver) { }) {

            checkDataForRules(token, companyNameValueList[0], "true", financialManager)

        }

        step("User go to wallet, check token") {
            with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
                e {
                    chooseWallet(wallet2.name)
                    setDisplayZeroBalance(true)
                }
                assert {
                    elementContainingTextNotPresented(token.tokenName)
                }
                openPage<AtmWalletPage>(driver)
                with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                    assert {
                        elementContainingTextNotPresented(token.tokenName)
                    }
                }
            }
        }

        with(openPage<AtmAdminTokensPage>(driver) { submit(financialManager) }) {
            deleteCompanyFromRules(token, companyNameValueList[0])
        }

    }

    @ResourceLock(Constants.ROLE_USER_FOR_RULES_OF_TOKEN)
    @TmsLink("ATMCH-6142")
    @Test
    @DisplayName("Configuring tokens visibility. Rule removing")
    fun configuringTokensVisibilityRuleRemoving() {

        with(openPage<AtmAdminTokensPage>(driver) { submit(financialManager) }) {
            addNewRules(token, companyNameValueList[1])
            deleteCompanyFromRules(token, companyNameValueList[1])
        }

        step("User go to wallet, check token") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(wallet2.name)
                    setDisplayZeroBalance(true)
                }
                assert {
                    elementContainingTextPresented(token.tokenName)
                }
                openPage<AtmWalletPage>(driver)

                with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                    assert {
                        elementContainingTextPresented(token.tokenName)
                    }
                }
            }
        }

    }

    @ResourceLock(Constants.ROLE_USER_FOR_RULES_OF_TOKEN)
    @TmsLink("ATMCH-6144")
    @Test
    @DisplayName("Configuring tokens visibility. Token status against the custom rule")
    fun configuringTokensVisibilityTokenStatusAgainstTheCustomRule() {

        with(openPage<AtmAdminTokensPage>(driver) { submit(financialManager) }) {

            addNewRules(token, companyNameValueList[1])

            val tokenButton = tokensTable.find {
                it[TICKER_SYMBOL]?.text == token.tokenSymbol
            }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol ${token.tokenSymbol}")
                ?: error("Row with Ticker symbol ${token.tokenSymbol} not found in table")

            editTokenStatus(tokenButton, AtmAdminTokensPage.StatusToken.UNAVAILABLE)

        }

        step("User go to wallet, check token") {
            with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
                e {
                    chooseWallet(wallet.name)
                    setDisplayZeroBalance(true)
                }
                assert {
                    elementWithTextNotPresented(token.tokenName)
                }
                openPage<AtmWalletPage>(driver)
                with(openPage<AtmMarketplacePage>(driver) { submit(user2) }) {
                    assert {
                        elementWithTextNotPresented(token.tokenName)
                    }
                }
            }
        }

        with(openPage<AtmAdminTokensPage>(driver) { submit(financialManager) }) {

            deleteCompanyFromRules(token, companyNameValueList[1])

            val tokenButton = tokensTable.find {
                it[TICKER_SYMBOL]?.text == token.tokenSymbol
            }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol ${token.tokenSymbol}")
                ?: error("Row with Ticker symbol ${token.tokenSymbol} not found in table")

            editTokenStatus(tokenButton, AtmAdminTokensPage.StatusToken.AVAILABLE)

        }

    }

    @ResourceLock(Constants.ROLE_USER_FOR_RULES_OF_TOKEN)
    @TmsLink("ATMCH-6153")
    @Test
    @DisplayName("Configuring tokens visibility. Participant role against custom rule")
    fun configuringTokensVisibilityParticipantRoleAgainstCustomRule() {

        val tokenIndustrial = CoinType.GF28ILN060
        val tokenETC = CoinType.ETC1
        val companyNameValueNonIndustrial = "ATMUSERFORRULESOFTOKENNONINDUSTRIAL"
        val companyNameValueNonEtc = "ATMUSERFORRULESOFTOKEN"

        with(openPage<AtmAdminTokensPage>(driver) { submit(financialManager) }) {
            addNewRules(tokenIndustrial, companyNameValueNonIndustrial)
            addNewRules(tokenETC, companyNameValueNonEtc)
        }

        step("User go to wallet, check token") {
            with(openPage<AtmWalletPage>(driver) { submit(userNotIndustrial) }) {
                e {
                    chooseWallet(walletNotIndustrial.name)
                    setDisplayZeroBalance(true)
                }
                assert {
                    elementContainingTextPresented(tokenIndustrial.tokenName)
                }
                openPage<AtmWalletPage>(driver)
                with(openPage<AtmMarketplacePage>(driver) { submit(userNotIndustrial) }) {
                    assert {
                        elementContainingTextPresented(tokenIndustrial.tokenName)
                    }
                }
            }
        }
        openPage<AtmProfilePage>().logout()
        step("User go to wallet, check token") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(wallet.name)
                    setDisplayZeroBalance(true)
                }
                assert {
                    elementContainingTextPresented(tokenETC.tokenName)
                }
                openPage<AtmWalletPage>(driver)
                with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                    assert {
                        elementContainingTextPresented(tokenETC.tokenName)
                    }
                }
            }
        }

        with(openPage<AtmAdminTokensPage>(driver) { submit(financialManager) }) {
            deleteCompanyFromRules(tokenIndustrial, companyNameValueNonIndustrial)
            deleteCompanyFromRules(tokenETC, companyNameValueNonEtc)
        }
    }

}