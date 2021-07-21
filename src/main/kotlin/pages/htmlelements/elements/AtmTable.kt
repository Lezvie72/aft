package pages.htmlelements.elements

import io.qameta.allure.Step
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.openqa.selenium.*
import org.openqa.selenium.support.FindBy
import pages.BasePage
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import kotlin.math.absoluteValue


class AtmTable<T : WebElement> : BaseBlock<BasePage>() {

    companion object {
        const val loadTimeoutInSeconds = 10L
    }

    @FindBy(xpath = "//nz-pagination//li[@title='Previous Page']")
    @Name("Prev page")
    lateinit var prevPageButton: Button

    @FindBy(xpath = "//nz-pagination//li[@title='Next Page']")
    @Name("Next page")
    lateinit var nextPageButton: Button

    @FindBy(css = "sdex-select[aria-label='rows']")
    @Name("Rows")
    lateinit var rowsSelect: SdexSelect

    @FindBy(css = "nz-pagination")
    @Name("Pages")
    lateinit var pagesTextBlock: TextBlock

    @FindBy(css = "sdex-empty-state, .no-records")
    lateinit var emptyTextBlock: TextBlock

    @FindBy(css = "table tbody")
    lateinit var emptyTextBlockATM: TextBlock

    @FindBy(css = "nz-spin")
    lateinit var loader: TextBlock

    @FindBy(css = "div")
    lateinit var input: Button

    lateinit var content: List<T>

    private fun waitUntilListIsLoaded() = wait(loadTimeoutInSeconds) {
        until("Couldn't load list in $loadTimeoutInSeconds seconds", loadTimeoutInSeconds) {
            (check { isElementGone(loader) } || content.isNotEmpty())
        }
    }

    @Step("Wait until table is ready")
    fun waitUntilReady() {
        try {
            waitUntilListIsLoaded()
        } catch (e: TimeoutException) {
            page.driver.navigate().refresh()
            waitUntilListIsLoaded()
        }
    }

    @Step("Get total page index")
    fun getTotalPageCount(): Int {
        val result = check {
            pagesTextBlock.ifPresented {
                val count = findElements(By.xpath("//nz-pagination//li")).size
                val currentCount = count - 1
                findElement(By.xpath("//nz-pagination//li[$currentCount]//a")).text.toInt()
            }
        }
        return result ?: 1
    }

    @Step("Get current page index")
    fun getCurrentPageIndex(): Int {
        val result = check {
            pagesTextBlock.ifPresented {
                findElement(By.xpath("//nz-pagination//li[contains(@class, 'ant-pagination-item-active')]//a")).text.toInt()
            }
        }
        return result ?: 0
    }

    @Step("Open page #{index}")
    fun openPage(index: Int) {
        val currentPageIndex = getCurrentPageIndex()
        val totalPagesCount = getTotalPageCount()
        if (currentPageIndex != index) {
            val elementToClick = if (currentPageIndex > index) prevPageButton else nextPageButton
            val destinationIndex = if (index > totalPagesCount) totalPagesCount else index
            val diff = (destinationIndex - currentPageIndex).absoluteValue
            repeat(diff) {
                e {
                    click(elementToClick)
                }
            }
        }
    }

    @Step("Go to next page")
    fun nextPage() {
        if (hasNextPage()) {
            e { click(nextPageButton) }
        }
    }

    @Step("Go to previous page")
    fun prevPage() {
        if (hasPrevPage()) {
            e { click(prevPageButton) }
        }
    }

    @Step("Check: have next page")
    fun hasNextPage(): Boolean {
        return check { isElementPresented(nextPageButton, 2L) && isElementEnabled(nextPageButton, 2L) }
    }

    @Step("Check: have previous page")
    fun hasPrevPage(): Boolean {
        return check { isElementPresented(prevPageButton, 2L) && isElementEnabled(prevPageButton, 2L) }
    }

    fun forEach(body: (T) -> Unit) {
        content.forEach(body)
    }

    fun openFirstPage() {
        while (hasPrevPage()) {
            prevPage()
        }
    }

    @Step("Search for item")
    fun find(predicate: (T) -> Boolean): T? {
        waitUntilReady()
        openFirstPage()
        val repeatCount = 100
        repeat(repeatCount) {
            val result = content.find(predicate)
            if (result != null) return result
            if (hasNextPage()) {
                nextPage()
                Thread.sleep(2000)
                waitUntilReady()
            } else {
                return null
            }
        }
        return null
    }

    @Step("Search for item")
    fun findListTable(predicate: (T) -> Boolean): T? {
        val repeatCount = 10
        repeat(repeatCount) {
            try {
                val result = content.find(predicate)
                if (result != null) return result
            } catch (e: NoSuchElementException) {
                input.sendKeys(Keys.PAGE_DOWN)
                runBlocking {
                    delay(2000)
                }
            }
        }
        return null
    }
}