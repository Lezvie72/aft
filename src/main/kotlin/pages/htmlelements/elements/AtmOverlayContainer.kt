package pages.htmlelements.elements

import org.openqa.selenium.support.FindBy
import pages.BasePage
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name

// TODO: Оптимизировать
class AtmOverlayContainer : BaseBlock<BasePage>() {

    companion object {
        const val loadTimeoutInSeconds = 30L
    }

    @FindBy(css = "sdex-preloader")
    @Name("Elements container")
    lateinit var preloader: SdexSelect

    @Name("USD Equivalent")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='auto.usdEquivalent']")
    lateinit var usdEquivalent: AtmInput
}