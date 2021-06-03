package pages.htmlelements.blocks.atm.blockchain

import org.openqa.selenium.support.FindBy
import pages.atm.AtmBlockchainExplorerPage
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput

@Name("Blockchain Date Filter Item")
@FindBy(css = "atm-date-filter-form")
class BlockchainTransactionDateFilterItem : BaseBlock<AtmBlockchainExplorerPage>() {

    //region TEXT CONSTANTS

    companion object {
        const val TEXT_FILTER_BY_TX_DATE = "Filter by Tx Date"
        const val TEXT_WITHIN_SEARCH_RESULT = "within Search results"
        const val TEXT_DATE_FROM = "Date from"
        const val TEXT_DATE_TO = "Date to"
    }

    //endregion


    //region ELEMENTS

    @Name("Filter by tx date header")
    @FindBy(xpath = ".//h3")
    lateinit var filterByTxDateHeaderLabel: TextBlock

    @Name("Within search results label")
    @FindBy(xpath = ".//p")
    lateinit var withinSearchResultsLabel: TextBlock

    @Name("Date from label")
    @FindBy(xpath = ".//nz-form-item[1]//label")
    lateinit var dateFromLabel: TextBlock

    @Name("Date from label")
    @FindBy(xpath = ".//nz-form-item[1]//label")
    lateinit var dateFromInput: TextInput

    @Name("Date to label")
    @FindBy(xpath = ".//nz-form-item[2]//label")
    lateinit var dateToLabel: TextBlock

    @Name("Date to label")
    @FindBy(xpath = ".//nz-form-item[2]//label")
    lateinit var dateToInput: TextInput

    @Name("Date to label")
    @FindBy(xpath = ".//button[1]")
    lateinit var applyButton: Button

    @Name("Date to label")
    @FindBy(xpath = ".//button[2]")
    lateinit var resetButton: Button

    //endregion


    //region ACTIONS

    //endregion


    //region ASSERTS

    fun assertTransactionDateFilterIsPresented() {

        assert {
            elementContainsTextWithIgnoreCase(filterByTxDateHeaderLabel, TEXT_FILTER_BY_TX_DATE)

            elementContainsTextWithIgnoreCase(withinSearchResultsLabel, TEXT_WITHIN_SEARCH_RESULT)

            elementContainsTextWithIgnoreCase(dateFromLabel, TEXT_DATE_FROM)
            elementPresented(dateFromInput)
            elementContainsTextWithIgnoreCase(dateToLabel, TEXT_DATE_TO)
            elementPresented(dateToInput)
            elementPresented(applyButton)
            elementPresented(resetButton)
        }

    }

    //endregion
}