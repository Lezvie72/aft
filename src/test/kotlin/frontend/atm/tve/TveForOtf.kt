package frontend.atm.tve

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.*
import ru.yandex.qatools.htmlelements.element.Button
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.to
import java.math.BigDecimal

@Tag("TVE")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Epic("Frontend")
@Feature("TVE.Streaming")
@Story("TVE for OTF")
class TveForOtf : BaseTest() {
    companion object {
        private var setupTve = mutableSetOf<String>()
    }
    // preconditions
    private val baseAsset = CoinType.VT
    private val baseAssetIT = CoinType.IT
    private val quoteAsset = CoinType.CC
    private val amountBuy = OtfAmounts.AMOUNT_10.amount
    private val maturityDateInnerDate = "22 September 2020"
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04

    @Order(10)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3720")
    @Test
    @DisplayName("TVE.Steaming. Green corridor checking")
    fun steamingGreenCorridorChecking() {
        // preconditions
        val unitPriceOffer = BigDecimal("2.500${RandomStringUtils.randomNumeric(5)}")
        val greenCorridorSize = "25"
        val yellowCorridorSize = "40"
        val baseAssetRate = "3"
        val quoteAssetRate = "1"

        // setting TVE
        with(openPage<AtmAdminTvePage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                // set corridors size
                setupCorridors(greenCorridorSize, yellowCorridorSize)

                // set states active for tokens
                setStateForToken(baseAsset, true)

                page.navigate().refresh()
                wait(5) { untilPresented(tveSettingsLabel) }
                setStateForToken(quoteAsset, true)

                // setting rate - base
                setupRateForToken(baseAsset, baseAssetRate)
                page.navigate().refresh()
                wait(5) { untilPresented(tveSettingsLabel) }

                // setting rate - quote
                setupRateForToken(quoteAsset, quoteAssetRate)
                wait(5) { untilPresented(tveSettingsLabel) }

                assertThat(
                    "Settings for TVE for green corridor was saved",
                    greenCorridor.text == greenCorridorSize
                )
                assertThat(
                    "Settings for TVE for yellow corridor was saved",
                    yellowCorridor.text == yellowCorridorSize
                )
            }
        }
        setupTve.add("Green")
        openPage<AtmAdminPage>(driver).logout()

        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$baseAsset/$quoteAsset",
                "$amountBuy $baseAsset",
                unitPriceOffer.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                userOne
            )
        }
        openPage<AtmProfilePage>(driver).logout()


        // check offer
        with(openPage<AtmStreamingPage>(driver) { submit(userTwo) }) {
            e {
                click(overview)
                click(overviewBreadcrumbs)
                setFilterBuyToday(baseAsset, quoteAsset)
                val incomingOffer = overviewOffersList.find {
                    it.unitPriceAmount == unitPriceOffer
                }?.to<Button>("Row rate with amount $unitPriceOffer")
                    ?: error("Couldn't find offer with amount $unitPriceOffer")
                click(incomingOffer)
                alert { checkErrorAlert() }
                acceptOffer(userTwo)
                alert { checkErrorAlert() }
                assert {
                    elementShouldBeHide(acceptOffer, 10)
                }
                setFilterBuyToday(baseAsset, quoteAsset)
                assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }
    }

    @Order(11)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3749")
    @Test
    @DisplayName("TVE.Blocktrade. Green corridor checking")
    fun blocktradeGreenCorridorChecking() {
        // preconditions
        val amountToReceive = BigDecimal("4.000${RandomStringUtils.randomNumeric(5)}")
        val amountToSend = BigDecimal("10.000${RandomStringUtils.randomNumeric(5)}")
        val typeCorridor = "Green"

        Assumptions.assumeTrue(
            setupTve.contains(typeCorridor),
            "Check completed preconditions - setup $typeCorridor corridor"
        )
        // create offer
        val walletID = openPage<AtmWalletPage>(driver) { submit(userTwo) }.takeWalletID()
        val companyName = openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(userOne) }) {
            createP2P(
                walletID,
                companyName,
                quoteAsset,
                amountToSend.toString(),
                baseAsset,
                amountToReceive.toString(),
                AtmP2PPage.ExpireType.GOOD_TILL_CANCELLED,
                userOne
            )
        }
        openPage<AtmProfilePage>(driver).logout()


        // check offer
        with(openPage<AtmP2PPage>(driver) { submit(userTwo) }) {
            e {
                click(viewIncomingP2P)
                click(incomingBT)
                setFilterBuyToday(quoteAsset)
                val incomingOffer = incomingOffers.find {
                    it.amountToSend == amountToReceive
                }?.to<Button>("Row rate with amount $amountToReceive")
                    ?: error("Couldn't find offer with amount $amountToReceive")
                click(incomingOffer)
                alert { checkErrorAlert() }
                click(acceptFromDetails)
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }
                assert {
                    elementShouldBeHide(acceptFromDetails, 10)
                }
                setFilterBuyToday(quoteAsset)
                assertThat(
                    "Offer with unitPrice $amountToReceive should not exist",
                    !isOfferExist(amountToReceive, incomingOffers)
                )
            }
        }
    }

    @Order(12)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3736")
    @Test
    @DisplayName("TVE.RFQ. Green corridor checking")
    fun rfqGreenCorridorChecking() {
        // preconditions
        val amount = BigDecimal("5.000${RandomStringUtils.randomNumeric(5)}")
        val totalOfferAmount = BigDecimal("14.000${RandomStringUtils.randomNumeric(5)}")
        val typeCorridor = "Green"

        Assumptions.assumeTrue(
            setupTve.contains(typeCorridor),
            "Check completed preconditions - setup $typeCorridor corridor"
        )

        // create offer
        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            createRFQ(AtmRFQPage.OperationType.BUY, baseAsset, quoteAsset, amount, "1", userOne)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        // deal
        with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
            createDeal(amount, totalOfferAmount, "1", userTwo)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        // accept and check
        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            acceptOffer(amount, totalOfferAmount, userOne)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
            e {
                click(viewRequest)
                setFilterBuyToday(baseAsset, quoteAsset)
                assertThat(
                    "Offer with amount $amount should not exist",
                    !isOfferExist(amount, outgoingOffers)
                )
            }
        }
    }

    @Order(13)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3733")
    @Test
    @DisplayName("TVE.Steaming. Yellow corridor checking")
    fun steamingYellowCorridorChecking() {
        // preconditions
        val unitPriceOffer = BigDecimal("2.000${RandomStringUtils.randomNumeric(5)}")
        val greenCorridorSize = "20"
        val yellowCorridorSize = "45"
        val baseAssetRate = "3"
        val quoteAssetRate = "1"

        // setting TVE
        with(openPage<AtmAdminTvePage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                // set corridors size
                setupCorridors(greenCorridorSize, yellowCorridorSize)

                // set states active for tokens
                setStateForToken(baseAsset, true)

                page.navigate().refresh()
                wait(5) { untilPresented(tveSettingsLabel) }
                setStateForToken(quoteAsset, true)

                // setting rate - base
                setupRateForToken(baseAsset, baseAssetRate)
                page.navigate().refresh()
                wait(5) { untilPresented(tveSettingsLabel) }

                // setting rate - quote
                setupRateForToken(quoteAsset, quoteAssetRate)
                wait(5) { untilPresented(tveSettingsLabel) }

                assertThat(
                    "Settings for TVE for green corridor was saved",
                    greenCorridor.text == greenCorridorSize
                )
                assertThat(
                    "Settings for TVE for yellow corridor was saved",
                    yellowCorridor.text == yellowCorridorSize
                )
            }
        }
        setupTve.add("Yellow")
        openPage<AtmAdminPage>(driver).logout()


        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$baseAsset/$quoteAsset",
                "$amountBuy $baseAsset",
                unitPriceOffer.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                userOne
            )
        }
        openPage<AtmProfilePage>(driver).logout()

        // check offer
        with(openPage<AtmStreamingPage>(driver) { submit(userTwo) }) {
            e {
                click(overview)
                click(overviewBreadcrumbs)
                setFilterBuyToday(baseAsset, quoteAsset)
                val incomingOffer = overviewOffersList.find {
                    it.unitPriceAmount == unitPriceOffer
                }?.to<Button>("Row rate with amount $unitPriceOffer")
                    ?: error("Couldn't find offer with amount $unitPriceOffer")
                click(incomingOffer)
                alert { checkErrorAlert() }
                acceptOffer(userTwo)
                alert { checkErrorAlert() }
                setFilterBuyToday(baseAsset, quoteAsset)
                assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }
    }

    @Order(14)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3757")
    @Test
    @DisplayName("TVE.Blocktrade. Yellow corridor checking")
    fun blocktradeYellowCorridorChecking() {
        // preconditions
        val amountToReceive = BigDecimal("5.000${RandomStringUtils.randomNumeric(5)}")
        val amountToSend = BigDecimal("10.000${RandomStringUtils.randomNumeric(5)}")
        val typeCorridor = "Yellow"

        Assumptions.assumeTrue(
            setupTve.contains(typeCorridor),
            "Check completed preconditions - setup $typeCorridor corridor"
        )

        // create offer
        val walletID = openPage<AtmWalletPage>(driver) { submit(userTwo) }.takeWalletID()
        val companyName = openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(userOne) }) {
            createP2P(
                walletID,
                companyName,
                quoteAsset,
                amountToSend.toString(),
                baseAsset,
                amountToReceive.toString(),
                AtmP2PPage.ExpireType.GOOD_TILL_CANCELLED,
                userOne
            )
        }
        openPage<AtmProfilePage>(driver).logout()

        // check offer
        with(openPage<AtmP2PPage>(driver) { submit(userTwo) }) {
            e {
                click(viewIncomingP2P)
                click(incomingBT)
                setFilterBuyToday(quoteAsset)
                val incomingOffer = incomingOffers.find {
                    it.amountToSend == amountToReceive
                } ?: error("Couldn't find offer with amount $amountToReceive")
                click(incomingOffer)
                alert { checkErrorAlert() }
                click(acceptFromDetails)
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }
                assert {
                    elementShouldBeHide(acceptFromDetails, 10)
                }
                setFilterBuyToday(quoteAsset)
                assertThat(
                    "Offer with unitPrice $amountToReceive should not exist",
                    !isOfferExist(amountToReceive, incomingOffers)
                )
            }
        }
    }

    @Order(15)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3745")
    @Test
    @DisplayName("TVE.RFQ. Yellow corridor checking")
    fun rfqYellowCorridorChecking() {
        // preconditions
        val amount = BigDecimal("5.000${RandomStringUtils.randomNumeric(5)}")
        val totalOfferAmount = BigDecimal("11.000${RandomStringUtils.randomNumeric(5)}")
        val typeCorridor = "Yellow"

        Assumptions.assumeTrue(
            setupTve.contains(typeCorridor),
            "Check completed preconditions - setup $typeCorridor corridor"
        )

        // create offer
        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            createRFQ(AtmRFQPage.OperationType.BUY, baseAsset, quoteAsset, amount, "1", userOne)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        // deal
        with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
            createDeal(amount, totalOfferAmount, "1", userTwo)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        // accept and check
        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            acceptOffer(amount, totalOfferAmount, userOne)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
            e {
                click(viewRequest)
                setFilterBuyToday(baseAsset, quoteAsset)
                assertThat(
                    "Offer with amount $amount should not exist",
                    !isOfferExist(amount, outgoingOffers)
                )
            }
        }
    }

    @Order(16)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3734")
    @Test
    @DisplayName("TVE.Steaming. Red corridor checking")
    fun steamingRedCorridorChecking() {
        // preconditions
        val unitPriceOffer = BigDecimal("1.000${RandomStringUtils.randomNumeric(5)}")
        val greenCorridorSize = "20"
        val yellowCorridorSize = "45"
        val baseAssetRate = "3"
        val quoteAssetRate = "1"


        // setting TVE
        with(openPage<AtmAdminTvePage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                // set corridors size
                setupCorridors(greenCorridorSize, yellowCorridorSize)

                // set states active for tokens
                setStateForToken(baseAsset, true)
                page.navigate().refresh()
                wait(5) { untilPresented(tveSettingsLabel) }
                setStateForToken(quoteAsset, true)

                // setting rate - base
                setupRateForToken(baseAsset, baseAssetRate)
                page.navigate().refresh()
                wait(5) { untilPresented(tveSettingsLabel) }

                // setting rate - quote
                setupRateForToken(quoteAsset, quoteAssetRate)
                wait(5) { untilPresented(tveSettingsLabel) }

                assertThat(
                    "Settings for TVE for green corridor was saved",
                    greenCorridor.text == greenCorridorSize
                )
                assertThat(
                    "Settings for TVE for yellow corridor was saved",
                    yellowCorridor.text == yellowCorridorSize
                )
            }
        }
        setupTve.add("Red")
        openPage<AtmAdminPage>(driver).logout()


        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$baseAsset/$quoteAsset",
                "$amountBuy $baseAsset",
                unitPriceOffer.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                userOne
            )
        }
        openPage<AtmProfilePage>(driver).logout()

        // check offer
        with(openPage<AtmStreamingPage>(driver) { submit(userTwo) }) {
            e {
                click(overview)
                click(overviewBreadcrumbs)
                setFilterBuyToday(baseAsset, quoteAsset)
                val incomingOffer = overviewOffersList.find {
                    it.unitPriceAmount == unitPriceOffer
                }?.to<Button>("Row rate with amount $unitPriceOffer")
                    ?: error("Couldn't find offer with amount $unitPriceOffer")
                click(incomingOffer)
                click(acceptOffer)
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert {
                    waitAndCheckErrorAlertWithMessage("Unit price is out of the allowed price range")
                }
                click(cancelOffer)
                assert {
                    elementShouldBeHide(acceptOffer, 10)
                }
                setFilterBuyToday(baseAsset, quoteAsset)
                assertThat(
                    "Offer with unitPrice $unitPriceOffer should exist",
                    isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }
    }

    @Order(17)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3759")
    @Test
    @DisplayName("TVE.Blocktrade. Red corridor checking")
    fun blocktradeRedCorridorChecking() {
        // preconditions
        val amountToReceive = BigDecimal("10.000${RandomStringUtils.randomNumeric(5)}")
        val amountToSend = BigDecimal("10.000${RandomStringUtils.randomNumeric(5)}")
        val typeCorridor = "Red"

        Assumptions.assumeTrue(
            setupTve.contains(typeCorridor),
            "Check completed preconditions - setup $typeCorridor corridor"
        )

        // create offer
        val walletID = openPage<AtmWalletPage>(driver) { submit(userTwo) }.takeWalletID()
        val companyName = openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(userOne) }) {
            createP2P(
                walletID,
                companyName,
                quoteAsset,
                amountToSend.toString(),
                baseAsset,
                amountToReceive.toString(),
                AtmP2PPage.ExpireType.GOOD_TILL_CANCELLED,
                userOne
            )
        }
        openPage<AtmProfilePage>(driver).logout()

        // check offer
        with(openPage<AtmP2PPage>(driver) { submit(userTwo) }) {
            e {
                click(viewIncomingP2P)
                click(incomingBT)
                setFilterBuyToday(quoteAsset)
                val incomingOffer = incomingOffers.find {
                    it.amountToSend == amountToReceive
                } ?: error("Couldn't find offer with amount $amountToReceive")
                click(incomingOffer)
                alert { checkErrorAlert() }
                click(acceptFromDetails)
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert {
                    waitAndCheckErrorAlertWithMessage("Unit price is out of the allowed price range")
                }
                click(closeBlockTradeCard)
                assert {
                    elementShouldBeHide(acceptFromDetails, 10)
                }
                assertThat(
                    "Offer with unitPrice $amountToReceive should not exist",
                    isOfferExist(amountToReceive, incomingOffers)
                )
            }
        }
    }

    @Order(18)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3746")
    @Test
    @DisplayName("TVE.RFQ. Red corridor checking")
    fun rfqRedCorridorChecking() {
        // preconditions
        val amount = BigDecimal("5.000${RandomStringUtils.randomNumeric(5)}")
        val totalOfferAmount = BigDecimal("5.000${RandomStringUtils.randomNumeric(5)}")
        val typeCorridor = "Red"

        Assumptions.assumeTrue(
            setupTve.contains(typeCorridor),
            "Check completed preconditions - setup $typeCorridor corridor"
        )

        // create offer
        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            createRFQ(AtmRFQPage.OperationType.BUY, baseAsset, quoteAsset, amount, "1", userOne)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        // deal
        with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
            createDeal(amount, totalOfferAmount, "1", userTwo)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        // accept and check
        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            e {
                acceptOffer(amount, totalOfferAmount, userOne)
                alert {
                    waitAndCheckErrorAlertWithMessage("Unit price is out of the allowed price range")
                }
                click(viewRequest)
                setFilterBuyToday(baseAsset, quoteAsset)
                assertThat(
                    "Offer with amount $amount should not exist",
                    !isOfferExist(amount, outgoingOffers)
                )
            }
        }
    }

    @Order(19)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3735")
    @Test
    @DisplayName("TVE.Steaming. There is no corridor validation for IT")
    fun steamingThereIsNoCorridorValidationForIt() {
        // preconditions
        val unitPriceOffer = BigDecimal("1.000${RandomStringUtils.randomNumeric(5)}")
        val greenCorridorSize = "20"
        val yellowCorridorSize = "40"
        val baseAssetRate = "0"
        val quoteAssetRate = "1"


        // setting TVE
        with(openPage<AtmAdminTvePage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                // set corridors size
                setupCorridors(greenCorridorSize, yellowCorridorSize)

                // set states active for tokens
                setStateForToken(baseAssetIT, false)
                page.navigate().refresh()
                wait(5) { untilPresented(tveSettingsLabel) }
                setStateForToken(quoteAsset, true)

                // setting rate - base
                setupRateForToken(baseAssetIT, baseAssetRate)
                page.navigate().refresh()
                wait(5) { untilPresented(tveSettingsLabel) }

                // setting rate - quote
                setupRateForToken(quoteAsset, quoteAssetRate)
                wait(5) { untilPresented(tveSettingsLabel) }

                assertThat(
                    "Settings for TVE for green corridor was saved",
                    greenCorridor.text == greenCorridorSize
                )
                assertThat(
                    "Settings for TVE for yellow corridor was saved",
                    yellowCorridor.text == yellowCorridorSize
                )
            }
        }
        setupTve.add("IT")
        openPage<AtmAdminPage>(driver).logout()


        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$baseAssetIT/$quoteAsset",
                "$amountBuy $baseAssetIT",
                unitPriceOffer.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                userOne, maturityDateInnerDate
            )
        }
        openPage<AtmProfilePage>(driver).logout()


        // check offer
        with(openPage<AtmStreamingPage>(driver) { submit(userTwo) }) {
            e {
                click(overview)
                click(overviewBreadcrumbs)
                setFilterBuyToday(baseAssetIT, quoteAsset, maturityDateInnerDate)
                click(findOfferBy(unitPriceOffer, overviewOffersList))
                alert { checkErrorAlert() }
                acceptOffer(userTwo)
                alert { checkErrorAlert() }
                assert {
                    elementShouldBeHide(acceptOffer, 10)
                }
                assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }
    }

    @Order(20)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3760")
    @Test
    @DisplayName("TVE.Blocktrade. There is no corridor validation for IT")
    fun blocktradeThereIsNoCorridorValidationForIt() {
        // preconditions
        val amountToReceive = BigDecimal("10.000${RandomStringUtils.randomNumeric(5)}")
        val amountToSend = BigDecimal("10.000${RandomStringUtils.randomNumeric(5)}")
        val typeCorridor = "IT"

        Assumptions.assumeTrue(
            setupTve.contains(typeCorridor),
            "Check completed preconditions - setup $typeCorridor corridor"
        )


        // create offer
        val walletID = openPage<AtmWalletPage>(driver) { submit(userTwo) }.takeWalletID()
        val companyName = openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(userOne) }) {
            createP2P(
                walletID,
                companyName,
                baseAssetIT,
                amountToSend.toString(),
                baseAsset,
                amountToReceive.toString(),
                AtmP2PPage.ExpireType.GOOD_TILL_CANCELLED,
                userOne, maturityDateInnerDate
            )
        }
        openPage<AtmProfilePage>(driver).logout()


        // check offer
        with(openPage<AtmP2PPage>(driver) { submit(userTwo) }) {
            e {
                click(viewIncomingP2P)
                click(incomingBT)
                setFilterBuyToday(baseAssetIT)
                val incomingOffer = incomingOffers.find {
                    it.amountToReceive == amountToReceive
                } ?: error("Couldn't find offer with amount $amountToReceive")
                click(incomingOffer)
                alert { checkErrorAlert() }
                click(acceptFromDetails)
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }
                assert {
                    elementShouldBeHide(acceptFromDetails, 10)
                }
                assertThat(
                    "Offer with unitPrice $amountToReceive should not exist",
                    !isOfferExist(amountToReceive, incomingOffers)
                )
            }
        }
    }

    @Order(21)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3748")
    @Test
    @DisplayName("TVE.RFQ. There is no corridor validation for IT")
    fun rfqThereIsNoCorridorValidationForIt() {
        // preconditions
        val amount = BigDecimal("5.000${RandomStringUtils.randomNumeric(5)}")
        val totalOfferAmount = BigDecimal("5.000${RandomStringUtils.randomNumeric(5)}")
        val typeCorridor = "IT"

        Assumptions.assumeTrue(
            setupTve.contains(typeCorridor),
            "Check completed preconditions - setup $typeCorridor corridor"
        )

        // create offer
        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            createRFQ(AtmRFQPage.OperationType.BUY, baseAssetIT, quoteAsset, amount, "1", userOne)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        // deal
        with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
            createDeal(amount, totalOfferAmount, "1", userTwo)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        // accept and check
        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            acceptOffer(amount, totalOfferAmount, userOne)
            alert { checkErrorAlert() }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
            e {
                click(viewRequest)
                assertThat(
                    "Offer with amount $amount should not exist",
                    !isOfferExist(amount, outgoingOffers)
                )
            }
        }
    }

    @Order(22)
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("DEFAULT")
    @Test
    @DisplayName("TVE. Set default settings in TVE admin")
    fun setDefaultSettingsForTve() {
        // preconditions
        val greenCorridorSize = "20"
        val yellowCorridorSize = "40"

        // setting TVE
        with(openPage<AtmAdminTvePage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                // set corridors size
                setupCorridors(greenCorridorSize, yellowCorridorSize)

                // set states active for tokens
                setStateForToken(baseAsset, false)

                page.navigate().refresh()
                wait(5) { untilPresented(tveSettingsLabel) }
                setStateForToken(quoteAsset, false)

                page.navigate().refresh()
                wait(5) { untilPresented(tveSettingsLabel) }
                setStateForToken(baseAssetIT, false)

                assertThat(
                    "Settings for TVE for green corridor was saved",
                    greenCorridor.text == greenCorridorSize
                )
                assertThat(
                    "Settings for TVE for yellow corridor was saved",
                    yellowCorridor.text == yellowCorridorSize
                )
            }
        }
    }
}