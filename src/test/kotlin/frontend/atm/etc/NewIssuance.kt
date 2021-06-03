package frontend.atm.etc

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.ETC
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import pages.atm.AtmIssuancesPage
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.Constants
import utils.helpers.*
import java.math.BigDecimal


@Epic("Frontend")
@Feature("ETC")
@Story("New issuance")
class NewIssuance : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_2FA)
    @TmsLink("ATMCH-4427")
    @Test
    @DisplayName("Issuer ETC. Add volume. Has 2FA")
    fun issuerEtcAddVolumeHas2FA() {
        val fileName = "ETC_Nomenclature14"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_2FA
        val etcWallet = etcIssuer.walletList[0]
        val (amountRedemption, amountRedemption1, amountRedemption2, _) = FileHelper.createNomenclature(fileName)

        val (_, totalSupplyBefore, _) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getBalanceSupplyCirculationAndSale(ETC)
            }

        with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {

            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                chooseToken(ETC)
                e {
                    click(manageVolume)
                    click(addVolume)
                }
                uploadNomenclature(uploadNomenclature1, "${fileName}.csv", etcIssuer, etcWallet)
            }
            FileHelper.deleteFile("${fileName}.csv")
        }

        val amountEtcRedemption = (amountRedemption + "00000").toBigDecimal() * BigDecimal(1000)
        val amountEtcRedemption1 = (amountRedemption1 + "00000").toBigDecimal() * BigDecimal(1000)
        val amountEtcRedemption2 = (amountRedemption2 + "00000").toBigDecimal() * BigDecimal(1000)

        val (_, totalSupplyAfter, _) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getBalanceSupplyCirculationAndSale(ETC)
            }

        MatcherAssert.assertThat(
            "Expected totalSupply: $totalSupplyBefore, was: $totalSupplyAfter",
            totalSupplyAfter,
            Matchers.closeTo(
                totalSupplyBefore + amountEtcRedemption + amountEtcRedemption1 + amountEtcRedemption2,
                BigDecimal("0.01")
            )
        )
    }

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_2FA)
    @TmsLink("ATMCH-4426")
    @Test
    @DisplayName("Issuer ETC. Add volume interface")
    fun issuerEtcAddVolumeInterface() {

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_SECOND
        val etcWallet = etcIssuer.walletList[0]

        val fileName = "ETC_Nomenclature22"
        val fileName1 = "nomenclature_1.csv"

        FileHelper.createNomenclature(fileName)

        with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                chooseToken(ETC)
                e {
                    click(manageVolume)
                    click(addVolume)
                }
                val filePath = System.getProperty("user.dir") + "/src/test/resources/ETCDocument.png"

                val js: JavascriptExecutor = driver as JavascriptExecutor
                val locator = wait {
                    untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Upload documents')]"))
                }.to<TextInput>("Employee ''")
                js.executeScript("arguments[0].style.display='block';", locator)
                uploadDocument.sendKeys(filePath)

                uploadNomenclature(uploadNomenclature1, "${fileName}.csv", etcIssuer, etcWallet)
                FileHelper.deleteFile("${fileName}.csv")
            }
        }

        with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
            chooseToken(ETC)
            e {
                click(manageVolume)
            }
            assert {
                elementPresented(addVolume)
                elementContainingTextPresented("TOKEN TYPE")
                elementContainingTextPresented("UNDERLYING ASSET")
                elementContainingTextPresented("ISSUER")
                elementContainingTextPresented("AMOUNT")
                elementContainingTextPresented("SUBMITTED")
                elementContainingTextPresented("REQUEST ID")
                elementContainingTextPresented("REQUESTOR")
            }
            e {
                click(attachementsFromValume)
            }
            assert {
                elementPresented(attachementsList)
            }
            e {
                click(addVolume)
            }
            checkValuesFromBarList(fileName1, "1620.000", "966.000", "1230.000", "3")
        }
    }
}

