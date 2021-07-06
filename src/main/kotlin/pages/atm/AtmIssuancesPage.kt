package pages.atm

import io.qameta.allure.Step
import models.CoinType
import models.CoinType.*
import models.user.classes.DefaultUser
import models.user.classes.MainWallet
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.openqa.selenium.*
import org.openqa.selenium.support.FindBy
import pages.atm.AtmIssuancesPage.LimitType.MAX
import pages.atm.AtmIssuancesPage.LimitType.MIN
import pages.atm.AtmIssuancesPage.OperationType.*
import pages.atm.AtmIssuancesPage.StatusType.APPROVE
import pages.atm.AtmIssuancesPage.StatusType.DECLINE
import pages.core.annotations.Action
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.issuances.*
import pages.htmlelements.elements.AtmAmount
import pages.htmlelements.elements.AtmTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to
import java.io.File
import java.math.BigDecimal


@PageUrl("/issuances")
@PageName("Issuances page")
class AtmIssuancesPage(driver: WebDriver) : AtmPage(driver) {

    enum class LimitType {
        MIN, MAX;
    }

    enum class OperationType {
        SELL, REDEMPTION, RECEIVE, BUYBACK;
    }

    enum class StatusType {
        APPROVE, DECLINE;
    }

    @Name("Current queue")
    @FindBy(xpath = ".//atm-dist-deal//div[contains(text(),'Current queue')]/ancestor::div[2]//button[1]")
    lateinit var requestCurrentQueue: Button

    @Name("Current queue redemption")
    @FindBy(xpath = ".//atm-redemption-deal//div[contains(text(),'Current queue')]/ancestor::div[2]//button[1]")
    lateinit var redemptionCurrentQueue: Button

    @Name("Volume ( 24h ) for Redemption")
    @FindBy(xpath = ".//atm-redemption-deal//div[contains(text(),'Volume ( 24h )')]/ancestor::div[2]//button[1]")
    lateinit var volumeStatisticsButtonForRedemption: Button

    @Name("Volume ( 24h ) for Receive")
    @FindBy(xpath = ".//atm-dist-deal//div[contains(text(),'Volume ( 24h )')]/ancestor::div[2]//button[1]")
    lateinit var volumeStatisticsButtonForReceive: Button

    @Name("Manage volume")
    @FindBy(xpath = ".//span[contains(text(), 'Manage')]/ancestor::button")
    lateinit var manageVolume: Button

    @Name("Add volume")
    @FindBy(xpath = ".//span[contains(text(), 'Add volume')]/ancestor::button")
    lateinit var addVolume: Button

    @Name("Submit")
    @FindBy(xpath = ".//span[contains(text(), 'Submit')]/ancestor::button")
    lateinit var submit: Button

    @Name("Upload nomenclature")
    @FindBy(xpath = ".//nz-form-control//input[@formcontrolname='nomenclature']")
    lateinit var uploadNomenclature1: TextInput

    @Name("Upload document")
    @FindBy(xpath = ".//atm-file-upload//nz-form-control//div//input[@formcontrolname='file']")
    lateinit var uploadDocument: TextInput

    @Name("Add new file")
    @FindBy(xpath = ".//span[contains(text(), 'Add new file')]")
    lateinit var addNewFile: TextInput

    @Name("Redemption IT limits")
    @FindBy(xpath = "//atm-redemption-deal//div[contains(text(),'Order limits')]/ancestor::div[2]//button[1]")
    lateinit var redemptionItLimits: Button

    @Name("Redemption IT limits")
    @FindBy(xpath = "//atm-dist-deal//div[contains(text(),'Order limits')]/ancestor::atm-min-max-field//button")
    lateinit var receiveItLimits: Button

    @Name("Redemption CC limits")
    @FindBy(xpath = "//h3[contains(text(),'Buyback')]/ancestor::div[1]//atm-min-max-field//button")
    lateinit var redemptionCCLimits: Button

    @Name("Sell FT limits")
    @FindBy(xpath = ".//h3[contains(text(),'Sell')]/ancestor::div[1]//atm-min-max-field//button")
    lateinit var sellFTLimits: Button

    @Name("Min limit amount redemption")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='min']//input")
    lateinit var minLimitAmountRedemption: TextInput

    @Name("Max limit amount redemption")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='max']//input")
    lateinit var maxLimitAmountRedemption: TextInput

    @Name("Min limit value")
    @FindBy(xpath = ".//label//span[contains(text(),'Min')]/ancestor::div[1]//atm-amount")
    lateinit var minLimitValue: AtmAmount

    @Name("Max limit value")
    @FindBy(xpath = ".//label//span[contains(text(),'Max')]/ancestor::div[1]//atm-amount")
    lateinit var maxLimitValue: AtmAmount

    @Name("Min limit Receive value from form")
    @FindBy(xpath = ".//atm-dist-deal//div[contains(text(),'Order limits')]/ancestor::div[2]//atm-amount[1]")
    lateinit var minLimitReceiveValue: AtmAmount

    @Name("Max limit Receive value from form")
    @FindBy(xpath = ".//atm-dist-deal//div[contains(text(),'Order limits')]/ancestor::div[2]//atm-amount[2]")
    lateinit var maxLimitReceiveValue: AtmAmount

    @Name("Min limit Receive value from form")
    @FindBy(xpath = ".//atm-redemption-deal//div[contains(text(),'Order limits')]/ancestor::div[2]//atm-amount[1]")
    lateinit var minLimitRedemptionValue: AtmAmount

    @Name("Max limit Receive value from form")
    @FindBy(xpath = ".//atm-redemption-deal//div[contains(text(),'Order limits')]/ancestor::div[2]//atm-amount[2]")
    lateinit var maxLimitRedemptionValue: AtmAmount

    @FindBy(xpath = ".//label[contains(text(), 'Executed amount')]/ancestor::div//atm-amount-input//atm-amount")
    @Name("Executed amount")
    lateinit var executedAmount: AtmAmount

    @Name("Total tokenized weight")
    @FindBy(xpath = "(//atm-bar-items//span[contains(text(),'Total tokenized weight')])[1]/ancestor::atm-property-value[1]//atm-amount")
    lateinit var totalTokenizedWeight: AtmAmount

    @Name("Total bars")
    @FindBy(xpath = "(//atm-bar-items//span[contains(text(),'TOTAL BARS')])[1]/ancestor::atm-property-value//atm-amount")
    lateinit var totalBars: AtmAmount

    @Name("Total fine weight")
    @FindBy(xpath = "(//atm-bar-items//span[contains(text(),'Total fine weight')])[1]/ancestor::atm-property-value[1]//atm-amount")
    lateinit var totalFineWeight: AtmAmount

    @Name("Total gross weight")
    @FindBy(xpath = "(//atm-bar-items//span[contains(text(),'Total gross weight')])[1]/ancestor::atm-property-value[1]//atm-amount")
    lateinit var totalGrossWeight: AtmAmount

    @Name("Total amount")
    @FindBy(xpath = "(//div[contains(text(),'Total amount')])[1]/ancestor::atm-amount-field//div//atm-amount")
    lateinit var totalAmount: AtmAmount

    @Name("Issue list")
    @FindBy(xpath = "(//atm-ind-issue-list)[2]")
    lateinit var issueList: Button

    @Name("Request offers")
    @FindBy(css = "atm-issuance-requests")
    lateinit var requestOffers: AtmTable<IssuanceRequestItem>

    @Name("Redemption offers")
    @FindBy(css = "atm-issuance-requests")
    lateinit var redemptionOffers: AtmTable<IssuanceRedemptionItem>

    @Name("Issuance volume redemption statistics")
    @FindBy(css = "atm-volume-statistic")
    lateinit var issuanceVolumeStatistics: AtmTable<IssuanceVolumeRedemptionItem>

    @Name("Issuance volume receive statistics")
    @FindBy(css = "atm-volume-statistic")
    lateinit var issuanceVolumeReceiveStatistics: AtmTable<IssuanceVolumeReceiveItem>

    @Name("issuance token")
    @FindBy(css = "atm-issuances")
    lateinit var issuanceTokens: AtmTable<IssuanceTokenItem>

    @FindBy(xpath = ".//span[contains(text(), 'Approve')]/ancestor::button")
    @Name("Approve button")
    lateinit var approve: Button

    @FindBy(xpath = ".//span[contains(text(), 'Decline')]/ancestor::button")
    @Name("Decline button")
    lateinit var decline: Button

    @FindBy(xpath = ".//a[contains(text(), 'Cancel')]")
    @Name("Cancel button")
    lateinit var cancelButton: Button

    @FindBy(xpath = ".//span[contains(text(), 'Cancel')]")
    @Name("Cancel")
    lateinit var cancel: Button

    @FindBy(xpath = ".//atm-file-upload[@formcontrolname='newFiles']//span[contains(text(), 'Cancel')]/ancestor::button")
    @Name("Cancel upload new file")
    lateinit var cancelUploadNewFile: Button

    @Name("Amount redemption")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='amount']//input")
    lateinit var amountRedemption: TextInput

    @Name("Save")
    @FindBy(xpath = "//span[contains(text(), 'Save')]")
    lateinit var saveButton: Button

    @Name("Balance in circulation")
    @FindBy(xpath = ".//span[contains(text(), 'IN CIRCULATION')]/ancestor::atm-property-value//atm-amount")
    lateinit var balanceInCirculation: AtmAmount

    @Name("Total supply")
    @FindBy(xpath = ".//span[contains(text(), 'TOTAL SUPPLY')]/ancestor::atm-property-value//atm-amount")
    lateinit var totalSupply: AtmAmount

    @Name("On sale")
    @FindBy(xpath = ".//span[contains(text(), 'ON SALE')]/ancestor::atm-property-value//atm-amount")
    lateinit var onSale: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'Details')]/ancestor::button")
    @Name("Details")
    lateinit var details: Button

    @Name("Blank form for upload documents")
    @FindBy(css = "atm-emission-via-csv-form")
    lateinit var blankFormForUploadDocuments: Button

    @Name("Attachements")
    @FindBy(xpath = ".//atm-token-files")
    lateinit var attachements: Button

    @Name("Attachements from volume")
    @FindBy(xpath = ".//atm-custom-collapse-panel[contains(@class,'custom-collapse-panel attachments')]//atm-collapse-panel")
    lateinit var attachementsFromValume: Button

    @Name("Issuance info")
    @FindBy(xpath = "//atm-ind-detail//div[contains(text(),'Issuance info')]")
    lateinit var issuanceInfo: Button

    @Name("Deleted attachments")
//    @FindBy(xpath = ".//div[contains(text(),'Deleted attachments')]")
    @FindBy(xpath = ".//atm-token-files//div[2]//i")
    lateinit var deletedAttachments: Button

    @Name("Edit attachments")
    @FindBy(xpath = "//atm-token-files//span[contains(text(), 'Edit')]")
    lateinit var editAttachmentsButton: Button

    @Name("Attachment description")
    @FindBy(xpath = "//input[@formcontrolname='description']")
    lateinit var description: TextInput

    @Name("Edit issuance info")
    @FindBy(xpath = "//atm-ind-detail//span[contains(text(), 'Edit')]")
    lateinit var editIssuanceInfoButton: Button

    @Name("Add new parameter")
    @FindBy(xpath = "//atm-ind-info-editor//span[contains(text(),'Add new parameter')]/ancestor::button")
    lateinit var addNewParameter: Button


    @Name("Sign")
    @FindBy(xpath = "//span[contains(text(), 'Sign')]")
    lateinit var signButton: Button

    @Name("Show chat")
    @FindBy(xpath = "//span[contains(text(), 'Show chat')]")
    lateinit var showChat: Button

    @Name("Chat input")
    @FindBy(xpath = "//atm-chat//textarea[@formcontrolname='message']")
    lateinit var chatInput: TextInput

    @Name("Chat button")
    @FindBy(xpath = "(//atm-issuances//atm-collapse-panel)[2]/div[1]")
    lateinit var chatButton: Button

    @Name("Chat send button")
    @FindBy(xpath = "//atm-chat//button")
    lateinit var chatSendButton: Button

    @FindBy(xpath = ".//span[contains(text(), 'Proceed')]/ancestor::button")
    @Name("Proceed button")
    lateinit var proceed: Button

    @Name("Link register wallet")
    @FindBy(xpath = "//a[@href='wallets/new']")
    lateinit var linkRegisterWallet: Button

    @Name("Attachments list")
    @FindBy(xpath = ".//a[contains(@class,'attachments')]")
    lateinit var attachmentsList: Button

    fun <T> retry(repeatCount: Int, body: () -> T): T {
        repeat(repeatCount) {
            try {
                return body()
            } catch (e: Exception) {
                if (it == repeatCount) {
                    throw e
                }
            }
        }
        throw Exception("First argument incorrect type or value")
    }


    fun getInCirculation(coinType: CoinType): BigDecimal =
        getBalanceInCirculationForToken(coinType).toBigDecimal()

    @Step("Get user balance")
    @Action("Get user balance")
    fun getBalanceInCirculationForToken(coinType: CoinType): String {
        e {
            chooseToken(coinType)
            wait(100L) {
                until("Couldn't load in circulation balance for token") {
                    check { isElementPresented(balanceInCirculation) }
                }
            }
        }
        return balanceInCirculation.amount.toString()
    }

    @Step("Get user Balance Supply,Circulation And Sale")
    @Action("Get user Balance Supply, Circulation And Sale")
    fun getBalanceSupplyCirculationAndSale(coinType: CoinType): Triple<BigDecimal, BigDecimal, BigDecimal> {
        e {
            chooseToken(coinType)
            retry(3) {
                wait(100L) {
                    until("Couldn't load in circulation balance for token") {
                        check { isElementPresented(manageVolume) || isElementPresented(issueList) }
                    }
                }
            }
        }
        return Triple(balanceInCirculation.amount, totalSupply.amount, onSale.amount)
    }

    @Step("User choose token {coinType.tokenName}")
    @Action("choose token")
    fun chooseToken(coinType: CoinType) {
        val tokenLocator =
            By.xpath(".//atm-issuer-token//div[contains(@class, 'issuer-token__symbol')][text()='${coinType.tokenName}']")

        val tokenButton = try {
            wait {
                untilPresented<Button>(tokenLocator, "${coinType.tokenName} button")
            }
        } catch (e: TimeoutException) {
            error("Couldn't find token with name ${coinType.tokenName}. Expected token name to be equal to name in Admin Token Panel")
        }
        e {
            click(tokenButton)
        }
    }

    @Step("User find and open requested offer")
    fun changeStatusForOfferWithAmount(
        coinType: CoinType, amount: BigDecimal, statusType: StatusType, user: DefaultUser, wallet: MainWallet
    ) {
        e {
            chooseToken(coinType)
//            click(requestCurrentQueue)
            val currentQueue = wait {
                untilPresented<WebElement>(By.xpath(".//atm-dist-deal//div[contains(text(),'Current queue')]/ancestor::div[2]//button[1]"))
            }.to<Button>("requestCurrentQueue")

            click(currentQueue)
        }
        val myOffer = requestOffers.find {
            it.totalRequestedAmount == amount
        } ?: error("Can't find offer with unit price '$amount'")
        myOffer.clickProceedButton()
        e {
            when (statusType) {
                APPROVE -> click(approve)
                DECLINE -> click(decline)
            }
        }
        signAndSubmitMessage(user, wallet.secretKey)
    }

    @Step("User find and open redemption offer")
    fun findRedemptionOffers(
        coinType: CoinType,
        amount: BigDecimal,
        redemptionAmount: BigDecimal,
        user: DefaultUser,
        wallet: MainWallet,
        statusType: StatusType
    ) {
        e {
            chooseToken(coinType)

            val currentQueue = wait {
                untilPresented<WebElement>(By.xpath(".//atm-redemption-deal//div[contains(text(),'Current queue')]/ancestor::div[2]//button[1]"))
            }.to<Button>("requestCurrentQueue")

            click(currentQueue)
        }
        val myOffer = redemptionOffers.find {
            it.requestAmount == amount
        } ?: error("Can't find offer with unit price '$amount'")
        myOffer.clickProceedButton()
        e {
            click(amountRedemption)
            deleteData(amountRedemption)
//            sendKeys(amountRedemption, ".")
            sendKeys(amountRedemption, redemptionAmount.toString())
            Thread.sleep(1000)
            e {
                when (statusType) {
                    APPROVE -> click(approve)
                    DECLINE -> click(decline)
                }
            }
        }
        signAndSubmitMessage(user, wallet.secretKey)
    }

    @Step("User find and approve or decline redemption offer for ETC token")
    fun findRedemptionOffersForEtcTokenAndSetStatus(
        amount: BigDecimal,
        user: DefaultUser,
        wallet: MainWallet,
        statusType: StatusType
    ) {
        e {
            chooseToken(ETC)
            click(redemptionCurrentQueue)
            Thread.sleep(10000)
        }
        val myOffer = redemptionOffers.find {
            it.tokenQuantityRequestToRedeem == amount
        } ?: error("Can't find offer with unit price '$amount'")
        myOffer.clickProceedButton()
        e {
            when (statusType) {
                APPROVE -> click(approve)
                DECLINE -> click(decline)
            }
        }
        signAndSubmitMessage(user, wallet.secretKey)
    }

    @Step("Upload File")
    fun uploadNomenclature(e: WebElement, fileName: String, user: DefaultUser, wallet: MainWallet) {
        val filePath = System.getProperty("user.dir") + "/src/test/resources/${fileName}"

        val js: JavascriptExecutor = driver as JavascriptExecutor
        val locator = wait {
            untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Upload nomenclature')]"))
        }.to<TextInput>("Employee ''")
        js.executeScript("arguments[0].style.display='block';", locator)
        e.sendKeys(filePath)
        e {
            click(submit)
        }
        signAndSubmitMessage(user, wallet.secretKey)

    }

    @Step("Upload nomenclature without submit")
    fun uploadNomenclatureWithoutSubmit(e: WebElement, fileName: String) {
        val filePath = System.getProperty("user.dir") + "/src/test/resources/${fileName}"

        chooseToken(ETC)
        e {
            click(manageVolume)
            click(addVolume)
        }

        val js: JavascriptExecutor = driver as JavascriptExecutor
        val locator = wait {
            untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Upload nomenclature')]"))
        }.to<TextInput>("Employee ''")
        js.executeScript("arguments[0].style.display='block';", locator)
        e.sendKeys(filePath)
    }

    @Step("Upload document")
    fun uploadDocument(e: WebElement, fileName: String, user: DefaultUser, wallet: MainWallet) {
        val filePath = System.getProperty("user.dir") + "/src/test/resources/${fileName}"

        val js: JavascriptExecutor = driver as JavascriptExecutor
        val locator = wait {
            untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Add new file')]"))
        }.to<TextInput>("Employee ''")
        js.executeScript("arguments[0].style.display='block';", locator)
        e.sendKeys(filePath)
        e {
            click(signButton)
        }
        signAndSubmitMessage(user, wallet.secretKey)

    }

    @Step("Chek download file")
    fun checkDownloadFile(fileName: String): Boolean {
        val folder = File(System.getProperty("user.dir"))
        val listOfFiles: Array<File> = folder.listFiles()
        var found = false
        for (listOfFile in listOfFiles) {
            if (listOfFile.isFile) {
                if (listOfFile.name.matches(Regex(fileName))) {
                    found = true
                }
            }
        }
        return found
    }

    @Step("Delete document and check this document")
    fun deleteDocumentAndCheckThisDocument(
        coinType: CoinType,
        fileName: String,
        user: DefaultUser,
        wallet: MainWallet
    ) {
        chooseToken(coinType)
        e {
            click(attachements)
            click(editAttachmentsButton)
        }
        val document = wait {
            untilPresented<WebElement>(By.xpath(".//span[contains(text(), '${fileName}')]/ancestor::div[1]//button"))
        }.to<Button>("Card '$fileName'")

        e { click(document) }
        signAndSubmitMessage(user, wallet.secretKey)

        e {
            click(deletedAttachments)
        }
        val deletedDocument = wait {
            untilPresented<WebElement>(By.xpath(".//a[contains(@class,'attachments__item')]//div[contains(text(), '${fileName}')]"))
        }.to<Button>("Card '$fileName'")

        assert { elementPresented(deletedDocument) }
    }

    @Step("check the message from chat")
    fun checkTheMessageFromChat(message: String) {
        val textMessage = wait {
            untilPresented<WebElement>(By.xpath(".//atm-chat-message//atm-span[contains(text(),'${message}')]"))
        }.to<Button>("Card '$message'")
        assert { elementPresented(textMessage) }
        assertThat("Order", textMessage.text, `is`(message))
    }

    @Step("User find for token in redemption volume statistics")
    fun findOfferForTokenInRedemptionVolumeStatisticsAndCheckStatus(
        coinType: CoinType, expectedStatus: String, amount: BigDecimal
    ) {
        e {
            chooseToken(coinType)

            val volumeRedemption = wait {
                untilPresented<WebElement>(By.xpath(".//atm-redemption-deal//div[contains(text(),'Volume ( 24h )')]/ancestor::div[2]//button[1]"))
            }.to<Button>("volumeRedemption")

            click(volumeRedemption)
        }
        val order = issuanceVolumeStatistics.find {
            it.tokenQuantityRequestToRedeem == amount
        } ?: error("Can't find order with amount '$amount'")
        assertThat(
            "Order with amount $amount should be is not null value",
            order,
            notNullValue()
        )
        order.checkStatus(expectedStatus)
    }

    @Step("User find for token in receive volume statistics")
    fun findOfferForTokenInReceiveVolumeStatisticsAndCheckStatus(
        coinType: CoinType, expectedStatus: String, amount: BigDecimal
    ) {
        e {
            chooseToken(coinType)

            val volumeReceive = wait {
                untilPresented<WebElement>(By.xpath(".//atm-dist-deal//div[contains(text(),'Volume ( 24h )')]/ancestor::div[2]//button[1]"))
            }.to<Button>("volumeReceive")

            click(volumeReceive)
        }
        wait {
            until("wait for loading page after refresh", 30) {
                check {
                    isElementPresented(issuanceVolumeReceiveStatistics)
                }
            }
        }
        val order = issuanceVolumeReceiveStatistics.find {
            it.tokenQuantityRequestToReceive == amount
        } ?: error("Can't find order with amount '$amount'")
        assertThat(
            "Order with amount $amount should be is not null value",
            order,
            notNullValue()
        )
        order.checkStatus(expectedStatus)
    }


    @Step("User find and open requested offer")
    fun findOffersForTokenById(
        coinType: CoinType, id: String, statusType: StatusType, user: DefaultUser, wallet: MainWallet
    ) {
        e {
            chooseToken(coinType)
            click(requestCurrentQueue)
        }
        val myOffer = requestOffers.find {
            it.requestedId.text == id
        } ?: error("Can't find offer with id '$id'")
        myOffer.clickProceedButton()
        e {
            when (statusType) {
                APPROVE -> click(approve)
                DECLINE -> click(decline)
            }
        }
        signAndSubmitMessage(user, wallet.secretKey)
    }

    @Step("User find and open requested offer")
    fun findOffersForTokenByAmount(
        coinType: CoinType, amount: BigDecimal, statusType: StatusType, user: DefaultUser, wallet: MainWallet
    ) {
        e {
            chooseToken(coinType)
            click(requestCurrentQueue)
        }
        val myOffer = requestOffers.find {
            it.amountToSend == amount
        } ?: error("Can't find offer with amount '$amount'")
        myOffer.clickProceedButton()
        e {
            when (statusType) {
                APPROVE -> click(approve)
                DECLINE -> click(decline)
            }
        }
        signAndSubmitMessage(user, wallet.secretKey)
    }

    @Step("User change limit of redemption")
    fun changeLimitAmount(
        coinType: CoinType,
        operationType: OperationType,
        limitType: LimitType,
        limitAmount: String,
        user: DefaultUser,
        wallet: MainWallet
    ): Pair<BigDecimal, BigDecimal> {

        chooseToken(coinType)
        //TODO проработать более удобную и понятное
        e {
            when (operationType to coinType) {

                REDEMPTION to CC -> click(redemptionCCLimits)
                REDEMPTION to IT -> click(redemptionItLimits)
                RECEIVE to IT -> click(receiveItLimits)
                SELL to FT -> click(sellFTLimits)
                SELL to CC -> click(sellFTLimits)
                SELL to VT -> click(sellFTLimits)

            }
        }
        val minBefore = minLimitValue.amount
        val maxBefore = maxLimitValue.amount
        e {
            when (limitType) {
                MIN -> deleteData(minLimitAmountRedemption).also {
                    sendKeys(minLimitAmountRedemption, "0")
                    sendKeys(minLimitAmountRedemption, limitAmount)
                    Thread.sleep(1000)
                }
                MAX -> deleteData(maxLimitAmountRedemption).also {
                    sendKeys(minLimitAmountRedemption, "0")
                    sendKeys(maxLimitAmountRedemption, limitAmount)
                    Thread.sleep(1000)
                }
            }
            click(saveButton)
        }
        signAndSubmitMessage(user, wallet.secretKey)
        return minBefore to maxBefore
    }

    fun findNewParameterAndCheckValue(name: String, value: String) {
        chooseToken(IT)
        e {
            click(issuanceInfo)
            click(editIssuanceInfoButton)
            click(addNewParameter)
        }
        val newParamName =
            findElements(By.xpath("//label[contains(text(),'Parameter name')]/ancestor::nz-form-control//input"))
        val newParamValue =
            findElements(By.xpath("//label[contains(text(),'Parameter value')]/ancestor::nz-form-control//input"))
        e {
            sendKeys(newParamName.last(), name)
            sendKeys(newParamName.last(), name)
        }
    }

    @Step("Issuer upload nomenclature and check the value of weight bars from barlist")
    fun checkValuesFromBarList(
        fileName: String,
        fineWeight: String,
        grossWeight: String,
        tokenizedWeight: String,
        barNoVal: String
    ) {
        val filePath = System.getProperty("user.dir") + "/src/test/resources/${fileName}"

        val js: JavascriptExecutor = driver as JavascriptExecutor
        val locator = wait {
            untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Upload nomenclature')]"))
        }.to<TextInput>("Employee ''")
        js.executeScript("arguments[0].style.display='block';", locator)
        uploadNomenclature1.sendKeys(filePath)
        //TODO не понятно к чему привязать ожидание прогрузки страницы после загрузки документов,
        // так как загружаются не только корректные файлы, но и по итогу пустые
        Thread.sleep(2000)
//        val barNoSize1 = findElements(
//            By.xpath("(//atm-bar-items//atm-bars-list)[1]//div[contains(@class,'property__key-name ng-star-inserted')]//span")
//        ).size
        val barNoVal1 = totalBars.amount.toInt()

        val totalFineWeight1 = totalFineWeight.amount
        val totalGrossWeight1 = totalGrossWeight.amount
        val totalTokenizedWeight1 = totalTokenizedWeight.amount
        val totalAmount1 = totalAmount.amount

        assertThat(
            "Expected totalFineWeight value",
            totalFineWeight1,
            Matchers.closeTo(fineWeight.toBigDecimal(), BigDecimal("0.01"))
        )

        assertThat(
            "Expected totalGrossWeight value",
            totalGrossWeight1,
            Matchers.closeTo(grossWeight.toBigDecimal(), BigDecimal("0.01"))
        )

        assertThat(
            "Expected  totalTokenizedWeight value",
            totalTokenizedWeight1,
            Matchers.closeTo(tokenizedWeight.toBigDecimal(), BigDecimal("0.01"))
        )

        assertThat(
            "Expected totalAmount value",
            totalAmount1,
            Matchers.closeTo(
                (totalTokenizedWeight1.toString() + "00000").toBigDecimal() * BigDecimal(1000),
                BigDecimal("0.01")
            )
        )

        assertThat(
            "barNoSize",
            barNoVal.toInt(),
            Matchers.equalTo(barNoVal1)
        )

        driver.navigate().refresh()
        e { click(addVolume) }
    }

}