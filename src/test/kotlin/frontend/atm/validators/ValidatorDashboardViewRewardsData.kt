package frontend.atm.validators

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.VT
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import pages.atm.AtmAdminNodesManagementPage.NodeType.ENDORSER
import pages.atm.AtmValidatorPage.NodeType
import utils.Constants
import utils.TagNames
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.VALIDATORS.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Feature("Validator")
@Story("Validator dashboard. View rewards data")
class ValidatorDashboardViewRewardsData : BaseTest() {

    private val amountEndorser = "1000"

    @TmsLink("ATMCH-4240")
    @Test
    @DisplayName("Validator dashboard. View rewards data")
    fun validatorDashboardViewRewardsData() {
        val userBuyer = Users.ATM_USER_NOT_VALIDATOR

        step("User go ot Validator page check elements") {
            with(openPage<AtmProfilePage>(driver) { submit(userBuyer) }) {
                assert { elementNotPresented(validator) }
            }
        }
    }

    @Disabled("Invalid key is not worked")
    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    @TmsLink("ATMCH-4153")
    @Test
    @DisplayName("Validator dashboard - get reward with wrong signature")
    fun validatorDashboardGetRewardWithWrongSignature() {

        val userBuyer = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.walletList[0]

        val user = Users.ATM_USER_FOR_ETC_TOKENS
        val wallet = user.mainWallet

        step("User buy VT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, amountEndorser, mainWallet)
                openPage<AtmMarketplacePage>(driver) { submit(userBuyer) }.buyOrReceiveToken(
                    VT,
                    amountEndorser,
                    userBuyer,
                    mainWallet
                )
            }
        }

        openPage<AtmWalletPage>(driver)

        val companyNameValue = openPage<AtmProfilePage>(driver) { submit(userBuyer) }.getCompanyName()

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                addNodeValidator(NodeType.ENDORSER, userBuyer, mainWallet)
            }
        }

        step("Admin nodes management page check elements, upload certificate and approve Node") {
            with(openPage<AtmAdminNodesManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                findAndOpenRequestOfNode(
                    ENDORSER,
                    companyNameValue,
                    "STAKE ACCEPT"
                )
                uploadDocument(uploadCertificate, "endorosercert.pem")
                findAndOpenRequestOfNode(
                    ENDORSER,
                    companyNameValue,
                    "CERTIFICATE ISSUED"
                )
                e {
                    setCheckbox(successfullyTested, true)
                    click(save)
                }
            }
        }
        step("User go to Node check element and Get Reward") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(getReward)
                }
                signAndSubmitMessage(userBuyer, itWallet.secretKey)
                assert { elementContainingTextPresented("Invalid key") }
                signAndSubmitMessage(userBuyer, wallet.secretKey)
                assert { elementContainingTextPresented("Invalid key") }

                signAndSubmitMessage(userBuyer, mainWallet.secretKey)
                assert {
                    elementContainingTextPresented("You have successfully released your validator reward")
                }
            }

        }

    }

    //TODO 2FA
    @Disabled
    @ResourceLock(Constants.ROLE_USER_VALIDATOR_2FA)
    @TmsLink("ATMCH-4152")
    @Test
    @DisplayName("Validator dashboard - get reward with wrong 2fa")
    fun validatorDashboardGetRewardWithWrong2fa() {

        val userBuyer = Users.ATM_USER_VALIDATOR_2FA
        val mainWallet = userBuyer.walletList[0]


        step("User buy VT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, amountEndorser, mainWallet)
                openPage<AtmMarketplacePage>(driver) { submit(userBuyer) }.buyOrReceiveToken(
                    VT,
                    amountEndorser,
                    userBuyer,
                    mainWallet
                )
            }
        }

        openPage<AtmWalletPage>(driver)

        val companyNameValue = openPage<AtmProfilePage>(driver) { submit(userBuyer) }.getCompanyName()

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                addNodeValidator(NodeType.ENDORSER, userBuyer, mainWallet)
            }
        }

        step("Admin nodes management page check elements, upload certificate and approve Node") {
            with(openPage<AtmAdminNodesManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                findAndOpenRequestOfNode(
                    ENDORSER,
                    companyNameValue,
                    "STAKE ACCEPT"
                )
                uploadDocument(uploadCertificate, "endorosercert.pem")
                findAndOpenRequestOfNode(
                    ENDORSER,
                    companyNameValue,
                    "CERTIFICATE ISSUED"
                )
                e {
                    setCheckbox(successfullyTested, true)
                    click(save)
                }
            }
        }
        step("User go to Node check element and Get Reward") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(getReward)

                    sendKeys(privateKey, userBuyer.mainWallet.secretKey)
                    click(confirmPrivateKeyButton)

//                    val code = if (OAuth.generateCode(userBuyer.oAuthSecret) == "123456") "123457" else "123456"
//                    sendKeys(atmOtpConfirmationInput, code)
//                    click(atmOtpConfirmationConfirmButton)
                }
//                assert {
//                    elementWithTextPresented(" Wrong code ")
//                }
//                e {
//                    deleteData(atmOtpConfirmationInput)
//                    val code = if (OAuth.generateCode(userBuyer.oAuthSecret) == "123456") "123457" else "123456"
//                    click(atmOtpConfirmationInput)
//                    sendKeys(atmOtpConfirmationInput, code)
//                    click(atmOtpConfirmationConfirmButton)
//                }
//                assert {
//                    elementWithTextPresented(" Wrong code ")
//                }
                e {
                    deleteData(atmOtpConfirmationInput)
                    click(atmOtpConfirmationInput)
                    sendKeys(atmOtpConfirmationInput, OAuth.generateCode(userBuyer.oAuthSecret))
                    click(atmOtpConfirmationConfirmButton)
                }
                assert {
                    elementContainingTextPresented("You have successfully released your validator reward")
                }
            }

        }

    }

    @ResourceLock(Constants.ROLE_USER_VALIDATOR_WITHOUT_2FA)
    @TmsLink("ATMCH-4150")
    @Test
    @DisplayName("Validator dashboard - get reward without 2fa")
    fun validatorDashboardGetRewardWithout2fa() {

        val userBuyer = Users.ATM_USER_VALIDATOR_WITHOUT_2FA
        val mainWallet = userBuyer.mainWallet

        step("User buy VT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, amountEndorser, mainWallet)
                openPage<AtmMarketplacePage>(driver) { submit(userBuyer) }.buyOrReceiveToken(
                    VT,
                    amountEndorser,
                    userBuyer,
                    mainWallet
                )
            }
        }
        openPage<AtmWalletPage>(driver)

        val companyNameValue = openPage<AtmProfilePage>(driver) { submit(userBuyer) }.getCompanyName()

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                addNodeValidator(NodeType.ENDORSER, userBuyer, mainWallet)
            }
        }

        step("Admin nodes management page check elements, upload certificate and approve Node") {
            with(openPage<AtmAdminNodesManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                findAndOpenRequestOfNode(
                    ENDORSER,
                    companyNameValue,
                    "STAKE ACCEPT"
                )
                uploadDocument(uploadCertificate, "endorosercert.pem")
                findAndOpenRequestOfNode(
                    ENDORSER,
                    companyNameValue,
                    "CERTIFICATE ISSUED"
                )
                e {
                    setCheckbox(successfullyTested, true)
                    click(save)
                }
            }
        }
        step("User go to Node check element and Get Reward") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(getReward)
                }
                signAndSubmitMessage(userBuyer, mainWallet.secretKey)
                assert {
                    elementContainingTextPresented("You have successfully released your validator reward")
                }
            }

        }

    }

}
