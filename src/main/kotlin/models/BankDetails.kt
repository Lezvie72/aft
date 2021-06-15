package models

import org.apache.commons.lang.RandomStringUtils
import pages.atm.AtmAdminBankDetailsPage.Headers.ADDITIONAL_INFORMATION
import pages.atm.AtmAdminBankDetailsPage.Headers.BANK_ADDRESS
import pages.atm.AtmAdminBankDetailsPage.Headers.BANK_DETAILS
import pages.atm.AtmAdminBankDetailsPage.Headers.BANK_NAME
import pages.atm.AtmAdminBankDetailsPage.Headers.BENEFICIARY
import pages.atm.AtmAdminBankDetailsPage.Headers.CORRESPONDENT_ACCOUNT
import pages.atm.AtmAdminBankDetailsPage.Headers.CORRESPONDENT_BANK
import pages.atm.AtmAdminBankDetailsPage.Headers.PAYMENT_SYSTEM
import pages.atm.AtmAdminBankDetailsPage.Headers.RECIPIENT_ADDRESS
import pages.atm.AtmAdminBankDetailsPage.Headers.RECIPIENT_NAME

data class BankDetails(
    val bankDetails: String,
    val recipientName: String,
    val recipientAddress: String,
    val bankName: String,
    val bankAddress: String,
    val beneficiary: String,
    val correspondentBank: String,
    val correspondentAccount: String,
    val paymentSystem: String,
    val information: String,
    val paymentSystemNumber: String
) {
    companion object {
        fun generate(): BankDetails {
            return BankDetails(
                bankDetails = RandomStringUtils.randomAlphanumeric(15),
                recipientName = RandomStringUtils.randomAlphanumeric(15),
                recipientAddress = RandomStringUtils.randomAlphanumeric(20),
                bankName = RandomStringUtils.randomAlphanumeric(10),
                bankAddress = RandomStringUtils.randomAlphanumeric(20),
                beneficiary = RandomStringUtils.randomNumeric(25),
                correspondentBank = RandomStringUtils.randomAlphanumeric(15),
                correspondentAccount = RandomStringUtils.randomNumeric(25),
                paymentSystem = "SWIFT",
                information = RandomStringUtils.randomAlphanumeric(25),
                paymentSystemNumber = listOf(
                    "RAIFCH22290",
                    "RAIFCH22B03",
                    "NBPSCHGGXXX",
                    "IVESCHZZXXX",
                    "MGTCCHGGXXX"
                ).random()

            )
        }

        fun fromRow(row: Map<String, String>): BankDetails {
            return BankDetails(
                bankDetails = row[BANK_DETAILS] ?: "undefined",
                recipientName = row[RECIPIENT_NAME] ?: "undefined",
                recipientAddress = row[RECIPIENT_ADDRESS] ?: "undefined",
                bankName = row[BANK_NAME] ?: "undefined",
                bankAddress = row[BANK_ADDRESS] ?: "undefined",
                beneficiary = row[BENEFICIARY] ?: "undefined",
                correspondentBank = row[CORRESPONDENT_BANK] ?: "undefined",
                correspondentAccount = row[CORRESPONDENT_ACCOUNT] ?: "undefined",
                paymentSystem = "SWIFT",
                information = row[ADDITIONAL_INFORMATION] ?: "undefined",
                paymentSystemNumber = row[PAYMENT_SYSTEM]?.split(" ")?.get(1) ?: "undefined"
            )
        }
    }

}