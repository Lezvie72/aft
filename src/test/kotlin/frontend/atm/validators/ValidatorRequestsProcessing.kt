package frontend.atm.validators

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.VT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import pages.atm.AtmAdminNodesManagementPage.NodeType
import pages.atm.AtmValidatorPage.NodeType.ENDORSER
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.VALIDATORS.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Story("Validator requests processing")
@Feature("Validator")
class ValidatorRequestsProcessing : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET)
    @TmsLink("ATMCH-4155")
    @Test
    @DisplayName("Nodes management. Check interface")
    fun nodesManagementCheckInterface() {

        val userBuyer = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = userBuyer.mainWallet

        step("User buy VT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, "1000", mainWallet)
                openPage<AtmMarketplacePage>(driver) { submit(userBuyer) }.buyTokenNew(
                    VT,
                    "1000",
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

}
