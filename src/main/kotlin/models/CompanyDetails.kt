package models

import org.apache.commons.lang.RandomStringUtils
import java.time.LocalDate

data class CompanyDetails(
    val shortName: String,
    val fullName: String,
    val address: String,
    val registrationCountry: String,
    val registrationDate: String,
    val regDocNumber: String,
    val regDocDate: String
) {
    companion object {
        fun generate(): CompanyDetails {
            return CompanyDetails(
                shortName = RandomStringUtils.randomAlphanumeric(10),
                fullName = RandomStringUtils.randomAlphanumeric(20),
                address = RandomStringUtils.randomAlphanumeric(30),
                registrationCountry = listOf(
                    "Albania",
                    "Algeria",
                    "Andorra",
                    "Belgium",
                    "Belarus"
                ).random(),
                registrationDate = LocalDate.now().toString(),
                regDocDate = LocalDate.now().toString(),
                regDocNumber = RandomStringUtils.randomNumeric(10)
            )
        }
    }
}