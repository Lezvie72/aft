package pages.htmlelements.elements

import org.openqa.selenium.By
import org.openqa.selenium.support.FindBy
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.HtmlElement

class DateSelect : HtmlElement() {

    @FindBy(xpath = ".//*[@aria-label='Choose month and year']")
    lateinit var startButton: Button

    fun cellXpath(value: String): By {
        return By.xpath(".//div[contains(@class, 'mat-calendar-body-cell-content') and contains(text(), '$value')]")
    }

    /**
     * date format : dd/MM/yyyy
     */
    fun selectDate(date: String, separator: String = "/") {
        startButton.click()
        val dates = date.split(separator)
        val day = dates[0]
        val month = dates[1]
        val year = dates[2]

        val monthValue = when (month) {
            "01", "1" -> "JAN"
            "02", "2" -> "FEB"
            "03", "3" -> "MAR"
            "04", "4" -> "APR"
            "05", "5" -> "MAY"
            "06", "6" -> "JUN"
            "07", "7" -> "JUL"
            "08", "8" -> "AUG"
            "09", "9" -> "SEP"
            "10" -> "OCT"
            "11" -> "NOV"
            "12" -> "DEC"
            else -> "JAN"
        }
        this.findElement(cellXpath(year)).click()
        this.findElement(cellXpath(monthValue)).click()
        this.findElement(cellXpath(day)).click()
    }

}