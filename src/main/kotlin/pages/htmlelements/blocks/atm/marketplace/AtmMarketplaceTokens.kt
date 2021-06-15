package pages.htmlelements.blocks.atm.marketplace

import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.TextBlock

@Name("Marketplace token Item")
@FindBy(css = "atm-market-item")
class AtmMarketplaceTokens : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//div[contains(@class,'currency-token-item__token-name')]")
    @Name("Token Name")
    lateinit var tokenName: TextBlock

    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'TOKEN TYPE')]]]]//div[@class='property__wrapper-val']")
    @Name("Token type")
    lateinit var tokenType: TextBlock

    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'UNDERLYING ASSET')]]]]//div[@class='property__wrapper-val']")
    @Name("Underlying asset")
    lateinit var underlyingAsset: TextBlock

    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'TICKER')]]]]//div[@class='property__wrapper-val']")
    @Name("Ticker")
    lateinit var ticker: TextBlock

    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'ISSUER')]]]]//div[@class='property__wrapper-val']")
    @Name("Issuer")
    lateinit var issuer: TextBlock

    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'ISSUER DESCRIPTION')]]]]//div[@class='property__wrapper-val']")
    @Name("Issuer Description")
    lateinit var issuerDescription: TextBlock

    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'BUY')]]]]//div[@class='property__wrapper-val']")
    @Name("Currency Token")
    lateinit var buyValue: AtmAmount

    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'SELL')]]]]//div[@class='property__wrapper-val']")
    @Name("Currency Token")
    lateinit var sellValue: AtmAmount

//    @FindBy(xpath = "//div[div[div[./span[contains(text(), 'DEAL TYPES')]]]]//div[@class='property__wrapper-val']")
//    @Name("Currency Token")
//     lateinit var dealTypes: TextBlock

    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'CHARGED IN')]]]]//div[@class='property__wrapper-val']")
    @Name("Charged In")
    lateinit var chargedIn: TextBlock
//todo улучшит селекторы через ancestor, они не очень то читаемы
    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'FEE RATE')]]]]//div[@class='property__wrapper-val']")
    @Name("Fee Rate")
    lateinit var feeRate: AtmAmount

    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'FLOOR')]]]]//div[@class='property__wrapper-val']")
    @Name("Floor")
    lateinit var floor: AtmAmount

    @FindBy(xpath = ".//div[div[div[./span[contains(text(), 'CAP')]]]]//div[@class='property__wrapper-val']")
    @Name("Cap")
    lateinit var cap: AtmAmount
}