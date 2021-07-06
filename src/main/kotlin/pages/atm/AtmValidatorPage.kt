package pages.atm

import io.qameta.allure.Step
import models.user.classes.DefaultUser
import models.user.interfaces.SimpleWallet
import org.junit.Assert
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmAmount
import pages.htmlelements.elements.AtmRadio
import pages.htmlelements.elements.AtmSelect
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to

@PageUrl("/validator/history")
@PageName("Validator page")
class AtmValidatorPage(driver: WebDriver) : AtmPage(driver) {

    enum class LimitType {
        MIN, MAX;
    }

    enum class OperationType {
        SELL, REDEMPTION, BUYBACK;
    }

    enum class StatusType {
        APPROVE, DECLINE;
    }

    enum class NodeType {
        ENDORSER, ORDERER;
    }

    @Name("Add node")
    @FindBy(xpath = "//a[contains(text(),'Add node')]")
    lateinit var addNode: Button

    @Name("Node type")
    @FindBy(xpath = "//label//span[contains(text(), 'Node type')]//ancestor::form//nz-select")
    lateinit var nodeType: AtmSelect

    @Name("Staking wallet")
    @FindBy(xpath = "//label//span[contains(text(), 'Staking wallet')]//ancestor::form//nz-select")
    lateinit var stakingWallet: AtmSelect

    @Name("Stake step")
    @FindBy(xpath = "//div[text() ='Stake']//ancestor::nz-step")
    lateinit var stakeStep: AtmRadio

    @Name("Submit")
    @FindBy(xpath = "//button//span[contains(text(), 'Submit')]")
    lateinit var submit: Button

    @Name("Cancel")
    @FindBy(xpath = "//button//span[contains(text(), 'Cancel')]")
    lateinit var cancel: Button

    @Name("Node steps")
    @FindBy(xpath = "//nz-steps")
    lateinit var nodeSteps: Button

    @Name("Ok")
    @FindBy(xpath = "//span[contains(text(), 'Ok')]")
    lateinit var ok: Button

    @Name("Get reward")
    @FindBy(xpath = "//span[contains(text(), 'GET REWARD')]")
    lateinit var getReward: Button

    @Name("Node details")
    @FindBy(xpath = "//a[contains(text(),'Node details')]")
    lateinit var nodeDetails: Button

    @Name("Node certificate details")
    @FindBy(xpath = "//atm-node-details//div[contains(@class,'node-details__message')]")
    lateinit var nodeCertificateDetails: TextInput

    @Name("Download certificate link")
    @FindBy(xpath = "//atm-node-details//a[contains(@class,'node-details__btn-link')]//atm-span[contains(text(),'Download certificate')]")
    lateinit var downloadCertificateLink: Button

    @Name("Available balance")
    @FindBy(xpath = "//span[contains(text(), 'AVAILABLE BALANCE')]/ancestor::atm-property-value//atm-amount")
    lateinit var availableBalance: AtmAmount

//    @Name("Node details")
//    @FindBy(xpath = "//a[contains(text(),'Node details')]")
//    lateinit var nodeDetails: Button

    @Action("find and open active node")
    @Step("User find and open active node")
    fun findActiveNode() {
        val cardWithStatus = wait {
            untilPresented<WebElement>(By.xpath(".//atm-validator-current-nodes//atm-validator-card//nz-tag[contains(@class,'ant-tag-success')][contains(text(),'Active')]//ancestor::atm-validator-card//button"))
        }.to<Button>("Card in statusActive")
        e {
            click(cardWithStatus)
            click(nodeDetails)
        }
    }

    @Action("find and open awaiting node")
    @Step("User find and open awaiting node")
    fun findAwaitingNode() {
        val cardWithStatus = wait {
            untilPresented<WebElement>(By.xpath(".//atm-validator-current-nodes//atm-validator-card//nz-tag[contains(@class,'ant-tag ant-tag-cyan')][contains(text(),'Awaiting')]//ancestor::atm-validator-card//a"))
        }.to<Button>("Card in statusActive")
        e {
            click(cardWithStatus)
        }
    }

    @Step("check node certificate status")
    @Action("User check node certificate status")
    fun checkNodeCertificateStatus(status: String) {
        val nodeCertificateStatus = check {
            isElementPresented(By.xpath(".//atm-node-details//atm-property-value//span[contains(text(),'TESTING')]//ancestor::atm-property-value//div//atm-span[contains(text(),'${status}')]"))
        }
        Assert.assertTrue("Certificate status is '${status}'", nodeCertificateStatus)
    }


    @Step("buy Node")
    @Action("User buy Node")
    fun addNodeValidator(typeNode: NodeType, user: DefaultUser, wallet: SimpleWallet) {
        e {
            click(addNode)
            select(nodeType, typeNode.name)
            click(submit)
            select(stakingWallet, wallet.name)
            click(submit)
            signAndSubmitMessage(user, wallet.secretKey)
            click(ok)
        }
    }


}