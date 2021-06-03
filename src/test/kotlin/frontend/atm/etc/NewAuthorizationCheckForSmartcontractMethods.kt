package frontend.atm.etc

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.ETC
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import pages.atm.AtmIssuancesPage
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.Constants
import utils.helpers.FileHelper
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.to


@Epic("Frontend")
@Feature("ETC")
@Story("New authorization check for smartcontract methods")
class NewAuthorizationCheckForSmartcontractMethods : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    @TmsLink("ATMCH-4452")
    @Test
    @DisplayName("ETC. Redemption. Authorized wallet. Controller")
    fun etcRedemptionAuthorizedWalletController() {
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA

        with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
            chooseToken(ETC)
            e {
                click(redemptionCurrentQueue)
                click(proceed)
                click(approve)
            }
            assert { elementWithTextPresentedIgnoreCase("Manual signature") }

            e {
                click(cancelSubmitPrivateKeyButton)
                click(cancelButton)
                click(proceed)
                click(decline)
            }
            assert { elementWithTextPresentedIgnoreCase("Manual signature") }

        }
    }

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN)
    @TmsLink("ATMCH-4456")
    @Test
    @DisplayName("Non-ETC. Authorized wallet. Controller")
    fun nonEtcAuthorizedWalletController() {
        val issuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val fileName = "ETC_Nomenclature13"

        FileHelper.createNomenclature(fileName)

        with(openPage<AtmIssuancesPage>(driver) { submit(issuer) }) {
            chooseToken(ETC)
            e {
                click(manageVolume)
                click(addVolume)
            }
            val filePath = System.getProperty("user.dir") + "/src/test/resources/${fileName}.csv"

            val js: JavascriptExecutor = driver as JavascriptExecutor
            val locator = wait {
                untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Upload nomenclature')]"))
            }.to<TextInput>("Employee ''")
            js.executeScript("arguments[0].style.display='block';", locator)
            uploadNomenclature1.sendKeys(filePath)
            Thread.sleep(2000)
            e {
                click(submit)
            }
            assert { elementWithTextPresentedIgnoreCase("Manual signature") }
            openPage<AtmIssuancesPage>(driver)
            chooseToken(ETC)
            e {
                click(redemptionCurrentQueue)
                click(proceed)
                click(approve)
            }
            assert { elementWithTextPresentedIgnoreCase("Manual signature") }

            e {
                click(cancelSubmitPrivateKeyButton)
                click(cancelButton)
                click(proceed)
                click(decline)
            }
            assert { elementWithTextPresentedIgnoreCase("Manual signature") }
            e {
                click(cancelSubmitPrivateKeyButton)
                click(cancelButton)
            }
            assert { urlEndsWith("/issuances/ETT/redemption") }
        }
    }

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_NOT_CONTROLLER)
    @TmsLink("ATMCH-4453")
    @Test
    @DisplayName("ETC. Redemption. Authorized wallet. Not a controller")
    fun etcRedemptionAuthorizedWalletNotController() {
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_NOT_CONTROLLER

        with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
            chooseToken(ETC)
            e {
                click(redemptionCurrentQueue)
                click(proceed)
                click(approve)
            }
            assert {
                elementWithTextPresentedIgnoreCase("NOT ALLOWED")
                elementWithTextPresentedIgnoreCase("You are not allowed to perform this action with smartcontract")
            }

            e {
                click(cancelSubmitPrivateKeyButton)
                click(cancelButton)
                click(proceed)
                click(decline)
            }
            assert {
                elementWithTextPresentedIgnoreCase("NOT ALLOWED")
                elementWithTextPresentedIgnoreCase("You are not allowed to perform this action with smartcontract")
            }

        }
    }

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_ONE)
    @TmsLink("ATMCH-4457")
    @Test
    @DisplayName("Non-ETC. Authorized wallet. Not a controller")
    fun nonEtcAuthorizedWalletNotController() {
        val fileName = "ETC_Nomenclature12"

        val issuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_ONE
        FileHelper.createNomenclature(fileName)

        with(openPage<AtmIssuancesPage>(driver) { submit(issuer) }) {
            chooseToken(ETC)
            e {
                click(manageVolume)
                click(addVolume)
            }
            val filePath = System.getProperty("user.dir") + "/src/test/resources/${fileName}.csv"

            val js: JavascriptExecutor = driver as JavascriptExecutor
            val locator = wait {
                untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Upload nomenclature')]"))
            }.to<TextInput>("Employee ''")
            js.executeScript("arguments[0].style.display='block';", locator)
            uploadNomenclature1.sendKeys(filePath)
            e {
                click(submit)
            }
            assert {
                elementWithTextPresentedIgnoreCase("NOT ALLOWED")
                elementWithTextPresentedIgnoreCase("You are not allowed to perform this action with smartcontract")
            }

            FileHelper.deleteFile("${fileName}.csv")
            openPage<AtmIssuancesPage>(driver)
            chooseToken(ETC)
            e {
                click(redemptionCurrentQueue)
                click(proceed)
                click(approve)
            }
            assert {
                elementWithTextPresentedIgnoreCase("NOT ALLOWED")
                elementWithTextPresentedIgnoreCase("You are not allowed to perform this action with smartcontract")
            }

            e {
                click(cancelSubmitPrivateKeyButton)
                click(cancelButton)
                click(proceed)
                click(decline)
            }
            assert {
                elementWithTextPresentedIgnoreCase("NOT ALLOWED")
                elementWithTextPresentedIgnoreCase("You are not allowed to perform this action with smartcontract")
            }
            e {
                click(cancelSubmitPrivateKeyButton)
                click(cancelButton)
            }
            assert { urlEndsWith("/issuances/ETT/redemption") }
        }
    }

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_NOT_CONTROLLER)
    @TmsLink("ATMCH-4450")
    @Test
    @DisplayName("ETC. Add volume. Authorized wallet. Not a controller")
    fun etcAddVolumeAuthorizedWalletNotController() {
        val fileName = "ETC_Nomenclature5"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_NOT_CONTROLLER
        FileHelper.createNomenclature(fileName)
        with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
            chooseToken(ETC)
            e {
                click(manageVolume)
                click(addVolume)
            }
            val filePath = System.getProperty("user.dir") + "/src/test/resources/${fileName}.csv"

            val js: JavascriptExecutor = driver as JavascriptExecutor
            val locator = wait {
                untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Upload nomenclature')]"))
            }.to<TextInput>("Employee ''")
            js.executeScript("arguments[0].style.display='block';", locator)
            uploadNomenclature1.sendKeys(filePath)
            e {
                click(submit)
            }
            assert {
                elementWithTextPresentedIgnoreCase("NOT ALLOWED")
                elementWithTextPresentedIgnoreCase("You are not allowed to perform this action with smartcontract")
            }
        }
        FileHelper.deleteFile("${fileName}.csv")
    }

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    @TmsLink("ATMCH-4448")
    @Test
    @DisplayName("ETC. Add volume. Authorized wallet. Controller")
    fun etcAddVolumeAuthorizedWalletController() {
        val fileName = "ETC_Nomenclature6"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        FileHelper.createNomenclature(fileName)// генерация номенклатуры с токенами с заданным имеенем файла

        with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
            chooseToken(ETC)
            e {
                click(manageVolume)
                click(addVolume)
            }
            val filePath = System.getProperty("user.dir") + "/src/test/resources/${fileName}.csv"
            // загрузка на старницу сгенерированного файла
            // метод применяется для того чтобы избежать открытия стандартнгого окна выбора файла например в ОС Windows

            val js: JavascriptExecutor = driver as JavascriptExecutor
            val locator = wait {
                untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Upload nomenclature')]"))// кнопка закгруки
            }.to<TextInput>("Employee ''")
            js.executeScript("arguments[0].style.display='block';", locator)
            uploadNomenclature1.sendKeys(filePath)//загрузка происходит сразу по локатору и берет файл из указанного пути который был ранее сгенерирован
            e {
                click(submit)
            }
            assert { elementWithTextPresentedIgnoreCase("Manual signature") }
        }
        FileHelper.deleteFile("${fileName}.csv")//удаление файла
    }

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_THIRD)
    @TmsLink("ATMCH-4430")
    @Test
    @DisplayName("Issuer ETC. Add volume. Nomenclature validating")
    fun issuerETCAddVolumeNomenclatureValidating() {
        val fileName1 = "nomenclature_1.csv"
        val fileName2 = "nomenclature_2.csv"
        val fileName3 = "nomenclature_3.csv"
        val fileName4 = "nomenclature_4.csv"
        val fileName5 = "nomenclature_5.csv"
        val fileName6 = "nomenclature_6.csv"
        val fileName7 = "nomenclature_7.csv"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_THIRD

        with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
            chooseToken(ETC)
            e {
                click(manageVolume)
                click(addVolume)
            }
            checkValuesFromBarList(fileName1, "1620.000", "966.000", "1230.000", "3")
            checkValuesFromBarList(fileName2, "1621.365", "966.000", "1230.000", "3")
            checkValuesFromBarList(fileName3, "1621.365", "966.000", "1230.000", "3")
            checkValuesFromBarList(fileName4, "0.000", "0.000", "0.000", "0")
            checkValuesFromBarList(fileName5, "0.000", "0.000", "0.000", "0")
            checkValuesFromBarList(fileName6, "0.000", "0.000", "0.000", "0")
            checkValuesFromBarList(fileName7, "0.000", "0.000", "0.000", "0")

        }
    }
}