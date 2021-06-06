package pages.htmlelements.elements

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import ru.yandex.qatools.htmlelements.element.TypifiedElement
import java.math.BigDecimal
import kotlin.random.Random

class AtmAmount(wrappedElement: WebElement) : TypifiedElement(wrappedElement) {

    private val amountLocator = By.cssSelector(".token-amount")
    private val partLocator = By.cssSelector(".decimal-part")
    private val tokenLocator = By.cssSelector(".token-name--left")

    val amount: BigDecimal
        get() = getAmountMethod()

    val heldInOffers: BigDecimal
        get() = getAmountMethod()

    val textBody: String
        get() = getAmountMethodAsString()

    val minLimit: String
        get() = lessThanMinLimitValue()

    val maxLimit: String
        get() = moreThanMaxValue()

    val minLimitVal: String
        get() = minLimitValue()

    val maxLimitVal: String
        get() = maxLimitValue()

    val currency: String
        get() = this.findElement(tokenLocator).text

    private fun getAmountMethod(): BigDecimal {

        val amount = try {
            this.findElement(amountLocator).text.replace(" ", "")
        } catch (e: org.openqa.selenium.NoSuchElementException) {
            "0"
        }

        val part = try {
            this.findElement(partLocator).text.replace(" ", "")
        } catch (e: org.openqa.selenium.NoSuchElementException) {
            ".0"
        }

        val result = amount + part

        return try {
            BigDecimal(result)
        } catch (e: NumberFormatException) {
            BigDecimal(0)
        }
    }

    private fun getAmountMethodAsString(): String {

        val amount = try {
            this.findElement(amountLocator).text.replace(" ", "")
        } catch (e: org.openqa.selenium.NoSuchElementException) {
            "0"
        }

        val part = try {
            this.findElement(partLocator).text.replace(" ", "")
        } catch (e: org.openqa.selenium.NoSuchElementException) {
            ".0"
        }

        val result = amount + part

        return try {
            result
        } catch (e: NumberFormatException) {
            ""
        }
    }

    private fun minLimitValue(): String {
        val minLimit =
            this.getAttribute("innerHTML").replaceAfter("/", "")
                .replace("MIN ", "").replace(" /", "")
                .replace(",", ".")
        return minLimit
    }

    private fun maxLimitValue(): String {
        val maxLimit =
            this.getAttribute("innerHTML").replaceBefore("/", "")
                .replace("MAX ", "").replace("/ ", "")
                .replace("&nbsp;", "")
                .replace(",", ".")
        return maxLimit
    }

    private fun lessThanMinLimitValue(): String {
        val amount = Random.nextDouble(0.00000001, 0.00000099)
        val value: Double
        val minLimit =
            this.getAttribute("innerHTML").replaceAfter("/", "").replace("MIN ", "").replace(" /", "").toDouble()
        return if (minLimit == "0.00000001".toDouble() || minLimit == "0.00000000".toDouble()) {
            "0.00000000"
        } else {
            value = minLimit - amount
            String.format("%.8f", value)
        }
    }

    private fun moreThanMaxValue(): String {
        val amount = Random.nextDouble(0.000001, 0.999999)
        val value: Double
        val maxLimit =
            this.getAttribute("innerHTML").replaceBefore("/", "").replace("MAX ", "").replace("/ ", "")
                .toDouble()
        value = maxLimit + amount
        return String.format("%.6f", value)
    }

}