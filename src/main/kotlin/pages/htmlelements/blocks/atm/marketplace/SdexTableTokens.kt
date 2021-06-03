package pages.htmlelements.blocks.atm.marketplace

import org.openqa.selenium.support.FindBy
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button

class SdexTableTokens : SdexTable() {
    // TODO: Можно для сортировки сделать отдельный класс, который будет ее контроллировать
    @FindBy(css = "th.mat-column-id[role=columnheader]")
    @Name("Id Sort")
    lateinit var idSort: Button

    @FindBy(css = "th.mat-column-name[role=columnheader]")
    @Name("Name Sort")
    lateinit var nameSort: Button

    @FindBy(css = "th.mat-column-symbol[role=columnheader]")
    @Name("Symbol Sort")
    lateinit var symbolSort: Button

    @FindBy(css = "th.mat-column-type[role=columnheader]")
    @Name("Type Sort")
    lateinit var typeSort: Button

    @FindBy(css = "th.mat-column-status[role=columnheader]")
    @Name("Status Sort")
    lateinit var statusSort: Button

    @FindBy(css = "th.mat-column-tokenDescription[role=columnheader]")
    @Name("Description Sort")
    lateinit var descriptionSort: Button

    @FindBy(css = "th.mat-column-issuerName[role=columnheader]")
    @Name("IssuerName Sort")
    lateinit var issuerNameSort: Button

    @FindBy(css = "th.mat-column-underlyingAsset[role=columnheader]")
    @Name("UnderlyingAsset Sort")
    lateinit var underlyingAssetSort: Button

    @FindBy(css = "th.mat-column-usdEquivalent[role=columnheader]")
    @Name("Usd Equivalent Sort")
    lateinit var usdEquivalentSort: Button

    @FindBy(css = "th.mat-column-created[role=columnheader]")
    @Name("Created Sort")
    lateinit var createdSort: Button

    @FindBy(css = "th.mat-column-updated[role=columnheader]")
    @Name("Updated Sort")
    lateinit var updatedSort: Button

}