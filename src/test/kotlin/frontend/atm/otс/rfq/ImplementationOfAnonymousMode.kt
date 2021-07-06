package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmAdminCompaniesPage
import pages.atm.AtmProfilePage
import pages.atm.AtmRFQPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Implementation of anonymous mode.")
@TmsLink("ATMCH-2465")
class ImplementationOfAnonymousMode : BaseTest() {
    private val user1 = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val user2 = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3694")
    @Test
    @DisplayName("RFQ. Anonymous mode enabled.")
    fun anonymousModeEnabled() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val companyNameUserOne = utils.helpers.openPage<AtmProfilePage>(driver) { submit(user1) }.getCompanyName()
        utils.helpers.openPage<AtmWalletPage>(driver).logout()

        with(utils.helpers.openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            setFlagShowOnUtf(companyNameUserOne, false)
        }

        with(utils.helpers.openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(AtmRFQPage.OperationType.BUY, baseAsset, quoteAsset, amount, "1", user1)
        }
        utils.helpers.openPage<AtmWalletPage>(driver).logout()

        with(utils.helpers.openPage<AtmRFQPage>(driver) { submit(user2) }) {
            e {
                click(viewRequest)

                val myOffer = incomingOffers.find {
                    it.baseAmount == amount
                } ?: error("Can't find offer with base amount '$amount'")

                assert {
                    elementPresented(myOffer.showCounterparty)
                }

                click(myOffer.showCounterparty)

                wait {
                    until("Offer with amount $amount should contain company name $companyNameUserOne") {
                        check {
                            myOffer.counterpartyValue.text.contains(companyNameUserOne)
                        }
                    }
                }

                page.navigate().refresh()

                val offer = incomingOffers.find {
                    it.baseAmount == amount
                } ?: error("Can't find offer with unit price '$amount'")

                MatcherAssert.assertThat(
                    "Offer with amount $amount should contain company name $companyNameUserOne",
                    offer.counterpartyValue.text.contains(companyNameUserOne)
                )

                assert {
                    elementNotPresented(offer.showCounterparty)
                }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3696")
    @Test
    @DisplayName("RFQ. Anonymous mode disabled.")
    fun anonymousModeDisabled() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val companyNameUserOne = utils.helpers.openPage<AtmProfilePage>(driver) { submit(user1) }.getCompanyName()
        utils.helpers.openPage<AtmWalletPage>(driver).logout()

        with(utils.helpers.openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            setFlagShowOnUtf(companyNameUserOne, true)
        }

        with(utils.helpers.openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(AtmRFQPage.OperationType.BUY, baseAsset, quoteAsset, amount, "1", user1)
        }
        utils.helpers.openPage<AtmWalletPage>(driver).logout()

        with(utils.helpers.openPage<AtmRFQPage>(driver) { submit(user2) }) {
            e {
                click(viewRequest)

                val offer = incomingOffers.find {
                    it.baseAmount == amount
                } ?: error("Can't find offer with unit price '$amount'")

                MatcherAssert.assertThat(
                    "Offer with amount $amount should contain company name $companyNameUserOne",
                    offer.counterpartyValue.text.contains(companyNameUserOne)
                )

                assert {
                    elementNotPresented(offer.showCounterparty)
                }
            }
        }
    }
}