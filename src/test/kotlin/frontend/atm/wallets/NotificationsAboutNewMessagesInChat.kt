package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.CoinType.ETC
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.*
import org.openqa.selenium.By
import pages.atm.*
import pages.atm.AtmWalletPage.RedemptionTypeETC.AUTO
import utils.Constants
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Epic("Frontend")
@Feature("Wallets")
@Story("Notifications about new messages in chat")
class NotificationsAboutNewMessagesInChat : BaseTest() {

    @Order(1)
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5171")
    @Test
    @DisplayName("ETC Redemption.Notification about new messages")
    fun etcRedemptionNotificationAboutNewMessages() {
        val fileName = "ETC_Nomenclature14"

        val textUser = "Message for User ${RandomStringUtils.randomNumeric(4)}"
        val textIssuer = "Message for Issuer ${RandomStringUtils.randomNumeric(4)}"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val etcWallet = etcIssuer.mainWallet

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS
        val wallet = etcUser.mainWallet

        val (amountRedemption, _) = prerequisite { prerequisiteForEtc(etcWallet, wallet, fileName, etcIssuer) }

        openPage<AtmWalletPage>(driver).logout()

        val amountEtcRedemption = (amountRedemption + "00000").toBigDecimal() * BigDecimal(1000)

        val etcIssuerBrowser = driver
        val etcUserBrowser = createDriver()

        step("User make Redemption ETC token") {
            with(openPage<AtmWalletPage>(etcUserBrowser) { submit(etcUser) }) {
                redeemEtcToken(
                    ETC,
                    wallet,
                    AUTO,
                    amountRedemption,
                    "",
                    etcUser
                )
            }

            Thread.sleep(2000)
            with(openPage<AtmOrdersPage>(etcUserBrowser) { submit(etcUser) })
            {
                findOrderAndOpenCard(
                    wallet,
                    ETC,
                    amountEtcRedemption
                )
                e {
                    sendKeys(chatInput, textIssuer)
                    click(chatSendButton)
                }

            }
        }
        val since = LocalDateTime.now(ZoneOffset.UTC)

        step("ETC issuer go to page from email and check message from User") {
            with(openPage<AtmIssuancesPage>(etcIssuerBrowser) { submit(etcIssuer) }) {
                val href = GmailApi.getNotificationAboutOrder(etcIssuer.email, since)
                val body = GmailApi.getTextNotificationAboutOrder(etcIssuer.email, since)
                driver.navigate().to(href)
                wait {
                    until("Button Show chat is displayed", 30) {
                        check {
                            isElementPresented(By.xpath("//span[contains(text(), 'Show chat')]"))
                        }
                    }
                }
                e {
                    click(showChat)
                    checkTheMessageFromChat(textIssuer)
                    assert {
                        textEmail(
                            body,
                            "Dear issuer!Participant etcAutotest has sent you new message." +
                                    "Please follow the ${href.trim()} to read it." +
                                    "P.S. Please do not reply to this email. This email came from an automated, unmonitored mailbox."
                        )
                    }
                    sendKeys(chatInput, textUser)
                    click(chatSendButton)
                }
            }
        }
        val since1 = LocalDateTime.now(ZoneOffset.UTC)
        step("User go to page from email and check message from ETC Issuer") {
            with(openPage<AtmOrdersPage>(etcUserBrowser) { submit(etcUser) })
            {
                val href = GmailApi.getMessageFromIssuer(etcUser.email, since1)
                val body = GmailApi.getTextMessageFromIssuer(etcUser.email, since1)
                driver.navigate().to(href)
                wait {
                    until("Chat input is displayed", 30) {
                        check {
                            isElementPresented(By.xpath("//atm-chat//textarea[@formcontrolname='message']"))
                        }
                    }
                }
                checkTheMessageFromChat(textUser)
                assert {
                    textEmail(
                        body,
                        "Dear Atomyze participant!Issuer Issuer name - test has sent you new message." +
                                "Please follow the ${href.trim()} to read it." +
                                "P.S. Please do not reply to this email. This email came from an automated, unmonitored mailbox."
                    )
                }
            }
        }
    }

    @Order(2)
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_IT_TOKEN_SECOND, mode = ResourceAccessMode.READ),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_SECOND, mode = ResourceAccessMode.READ)
    )
    @TmsLink("ATMCH-5175")
    @Test
    @DisplayName("Distribution-Upon-Request.Notification about new messages")
    fun distributionUponRequestNotificationAboutNewMessages() {
        val textUser = "Message for User ${RandomStringUtils.randomNumeric(4)}"
        val textIssuer = "Message for Issuer ${RandomStringUtils.randomNumeric(4)}"
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_SECOND

        val user = Users.ATM_USER_MAIN_FOR_IT_SECOND
        val userWallet = user.mainWallet

        step("User buy CC token") {
            prerequisite {
                addCurrencyCoinToWallet(user, "10", userWallet)
            }
        }

        val itIssuerBrowser = driver
        val itUserBrowser = createDriver()

        step("User buy IT token") {
            with(openPage<AtmMarketplacePage>(itUserBrowser) { submit(user) }) {
                buyTokenNew(CoinType.IT, amount.toString(), user, userWallet)
            }
        }

        step("User go to Orders page and send message to Issuer") {
            with(openPage<AtmOrdersPage>(itUserBrowser) { submit(user) })
            {
                findOrderAndOpenCard(
                    userWallet,
                    CoinType.IT,
                    amount
                )
                e {
                    sendKeys(chatInput, textIssuer)
                    click(chatSendButton)
                }
            }
        }
        val since = LocalDateTime.now(ZoneOffset.UTC)

        step("Issuer go to Issuance and check Message from User") {
            with(openPage<AtmIssuancesPage>(itIssuerBrowser) { submit(itIssuer) }) {
                val href = GmailApi.getNotificationAboutOrder(itIssuer.email, since)
                val body = GmailApi.getTextNotificationAboutOrder(itIssuer.email, since)
                driver.navigate().to(href)
                wait {
                    until("Button Show chat is displayed", 30) {
                        check {
                            isElementPresented(By.xpath("//span[contains(text(), 'Show chat')]"))
                        }
                    }
                }
                e {
                    click(showChat)
                    checkTheMessageFromChat(textIssuer)
                    assert {
                        textEmail(
                            body,
                        "Dear issuer!Participant autotesITsecond has sent you new message." +
                                "Please follow the ${href.trim()} to read it.P.S." +
                                " Please do not reply to this email." +
                                " This email came from an automated, unmonitored mailbox."
                        )
                    }
                    sendKeys(chatInput, textUser)
                    click(chatSendButton)
                }
            }
        }
        val since1 = LocalDateTime.now(ZoneOffset.UTC)

        step("User go to Orders page and check message from Issuer") {
            with(openPage<AtmOrdersPage>(itUserBrowser) { submit(user) })
            {
                val href = GmailApi.getMessageFromIssuer(user.email, since1)
                val body = GmailApi.getTextMessageFromIssuer(user.email, since1)

                driver.navigate().to(href)
                wait {
                    until("Chat input is displayed", 30) {
                        check {
                            isElementPresented(By.xpath("//atm-chat//textarea[@formcontrolname='message']"))
                        }
                    }
                }
                checkTheMessageFromChat(textUser)
                assert {
                    textEmail(
                        body,
                        "Dear Atomyze participant!Issuer a27a46e0-53fc-4f51-97c6-3cd56486bbed has sent you new message." +
                                "Please follow the ${href.trim()} to read it." +
                                "P.S. Please do not reply to this email. This email came from an automated, unmonitored mailbox."
                    )
                }
            }
        }
    }

    @Order(2)
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN, mode = ResourceAccessMode.READ),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND, mode = ResourceAccessMode.READ)
    )
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("IT Redemption.Notification about new messages")
    fun itRedemptionNotificationAboutNewMessages() {
        val textUser = "Message for User ${RandomStringUtils.randomNumeric(4)}"
        val textIssuer = "Message for Issuer ${RandomStringUtils.randomNumeric(4)}"
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_ONE
        val itWallet = itIssuer.mainWallet

        val user = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
        val userWallet = user.mainWallet

        step("Issuer change redemption limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    CoinType.IT,
                    AtmIssuancesPage.OperationType.RECEIVE,
                    AtmIssuancesPage.LimitType.MIN, "0.00000001", itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    CoinType.IT,
                    AtmIssuancesPage.OperationType.REDEMPTION,
                    AtmIssuancesPage.LimitType.MAX, "100.00000000", itIssuer, itWallet
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        step("User buy IT token") {
            prerequisite { addITToken(user, itIssuer, "10", userWallet, itWallet, amount) }
            AtmProfilePage(driver).logout()
        }

        val itIssuerBrowser = driver
        val itUserBrowser = createDriver()

        step("User make Redemption") {
            with(openPage<AtmWalletPage>(itUserBrowser) { submit(user) }) {
                redeemToken(
                    CoinType.IT,
                    userWallet,
                    amount.toString(),
                    "",
                    user
                )
            }
        }

        step("User go to Orders page and send message to Issuer") {
            with(openPage<AtmOrdersPage>(itUserBrowser) { submit(user) })
            {
                findOrderAndOpenCard(
                    userWallet,
                    CoinType.IT,
                    amount
                )
                e {
                    sendKeys(chatInput, textIssuer)
                    click(chatSendButton)
                }
            }
        }
        val since = LocalDateTime.now(ZoneOffset.UTC)

        step("Issuer go to page from email and check message from User") {
            with(openPage<AtmIssuancesPage>(itIssuerBrowser) { submit(itIssuer) }) {
                val href = GmailApi.getNotificationAboutOrder(itIssuer.email, since)
                val body = GmailApi.getTextNotificationAboutOrder(itIssuer.email, since)
                driver.navigate().to(href)
                wait {
                    until("Button Show chat is displayed", 30) {
                        check {
                            isElementPresented(By.xpath("//span[contains(text(), 'Show chat')]"))
                        }
                    }
                }
                e {
                    click(showChat)
                    checkTheMessageFromChat(textIssuer)
                    assert {
                        textEmail(
                            body,
                            "Dear issuer!Participant otfoperatonsecondautotest has sent you new message." +
                                    "Please follow the ${href.trim()} to read it." +
                                    "P.S. Please do not reply to this email. This email came from an automated, unmonitored mailbox."
                        )
                    }
                    sendKeys(chatInput, textUser)
                    click(chatSendButton)
                }
            }
        }
        val since1 = LocalDateTime.now(ZoneOffset.UTC)

        step("User go to Orders page and check message from Issuer") {
            with(openPage<AtmOrdersPage>(itUserBrowser) { submit(user) })
            {
                val href = GmailApi.getMessageFromIssuer(user.email, since1)
                val body = GmailApi.getTextMessageFromIssuer(user.email, since1)

                driver.navigate().to(href)
                wait {
                    until("Chat input is displayed", 30) {
                        check {
                            isElementPresented(By.xpath("//atm-chat//textarea[@formcontrolname='message']"))
                        }
                    }
                }
                checkTheMessageFromChat(textUser)
                assert {
                    textEmail(
                        body,
                        "Dear Atomyze participant!Issuer Issuer name - test has sent you new message." +
                                "Please follow the ${href.trim()} to read it." +
                                "P.S. Please do not reply to this email. This email came from an automated, unmonitored mailbox."
                    )
                }
            }
        }

    }
}
