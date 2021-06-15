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

@Name("Issuances Volume Redemption Item")
@FindBy(css = "atm-volume-item")
class IssuanceVolumeRedemptionItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//span[contains(text(), 'Token quantity requested to redeem')]//ancestor::div//atm-amount")
    @Name("Requested amount")
    lateinit var requestedAmount: AtmAmount

    @Step("check the status for order")
    fun checkStatus(status: String) {
        val cardWithStatus = wait {
            untilPresented<WebElement>(By.xpath(".//atm-redemption-deal-base//nz-tag[contains(text(),'${status.toUpperCase()}')]"))
        }.to<Button>("Card '$status'")
        assert { elementPresented(cardWithStatus) }
        MatcherAssert.assertThat("Order", cardWithStatus.text, Matchers.`is`(status.toUpperCase()))
    }

    val tokenQuantityRequestToRedeem
        get() = requestedAmount.amount


}