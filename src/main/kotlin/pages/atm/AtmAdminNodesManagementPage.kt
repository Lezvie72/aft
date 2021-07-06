package pages.atm

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmAdminSelect
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@PageUrl("/nodes-management")
class AtmAdminNodesManagementPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val REQUEST_ID = "Request ID"
        const val WALLET_ID = "Wallet ID"
        const val NODE_ID = "Node ID"
        const val ACCOUNT_OWNER = "Account owner"
        const val STATUS = "Status"
        const val AMOUNT = "Amount"
        const val NOTE = "Note"
        const val COMPANY_NAME = "Company name"
        const val NODE_TYPE = "Node type"
        const val NODE_STATUS = "Node status"
    }

    enum class NodeType {
        ENDORSER, ORDERER;
    }

    @Name("Nodes management table")
    @FindBy(css = "sdex-nodes-management")
    lateinit var nodesManagementTable: SdexTable

    @Name("Cashout dialog")
    @FindBy(xpath = "//sdex-info-cashout-dialog")
    lateinit var cashOutDialog: Button

    @Name("Edit")
    @FindBy(xpath = "//span[contains(text(),'EDIT')]")
    lateinit var edit: Button

    @Name("Save")
    @FindBy(xpath = "//span[contains(text(),'SAVE')]")
    lateinit var save: Button

    @Name("Cancel")
    @FindBy(xpath = "//span[contains(text(),'CANCEL')]")
    lateinit var cancel: Button

    @Name("Create date from")
    @FindBy(xpath = "//input[@formcontrolname='fromdate']")
    lateinit var createDateFrom: TextInput

    @Name("Create date to")
    @FindBy(xpath = "//input[@formcontrolname='todate']")
    lateinit var createDateTo: TextInput

    @Name("Update date to")
    @FindBy(xpath = "//input[@formcontrolname='updateto']")
    lateinit var updateDateTo: TextInput

    @Name("Update date from")
    @FindBy(xpath = "//input[@formcontrolname='updatefrom']")
    lateinit var updateDateFrom: TextInput

    @Name("Status")
    @FindBy(xpath = "//mat-select[@formcontrolname='nodestatus']")
    lateinit var statusSelect: AtmAdminSelect

    @Name("Stake")
    @FindBy(xpath = "//mat-checkbox[@formcontrolname='stake']")
    lateinit var stake: CheckBox

    @Name("Certificate Issued")
    @FindBy(xpath = "//mat-checkbox[@formcontrolname='certificateIssued']")
    lateinit var certificateIssued: CheckBox

    @Name("Successfully tested")
//    @FindBy(xpath = "//mat-checkbox[@formcontrolname='tested']")
    @FindBy(xpath = "//span[contains(text(), 'Successfully tested')]/ancestor::label")
    lateinit var successfullyTested: CheckBox

    @Name("Node Activated")
//    @FindBy(xpath = "//mat-checkbox[@formcontrolname='activated']")
    @FindBy(xpath = "//span[contains(text(), 'Node activated')]/ancestor::label")
    lateinit var nodeActivated: CheckBox

    @Name("Search")
    @FindBy(xpath = "//input[@formcontrolname='search']")
    lateinit var search: TextInput

    @Name("Upload certificate")
    @FindBy(xpath = ".//mat-dialog-content//input[@type='file']")
    lateinit var uploadCertificate: TextInput

    @Name("Company name")
    @FindBy(xpath = "//input[@formcontrolname='companyName']")
    lateinit var companyName: TextInput

    @Name("Wallet ID")
    @FindBy(xpath = "//input[@formcontrolname='fromWallet']")
    lateinit var walletId: TextInput

    @Name("Node ID")
    @FindBy(xpath = "//input[@formcontrolname='nodeId']")
    lateinit var nodeId: TextInput


    //endregion


    @Action("add Status To request of Node")
    @Step("Admin add Status To request of Node")
    fun addStatusToNode(nodeType: String, companyName: String, e: WebElement, fileName: String) {
        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        e {
            sendKeys(search, companyName)
            sendKeys(createDateFrom, date)
        }
        val row = nodesManagementTable.find {
            it[NODE_TYPE]?.text == nodeType && it[COMPANY_NAME]?.text == companyName
        }?.get(NODE_TYPE)?.to<Button>("Ticker symbol $nodeType")
            ?: error("Row with Ticker symbol $nodeType not found in table")
        e {
            click(row)
            click(edit)
        }
        uploadDocument(e, fileName)
    }

    @Action("find and open request of Node")
    @Step("Admin find and open request of Node")
    fun findAndOpenRequestOfNode(nodeType: NodeType, companyName: String, nodeStatus: String) {
        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        e {
            deleteData(search)
            sendKeys(search, companyName)
            deleteData(createDateFrom)
            sendKeys(createDateFrom, date)
        }
        val row = nodesManagementTable.find {
            it[NODE_TYPE]?.text == nodeType.name && it[COMPANY_NAME]?.text == companyName && it[NODE_STATUS]?.text == nodeStatus
        }?.get(NODE_TYPE)?.to<Button>("Ticker symbol $nodeType")
            ?: error("Row with Ticker symbol $nodeType and $nodeStatus not found in table")
        e {
            click(row)
            click(edit)
        }
    }

    @Action("find and check status request of Node")
    @Step("Admin find and and check status request of Node")
    fun findRequestOfNodeAndCheckStatus(
        nodeType: NodeType,
        companyName: String,
        nodeStatus: String,
        status: String,
        wallet: String,
        node: String
    ) {
        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        e {
            deleteData(search)
            sendKeys(search, companyName)
            deleteData(createDateFrom)
            sendKeys(createDateFrom, date)
            select(statusSelect, status)
        }
        val row = nodesManagementTable.find {
            it[NODE_TYPE]?.text == nodeType.toString()
                    && it[COMPANY_NAME]?.text == companyName
                    && it[WALLET_ID]?.text == wallet
                    && it[NODE_ID]?.text == node
        } ?: error("Row with Ticker symbol $nodeType and $companyName not found in table")
        val status = row[NODE_STATUS]?.text?.toLowerCase()

        MatcherAssert.assertThat(
            "No row found with '$status'",
            status,
            Matchers.hasToString(nodeStatus.toLowerCase())
        )
    }

    @Step("Upload document")
    fun uploadDocument(e: WebElement, fileName: String) {
        val filePath = System.getProperty("user.dir") + "/src/test/resources/${fileName}"

        val js: JavascriptExecutor = driver as JavascriptExecutor
        val locator = wait {
            untilPresented<WebElement>(By.xpath(".//span[contains(text(), 'Upload certificate')]"))
        }.to<TextInput>("Employee ''")
        js.executeScript("arguments[0].style.display='block';", locator)
        e.sendKeys(filePath)
        e {
            click(save)
        }
    }

}