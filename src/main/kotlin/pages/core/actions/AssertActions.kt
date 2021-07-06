package pages.core.actions

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Assert
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import pages.BasePage
import pages.core.annotations.Action
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.element.TypifiedElement
import utils.helpers.getName
import utils.helpers.step

class AssertActions<T : WebDriver>(page: BasePage, driver: T, val timeoutInSeconds: Long = page.getTimeoutInSeconds()) :
    BaseActions<BasePage, T>(page, driver) {

    companion object {
        val EMAIL_VALIDATION = mapOf(
            "less than 6 characters" to "q@q.q",
            "no @ symbol" to "qqqQqq.ru",
            "no characters before @" to "@qqqq.ru",
            "less than 3 characters after @" to "uat.aft.sdex+112344@ru",
            "no dot after @" to "uat.aft.sdex+123123@gmailcom",
            "consecutive dot and @" to "uat.aft.sdex+1123344555@.com",
            "less than 2 characters after dot" to "uat.aft.sdex+1123344555@gmail.c"
        )

        val PASSWORD_VALIDATION = mapOf(
            "less than 8 characters" to "1qaz!QA",
            "no capital letter" to "1qaz!qaz",
            "no lowercase letter" to "1QAZ!QAZ",
            "no number" to "!qaz!QAZ",
            "no special symbol" to "1qaz1QAZ"
        )

        val STRING_VALIDATION = mapOf(
            "string characters" to "test"
        )

        val INT_VALIDATION = mapOf(
            "int characters" to "123"
        )
    }

    @Step("Assertion: Validation of field {e.name}")
    fun validateField(
        e: WebElement,
        dataMap: Map<String, String>,
        errorMessage: String,
        elToChangeFocus: WebElement = e
    ) {
        dataMap.forEach { (stepName, value) ->
            step(stepName) {
                page.e {
                    sendKeys(e, value)
                    click(elToChangeFocus)
                }
                page.assert {
                    elementContainingTextPresented(errorMessage)
                }
                page.e {
                    clear(e)
                }
            }
        }
    }

    @Step("Assertion: Validation of field {e.name}")
    fun validateFieldSimple(e: WebElement, dataMap: Map<String, String>) {
        dataMap.forEach { (stepName, value) ->
            step(stepName) {
                page.e {
                    sendKeys(e, value)
                }
                page.assert {
                    elementContainsText(e, value)
                }
                page.e {
                    clear(e)
                }
            }
        }
    }

    @Action("assert checkbox state")
    fun checkboxState(name: String, state: String) {
        val e: WebElement = page.findElementByName(name)
        val isSelected = when (state) {
            "ON" -> true
            else -> false
        }
        checkboxState(e, isSelected)
    }

    @Step("Assertion: Element '{e.name}' should be in state '{expectedState}'")
    fun checkboxState(e: WebElement, expectedState: Boolean) {
        val actual = page.check { isSelectedRadioButton(e) == expectedState }
        assert(actual == expectedState) {
            "Element: ${e.getName()}. Expected: '$expectedState'. Actual: '$actual'"
        }
    }

    @Step("Assertion: Is element with text presented")
    @Action("assert element with text presented")
    fun elementWithTextPresented(text: String) {
        assert(page.check { isElementWithTextPresented(text, timeoutInSeconds) }) {
            "No element with text '$text' is presented on page"
        }
    }

    @Step("Assertion: Is element with text presented (ignore case)")
    @Action("assert element with text presented")
    fun elementWithTextPresentedIgnoreCase(text: String) {
        assert(page.check { isElementWithTextPresentedIgnoreCase(text, timeoutInSeconds) }) {
            "No element with text '$text' is presented on page"
        }
    }

    @Step("Assertion: Is element with text presented")
    @Action("assert element with text presented")
    fun elementWithTextNotPresented(text: String) {
        assert(!page.check { isElementWithTextPresented(text, timeoutInSeconds) }) {
            "Element with text '$text' is presented on page. Expected: not presented"
        }
    }

    @Step("Check: element should be disappeared")
    @Action("assert element with text should be not presented or disappeared")
    fun elementShouldBeDisappeared(element: TypifiedElement, timeoutInSeconds: Long = page.getTimeoutInSeconds()) {
        page.wait {
            until("Element should be hide ${element.name}", timeoutInSeconds) {
                page.check {
                    !isElementPresented(element, 1L)
                }
            }
        }
    }

    @Step("Assertion: Is element containing text presented")
    @Action("assert element containing text presented")
    fun elementContainingTextPresented(partialText: String, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(page.check { isElementContainingTextPresented(partialText, timeoutInSeconds) }) {
            "No element containing text '$partialText' is presented on page"
        }
    }

    @Step("Assertion: Is element containing text not presented")
    @Action("assert element containing text not presented")
    fun elementContainingTextNotPresented(partialText: String, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(!page.check { isElementContainingTextPresented(partialText, timeoutInSeconds) }) {
            "Element containing text '$partialText' is presented on page, but should not"
        }
    }

    @Action("assert element contains text")
    fun elementContainsText(name: String, value: String, timeoutInSeconds: Long = this.timeoutInSeconds) {
        val e: WebElement = page.findElementByName(name)
        elementContainsText(e, value)
    }

    @Step("Assertion: element '{e.name}' should contain text {text}")
    fun elementContainsText(e: WebElement, text: String, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(page.check { isElementContainsText(e, text, timeoutInSeconds) }) {
            "Element ${e.getName()} doesn't contain text $text"
        }
    }

    @Step("Assertion: element '{e.name}' should contain text {text}")
    fun elementContainsTextWithIgnoreCase(e: WebElement, text: String, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(page.check { isElementContainsText(e, text, timeoutInSeconds, true) }) {
            "Element ${e.getName()} doesn't contain text $text"
        }
    }

    @Step("Assertion: element '{e.name}' should not contain text {text}")
    fun elementNotContainsText(e: WebElement, text: String, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(page.check { !isElementContainsText(e, text, timeoutInSeconds) }) {
            "Element ${e.getName()} contain text $text"
        }
    }

    @Action("assert element enabled")
    fun elementEnabled(name: String) {
        val e: WebElement = page.findElementByName(name)
        elementEnabled(e)
    }

    @Step("Assertion: element '{e.name}' is enabled")
    fun elementEnabled(e: WebElement, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(page.check { isElementEnabled(e, timeoutInSeconds) }) {
            "Element '${e.getName()}' is disabled"
        }
    }

    @Action("Assert element '{e.name}' is disabled")
    fun elementIsDisabled(name: String) {
        val e: WebElement = page.findElementByName(name)
        assert(e.getAttribute("disabled") == "true") {
            "Element '${e.getName()}' is enabled"
        }
    }

    @Step("Assertion: element '{e.name}' is disabled")
    fun elementDisabled(e: WebElement, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(!page.check { isElementEnabled(e, timeoutInSeconds) }) {
            "Element '${e.getName()}' is enabled"
        }
    }

    @Action("assert element is displayed")
    fun elementIsDisplayed(name: String, timeoutInSeconds: Long = this.timeoutInSeconds) {
        val e: WebElement = page.findElementByName(name)
        assert(page.check { isElementPresented(e, timeoutInSeconds) })
    }

    @Step("Assertion: element '{e.name}' should be not presented on page")
    fun elementNotPresented(e: WebElement, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(page.check { !isElementPresented(e, timeoutInSeconds) }) {
            "Element ${e.getName()} presented on page"
        }
    }

    @Step("Assertion: element '{e.name}' should be presented on page")
    fun elementPresented(e: WebElement, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(page.check { isElementPresented(e, timeoutInSeconds) }) {
            "Element ${e.getName()} not presented on page"
        }
    }

    @Action("assert rows count on page")
    fun rowsCountOnCurrentPageInTable(name: String, count: String) {
        val e: SdexTable = page.findElementByName(name)
        rowsCountOnCurrentPageInTable(e, count.toInt())
    }

    @Step("Assertion: table '{e.name}' should have '{count}' items on current page")
    fun rowsCountOnCurrentPageInTable(e: SdexTable, count: Int) {
        e.waitUntilReady()
        val rowCount = e.table.rows.size
        assert(rowCount == count) {
            "Table: ${e.name}. Expected: '$count' rows. Actual: '$rowCount'"
        }
    }

    //TODO: DELETE BLOCKS
//    @Action("assert page count")
//    fun pageCountInTable(name: String, count: String) {
//        val e: SdexTable = page.findElementByName(name)
//        pageCountInTable(e, count.toInt())
//    }
//
//    @Step("Assertion: table '{e.name}' should have '{count}' pages")
//    fun pageCountInTable(e: SdexTable, count: Int) {
//        e.waitUntilReady()
//        val pageCount = e.getTotalPageCount()
//        assert(pageCount == count) {
//            "Table: ${e.name}. Expected: '$count' pages. Actual: '$pageCount'"
//        }
//    }

    @Action("assert table contains row")
    fun tableContainsRowMatching(name: String, filter: Map<String, String>) {
        val e: SdexTable = page.findElementByName(name)
        tableContainsRowMatching(e, filter)
    }

    @Action("assert table not contains row")
    fun tableDoesntContainsRowMatching(name: String, filter: Map<String, String>) {
        val e: SdexTable = page.findElementByName(name)
        tableContainsRowMatching(e, filter)
    }

    @Step("Assertion: table '{e.name}' should contain row matching filter '{filter}'")
    fun tableContainsRowMatching(e: SdexTable, filter: Map<String, String>) {
        e.waitUntilReady()
        val row = e.find {
            filter.all { f ->
                it[f.key]?.text?.normalize()?.contains(f.value) ?: false
            }
        }
        assert(null != row) {
            "Couldn't find row matching given filter $filter"
        }
    }

    @Step("Assertion: table '{e.name}' should not contain row matching filter '{filter}'")
    fun tableDoesntContainsRowMatching(e: SdexTable, filter: Map<String, String>) {
        e.waitUntilReady()
        val row = e.find {
            filter.all { f ->
                it[f.key]?.text?.normalize()?.contains(f.value) ?: false
            }
        }
        assert(null == row) {
            "Row with specified parameters was found."
        }
    }

    @Action("assert url matches")
    @Step("Assertion: url matches '{regex}'")
    fun urlMatches(regex: String, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(page.check { urlMatches(regex, timeoutInSeconds) }) {
            "Expected url to match '$regex'. Current url: ${driver.currentUrl}"
        }
    }

    @Action("assert url ends")
    @Step("Assertion: url ends with '{regex}'")
    fun urlEndsWith(regex: String, timeoutInSeconds: Long = this.timeoutInSeconds) {
        assert(page.check { urlMatches("^.*$regex$", timeoutInSeconds) }) {
            "Expected url to ends with '$regex'. Current url: ${driver.currentUrl}"
        }
    }

    @Action("assert text mail")
    @Step("Assertion: mail '{text}'")
    fun textEmail(href: String, text: String) {//TODO доработать регулярку
        val str = href.replace("\\r\\n|<[^>]*>".toRegex(), "")
            .replace(" {3}Dear employee|Thanks,The Atomyze Team.".toRegex(), "")
        assertThat(
            str,
            Matchers.containsString(text)
        )
    }

    @Action("assert text mail")
    @Step("Assertion: mail '{text}'")
    fun newTextEmail(href: String, text: String) {//TODO доработать регулярку
        val str = href.replace("\\r\\n|<[^>]*>".toRegex(), "")
            .replaceBefore("Atomyze","")
            .replaceAfterLast("!","")
            .replace("^ +| +$|( )+".toRegex(), " ")
        assertThat(
            str,
            Matchers.containsString(text)
        )
    }

    @Action("check any match in left and right sets")
    @Step("Set A and Set B should have at least one common meaning.")
    fun <T> hasLeastOneCommonMeaning(setA: Set<T>, SetB: Set<T>, message: String = "") {
        Assert.assertTrue(
            message,
            setA.stream().anyMatch({ item1 -> SetB.stream().anyMatch({ item2 -> item1?.equals(item2) == true }) })
        )
    }

    @Action("check any match in left and right sets")
    @Step("Set A and Set B should not have at least one common meaning")
    fun <T> hasNotLeastOneCommonMeaning(setA: Set<T>, SetB: Set<T>, message: String = "") {
        Assert.assertFalse(
            message,
            setA.stream().anyMatch({ item1 -> SetB.stream().anyMatch({ item2 -> item1?.equals(item2) == true }) })
        )
    }

    fun elementContainsValue(e: WebElement, value: String) {
        val actualValue = e.getAttribute("value")
        assertThat(value, Matchers.containsString(actualValue))
    }


    fun that(check: CheckActions<T>.() -> Boolean) {
        assert(check(CheckActions(page, driver)))
    }

    fun String.normalize(): String {
        return this.replace("[, ]".toRegex(), "")
    }
}