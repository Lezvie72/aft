package pages.htmlelements.elements

import models.CoinType
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name
import utils.helpers.to
import java.math.BigDecimal

@Name("Tokens list")
@FindBy(css = "atm-tokens-list")
class AtmTokenList : BaseBlock<AtmPage>() {

    fun getTokenBalance(equivalent: String, coinType: CoinType): BigDecimal {
        val locator = when (equivalent) {
            "USD" -> ".//atm-token-info//atm-span[contains(text(), '${coinType.tokenName}')]//ancestor::atm-token-info//atm-property-value//atm-amount[2]"
            else -> ".//atm-token-info//atm-span[contains(text(), '${coinType.tokenName}')]//ancestor::atm-token-info//atm-property-value//atm-amount[1]"
        }
        val balance = wait {
            untilPresented<WebElement>(By.xpath(locator))
        }.to<AtmAmount>("Token '$coinType' is not presented")
        return balance.amount
    }
}