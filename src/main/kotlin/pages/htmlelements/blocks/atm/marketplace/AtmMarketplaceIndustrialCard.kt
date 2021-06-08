package pages.htmlelements.blocks.atm.marketplace

import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.TextBlock

@Name("Marketplace industrial card in overview")
@FindBy(xpath = "//atm-market-item")
class AtmMarketplaceIndustrialCard : BaseBlock<AtmPage>() {
    @FindBy(xpath = ".//div[contains(@class,'token-name')]")
    @Name("Token Name")
    lateinit var tokenName: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'TOKEN TYPE')]//ancestor::atm-property-value//div[@class='property__wrapper-val']")
    @Name("Token type")
    lateinit var tokenType: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'UNDERLYING ASSET')]//ancestor::atm-property-value//div[@class='property__wrapper-val']")
    @Name("Token type")
    lateinit var underlyingAsset: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'ISSUER')]//ancestor::atm-property-value//div[@class='property__wrapper-val']")
    @Name("Token type")
    lateinit var issuer: TextBlock
}