package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.*
import utils.TagNames
import utils.helpers.Users
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Admin with viewer role checks links")
class AdminRolesVIEWER: BaseTest(){
    @TmsLink("ATMCH-5510")
    @Test
    @DisplayName("Admin with viewer role checks links")
    fun adminWithViewerRoleChecksLinks() {
        val user1 = Users.ATM_USER_VIEWER_ROLE
        with(utils.helpers.openPage<AtmAdminEmployeesPage>(driver) { submit(user1) }) {
            step("Check all tabs are changed for admin with viewer role") {
                checkAllTabsAreChangedForAdminWithViewerRole()
            }
        }
        with(utils.helpers.openPage<AtmAdminEmployeesPage>(driver)) {
            assert {
                elementWithTextPresented(" Employees approval ")
            }
        }
        with(utils.helpers.openPage<AtmAdminPaymentsPage>(driver)) {
            assert {
                elementWithTextPresented(" Payments ")
            }
        }
        with(utils.helpers.openPage<AtmAdminFiatWithdrawalPage>(driver)) {
            assert {
                elementWithTextPresented(" Fiat withdraw ")
            }
        }
        with(utils.helpers.openPage<AtmAdminCompaniesPage>(driver)) {
            assert {
                elementWithTextPresented(" Companies ")
            }
        }
        with(utils.helpers.openPage<AtmAdminBankDetailsPage>(driver)) {
            assert {
                elementWithTextPresented(" Bank details ")
            }
        }
//        with(utils.helpers.openPage<AtmAdminBankDetailsPage>(driver)) {
//            e {
//                click(tokenTab)
//            }
//            assert {
//                elementWithTextPresented(" Tokens ")
//            }
//        }
        with(utils.helpers.openPage<AtmAdminRegisterOfIssuersPage>(driver)) {
            assert {
                elementWithTextPresented(" Register of issuers ")
            }
        }






        with(utils.helpers.openPage<AtmAdminFinancialDataSourcesManagement>(driver)) {
            assert {
                elementWithTextPresented(" Financial data sources management ")
            }
        }
        with(utils.helpers.openPage<AtmAdminGeneralSettingsPage>(driver)) {
            assert {
                elementWithTextPresented(" OTF general settings ")
            }
        }
        with(utils.helpers.openPage<AtmAdminStreamingSettingsPage>(driver)) {
            assert {
                elementWithTextPresented(" Streaming settings ")
            }
        }
        with(utils.helpers.openPage<AtmAdminRfqSettingsPage>(driver)) {
            assert {
                elementWithTextPresented(" Rfq settings ")
            }
        }
        with(utils.helpers.openPage<AtmAdminBlocktradeSettingsPage>(driver)) {
            assert {
                elementWithTextPresented(" Blocktrade settings ")
            }
        }
        with(utils.helpers.openPage<AtmAdminTvePage>(driver)) {
            assert {
                elementWithTextPresented(" Tve settings ")
            }
        }
        with(utils.helpers.openPage<AtmAdminAccessRightPage>(driver)) {
            assert {
                elementWithTextPresented(" Access right ")
            }
        }
        with(utils.helpers.openPage<AtmAdminUserManagementPage>(driver)) {
            assert {
                elementWithTextPresented(" Users list ")
            }
        }
        with(utils.helpers.openPage<AtmAdminNodesManagementPage>(driver)) {
            assert {
                elementWithTextPresented(" Nodes management ")
            }
        }
        with(utils.helpers.openPage<AtmAdminTranslate>(driver)) {
            assert {
                elementWithTextPresented(" Available languages ")
            }
        }
        with(utils.helpers.openPage<AtmAdminKycManagementPage>(driver)) {
            assert {
                elementWithTextPresented(" KYC management ")
            }
        }
    }
}