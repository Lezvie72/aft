package models

enum class OtfAmounts(val amountVal: String) {
    AMOUNT_1("1") {
        override var amount: String = "1.00000000"
    },
    AMOUNT_10("10") {
        override var amount: String = "10.00000000"
    },
    AMOUNT_100("100") {
        override var amount: String = "100.00000000"
    },
    AMOUNT_1000("1000") {
        override var amount: String = "1000.00000000"
    };

    abstract var amount: String

}