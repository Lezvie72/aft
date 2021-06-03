package utils

object Constants {

    //Frontend
    const val DEFAULT_AUTH_CODE = "123456"
    const val DEFAULT_PASSWORD = "1qaz!QAZ"
    const val DEFAULT_NEW_PASSWORD = "1qaz!QAZ1"

    //Epic
    const val FRONTEND = "Frontend"
    const val BACKEND = "Backend"

    //Story
    const val POSITIVE = "Positive test"
    const val PAGE_CHECKS = "Page checks"
    const val NEGATIVE = "Negative tests"
    const val DIRECT = "Direct flow"

    //Resource locks
    const val REGISTRATION_LOCK = "Registration lock"
    const val SIGNATURE_LOCK = "Signature lock"
    const val DEPTH_OF_MARKET_LOCK = "DOM lock"
    const val USER_BALANCE_LOCK = "Balance lock"

    //User roles
    const val ROLE_USER_VER_0 = "User ver 0"
    const val ROLE_USER_VER_0_KYC = "User ver 0: KYC"
    const val ROLE_USER_VER_1 = "User ver 1"

    const val ROLE_USER_2FA_OTF = "Atm user: OAuth, Manual signature, OTF1 (autotestOTF)"
    const val ROLE_USER_OTF_FOR_OTF = "Atm user: OAuth, Manual signature, OTFWalletForOTFAUTOTEST"
    const val ROLE_USER_2FA_OTF2 = "Atm user: OAuth, Manual signature, OTF, autotestOTF"
    const val ROLE_USER_WITHOUT2FA_OTF = "Atm user: WITHOUT OAuth and OTF, OTFWITHOUTOAUTH_AUTOTEST"

    const val ROLE_USER_2FA_OTF_OPERATION = "OTF_OPERATION"
    const val ROLE_USER_2FA_OTF_OPERATION_SECOND = "OTF_OPERATION_2"
    const val ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA = "OTF_OPERATION_WITHOUT2FA"
    const val ROLE_USER_2FA_OTF_OPERATION_THIRD = "ATM_USER_2FA_OTF_OPERATION_THIRD"
    const val ROLE_USER_2FA_OTF_OPERATION_FORTH = "ATM_USER_2FA_OTF_OPERATION_FORTH"
    const val ROLE_USER_2FA_OTF_OPERATION_FIFTH = "ATM_USER_2FA_OTF_OPERATION_FIFTH"
    const val ROLE_USER_2FA_OTF_OPERATION_SIXTH = "ATM_USER_2FA_OTF_OPERATION_SIXTH"
    const val ROLE_USER_2FA_OTF_OPERATION_SEVENTH = "ATM_USER_2FA_OTF_OPERATION_SEVENTH"
    const val ROLE_USER_2FA_OTF_OPERATION_EIGHTH = "ATM_USER_2FA_OTF_OPERATION_EIGHTH"

    const val ROLE_USER_2FA_MAIN_WALLET = "Atm user: OAuth, Manual signature, autotestMain"
    const val ROLE_USER_2FA_2MAIN_WALLET = "Atm user: 2Main wallets"

    const val USER_FOR_BANK_ACC = "USER_FOR_BANK_ACC,autotestBankAcc"
    const val USER_FOR_BANK_ACC_ONE = "USER_FOR_BANK_ACC_ONE,autotestBankAccOne"
    const val USER_FOR_BANK_ACC_TWO = "USER_FOR_BANK_ACC_TWO,autotestBankAccTwo"

    const val ATM_USER_EMPLOYEE_ADMIN_ROLE = "ATM_USER_EMPLOYEE_ADMIN_ROLE"

    const val ROLE_USER_MAIN_OTF_MOVE = "Main, OTF, autotest Move"

    const val ROLE_USER_ETC_TOKEN = "ATM_USER_FOR_ETC_TOKENS"
    const val ROLE_USER_ETC_TOKEN_ONE = "ATM_USER_FOR_ETC_TOKENS_ONE"
    const val ROLE_USER_ETC_TOKEN_SECOND = "ATM_USER_FOR_ETC_TOKENS_SECOND"
    const val ROLE_USER_ETC_TOKEN_THIRD = "ATM_USER_FOR_ETC_TOKENS_THIRD"

    const val ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA = "ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA"
    const val ROLE_USER_FOR_ACCEPT_ETC_TOKEN_2FA = "ATM_USER_FOR_ACCEPT_ETC_TOKENS_2FA"
    const val ROLE_USER_FOR_ACCEPT_ETC_TOKEN_SECOND = "ATM_USER_FOR_ACCEPT_ETC_TOKENS_SECOND"
    const val ROLE_USER_FOR_ACCEPT_ETC_TOKEN_THIRD = "ATM_USER_FOR_ACCEPT_ETC_TOKENS_THIRD"
    const val ROLE_USER_FOR_ACCEPT_ETC_TOKEN_NOT_CONTROLLER = "ATM_USER_FOR_ACCEPT_ETC_TOKENS_NOT_CONTROLLER"

    const val ROLE_USER_FOR_ACCEPT_IT_TOKEN = "ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS"
    const val ROLE_USER_FOR_ACCEPT_IT_TOKEN_ONE = "ATM_USER_FOR_ACCEPT_IT_TOKEN_ONE"
    const val ROLE_USER_FOR_ACCEPT_IT_TOKEN_SECOND = "ATM_USER_FOR_ACCEPT_IT_TOKEN_SECOND"
    const val ROLE_USER_FOR_ACCEPT_IT_TOKEN_THIRD = "ATM_USER_FOR_ACCEPT_IT_TOKEN_THIRD"

    const val ROLE_USER_IT_TOKEN_ONE = "ATM_USER_MAIN_FOR_IT_ONE"
    const val ROLE_USER_IT_TOKEN_SECOND = "ATM_USER_MAIN_FOR_IT_SECOND"
    const val ROLE_USER_IT_TOKEN_THIRD = "ATM_USER_MAIN_FOR_IT_THIRD"

    const val ROLE_USER_VALIDATOR_2FA  = "ATM_USER_VALIDATOR_2FA"
    const val ROLE_USER_VALIDATOR_WITHOUT_2FA = "ATM_USER_VALIDATOR_WITHOUT_2FA"
    const val ROLE_USER_VALIDATOR_WITHOUT_FUNDS = "ATM_USER_VALIDATOR_WITHOUT_FUNDS"

    const val ROLE_ADMIN = "ATM_ADMIN"

    //Token -> Fee size and type
    const val FEE = "Fixed fee by precondition"

    //etc
    const val DEFAULT_BANK_DETAILS = "Autotest Bank Details"

}