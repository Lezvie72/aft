package pages.htmlelements.blocks.atm.issuances

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import utils.helpers.to

@Name("Issuances Volume Receive Item")
@FindBy(css = "atm-ind-dist-deal-base")
class IssuanceVolumeReceiveItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//div[contains(text(), 'Requested') or contains(text(), 'Total requested')]/ancestor::atm-amount-field//atm-amount")
    @Name("Total requested")
    lateinit var totalRequested: AtmAmount

    @Step("check the status for order")
    fun checkStatus(status: String) {
        val cardWithStatus = wait {
            untilPresented<WebElement>(By.xpath(".//atm-ind-dist-deal-base//nz-tag[contains(text(),'${status.toUpperCase()}')]"))
        }.to<Button>("Card '$status'")
        assert { elementPresented(cardWithStatus) }
        MatcherAssert.assertThat("Order", cardWithStatus.text, Matchers.`is`(status.toUpperCase()))
    }

    val tokenQuantityRequestToReceive
        get() = totalRequested.amount

}