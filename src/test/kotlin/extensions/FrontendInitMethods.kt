package extensions

import frontend.BaseTest
import models.CoinType
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.openqa.selenium.WebDriver
import pages.atm.AtmAdminTokensPage
import utils.helpers.Users

import utils.helpers.openPage

class FrontendInitMethods : BeforeAllCallback {

    companion object {

        private var balancesInitialized: Boolean = false

        fun initTokensNames(driver: WebDriver) {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                val coins = tokensTable.getRowsMappedToHeadingFromAllPagesAsString()
                CoinType.values().forEach { coin ->
                    val name = coins.find {
                        it[AtmAdminTokensPage.TICKER_SYMBOL] == coin.tokenSymbol
                    }?.get(AtmAdminTokensPage.TOKEN_NAME)
                    name?.let {
                        coin.tokenName = it
                    }
                }
            }
        }

        fun initBalances() {
            if (!balancesInitialized
                && System.getProperty("smokeOnly", "false") != "true"
                && System.getProperty("skipBalances", "false") != "true"
            ) {
                BaseTest.prerequisite {
                    this.initBalances()
                    balancesInitialized = true
                }
            }
        }
    }

    override fun beforeAll(context: ExtensionContext?) {
        val driver = BaseTest.newDriver()
        try {
            if (System.getProperty("skipInitToken") != "true") {
                initTokensNames(driver)
            }
        } catch (e: Exception) {
        } finally {
            driver.close()
        }
        initBalances()
    }

}