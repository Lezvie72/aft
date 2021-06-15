package pages.htmlelements.elements

import io.qameta.allure.Step
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.BasePage
import pages.core.annotations.Action
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.Table
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.helpers.to

open class SdexTable : BaseBlock<BasePage>() {

    companion object {
        const val loadTimeoutInSeconds = 30L
    }

    @FindBy(css = "button[aria-label='Previous page']")
    @Name("Prev page")
    lateinit var prevPageButton: Button

    @FindBy(css = "button[aria-label='Next page']")
    @Name("Next page")
    lateinit var nextPageButton: Button

    @FindBy(css = "sdex-select[aria-label='rows']")
    @Name("Rows")
    lateinit var rowsSelect: SdexSelect

    @FindBy(css = "sdex-paginator .sdex-page-numbers")
    @Name("Pages")
    lateinit var pagesTextBlock: TextBlock

    @FindBy(css = "table")
    lateinit var table: Table

    @FindBy(css = "sdex-empty-state, .no-records")
    lateinit var emptyTextBlock: TextBlock

    @FindBy(css = "table tbody")
    lateinit var emptyTextBlockATM: TextBlock

    @FindBy(css = "sdex-loader")
    lateinit var loader: TextBlock

    private fun waitUntilTableIsLoaded() = wait(loadTimeoutInSeconds) {
        until("Couldn't load table in $loadTimeoutInSeconds seconds", loadTimeoutInSeconds) {
            check {
                try {
                    (table.rows.size > 0 || isElementPresented(emptyTextBlock) || isElementPresented(emptyTextBlockATM))
                } catch (e: StaleElementReferenceException) {
                    false
                }
            }
        }
    }

    fun waitUntilReady() {
        try {
            waitUntilTableIsLoaded()
        } catch (e: TimeoutException) {
            page.driver.navigate().refresh()
            waitUntilTableIsLoaded()
        }
    }


    //todo: delete comments
//    fun getTotalPageCount(): Int {
//        val result = check {
//            pagesTextBlock.ifPresented {
//                pagesTextBlock.text.split("/")[1].trim().toInt()
//            }
//        }
//        return result ?: -1
//    }

//    fun getCurrentPageIndex(): Int {
//        val result = check {
//            pagesTextBlock.ifPresented {
//                pagesTextBlock.text.split("/")[0].trim().toInt()
//            }
//        }
//        return result ?: 0
//    }

//    fun openPage(index: Int) {
//        val currentPageIndex = openFirstPage()
//        if (currentPageIndex != index) {
//            val totalPagesCount = getTotalPageCount()
//            val elementToClick = if (currentPageIndex > index) prevPageButton else nextPageButton
//            val destinationIndex = if (index > totalPagesCount) totalPagesCount else index
//            val diff = (destinationIndex - currentPageIndex).absoluteValue
//            repeat(diff) {
//                e {
//                    click(elementToClick)
//                }
//            }
//        }
//    }

    fun nextPage() {
        if (hasNextPage()) {
            e { click(nextPageButton) }
            Thread.sleep(3_000)
        }
    }

    fun prevPage() {
        if (hasPrevPage()) {
            e { click(prevPageButton) }
            Thread.sleep(3_000)
        }
    }

    fun hasNextPage(): Boolean {
        return check { isElementPresented(nextPageButton) && isElementEnabled(nextPageButton) }
    }

    fun hasPrevPage(): Boolean {
        return check { isElementPresented(prevPageButton) && isElementEnabled(prevPageButton) }
    }

    fun getRowsMappedToHeadingFromCurrentPage(): List<Map<String, WebElement>> {
        waitUntilReady()
        return table.rowsMappedToHeadings
    }

    fun getRowsMappedToHeadingFromCurrentPage(vararg headings: String): List<Map<String, WebElement>> {
        waitUntilReady()
        return table.getRowsMappedToHeadings(headings.toList())
    }

    fun openFirstPage() {
        while (hasPrevPage()) {
            prevPage()
        }
    }

    fun getRowsMappedToHeadingFromAllPagesAsString(headings: List<String>? = null): List<Map<String, String>> {
        waitUntilReady()
        val mapper: (Map<String, WebElement>) -> Map<String, String> = { map ->
            map.mapValues {
                it.value.text
            }
        }
        openFirstPage()
        val result: MutableList<Map<String, String>> = mutableListOf()
        while (true) {
            val rows = headings?.let {
                table.getRowsMappedToHeadings(it)
            } ?: table.rowsMappedToHeadings
            result.addAll(rows.map(mapper))
            if (hasNextPage()) {
                nextPage()
            } else {
                return result
            }
        }
    }

    fun setRowsPerPage(rowsPerPage: Int) {
        waitUntilReady()
        check {
            rowsSelect.ifPresented {
                e {
                    select(it, "$rowsPerPage rows")
                }
            }
        }
    }

    fun find(predicate: (Map<String, WebElement>) -> Boolean): Map<String, WebElement>? {
        openFirstPage()
        var index = 0
        val repeatCount = 100
        repeat(repeatCount) {
            val result = getRowsMappedToHeadingFromCurrentPage().find(predicate)
            if (result != null) return result
            if (hasNextPage()) {
                nextPage()
                index++
            } else {
                return null
            }
        }
        return null
    }

    //sort order - ASC, DESC, OFF
    @Step("User sorts of {column} by {sortOrder}")
    @Action("sort by")
    fun sortBy(column: String, sortOrder: String) {
        val th = table.headings.find {
            it.text == column
        }?.to<Button>(column) ?: error("No column $column found")
        val ariaSort = when (sortOrder) {
            "ASC" -> "ascending"
            "DESC" -> "descending"
            else -> null
        }
        e {
            until("Couldn't set sort order to $sortOrder") {
                click(th)
                th.getAttribute("aria-sort") == ariaSort
            }
        }
    }

    @Step("User checks sort order in column {column} by {sortOrder}")
    @Action("assert sort")
    fun assertSort(column: String, sortOrder: String) {
        sortBy(column, sortOrder)
        val values = table.getRowsMappedToHeadings(listOf(column)).map {
            val raw = it[column]?.text?.replace(",", "") ?: ""
            if (raw.isNotEmpty())
                raw
            else
                "0"
        }
        val sortAssertionFunction: (String, String) -> Boolean = when (sortOrder) {
            "ASC" -> { a, b ->
                try {
                    a.toBigDecimal() <= b.toBigDecimal()
                } catch (e: NumberFormatException) {
                    a <= b
                }
            }
            else -> { a, b ->
                try {
                    a.toBigDecimal() >= b.toBigDecimal()
                } catch (e: NumberFormatException) {
                    a >= b
                }
            }
        }

        assert(values.asSequence().zipWithNext(sortAssertionFunction).all { it }) {
            "Assertion error: sort order is wrong in column '$column' sorted by '$sortOrder'"
        }

    }

    @Step("get all values by column name")
    @Action("Admin get values in field by name")
    fun getColumnsByNamesForAllPages(vararg fieldNames: String): HashMap<String, MutableList<String>>? {
        if (fieldNames.isEmpty()) return null
        openFirstPage()
        var headings = table.headingsAsString
        var values: HashMap<String, MutableList<String>> = HashMap()
        while (true) {
            fieldNames.forEach { it1 ->
                val result = table.getColumnByIndex(headings.indexOf(it1) + 1)
                if (!values.containsKey(it1)) values[it1] = result.map { it.text }.toMutableList()
                else values[it1]?.addAll(result.map { it.text }.toMutableList())
            }
            if (hasNextPage())  nextPage() else return values
        }
    }

//    @Action("check items per page")
//    @Step("User asserts pages count and items per page")
//    fun checkItemsPerPage(itemsInPage: Int) {
//        setRowsPerPage(itemsInPage)
//        val totalItems = getRowsMappedToHeadingFromAllPagesAsString().size
//        val totalPages = getTotalPageCount()
//        val additionalPage = if (totalItems % itemsInPage > 0) 1 else 0
//        val expectedTotalPages = totalItems / itemsInPage + additionalPage
//        openPage(1)
//        MatcherAssert.assertThat(totalPages, Matchers.equalTo(expectedTotalPages))
//        assert {
//            while (hasNextPage()) {
//                rowsCountOnCurrentPageInTable(this@SdexTable, itemsInPage)
//                nextPage()
//            }
//            rowsCountOnCurrentPageInTable(this@SdexTable, totalItems % itemsInPage)
//        }
//    }
}