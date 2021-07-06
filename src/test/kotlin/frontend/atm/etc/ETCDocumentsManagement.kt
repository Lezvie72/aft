package frontend.atm.etc

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.CoinType.ETC
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import pages.atm.AtmIssuancesPage
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to

@Tags(Tag(TagNames.Epic.ETC.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Feature("ETC")
@Story("ETC Documents Management")
class ETCDocumentsManagement : BaseTest() {
    private val fileName = "ETCDocument.png"

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
                assert {
                    elementPresented(submit)
                    elementPresented(cancel)
                    elementPresented(addNewFile)
                }
                val filePath = System.getProperty("user.dir") + "/src/test/resources/$fileName"

                val js: JavascriptExecutor = driver as JavascriptExecutor
                val locator = wait {
                    untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Add new file')]"))
                }.to<TextInput>("Employee ''")
                js.executeScript("arguments[0].style.display='block';", locator)
                uploadDocument.sendKeys(filePath)

                e {
                    click(cancelUploadNewFile)
                }
                assert {
                    elementWithTextNotPresented(fileName)
                }
                uploadDocument(uploadDocument, fileName, etcIssuer, etcWallet)

                assert {
                    elementContainingTextPresented(fileName)
                }
            }
        }

        step("User go to Issuance, Removing document and check this document") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                deleteDocumentAndCheckThisDocument(ETC, fileName, etcIssuer, etcWallet)
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_THIRD)
    @TmsLink("ATMCH-4419")
    @Test
    @DisplayName("ETC. Downloading the document")
    fun downloadingTheDocument() {
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_THIRD
        val etcWallet = etcIssuer.walletList[0]

//        Assumptions.assumeTrue(
//            setupTve.contains(preconditionTest),
//            "Check completed preconditions - completed test $preconditionTest"
//        )

        step("User go to Issuance and Upload document") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                chooseToken(ETC)
                e {
                    click(attachements)
                    click(editAttachmentsButton)
                    uploadDocument(uploadDocument, fileName, etcIssuer, etcWallet)
                    click(cancel)

                    wait {
                        untilPresented<WebElement>(By.xpath("//div[contains(text(), '$fileName')]"))
                    }.to<Button>("Document for downloading").let { click(it) }
                    Thread.sleep(4000L)

                    val isObjectNotFoundInStorage: Boolean =
                        check { isElementContainingTextPresented("No such object", 10L) }
                    val isDocumentDownloaded: Boolean = check { urlMatches(".*/$fileName$") }

                    step("User go to Issuance, Removing document and check this document") {
                        with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                            deleteDocumentAndCheckThisDocument(ETC, fileName, etcIssuer, etcWallet)
                        }
                    }

                    assertThat(
                        "File $fileName not found", isDocumentDownloaded || isObjectNotFoundInStorage
                    )
                }
            }
        }
    }

    @TmsLink("ATMCH-5185")
    @Test
    @DisplayName("Documents for tokens. Edit descriptions of documents")
    fun editDescriptionDocuments() {
        val user = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_2FA
        val randomText = RandomStringUtils.randomAlphabetic(4)
        with(openPage<AtmIssuancesPage>(driver) { submit(user) }) {
            e {
                chooseToken(ETC)
                click(attachements)
                click(editAttachmentsButton)
                description.clear()
                sendKeys(description, randomText)
            }
            assert {
                elementPresented(submit)
                elementPresented(cancel)
            }
            e{
                click(submit)
            }
            assert {
                elementContainingTextPresented(randomText)
            }
        }

    }
}

