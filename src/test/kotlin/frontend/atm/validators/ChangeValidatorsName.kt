package frontend.atm.validators

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType.CC
import models.CoinType.VT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import pages.atm.AtmAdminNodesManagementPage.NodeType
import pages.atm.AtmIssuancesPage.LimitType.MAX
import pages.atm.AtmIssuancesPage.OperationType.SELL
import pages.atm.AtmValidatorPage.NodeType.ENDORSER
import pages.atm.AtmValidatorPage.NodeType.ORDERER
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.VALIDATORS.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Feature("Validator")
@Story("Change Validators name")
class ChangeValidatorsName : BaseTest() {

    private val amountOrderer = "11000"

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-5743")
    @Test
    @DisplayName("Nodes management.Admin panel view")
    fun nodesManagementAdminPanelView() {

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
                    "10000",
                    userBuyer,
                    mainWallet
                )
            }
        }
        openPage<AtmWalletPage>(driver)

        val companyNameValue = openPage<AtmProfilePage>(driver) { submit(userBuyer) }.getCompanyName()

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                addNodeValidator(ENDORSER, userBuyer, mainWallet)
            }
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
                findAndOpenRequestOfNode(NodeType.ENDORSER, companyNameValue, "STAKE ACCEPTED")
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
            }
        }
    }

    @Issue("ATMCH-5773")
    @ResourceLock(Constants.ROLE_USER_VALIDATOR_WITHOUT_2FA)
    @TmsLink("ATMCH-5744")
    @Test
    @DisplayName("Node management. Admin panel. Changing Node status name")
    fun validatorOrderer() {

        val userBuyer = Users.ATM_USER_VALIDATOR_WITHOUT_2FA
        val mainWallet = userBuyer.walletList[0]

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
                    "10000",
                    userBuyer,
                    mainWallet
                )
            }
        }

        openPage<AtmWalletPage>(driver)

        val companyNameValue = openPage<AtmProfilePage>(driver) { submit(userBuyer) }.getCompanyName()

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(addNode)
                    select(nodeType, ORDERER.name)
                    click(submit)
                }
            }
            openPage<AtmValidatorPage>(driver)

            step("User go ot Validator page check elements and buy Node") {
                with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                    addNodeValidator(ENDORSER, userBuyer, mainWallet)
                }
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

                    findRequestOfNodeAndCheckStatus(NodeType.ORDERER, companyNameValue, "Stake", "Awaiting", "", "")
                    openPage<AtmAdminNodesManagementPage>(driver)
                    findRequestOfNodeAndCheckStatus(
                        NodeType.ENDORSER,
                        companyNameValue,
                        "Stake accepted",
                        "Awaiting",
                        mainWallet.walletId,
                        ""
                    )

                }
            }
        }
    }
}
