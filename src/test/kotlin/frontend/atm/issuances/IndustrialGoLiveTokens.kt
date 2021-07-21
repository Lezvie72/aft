package frontend.atm.issuances

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import models.CoinType
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import pages.atm.*
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.ISSUANCE.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Industrial go-live tokens")
class IndustrialGoLiveTokens : BaseTest(){

    @TmsLink("ATMCH-5183")
    @Test
    @DisplayName("Real IT. Displaying the IT tokens in Client wallet")
    fun displayingItTokensInClientWallet() {
        val user = Users.ATM_USER_FOR_RULES_OF_TOKEN
        val token1 = CoinType.GF46ILN061A
        val token2 = CoinType.GF28ILN060A
        val token3 = CoinType.GF28ILN060B
        val token4 = CoinType.GF29ILN037C
        val token5 = CoinType.GF29ILN037D
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(user.mainWallet.name)
                setDisplayZeroBalance(true)
            }
            assert {
                elementContainingTextPresented(token2.tokenName)
                elementContainingTextPresented(token3.tokenName)
                elementContainingTextPresented(token4.tokenName)
                elementContainingTextPresented(token5.tokenName)
            }
            e{
                expandMaturnityDatesOfToken(token2.tokenName)
            }
            assert {
                elementContainingTextPresented("12.10.20")
                elementContainingTextPresented("12.31.20")
                elementContainingTextPresented("01.31.21")
            }
            e{
                expandMaturnityDatesOfToken(token3.tokenName)
            }
            assert {
                elementContainingTextPresented("12.10.20")
                elementContainingTextPresented("12.31.20")
                elementContainingTextPresented("01.31.21")
            }
            e{
                expandMaturnityDatesOfToken(token4.tokenName)
            }
            assert {
                elementContainingTextPresented("01.05.21")
                elementContainingTextPresented("02.05.21")
                elementContainingTextPresented("03.05.21")
            }
            e{
                expandMaturnityDatesOfToken(token5.tokenName)
            }
            assert {
                elementContainingTextPresented("01.05.21")
                elementContainingTextPresented("02.05.21")
                elementContainingTextPresented("03.05.21")
            }
        }
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e{
                sendKeys(search, token1.tokenName)
            }
            assert {
                elementContainingTextPresented(token1.tokenName)
            }
            e{
                deleteData(search)
                sendKeys(search, token2.tokenName)
            }
            assert {
                elementContainingTextPresented(token2.tokenName)
            }
            e{
                deleteData(search)
                sendKeys(search, token3.tokenName)
            }
            assert {
                elementContainingTextPresented(token3.tokenName)
            }
            e{
                deleteData(search)
                sendKeys(search, token4.tokenName)
            }
            assert {
                elementContainingTextPresented(token4.tokenName)
            }
            e{
                deleteData(search)
                sendKeys(search, token5.tokenName)
            }
            assert {
                elementContainingTextPresented(token5.tokenName)
            }
        }
    }

    @TmsLink("ATMCH-5172")
    @Test
    @DisplayName("Real IT. Displaying the IT tokens on Marketplace")
    fun displayingItTokensOnMarketplace() {
        val user = Users.ATM_USER_FOR_RULES_OF_TOKEN
        val token2 = CoinType.GF28ILN060A
        val token3 = CoinType.GF28ILN060B
        val token4 = CoinType.GF29ILN037C
        val token5 = CoinType.GF29ILN037D
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            assert {
                elementContainingTextPresented(token2.tokenName)
                elementContainingTextPresented(token3.tokenName)
                elementContainingTextPresented(token4.tokenName)
                elementContainingTextPresented(token5.tokenName)
            }
            e{
                chooseToken(token2)
                chooseMaturnityDate("12.10.20")
            }
            assert {
                elementContainingTextPresented("250")
            }
            e{
                chooseMaturnityDate("12.31.20")
            }
            assert {
                elementContainingTextPresented("250")
            }
            e{
                chooseMaturnityDate("01.31.21")
            }
            assert {
                elementContainingTextPresented("250")
            }
            openPage<AtmMarketplacePage>(driver)
            e{
                chooseToken(token3)
                chooseMaturnityDate("12.10.20")
            }
            MatcherAssert.assertThat(
                "1000.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            e{
                chooseMaturnityDate("12.31.20")
            }
            MatcherAssert.assertThat(
                "1000.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            e{
                chooseMaturnityDate("01.31.21")
            }
            MatcherAssert.assertThat(
                "1000.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            openPage<AtmMarketplacePage>(driver)
            e{
                chooseToken(token4)
                chooseMaturnityDate("01.05.21")
            }
            MatcherAssert.assertThat(
                "2500.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            e{
                chooseMaturnityDate("02.05.21")
            }
            MatcherAssert.assertThat(
                "2500.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            e{
                chooseMaturnityDate("03.05.21")
            }
            MatcherAssert.assertThat(
                "2500.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            openPage<AtmMarketplacePage>(driver)
            e{
                chooseToken(token5)
                chooseMaturnityDate("01.05.21")
            }
            MatcherAssert.assertThat(
                "4000.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            e{
                chooseMaturnityDate("02.05.21")
            }
            MatcherAssert.assertThat(
                "4000.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
        }
    }

    @TmsLink("ATMCH-5181")
    @Test
    @DisplayName("Real IT. Displaying the IT tokens on Issuer side ")
    fun displayingItTokensOnIssuerSide() {
        val user = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val token1 = CoinType.GF46ILN061A
        val token2 = CoinType.GF28ILN060A
        val token3 = CoinType.GF28ILN060B
        val token4 = CoinType.GF29ILN037C
        val token5 = CoinType.GF29ILN037D
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e{
                sendKeys(search, token1.tokenName)
            }
            assert {
                elementContainingTextPresented(token1.tokenName)
            }
            e{
                deleteData(search)
                sendKeys(search, token2.tokenName)
            }
            assert {
                elementContainingTextPresented(token2.tokenName)
            }
            e{
                deleteData(search)
                sendKeys(search, token3.tokenName)
            }
            assert {
                elementContainingTextPresented(token3.tokenName)
            }
            e{
                deleteData(search)
                sendKeys(search, token4.tokenName)
            }
            assert {
                elementContainingTextPresented(token4.tokenName)
            }
            e{
                deleteData(search)
                sendKeys(search, token5.tokenName)
            }
            assert {
                elementContainingTextPresented(token5.tokenName)
            }
        }
        with(openPage<AtmIssuancesPage>(driver) { submit(user) }) {
            e{
                chooseToken(token2)
                chooseMaturnityDate("12.10.20")
            }
            assert {
                elementContainingTextPresented("250")
            }
            e{
                chooseMaturnityDate("12.31.20")
            }
            assert {
                elementContainingTextPresented("250")
            }
            e{
                chooseMaturnityDate("01.31.21")
            }
            assert {
                elementContainingTextPresented("250")
            }
            openPage<AtmIssuancesPage>(driver)
            e{
                chooseToken(token3)
                chooseMaturnityDate("12.10.20")
            }
            MatcherAssert.assertThat(
                "1000.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            e{
                chooseMaturnityDate("12.31.20")
            }
            MatcherAssert.assertThat(
                "1000.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            e{
                chooseMaturnityDate("01.31.21")
            }
            MatcherAssert.assertThat(
                "1000.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            openPage<AtmIssuancesPage>(driver)
            e{
                chooseToken(token4)
                chooseMaturnityDate("01.05.21")
            }
            MatcherAssert.assertThat(
                "2500.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            e{
                chooseMaturnityDate("02.05.21")
            }
            MatcherAssert.assertThat(
                "2500.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            e{
                chooseMaturnityDate("03.05.21")
            }
            MatcherAssert.assertThat(
                "2500.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            openPage<AtmIssuancesPage>(driver)
            e{
                chooseToken(token5)
                chooseMaturnityDate("01.05.21")
            }
            MatcherAssert.assertThat(
                "4000.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
            e{
                chooseMaturnityDate("02.05.21")
            }
            MatcherAssert.assertThat(
                "4000.00000000",
                Matchers.equalTo(supply.amount.toString())
            )
        }
    }

    @TmsLink("ATMCH-5195")
    @Test
    @DisplayName("Real IT. Displaying the IT tokens in Issuer wallet")
    fun displayingItTokensOnIssuerWallet() {
        val user = Users.ATM_USER_FOR_RULES_OF_TOKEN
        val token1 = CoinType.GF46ILN061A
        val token2 = CoinType.GF28ILN060A
        val token3 = CoinType.GF28ILN060B
        val token4 = CoinType.GF29ILN037C
        val token5 = CoinType.GF29ILN037D
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(user.mainWallet.name)
                setDisplayZeroBalance(true)
            }
            assert {
                elementContainingTextPresented(token2.tokenName)
                elementContainingTextPresented(token3.tokenName)
                elementContainingTextPresented(token4.tokenName)
                elementContainingTextPresented(token5.tokenName)
            }
            e{
                expandMaturnityDatesOfToken(token2.tokenName)
            }
            assert {
                elementContainingTextPresented("12.10.20")
                elementContainingTextPresented("12.31.20")
                elementContainingTextPresented("01.31.21")
            }
            e{
                expandMaturnityDatesOfToken(token3.tokenName)
            }
            assert {
                elementContainingTextPresented("12.10.20")
                elementContainingTextPresented("12.31.20")
                elementContainingTextPresented("01.31.21")
            }
            e{
                expandMaturnityDatesOfToken(token4.tokenName)
            }
            assert {
                elementContainingTextPresented("01.05.21")
                elementContainingTextPresented("02.05.21")
                elementContainingTextPresented("03.05.21")
            }
            e{
                expandMaturnityDatesOfToken(token5.tokenName)
            }
            assert {
                elementContainingTextPresented("01.05.21")
                elementContainingTextPresented("02.05.21")
                elementContainingTextPresented("03.05.21")
            }
        }
    }
}