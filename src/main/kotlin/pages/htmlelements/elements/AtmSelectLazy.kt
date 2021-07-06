package pages.htmlelements.elements

import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import pages.BasePage
import pages.core.actions.isEnabledSafety
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TypifiedElement
import utils.helpers.to

class AtmSelectLazy(wrappedElement: WebElement) : TypifiedElement(wrappedElement) {

    private val dataNotLoad = By.xpath("//p[@class='ant-empty-description ng-star-inserted']")
    private val connectorInput = By.xpath(".//nz-select-search/input")
    private val virtualPort = "//cdk-virtual-scroll-viewport"
    private val itemOption = "//nz-option-item"
    private val itemSelect = "//nz-select-item"
    private val activeLine = "$virtualPort$itemOption[contains(@class, 'active')]"

    @Step("User selects before element should not be '{text}'")
    fun selectBeforeOther(text: String, page: BasePage) {
        waitLazyLabelText(page)
        page.e {
            val set = this@AtmSelectLazy.getHeadersAsString(page).filter { it != text }
            selectByText(set.last(), page)
        }
    }

    @Step("User selects '{text}' from context menu '{this.name}'")
    fun selectByText(text: String, page: BasePage) {
        page.e {
            waitLazyLabelText(page)
            if (this@AtmSelectLazy.text != text) {
                click(this@AtmSelectLazy)
                Thread.sleep(2000)
                val first = this@AtmSelectLazy.text
                //chek goal
                val isFound: Boolean =
                    page.isEnabledSafety(this@AtmSelectLazy, "$virtualPort$itemOption[@title='${text}']")
                //cycle for find goal
                if (!isFound) {
                    var beforeThrowException = 0
                    var valueFound = false
                    val input = page.wait(5L) {
                        until("") {
                            this@AtmSelectLazy.findElement(connectorInput)
                        }
                    }?.to<Button>("Row for input UP key")
                    while (!valueFound) {
                        var current = ""
                        input?.let { bt -> repeat(1) { bt.sendKeys(Keys.UP) } }
                        current = page.wait {
                            untilPresented<Button>(
                                By.xpath("$activeLine"),
                                "Selected active line"
                            ).text
                        }
                        if (page.isEnabledSafety(this@AtmSelectLazy, "$virtualPort$itemOption[@title='${text}']")) {
                            val item = page.wait {
                                untilPresented<Button>(
                                    By.xpath("$virtualPort$itemOption[@title='${text}']"),
                                    "Selected item"
                                )
                            }
                            click(item)
                            this@AtmSelectLazy.text == text
                            break
                        }
                        if (first == current) throw error("Not found value ${text} into lazy list elements")
                        beforeThrowException++
                        // external check
                        if (beforeThrowException > 10) {
                            throw Exception("Not found value ${text} into lazy list elements after ${beforeThrowException} attempts")
                        }
                    }
                } else {
                    page.wait {
                        until("Message", 10L) {
                            val item = untilPresented<Button>(
                                By.xpath("$virtualPort$itemOption[@title='${text}']"),
                                "Selected item"
                            )
                            click(item)
                            this@AtmSelectLazy.text == text
                        }
                    }
                }
            }
        }
    }

    @Step("get data as string from element")
    fun getHeadersAsString(page: BasePage, setWithoutSplit : Boolean = false): MutableSet<String> {
        waitLazyLabelText(page)
        page.wait {
            until("Item in lazy list should has label") {
                page.check {
                    this@AtmSelectLazy.findElement(By.xpath(".$itemSelect")).getAttribute("title").isNotEmpty()
                }
            }
        }
        val rowSet: MutableSet<String> = mutableSetOf()
        rowSet.add(this@AtmSelectLazy.findElement(By.xpath(".$itemSelect")).getAttribute("title"))
        page.e {
            click(this@AtmSelectLazy)
            page.wait {
                until("Active element loaded", 15) {
                    this@AtmSelectLazy.findElement(By.xpath(".//nz-select-item")).isEnabled
                }
            }
            //external wait when all elements loaded - not possible with wait because count elements not known
            Thread.sleep(2000)
            //element that can be used as active for send keys
            val intractableElement = this@AtmSelectLazy.findElement(connectorInput)
            //cycle for find goal
            val beforeThrowException = 0
            while (true) {
                intractableElement.sendKeys(Keys.DOWN)
                var currentValue = ""
                if (page.isEnabledSafety(this@AtmSelectLazy, activeLine)) {
                    currentValue = this@AtmSelectLazy.findElement(By.xpath(activeLine)).getAttribute("title")
                }
                if (rowSet.contains(currentValue)) {
                    return@e rowSet
                } else rowSet.add(currentValue)
                if (beforeThrowException > 30) {
                    throw Exception("Not found value ${text} into lazy list elements after ${beforeThrowException} attempts")
                }
            }
        }

        if(setWithoutSplit){
            val set = mutableSetOf<String>()
            rowSet
                .filter { it.isNotBlank() }
                .map { it ->
                    set.add(it)
                }

            return set
        }

        val set = mutableSetOf<String>()
        rowSet
            .filter { it.isNotBlank() }
            .map { it ->
                if (it.contains("/")) {
                    it.split("/")
                        .map { it1 -> set.add(it1) }
                } else {
                    set.add(it)
                }
            }
        page.e { click(this@AtmSelectLazy) }
        return set
    }

    private fun waitLazyLabelText(page: BasePage) {
        page.wait {
            until("Selector lazy element should not be empty", 10L) {
                page.check {
                    this@AtmSelectLazy.text.isNotEmpty()
                }
            }
        }
    }
}