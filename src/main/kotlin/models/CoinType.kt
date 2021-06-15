package models

enum class CoinType(val tokenSymbol: String) {
    CC("CC") {
        override var tokenName: String = "CUSDNN-01-2020"
    },
    VT("VT") {
        override var tokenName: String = "VALIDTOK-01-2020"
    },
    FT("FT") {
        override var tokenName: String = "Fractional Token"
    },
    IT("IT") {
        override var tokenName: String = "Industrial Token"
    },
    FIAT("FIAT") {
        override var tokenName: String = "Fiat Token"
    },
    ETC("ETT") {
        override var tokenName: String = "ETC token"
    },

    //c ETC1 - ETC6 это разные токены
    ETC1("GF79IAF056E") {
        override var tokenName: String = "ETC Test Token 1"
    },
    ETC2("GF29IAF058E") {
        override var tokenName: String = "ETC Test Token 2"
    },
    ETC3("GF78IAF054E") {
        override var tokenName: String = "ETC Test Token 3"
    },
    ETC4("GF47IAF057E") {
        override var tokenName: String = "ETC Test Token 4"
    },
    ETC5("GF28IAF059E") {
        override var tokenName: String = "ETC Test Token 5"
    },
    ETC6("GF46IAF055E") {
        override var tokenName: String = "ETC Test Token 6"
    },

    //
    GF28ILN060("GF28ILN060") {
        override var tokenName: String = "GF28ILN060"
    },
    GF29ILN037B("GF29ILN037B") {
        override var tokenName: String = "GF29ILN037B"
    },
    GF46ILN046A("GF46ILN046A") {
        override var tokenName: String = "GF46ILN046A"
    },
    GF78ILN049("GF78ILN049") {
        override var tokenName: String = "GF78ILN049"
    },
    GF29ILN037A("GF29ILN037A") {
        override var tokenName: String = "GF29ILN037A"
    },
    GF46ILN046B("GF46ILN046B") {
        override var tokenName: String = "GF46ILN046B"
    },

    // none isn't contained in list
    NONE("Non") {
        override var tokenName: String = "undefined"
    };

    abstract var tokenName: String

}