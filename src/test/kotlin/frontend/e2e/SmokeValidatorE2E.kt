package frontend.e2e

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import pages.atm.AtmAdminNodesManagementPage.NodeType.ENDORSER
import pages.atm.AtmAdminNodesManagementPage.NodeType.ORDERER
import pages.atm.AtmIssuancesPage.LimitType.MAX
import pages.atm.AtmIssuancesPage.OperationType.SELL
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.isChecked
import java.math.BigDecimal

@Tag(TagNames.Flow.SMOKEE2E)
@Epic("Frontend")
@Feature("E2E")
@Story("Validator")
class SmokeValidatorE2E : BaseTest() {

    private val amountOrderer = "10000"
    private val amountEndorser = "1000"

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-5227")
    @Test
    @DisplayName("Validator ENDORSER")
    fun validatorEndorser() {

        val userBuyer = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    CC,
                    SELL,
                    MAX, amountOrderer, itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    VT,
                    SELL,
                    MAX, amountOrderer, itIssuer, itWallet
                )
            }
        }
        AtmProfilePage(driver).logout()

        step("User buy VT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, amountOrderer, mainWallet)
                openPage<AtmMarketplacePage>(driver) { submit(userBuyer) }.buyOrReceiveToken(
                    VT,
                    amountOrderer,
                    userBuyer,
                    mainWallet
                )
            }
        }
        openPage<AtmWalletPage>(driver)

        val balanceBefore = step("User check balance before operation") {
            openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(VT, mainWallet.name)
        }

        val companyNameValue = openPage<AtmProfilePage>(driver) { submit(userBuyer) }.getCompanyName()

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(addNode)
                }
                assert {
                    elementPresented(submit)
                    elementPresented(cancel)
                    elementPresented(nodeSteps)
                    elementPresented(nodeType)
                }
                e {
                    select(nodeType, "ENDORSER")
                    click(submit)
                }
                assert {
                    elementPresented(submit)
                    elementPresented(cancel)
                    elementPresented(stakingWallet)
                    elementContainingTextPresented("AMOUNT TO STAKE")
                    elementContainingTextPresented("Available balance")
                }
                e {
                    select(stakingWallet, mainWallet.name)
                    click(submit)
                    signAndSubmitMessage(userBuyer, mainWallet.secretKey)
                    click(ok)
                }
                //TODO email
            }
        }

        val balanceAfter = step("User check balance after operation") {
            openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(VT, mainWallet.name)
        }

        step("Admin nodes management page check elements, upload certificate and approve Node") {
            with(openPage<AtmAdminNodesManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                assert {
                    elementPresented(search)
                    elementPresented(createDateFrom)
                    elementPresented(createDateTo)
                    elementPresented(updateDateFrom)
                    elementPresented(updateDateTo)
                    elementPresented(statusSelect)
                    elementContainingTextPresented("Request ID")
                    elementContainingTextPresented("Wallet ID")
                    elementContainingTextPresented("Node ID")
                    elementContainingTextPresented("Node type")
                    elementContainingTextPresented("Company ID")
                    elementContainingTextPresented("Company name")
                    elementContainingTextPresented("Request created")
                    elementContainingTextPresented("Acceptance date")
                    elementContainingTextPresented("Stake accepted")
                    elementContainingTextPresented("Certificate issued")
                    elementContainingTextPresented("Successfully tested")
                    elementContainingTextPresented("Node activated")
                    elementContainingTextPresented("Node status")
                    elementContainingTextPresented("Updated")
                }

                findAndOpenRequestOfNode(ENDORSER, companyNameValue, "STAKE ACCEPT")
                assert {
                    elementPresented(save)
                    elementPresented(cancel)
                    elementPresented(nodeActivated)
                    elementPresented(stake)
                    elementPresented(certificateIssued)
                    elementPresented(successfullyTested)
                    elementPresented(walletId)
                    elementPresented(nodeId)
                    elementPresented(companyName)
                    elementContainingTextPresented("Edit node")
                }

                uploadDocument(uploadCertificate, "endorosercert.pem")

                findAndOpenRequestOfNode(ENDORSER, companyNameValue, "CERTIFICATE ISSUED")
                e {
                    setCheckbox(successfullyTested, true)
                    click(save)
                }
                findAndOpenRequestOfNode(ENDORSER, "otfoperatonautotest", "ACTIVE")
                Assert.assertTrue("Node is activate", nodeActivated.isChecked())
            }
        }
        step("User go to Node check element and Get Reward") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                findActiveNode()
                assert {
                    elementContainingTextPresented("SUBMISSION DATE")
                    elementContainingTextPresented("STAKED AMOUNT")
                    elementContainingTextPresented("CERTIFICATE ISSUED DATE")
                    elementContainingTextPresented("PAYMENT DATE")
                    elementContainingTextPresented("CERTIFICATE")
                    elementPresented(downloadCertificateLink)
                    elementPresented(nodeCertificateDetails)
                }
                checkNodeCertificateStatus("Success")
                e {
                    click(ok)
                }

                e {
                    click(getReward)
                }
                signAndSubmitMessage(userBuyer, mainWallet.secretKey)
                assert {
                    elementContainingTextPresented("You have successfully released your validator reward")
                }
            }

        }

        assertThat(
            "Expected base balance: $balanceAfter, was: $balanceBefore",
            balanceAfter,
            Matchers.closeTo(balanceBefore - amountEndorser.toBigDecimal(), BigDecimal("0.01"))
        )

        AtmProfilePage(driver).logout()

        step("User change limit for order back") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    CC,
                    SELL,
                    MAX, amountEndorser, itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    VT,
                    SELL,
                    MAX, amountEndorser, itIssuer, itWallet
                )
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    @TmsLink("ATMCH-5227")
    @Test
    @DisplayName("Validator ORDERER")
    fun validatorOrderer() {

        val userBuyer = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    CC,
                    SELL,
                    MAX, amountOrderer, itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    VT,
                    SELL,
                    MAX, amountOrderer, itIssuer, itWallet
                )
            }
        }

        AtmProfilePage(driver).logout()

        step("User buy VT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, amountOrderer, mainWallet)
                openPage<AtmMarketplacePage>(driver) { submit(userBuyer) }.buyOrReceiveToken(
                    VT,
                    amountOrderer,
                    userBuyer,
                    mainWallet
                )
            }
        }

        openPage<AtmWalletPage>(driver)

        val balanceBefore = step("User check balance before operation") {
            openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(VT, mainWallet.name)
        }

        val companyNameValue = openPage<AtmProfilePage>(driver) { submit(userBuyer) }.getCompanyName()

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(addNode)
                }
                assert {
                    elementPresented(submit)
                    elementPresented(cancel)
                    elementPresented(nodeSteps)
                    elementPresented(nodeType)
                }
                e {
                    select(nodeType, "ORDERER")
                    click(submit)
                }
                assert {
                    elementPresented(submit)
                    elementPresented(cancel)
                    elementPresented(stakingWallet)
                    elementContainingTextPresented("AMOUNT TO STAKE")
                    elementContainingTextPresented("Available balance")
                }
                e {
                    select(stakingWallet, mainWallet.name)
                    click(submit)
                    signAndSubmitMessage(userBuyer, mainWallet.secretKey)
                    click(ok)
                }
                //TODO email
            }
        }

        val balanceAfter = step("User check balance after operation") {
            openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(VT, mainWallet.name)
        }

        step("Admin nodes management page check elements, upload certificate and approve Node") {
            with(openPage<AtmAdminNodesManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                assert {
                    elementPresented(search)
                    elementPresented(createDateFrom)
                    elementPresented(createDateTo)
                    elementPresented(updateDateFrom)
                    elementPresented(updateDateTo)
                    elementPresented(statusSelect)
                    elementContainingTextPresented("Request ID")
                    elementContainingTextPresented("Wallet ID")
                    elementContainingTextPresented("Node ID")
                    elementContainingTextPresented("Node type")
                    elementContainingTextPresented("Company ID")
                    elementContainingTextPresented("Company name")
                    elementContainingTextPresented("Request created")
                    elementContainingTextPresented("Acceptance date")
                    elementContainingTextPresented("Stake accepted")
                    elementContainingTextPresented("Certificate issued")
                    elementContainingTextPresented("Successfully tested")
                    elementContainingTextPresented("Node activated")
                    elementContainingTextPresented("Node status")
                    elementContainingTextPresented("Updated")
                }

                findAndOpenRequestOfNode(ORDERER, companyNameValue, "STAKE ACCEPT")
                assert {
                    elementPresented(save)
                    elementPresented(cancel)
                    elementPresented(nodeActivated)
                    elementPresented(stake)
                    elementPresented(certificateIssued)
                    elementPresented(successfullyTested)
                    elementPresented(walletId)
                    elementPresented(nodeId)
                    elementPresented(companyName)
                    elementContainingTextPresented("Edit node")
                }

                uploadDocument(uploadCertificate, "orderer.pem")

                findAndOpenRequestOfNode(ORDERER, companyNameValue, "CERTIFICATE ISSUED")
                e {
                    setCheckbox(successfullyTested, true)
                    click(save)
                }
                findAndOpenRequestOfNode(ORDERER, companyNameValue, "ACTIVE")
                Assert.assertTrue("Node is activate", nodeActivated.isChecked())
            }
        }
        step("User go to Node check element and Get Reward") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                findActiveNode()
                assert {
                    elementContainingTextPresented("SUBMISSION DATE")
                    elementContainingTextPresented("STAKED AMOUNT")
                    elementContainingTextPresented("CERTIFICATE ISSUED DATE")
                    elementContainingTextPresented("PAYMENT DATE")
                    elementContainingTextPresented("CERTIFICATE")
                    elementPresented(downloadCertificateLink)
                    elementPresented(nodeCertificateDetails)
                }
                checkNodeCertificateStatus("Success")
                e {
                    click(ok)
                }

                e {
                    click(getReward)
                }
                signAndSubmitMessage(userBuyer, mainWallet.secretKey)
                assert {
                    elementContainingTextPresented("You have successfully released your validator reward")
                }
            }

        }

        assertThat(
            "Expected base balance: $balanceAfter, was: $balanceBefore",
            balanceAfter,
            Matchers.closeTo(balanceBefore - amountOrderer.toBigDecimal(), BigDecimal("0.01"))
        )

        AtmProfilePage(driver).logout()

        step("User change limit for order back") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    CC,
                    SELL,
                    MAX, amountEndorser, itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    VT,
                    SELL,
                    MAX, amountEndorser, itIssuer, itWallet
                )
            }
        }
    }
}
