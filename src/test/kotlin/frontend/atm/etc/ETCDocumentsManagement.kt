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
import pages.atm.AtmMarketplacePage
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.Constants
import utils.helpers.*
import java.math.BigDecimal


@Epic("Frontend")
@Feature("ETC")
@Story("ETC Documents Management")
class ETCDocumentsManagement : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_THIRD)
    @TmsLink("ATMCH-4418")
    @Test
    @DisplayName("ETC. Document editing")
    fun etcDocumentEditing() {
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_THIRD
        val etcWallet = etcIssuer.walletList[0]

        step("User go to Issuance and Upload document") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                chooseToken(ETC)
                e {
                    click(attachements)
                    click(editAttachmentsButton)
//                    uploadDocument(uploadDocument, "ETCDocument.png", etcIssuer, etcWallet)
                }
                assert{
                    elementPresented(submit)
                    elementPresented(cancel)
                    elementPresented(addNewFile)
                }
                val filePath = System.getProperty("user.dir") + "/src/test/resources/ETCDocument.png"

                val js: JavascriptExecutor = driver as JavascriptExecutor
                val locator = wait {
                    untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Add new file')]"))
                }.to<TextInput>("Employee ''")
                js.executeScript("arguments[0].style.display='block';", locator)
                uploadDocument.sendKeys(filePath)

                e{
                    click(cancelUploadNewFile)
                }
                assert {
                    elementWithTextNotPresented("ETCDocument.png")
                }
                uploadDocument(uploadDocument, "ETCDocument.png", etcIssuer, etcWallet)

                assert {
                    elementContainingTextPresented("ETCDocument.png")
                }
            }
        }

        step("User go to Issuance, Removing document and check this document") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                deleteDocumentAndCheckThisDocument(ETC, "ETCDocument.png", etcIssuer, etcWallet)
            }
        }
    }
}

