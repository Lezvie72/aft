package models

import utils.Environment
import utils.helpers.Users.Stand

enum class CoinType {
    CC {
        override var tokenName: String = "CUSDNN-01-2020"
        override val tokenSymbol: String
            get() = when (Environment.stand) {
                Stand.DEVELOP -> "CC"
                Stand.RELEASE -> "CC"
                Stand.PREPROD -> "CC"
                Stand.PROD -> ""
                Stand.SHARED -> "AT99USD"
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> "AT99USD"
            }
    },
    VT {
        override var tokenName: String = "VALIDTOK-01-2020"
        override val tokenSymbol: String
            get() = when (Environment.stand) {
                Stand.DEVELOP -> "VT"
                Stand.RELEASE -> "VT"
                Stand.PREPROD -> "VT"
                Stand.PROD -> ""
                Stand.SHARED -> "AT00VAL"
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> "AT00VAL"
            }
    },
    FT {
        override var tokenName: String = "Fractional Token"
        override val tokenSymbol: String
            get() = when (Environment.stand) {
                Stand.DEVELOP -> "FT"
                Stand.RELEASE -> "FT"
                Stand.PREPROD -> "FT"
                Stand.PROD -> ""
                Stand.SHARED -> ""
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> ""
            }
    },
    IT {
        override var tokenName: String = "Industrial Token"
        override val tokenSymbol: String
            get() = when (Environment.stand) {
                Stand.DEVELOP -> "GF46ILN046B"
                Stand.RELEASE -> "IT"
                Stand.PREPROD -> "IT"
                Stand.PROD -> ""
                Stand.SHARED -> "GF46ILN061A"
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> "GF46ILN061A"
            }
    },
    FIAT {
        override var tokenName: String = "Fiat Token"
        override val tokenSymbol: String
            get() = when (Environment.stand) {
                Stand.DEVELOP -> "FIAT"
                Stand.RELEASE -> "FIAT"
                Stand.PREPROD -> "FIAT"
                Stand.PROD -> ""
                Stand.SHARED -> "USD"
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> "USD"
            }
    },
    ETC {
        override var tokenName: String = "ETC token"
        override val tokenSymbol: String
            get() = when (Environment.stand) {
                Stand.DEVELOP -> "ETT"
                Stand.RELEASE -> "ETT"
                Stand.PREPROD -> "ETT"
                Stand.PROD -> ""
                Stand.SHARED -> "GF46IAF055E"
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> "GF46IAF055E"
            }
    },

    //c ETC1 - ETC6 это разные токены
    ETC1() {
        override var tokenName: String = "ETC Test Token 1"
        override var tokenSymbol: String = "GF79IAF056E"

    },
    ETC2() {
        override var tokenName: String = "ETC Test Token 2"
        override var tokenSymbol: String = "GF29IAF058E"

    },
    ETC3 {
        override var tokenName: String = "ETC Test Token 3"
        override var tokenSymbol: String = "GF78IAF054E"

    },
    ETC4 {
        override var tokenName: String = "ETC Test Token 4"
        override var tokenSymbol: String = "GF47IAF057E"

    },
    ETC5 {
        override var tokenName: String = "ETC Test Token 5"
        override var tokenSymbol: String = "GF28IAF059E"

    },
    ETC6 {
        override var tokenName: String = "ETC Test Token 6"
        override var tokenSymbol: String = "GF46IAF055E"

    },

    //
    GF28ILN060 {
        override var tokenName: String = "GF28ILN060"
        override var tokenSymbol: String = "GF28ILN060"
    },
    GF29ILN037B {
        override var tokenName: String = "GF29ILN037B"
        override var tokenSymbol: String = "GF29ILN037B"
    },
    GF46ILN046A {
        override var tokenName: String = "GF46ILN046A"
        override var tokenSymbol: String = "GF46ILN046A"
    },
    GF78ILN049 {
        override var tokenName: String = "GF78ILN049"
        override var tokenSymbol: String = "GF78ILN049"
    },
    GF29ILN037A {
        override var tokenName: String = "GF29ILN037A"
        override var tokenSymbol: String = "GF29ILN037A"
    },
    GF46ILN046B {
        override var tokenName: String = "GF46ILN046B"
        override var tokenSymbol: String = "GF46ILN046B"
    },
    GF28ILN060A() {
        override var tokenName: String = "GF28ILN060A"
        override var tokenSymbol: String = "GF28ILN060A"
    },
    GF28ILN060B() {
        override var tokenName: String = "GF28ILN060B"
        override val tokenSymbol: String = "GF28ILN060B"
    },
    GF29ILN037C() {
        override var tokenName: String = "GF29ILN037C"
        override val tokenSymbol: String = "GF29ILN037C"

    },
    GF29ILN037D() {
        override var tokenName: String = "GF29ILN037D"
        override val tokenSymbol: String = "GF29ILN037D"

    },
    GF46ILN061A() {
        override var tokenName: String = "GF46ILN061A"
        override val tokenSymbol: String = "GF46ILN061A"
    },

    // none isn't contained in list
    NONE() {
        override var tokenName: String = "undefined"
        override val tokenSymbol: String = "Non"
    };

    abstract var tokenName: String

    val date: String
        get() = when (this) {
            IT -> when (Environment.stand) {
                Stand.DEVELOP -> "22 September 2020"
                Stand.RELEASE -> "22 September 2020"
                Stand.PREPROD -> "22 September 2020"
                Stand.PROD -> ""
                Stand.SHARED -> ""
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> ""
            }

            else -> error("Token ${this.tokenSymbol} has not maturity date")
        }

    val tokenNameInAdminPanel: String
        get() = when (this) {
            IT -> when (Environment.stand) {
                Stand.DEVELOP -> ""
                Stand.RELEASE -> "${this.tokenSymbol}_202009"
                Stand.PREPROD -> ""
                Stand.PROD -> ""
                Stand.SHARED -> ""
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> ""
            }

            else -> error("Token ${this.tokenSymbol} has not maturity date")
        }

    val dateShortWrite: String
        get() = when (this) {
            IT -> when (Environment.stand) {
                Stand.DEVELOP -> "09.22.2020"
                Stand.RELEASE -> "09.22.2020"
                Stand.PREPROD -> "09.22.2020"
                Stand.PROD -> ""
                Stand.SHARED -> ""
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> ""
            }

            else -> error("Token ${this.tokenSymbol} has not maturity date")
        }

    val maturityDateMonthString: String
        get() = when (this) {
            IT -> when (Environment.stand) {
                Stand.DEVELOP -> "October 2021"
                Stand.RELEASE -> "September 2020"
                Stand.PREPROD -> "September 2020"
                Stand.PROD -> ""
                Stand.SHARED -> "October 2021"
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> "December 2020"
            }

            else -> error("Token ${this.tokenSymbol} has not maturity date")
        }

    val maturityDateMonthNumber: String
        get() = when (this) {
            IT -> when (Environment.stand) {
                Stand.DEVELOP -> this.tokenName + "_" + "31102021"
                Stand.RELEASE -> this.tokenName + "_" + "22092020"
                Stand.PREPROD -> this.tokenName + "_" + "22092020"
                Stand.PROD -> ""
                Stand.SHARED -> this.tokenName + "_" + "31102021"
                Stand.TOKEN_TRUST -> ""
                Stand.UAT_TOKEN_TRUST -> this.tokenName + "_" + "30122021"
            }

            else -> error("Token ${this.tokenSymbol} has not maturity date")
        }

    abstract val tokenSymbol: String
}