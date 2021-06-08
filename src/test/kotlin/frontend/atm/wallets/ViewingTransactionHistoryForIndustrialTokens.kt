package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType.IT
import org.apache.commons.lang.RandomStringUtils.randomNumeric
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.atm.AtmIssuancesPage
import pages.atm.AtmIssuancesPage.StatusType.APPROVE
import pages.atm.AtmIssuancesPage.StatusType.DECLINE
import pages.atm.AtmMarketplacePage
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import pages.htmlelements.elements.AtmRadio
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Issue("ATMCH-5912")
@Epic("Frontend")
@Feature("Wallets")
@Story("Viewing transaction history for industrial tokens (top tabs for navigations)")
class ViewingTransactionHistoryForIndustrialTokens : BaseTest() {

    private val maturityDate = "122020"

    private val maturityDateButton1: Pair<String, String> = "09.22.20" to "09.21.20"
    private val maturityDateButton2: Pair<String, String> = "10.22.20" to "10.21.20"
    private val maturityDateButton3: Pair<String, String> = "11.22.20" to "11.21.20"
    private val maturityDateButton4: Pair<String, String> = "12.22.20" to "12.21.20"


    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_IT_TOKEN_THIRD),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN)
    )
    @TmsLink("ATMCH-3247")
    @Test
    @DisplayName("IT. Viewing transaction history.")
    fun itViewingTransactionHistory() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_MAIN_FOR_IT_THIRD
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User buy and accepted IT token") {
            prerequisite {
                addCurrencyCoinToWallet(user, "10", mainWallet)
                placeAndProceedTokenRequest(IT, mainWallet, wallet, amount, APPROVE, user, user1)
            }
            AtmProfilePage(driver).logout()
        }
        step("User check transaction") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet(mainWallet.name)
                chooseToken(IT)

                wait {
                    until("transaction list is presented", 30) {
                        check {
                            isElementPresented(By.xpath("//atm-transactions//atm-ind-issue-list"))
                        }
                    }
                }

                val maturityButton = wait {
                    untilPresented<WebElement>(
                        By.xpath(
                            generateLocatorForMaturityButton(
                                maturityDateButton4.first,
                                maturityDateButton4.second
                            )
                        )
                    )
                }.to<AtmRadio>("Wallet '${maturityDateButton4.first}' or '${maturityDateButton4.second}'")

                e {
                    click(maturityButton)
                }

                //                TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                Thread.sleep(1000)

                val expectedTransactionList = transactionsList.content.map { it.transaction }.toSet()

                assertThat("Transaction amount is not in list", expectedTransactionList, hasItem(amount))
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_SECOND),
        ResourceLock(Constants.ROLE_USER_IT_TOKEN_SECOND)
    )
    @TmsLink("ATMCH-5331")
    @Test
    @DisplayName("IT.Receive transactions are shown in wallet under corresponding Maturity date")
    fun itReceiveTransactionsAreShownInWalletUnderCorrespondingMaturityDate() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_MAIN_FOR_IT_SECOND
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_SECOND
        val wallet = user1.walletList[0]

        step("User buy and accepted IT token") {
            prerequisite {
                addCurrencyCoinToWallet(user, "10", mainWallet)
                placeAndProceedTokenRequest(IT, mainWallet, wallet, amount, APPROVE, user, user1)
            }
            AtmProfilePage(driver).logout()
        }
        step("User check transaction") {

            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet(mainWallet.name)
                chooseToken(IT)

                wait {
                    until("transaction list is presented", 30) {
                        check {
                            isElementPresented(By.xpath("//atm-transactions//atm-ind-issue-list"))
                        }
                    }
                }

                val maturityButton = wait {
                    untilPresented<WebElement>(
                        By.xpath(
                            generateLocatorForMaturityButton(
                                maturityDateButton4.first,
                                maturityDateButton4.second
                            )
                        )
                    )
                }.to<AtmRadio>("Wallet '${maturityDateButton4.first}' or '${maturityDateButton4.second}'")

                e {
                    click(maturityButton)
                }

                //                TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                Thread.sleep(1000)

                softAssert {
                    elementContainingTextPresented("Type")
                    elementContainingTextPresented("Creation date")
                    elementContainingTextPresented("Time utc")
                    elementContainingTextPresented("Transaction amount")
                    elementContainingTextPresented("Balance after transaction")
                    elementContainingTextPresented("Tx id")
                    elementContainingTextPresented("Token group")
                }

                val expectedTransactionList = transactionsList.content.map { it.transaction }.toSet()

                assertThat("Transaction amount is not in list", expectedTransactionList, hasItem(amount))
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN)
    )
    @TmsLink("ATMCH-5333")
    @Test
    @DisplayName("IT.Receive transactions are shown in wallet under corresponding Maturity date - All tokens in contract")
    fun itReceiveTransactionsAreShownInWalletUnderCorrespondingMaturityDateAllTokensInContract() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        prerequisite {
            addCurrencyCoinToWallet(user, "10", mainWallet)
        }

        step("User buy and accepted IT token") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                chooseToken(IT)
                e {
                    click(newOrderButton)
                    click(allTokensButton)
                    select(selectWallet, mainWallet.publicKey)

                    tokenMultipleQuantity.forEach {
                        deleteData(it)
                        click(it)
                        sendKeys(it, amount.toString())
                        Thread.sleep(1000)
                    }
                    click(submitButton)
                    signAndSubmitMessage(user, mainWallet.secretKey)
                }
                AtmProfilePage(driver).logout()

                val multipleAmount = amount.multiply(BigDecimal(4))

                with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                    changeStatusForOfferWithAmount(IT, multipleAmount, APPROVE, user1, wallet)
                }
                AtmProfilePage(driver).logout()
            }
        }
        step("User check transaction") {

            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet(mainWallet.name)
                chooseToken(IT)
                wait {
                    until("transaction list is presented", 30) {
                        check {
                            isElementPresented(By.xpath("//atm-transactions//atm-ind-issue-list"))
                        }
                    }
                }

                val maturityButton1 = wait {
                    untilPresented<WebElement>(
                        By.xpath(
                            generateLocatorForMaturityButton(
                                maturityDateButton1.first,
                                maturityDateButton1.second
                            )
                        )
                    )
                }.to<AtmRadio>("Wallet '${maturityDateButton1.first}' or '${maturityDateButton1.second}'")

                e {
                    click(maturityButton1)
                }

                //                TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                Thread.sleep(1000)

                val expectedTransactionList1 = transactionsList.content.map { it.transaction }.toSet()

                val maturityButton2 = wait {
                    untilPresented<WebElement>(
                        By.xpath(
                            generateLocatorForMaturityButton(
                                maturityDateButton2.first,
                                maturityDateButton2.second
                            )
                        )
                    )
                }.to<AtmRadio>("Wallet '${maturityDateButton2.first}' or '${maturityDateButton2.second}'")

                e {
                    click(maturityButton2)
                }

                //                TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                Thread.sleep(1000)

                val expectedTransactionList2 = transactionsList.content.map { it.transaction }.toSet()

                val maturityButton3 = wait {
                    untilPresented<WebElement>(
                        By.xpath(
                            generateLocatorForMaturityButton(
                                maturityDateButton3.first,
                                maturityDateButton3.second
                            )
                        )
                    )
                }.to<AtmRadio>("Wallet '${maturityDateButton3.first}' or '${maturityDateButton3.second}'")
                e {
                    click(maturityButton3)
                }

                //                TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                Thread.sleep(1000)

                val expectedTransactionList3 = transactionsList.content.map { it.transaction }.toSet()

                val maturityButton4 = wait {
                    untilPresented<WebElement>(
                        By.xpath(
                            generateLocatorForMaturityButton(
                                maturityDateButton4.first,
                                maturityDateButton4.second
                            )
                        )
                    )
                }.to<AtmRadio>("Wallet '${maturityDateButton4.first}' or '${maturityDateButton4.second}'")
                e {
                    click(maturityButton4)
                }

                //                TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                Thread.sleep(1000)

                val expectedTransactionList4 = transactionsList.content.map { it.transaction }.toSet()

                assertThat(
                    "Transaction amount is not in list for $maturityDateButton1",
                    expectedTransactionList1,
                    hasItem(amount)
                )
                assertThat(
                    "Transaction amount is not in list for $maturityDateButton2",
                    expectedTransactionList2,
                    hasItem(amount)
                )
                assertThat(
                    "Transaction amount is not in list for $maturityDateButton3",
                    expectedTransactionList3,
                    hasItem(amount)
                )
                assertThat(
                    "Transaction amount is not in list for $maturityDateButton4",
                    expectedTransactionList4,
                    hasItem(amount)
                )
            }
        }

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    )
    @TmsLink("ATMCH-5334")
    @Test
    @DisplayName("IT. Transfer transactions are shown in wallet under corresponding Maturity date")
    fun itTransferTransactionsAreShownInWalletUnderCorrespondingMaturityDate() {
        val amount = BigDecimal("1.${randomNumeric(8)}")
        val amountToTransfer = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val mainWallet = user.mainWallet

        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val secondMainWallet = user2.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User buy, accepted and get balance from wallet IT token") {
            prerequisite {
                addCurrencyCoinToWallet(user, "10", mainWallet)
                placeAndProceedTokenRequest(IT, mainWallet, wallet, amount, APPROVE, user, user1)
            }
            AtmProfilePage(driver).logout()
        }

        step("User make transfer from to wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                transferFromWalletToWallet(
                    IT,
                    mainWallet,
                    secondMainWallet,
                    amountToTransfer.toString(),
                    maturityDate,
                    "note",
                    user
                )
            }
            step("User go to wallet and check transfer in transaction list") {

                with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                    chooseWallet(mainWallet.name)
                    chooseToken(IT)

                    wait {
                        until("transaction list is presented", 30) {
                            check {
                                isElementPresented(By.xpath("//atm-transactions//atm-ind-issue-list"))
                            }
                        }
                    }

                    val maturityButton = wait {
                        untilPresented<WebElement>(
                            By.xpath(
                                generateLocatorForMaturityButton(
                                    maturityDateButton4.first,
                                    maturityDateButton4.second
                                )
                            )
                        )
                    }.to<AtmRadio>("Wallet '${maturityDateButton4.first}' or '${maturityDateButton4.second}'")
                    e {
                        click(maturityButton)
                    }

                    //                TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                    Thread.sleep(1000)

                    val expectedTransactionList = transactionsList.content.map { it.transaction }.toSet()

                    assertThat("Transaction amount is not in list", expectedTransactionList, hasItem(amountToTransfer))
                }
            }

            AtmProfilePage(driver).logout()

            step("User 2 go to wallet and check transfer in transaction list") {
                with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
                    chooseWallet(secondMainWallet.name)
                    chooseToken(IT)

                    wait {
                        until("transaction list is presented", 30) {
                            check {
                                isElementPresented(By.xpath("//atm-transactions//atm-ind-issue-list"))
                            }
                        }
                    }

                    val maturityButton = wait {
                        untilPresented<WebElement>(
                            By.xpath(
                                generateLocatorForMaturityButton(
                                    maturityDateButton4.first,
                                    maturityDateButton4.second
                                )
                            )
                        )
                    }.to<AtmRadio>("Wallet '${maturityDateButton4.first}' or '${maturityDateButton4.second}'")
                    e {
                        click(maturityButton)
                    }

                    //                TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                    Thread.sleep(1000)

                    val expectedTransactionList = transactionsList.content.map { it.transaction }.toSet()

                    assertThat("Transaction amount is not in list", expectedTransactionList, hasItem(amountToTransfer))
                }
            }
        }
    }


    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_IT_TOKEN_ONE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_ONE)
    )
    @TmsLink("ATMCH-5358")
    @Test
    @DisplayName("IT.Redeem transactions are shown in wallet under corresponding Maturity date")
    fun itRedeemTransactionsAreShownInWalletUnderCorrespondingMaturityDate() {
        val min = BigDecimal("3.01234567")

        val amount = min + BigDecimal("2.${randomNumeric(8)}")
        val amountForRedemption = min + BigDecimal("1.${randomNumeric(8)}")
//        val maturityDate = LocalDateTime.now().month.getDisplayName(TextStyle.SHORT, Locale.US)
        val maturityDate = "December 2020"

        val userBuyer = Users.ATM_USER_MAIN_FOR_IT_ONE
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_ONE
        val itWallet = itIssuer.walletList[0]

        step("User buy IT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, "10", mainWallet)
                addITToken(userBuyer, itIssuer, "10", mainWallet, itWallet, amount)
            }
            AtmProfilePage(driver).logout()
        }

        step("User make redeem") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                redeemToken(
                    IT,
                    mainWallet,
                    amountForRedemption.toString(),
                    maturityDate,
                    userBuyer
                )
            }
        }
        step("User check transaction") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                chooseWallet(mainWallet.name)
                chooseToken(IT)

                wait {
                    until("transaction list is presented", 30) {
                        check {
                            isElementPresented(By.xpath("//atm-transactions//atm-ind-issue-list"))
                        }
                    }
                }

                val maturityButton = wait {
                    untilPresented<WebElement>(
                        By.xpath(
                            generateLocatorForMaturityButton(
                                maturityDateButton4.first,
                                maturityDateButton4.second
                            )
                        )
                    )
                }.to<AtmRadio>("Wallet '${maturityDateButton4.first}' or '${maturityDateButton4.second}'")
                e {
                    click(maturityButton)
                }

                //                TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                Thread.sleep(1000)

                val expectedTransactionList = transactionsList.content.map { it.transaction }.toSet()

                assertThat("Transaction amount is not in list", expectedTransactionList, hasItem(amount))
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_SECOND)
    )
    @TmsLink("ATMCH-5360")
    @Test
    @DisplayName("IT. Approved Redemption transactions are shown in wallet under corresponding Maturity date")
    fun itApprovedRedemptionTransactionsAreShownInWalletUnderCorrespondingMaturityDate() {
        val min = BigDecimal("3.01234567")

        val amount = min + BigDecimal("2.${randomNumeric(8)}")
        val amountForRedemption = min + BigDecimal("1.${randomNumeric(8)}")
//        val maturityDate = LocalDateTime.now().month.getDisplayName(TextStyle.SHORT, Locale.US)
        val maturityDate = "December 2020"

        val userBuyer = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_SECOND
        val itWallet = itIssuer.walletList[0]
        step("User buy IT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, "10", mainWallet)
                addITToken(userBuyer, itIssuer, "10", mainWallet, itWallet, amount)
            }
            AtmProfilePage(driver).logout()
        }

        step("User make redeem") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                redeemToken(
                    IT,
                    mainWallet,
                    amountForRedemption.toString(),
                    maturityDate,
                    userBuyer
                )
            }
        }
        AtmProfilePage(driver).logout()

        step("Issuer accept redeem") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                findRedemptionOffers(IT, amountForRedemption, amountForRedemption, itIssuer, itWallet, APPROVE)
                openPage<AtmIssuancesPage>(driver)
            }
        }
        AtmProfilePage(driver).logout()

        step("User check transaction") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                chooseWallet(mainWallet.name)
                chooseToken(IT)

                wait {
                    until("transaction list is presented", 30) {
                        check {
                            isElementPresented(By.xpath("//atm-transactions//atm-ind-issue-list"))
                        }
                    }
                }

                val maturityButton = wait {
                    untilPresented<WebElement>(
                        By.xpath(
                            generateLocatorForMaturityButton(
                                maturityDateButton4.first,
                                maturityDateButton4.second
                            )
                        )
                    )
                }.to<AtmRadio>("Wallet '${maturityDateButton4.first}' or '${maturityDateButton4.second}'")
                e {
                    click(maturityButton)
                }

//                TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                Thread.sleep(1000)

                val expectedTransactionList = transactionsList.content.map { it.transaction }.toSet()

                assertThat("Transaction amount is not in list", expectedTransactionList, hasItem(amount))
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_IT_TOKEN_SECOND),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_SECOND)
    )
    @TmsLink("ATMCH-5361")
    @Test
    @DisplayName("IT. Declined Redemption transactions are shown in wallet under corresponding Maturity date")
    fun itDeclinedRedemptionTransactionsAreShownInWalletUnderCorrespondingMaturityDate() {
        val min = BigDecimal("3.01234567")

        val amount = min + BigDecimal("2.${randomNumeric(8)}")
        val amountForRedemption = min + BigDecimal("1.${randomNumeric(8)}")
//        val maturityDate = LocalDateTime.now().month.getDisplayName(TextStyle.SHORT, Locale.US)
        val maturityDate = "December 2020"

        val userBuyer = Users.ATM_USER_MAIN_FOR_IT_SECOND
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_SECOND
        val itWallet = itIssuer.walletList[0]
        step("User buy IT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, "10", mainWallet)
                addITToken(userBuyer, itIssuer, "10", mainWallet, itWallet, amount)
            }
            AtmProfilePage(driver).logout()
        }

        step("User make redeem") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                redeemToken(
                    IT,
                    mainWallet,
                    amountForRedemption.toString(),
                    maturityDate,
                    userBuyer
                )
            }
        }
        AtmProfilePage(driver).logout()

        step("Issuer decline redeem") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                findRedemptionOffers(IT, amountForRedemption, amountForRedemption, itIssuer, itWallet, DECLINE)
                openPage<AtmIssuancesPage>(driver)
            }
        }
        AtmProfilePage(driver).logout()

        step("User check transaction") {

            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                chooseWallet(mainWallet.name)
                chooseToken(IT)

                wait {
                    until("transaction list is presented", 30) {
                        check {
                            isElementPresented(By.xpath("//atm-transactions//atm-ind-issue-list"))
                        }
                    }
                }

                val maturityButton = wait {
                    untilPresented<WebElement>(
                        By.xpath(
                            generateLocatorForMaturityButton(
                                maturityDateButton4.first,
                                maturityDateButton4.second
                            )
                        )
                    )
                }.to<AtmRadio>("Wallet '${maturityDateButton4.first}' or '${maturityDateButton4.second}'")
                e {
                    click(maturityButton)
                }
//TODO запилить нормальное ожидание работает почему то только через Thread sleep
//                wait {
//                    wait(30L) {
//                        until("Transaction list is not empty") {
//                            transactionsList.content.isNotEmpty()
//                        }
//                    }
//                }
                Thread.sleep(1000)

                val expectedTransactionList = transactionsList.content.map { it.transaction }.toSet()

                assertThat("Transaction amount is not in list", expectedTransactionList, hasItem(amount))
            }
        }
    }


    @TmsLink("ATMCH-5363")
    @Test
    @DisplayName("IT. Maturity date values comparing")
    fun itMaturityDateValuesComparing() {

        val userBuyer = Users.ATM_USER_MAIN_FOR_IT_THIRD
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_THIRD

        val maturityDates = step("Issuer check maturity dates") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                chooseToken(IT)
                val maturityDates =
                    findElements(By.xpath("//atm-ind-issue-list//label//span[2]")).map { it.text }.toMutableList()

                maturityDates
            }
        }
        AtmProfilePage(driver).logout()

        step("User check maturity dates") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                chooseWallet(mainWallet.name)
                chooseToken(IT)

                wait {
                    until("transaction list is presented", 30) {
                        check {
                            isElementPresented(By.xpath("//atm-transactions//atm-ind-issue-list"))
                        }
                    }
                }

                val maturityDatesWallets =
                    findElements(By.xpath("//atm-ind-issue-list//label//span[2]")).map { it.text }.toMutableList()

                assertThat("List not equality", maturityDates, equalTo(maturityDatesWallets))

            }
        }
    }
}