package frontend.atm.otс.blocktrade

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmAdminCompaniesPage
import pages.atm.AtmP2PPage
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.BLOCKTRADE.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Blocktrade. Implementation of anonymous mode.")
@TmsLink("ATMCH-2467")
class ImplementationOfAnonymousMode : BaseTest() {
    private val user1 = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val user2 = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3697")
    @Test
    @DisplayName("Blocktrade. Anonymous mode enabled.")
    fun anonymousModeEnabled() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val companyNameUserOne = openPage<AtmProfilePage>(driver) { submit(user1) }.getCompanyName()
        openPage<AtmWalletPage>(driver).logout()
        val walletID = openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()
        val companyNameUserTwo = openPage<AtmProfilePage>(driver).getCompanyName()
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            setFlagShowOnUtf(companyNameUserOne, false)
        }
        openPage<AtmAdminCompaniesPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(
                walletID,
                companyNameUserTwo,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                AtmP2PPage.ExpireType.GOOD_TILL_CANCELLED,
                user1
            )
        }
        openPage<AtmProfilePage>(driver).logout()
        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            e {
                click(viewIncomingP2P)

                val myOffer = incomingOffers.find {
                    it.amountToReceive == amount
                } ?: error("Can't find offer with unit price '$amount'")

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
                    it.amountToReceive == amount
                } ?: error("Can't find offer with unit price '$amount'")

                assertThat(
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
    @TmsLink("ATMCH-3699")
    @Test
    @DisplayName("Blocktrade. Anonymous mode disabled.")
    fun anonymousModeDisabled() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val companyNameUserOne = openPage<AtmProfilePage>(driver) { submit(user1) }.getCompanyName()
        openPage<AtmWalletPage>(driver).logout()
        val walletID = openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()
        val companyNameUserTwo = openPage<AtmProfilePage>(driver).getCompanyName()
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            setFlagShowOnUtf(companyNameUserOne, true)
        }
        openPage<AtmAdminCompaniesPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(
                walletID,
                companyNameUserTwo,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                AtmP2PPage.ExpireType.GOOD_TILL_CANCELLED,
                user1
            )
        }
        openPage<AtmProfilePage>(driver).logout()
        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            e {
                click(viewIncomingP2P)

                val offer = incomingOffers.find {
                    it.amountToReceive == amount
                } ?: error("Can't find offer with unit price '$amount'")

                assertThat(
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