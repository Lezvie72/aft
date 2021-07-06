package frontend.atm.etc

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.ETC
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.atm.AtmWalletPage
import ru.yandex.qatools.htmlelements.element.Button
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.ETC.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Feature("ETC")
@Story("Place redemption request for ETC FT token")
class PlaceRedemptionRequestForETCFTtoken : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_ETC_TOKEN)
    @TmsLink("ATMCH-4501")
    @Test
    @DisplayName("Place redemption request for ETC FT token. Auto. Check the interface.")
    fun placeRedemptionRequestForETCFTTokenAutoCheckTheInterface() {
        val fileName = "ETC_Nomenclature7"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val etcWallet = etcIssuer.mainWallet

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS
        val wallet = etcUser.walletList[0]

        val (amount, _) = prerequisite { prerequisiteForEtc(etcWallet, wallet, fileName, etcIssuer) }
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(etcUser) }) {
            chooseWallet(wallet.name)
            chooseToken(ETC)
            e {
                wait {
                    until("Button 'Redeem' should be enabled") {
                        redemption.getAttribute("disabled") == null
                    }
                }
                click(redemption)
            }
            assert {
                elementContainingTextPresented("TOKEN REDEMPTION")
                elementContainingTextPresented("AVAILABLE BALANCE")
                elementPresented(usdAmount)
                elementPresented(amountToken)
                elementPresented(proceedButton)
                elementPresented(cancel)
                elementContainingTextPresented("Token quantity requested to redeem")
                elementContainingTextPresented("Total tokenized weight")
                elementContainingTextPresented("Total fine weight")
                elementContainingTextPresented("Total gross weight")
                elementWithTextPresentedIgnoreCase(wallet.name)
//                elementWithTextPresentedIgnoreCase("Manual signature")
                elementWithTextPresentedIgnoreCase("WALLET ID")
                elementWithTextPresentedIgnoreCase("WALLET TYPE")
                elementWithTextPresentedIgnoreCase(ETC.tokenName)
                elementWithTextPresentedIgnoreCase("UNDERLYING ASSET")
                elementWithTextPresentedIgnoreCase("AVAILABLE")
                elementWithTextPresentedIgnoreCase("HELD IN ORDERS")
            }
            e {
                sendKeys(
                    usdAmount,
                    amount
                )// поле amountToken полная шляпа поэтому приходится так делать . Заполняется суммой из подложенного файла
                sendKeys(amountToken, (amount.toBigDecimal() * BigDecimal(1000)).toString())//сумма в ЕТС
                sendKeys(usdAmount, amount)
            }
            assert {
                elementContainingTextPresented("Selected bars to redemption")
//                elementContainingTextPresented(barNo)
            }

        }
    }

    @ResourceLock(Constants.ROLE_USER_ETC_TOKEN_THIRD)
    @TmsLink("ATMCH-4396")
    @Test
    @DisplayName("Place redemption request for ETC FT token. Manual. Check the interface.")
    fun placeRedemptionRequestForETCFTTokenManualCheckTheInterface() {
        val fileName = "ETC_Nomenclature1"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val etcWallet = etcIssuer.mainWallet

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS_THIRD
        val wallet = etcUser.walletList[0]

        val (_, _, _, barNo, _, _) = prerequisite { prerequisiteForEtc(etcWallet, wallet, fileName, etcIssuer) }
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(etcUser) }) {
            chooseWallet(wallet.name)
            chooseToken(ETC)
            e {
                wait {
                    until("Button 'Redeem' should be enabled") {
                        redemption.getAttribute("disabled") == null
                    }
                }
                click(redemption)
                click(redeemManual)
            }
            assert {
                elementWithTextPresentedIgnoreCase("TOKEN REDEMPTION")
                elementWithTextPresentedIgnoreCase("Manual")
                elementWithTextPresentedIgnoreCase("Available balance")
                elementWithTextPresentedIgnoreCase("DOWNLOAD INFO ABOUT ALL BARS")
//                elementWithTextPresentedIgnoreCase("Manual signature")
            }
            e {
                sendKeys(barsSelection, barNo)
                click(firstBarEtc)
            }
            assert {
                elementPresented(cancel)
                elementPresented(proceedButton)
                elementWithTextPresentedIgnoreCase(wallet.name)
//                elementWithTextPresentedIgnoreCase("Manual signature")
                elementWithTextPresentedIgnoreCase("WALLET ID")
                elementWithTextPresentedIgnoreCase("WALLET TYPE")
                elementWithTextPresentedIgnoreCase(ETC.tokenName)
                elementWithTextPresentedIgnoreCase("UNDERLYING ASSET")
                elementWithTextPresentedIgnoreCase("AVAILABLE")
                elementWithTextPresentedIgnoreCase("HELD IN ORDERS")
                elementWithTextPresentedIgnoreCase("All bars")
                elementWithTextPresentedIgnoreCase("Selected bars to redemption")
                elementWithTextPresentedIgnoreCase("TOKEN QUANTITY REQUESTED TO REDEEM")
                elementWithTextPresentedIgnoreCase(barNo)
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_SECOND)
    @TmsLink("ATMCH-4499")
    @Test
    @DisplayName("Place redemption request for ETC FT token. Manual. Remove selected bars")
    fun placeRedemptionRequestForETCFTtokenManualRemoveSelectedBars() {
        val fileName = "ETC_Nomenclature"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_SECOND
        val etcWallet = etcIssuer.walletList[0]

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS_THIRD
        val wallet = etcUser.walletList[0]

        val (amountRedemption, _, _, barNo, _, _) = prerequisite {
            prerequisiteForEtc(
                etcWallet,
                wallet,
                fileName,
                etcIssuer
            )
        }
        openPage<AtmWalletPage>(driver).logout()

        val amountEtcRedemption = (amountRedemption + "00000").toBigDecimal() * BigDecimal(1000)

        step("User make Manual Redemption and check token quantity") {
            with(openPage<AtmWalletPage>(driver) { submit(etcUser) }) {
                e {
                    chooseWallet(wallet.name)
                    chooseToken(ETC)
                    wait {
                        until("Button 'Redeem' should be enabled") {
                            redemption.getAttribute("disabled") == null
                        }
                    }
                    click(redemption)
                    click(manualRedeemEtc)
                }

                val tokenQuantityBefore = tokenQuantityRequestedToRedeem.amount
                e {
                    sendKeys(barsSelection, barNo)
                    pressEnter(barsSelection)
                    val barNoButton = wait {
                        untilPresented<WebElement>(By.xpath(".//atm-bar-item//span[contains(text(),'${barNo}')]"))
                    }.to<Button>("Wallet '$walletName'")
                    click(barNoButton)
                }

                val tokenQuantityAfter = tokenQuantityRequestedToRedeem.amount
                assertThat(
                    "Expected token quantity: $tokenQuantityBefore, was: $tokenQuantityAfter",
                    tokenQuantityAfter,
                    closeTo(tokenQuantityBefore + amountEtcRedemption, BigDecimal("0.01"))
                )

                e {
                    val barNoButton = wait {
                        untilPresented<WebElement>(By.xpath(".//atm-bar-item//span[contains(text(),'${barNo}')]"))
                    }.to<Button>("Wallet '$walletName'")
                    click(barNoButton)
                }

                val tokenQuantityAfterDelete = tokenQuantityRequestedToRedeem.amount
                assertThat(
                    "Expected token quantity: $tokenQuantityAfterDelete, was: $tokenQuantityBefore",
                    tokenQuantityBefore,
                    closeTo(tokenQuantityAfterDelete, BigDecimal("0.01"))
                )
            }
        }
    }
}

