package frontend.atm.validators

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import pages.atm.AtmAdminNodesManagementPage.NodeType
import pages.atm.AtmValidatorPage.NodeType.ENDORSER
import pages.atm.AtmValidatorPage.NodeType.ORDERER
import utils.Constants
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.time.LocalDateTime
import java.time.ZoneOffset

@Tags(Tag(TagNames.Epic.VALIDATORS.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Story("Validator node purchasing")
@Feature("Validator")
class ValidatorNodePurchasing : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_VALIDATOR_WITHOUT_FUNDS)
    @TmsLink("ATMCH-4415")
    @Test
    @DisplayName("Nodes management.Admin panel view")
    fun nodesManagementAdminPanelView() {
        val userBuyer = Users.ATM_USER_VALIDATOR_WITHOUT_FUNDS
        val mainWallet = userBuyer.mainWallet

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(addNode)
                    select(nodeType, ENDORSER.toString())
                    click(submit)
                    select(stakingWallet, mainWallet.name)
                    click(submit)
                }

                assert {
                    elementContainingTextPresented("Insufficient funds")
                }

                openPage<AtmValidatorPage>(driver)

                e {
                    click(addNode)
                    select(nodeType, ORDERER.toString())
                    click(submit)
                    select(stakingWallet, mainWallet.name)
                    click(submit)
                }

                //TODO уточнить про email
                assert {
                    elementContainingTextPresented("Insufficient funds")
                }

            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-4064")
    @Test
    @DisplayName("Validator. New node request - wrong 2fa")
    fun validatorNewNodeRequestWrong2fa() {

        val userBuyer = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    CoinType.CC,
                    AtmIssuancesPage.OperationType.SELL,
                    AtmIssuancesPage.LimitType.MAX, "11000", itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    CoinType.VT,
                    AtmIssuancesPage.OperationType.SELL,
                    AtmIssuancesPage.LimitType.MAX, "11000", itIssuer, itWallet
                )
            }
        }
        AtmProfilePage(driver).logout()

        step("User buy VT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, "11000", mainWallet)
                openPage<AtmMarketplacePage>(driver) { submit(userBuyer) }.buyTokenNew(
                    CoinType.VT,
                    "11000",
                    userBuyer,
                    mainWallet
                )
            }
        }

        openPage<AtmWalletPage>(driver)

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(addNode)
                    select(nodeType, ENDORSER.toString())
                    click(submit)
                    select(stakingWallet, mainWallet.name)
                    click(submit)
                    click(privateKey)
                    sendKeys(privateKey, userBuyer.mainWallet.secretKey)
                    click(confirmPrivateKeyButton)

                    sendKeys(atmOtpConfirmationInput, RandomStringUtils.randomNumeric(6).toString())
                    click(atmOtpConfirmationConfirmButton)
                }
                assert {
                    elementWithTextPresented(" Wrong code ")
                }
                e {
                    deleteData(atmOtpConfirmationInput)
                    val code = if (OAuth.generateCode(userBuyer.oAuthSecret) == "123456") "123457" else "123456"
                    click(atmOtpConfirmationInput)
                    sendKeys(atmOtpConfirmationInput, code)
                    click(atmOtpConfirmationConfirmButton)
                }
                assert {
                    elementWithTextPresented(" Wrong code ")
                }
                e {
                    deleteData(atmOtpConfirmationInput)
                    click(atmOtpConfirmationInput)
                    sendKeys(atmOtpConfirmationInput, OAuth.generateCode(userBuyer.oAuthSecret))
                    click(atmOtpConfirmationConfirmButton)
                    click(ok)
                }
                assert {
                    elementContainingTextPresented("Success")
                }

                openPage<AtmValidatorPage>(driver)

                with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                    e {
                        click(addNode)
                        select(nodeType, ORDERER.toString())
                        click(submit)
                        select(stakingWallet, mainWallet.name)
                        click(submit)
                        click(privateKey)
                        sendKeys(privateKey, userBuyer.mainWallet.secretKey)
                        click(confirmPrivateKeyButton)

                        sendKeys(atmOtpConfirmationInput, RandomStringUtils.randomNumeric(6).toString())
                        click(atmOtpConfirmationConfirmButton)
                    }
                    assert {
                        elementWithTextPresented(" Wrong code ")
                    }
                    e {
                        deleteData(atmOtpConfirmationInput)
                        val code = if (OAuth.generateCode(userBuyer.oAuthSecret) == "123456") "123457" else "123456"
                        click(atmOtpConfirmationInput)
                        sendKeys(atmOtpConfirmationInput, code)
                        click(atmOtpConfirmationConfirmButton)
                    }
                    assert {
                        elementWithTextPresented(" Wrong code ")
                    }
                    e {
                        deleteData(atmOtpConfirmationInput)
                        click(atmOtpConfirmationInput)
                        sendKeys(atmOtpConfirmationInput, OAuth.generateCode(userBuyer.oAuthSecret))
                        click(atmOtpConfirmationConfirmButton)
                        click(ok)
                    }
                    assert {
                        elementContainingTextPresented("Success")
                    }
                }

            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_VALIDATOR_2FA)
    @TmsLink("ATMCH-4063")
    @Test
    @DisplayName("Validator. New node request - wrong signature")
    fun validatorNewNodeRequestWrongSignature() {

        val userBuyer = Users.ATM_USER_VALIDATOR_2FA
        val mainWallet = userBuyer.walletList[0]

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    CoinType.CC,
                    AtmIssuancesPage.OperationType.SELL,
                    AtmIssuancesPage.LimitType.MAX, "11000", itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    CoinType.VT,
                    AtmIssuancesPage.OperationType.SELL,
                    AtmIssuancesPage.LimitType.MAX, "11000", itIssuer, itWallet
                )
            }
        }
        AtmProfilePage(driver).logout()

        step("User buy VT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, "11000", mainWallet)
                openPage<AtmMarketplacePage>(driver) { submit(userBuyer) }.buyTokenNew(
                    CoinType.VT,
                    "11000",
                    userBuyer,
                    mainWallet
                )
            }
        }
        openPage<AtmWalletPage>(driver)

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(addNode)
                    select(nodeType, ENDORSER.toString())
                    click(submit)
                    select(stakingWallet, mainWallet.name)
                    click(submit)
                    sendKeys(privateKey, RandomStringUtils.randomNumeric(18).toString())
                    click(confirmPrivateKeyButton)
                }
                assert { elementContainingTextPresented("Invalid key") }
                e {
                    deleteData(privateKey)
                    sendKeys(privateKey, itWallet.secretKey)
                    click(confirmPrivateKeyButton)
                }
                assert { elementContainingTextPresented("Invalid key") }
                e {
                    deleteData(privateKey)
                    sendKeys(privateKey, mainWallet.secretKey)
                    click(confirmPrivateKeyButton)
                    sendKeys(atmOtpConfirmationInput, OAuth.generateCode(userBuyer.oAuthSecret))
                    click(atmOtpConfirmationConfirmButton)
                    click(ok)
                }
                assert {
                    elementContainingTextPresented("Success")
                }
                openPage<AtmValidatorPage>(driver)

                with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                    e {
                        click(addNode)
                        select(nodeType, ORDERER.toString())
                        click(submit)
                        select(stakingWallet, mainWallet.name)
                        click(submit)
                        sendKeys(privateKey, RandomStringUtils.randomNumeric(18).toString())
                        click(confirmPrivateKeyButton)
                    }
                    assert { elementContainingTextPresented("Invalid key") }
                    e {
                        deleteData(privateKey)
                        sendKeys(privateKey, itWallet.secretKey)
                        click(confirmPrivateKeyButton)
                    }
                    assert { elementContainingTextPresented("Invalid key") }
                    e {
                        deleteData(privateKey)
                        sendKeys(privateKey, mainWallet.secretKey)
                        click(confirmPrivateKeyButton)
                        sendKeys(atmOtpConfirmationInput, OAuth.generateCode(userBuyer.oAuthSecret))
                        click(atmOtpConfirmationConfirmButton)
                        wait {
                            until("button Ok is presented", 30) {
                                check {
                                    isElementPresented(ok)
                                }
                            }
                        }
                        click(ok)
                    }
                    assert {
                        elementContainingTextPresented("Success")
                    }
                }

            }
        }
    }

    @Issue("ATMCH-6001")
    @ResourceLock(Constants.ROLE_USER_VALIDATOR_WITHOUT_2FA)
    @TmsLink("ATMCH-4062")
    @Test
    @DisplayName("Validator. New node request - positive case(without 2fa)")
    fun validatorNewNodeRequestPositiveCaseWithout2fa() {
        val userBuyer = Users.ATM_USER_VALIDATOR_WITHOUT_2FA
        val mainWallet = userBuyer.walletList[0]

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    CoinType.CC,
                    AtmIssuancesPage.OperationType.SELL,
                    AtmIssuancesPage.LimitType.MAX, "11000", itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    CoinType.VT,
                    AtmIssuancesPage.OperationType.SELL,
                    AtmIssuancesPage.LimitType.MAX, "11000", itIssuer, itWallet
                )
            }
        }
        AtmProfilePage(driver).logout()

        step("User buy VT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, "11000", mainWallet)
                openPage<AtmMarketplacePage>(driver) { submit(userBuyer) }.buyTokenNew(
                    CoinType.VT,
                    "11000",
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

        step("Admin nodes management page check, upload certificate, approve Node and check email message") {
            with(openPage<AtmAdminNodesManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                findAndOpenRequestOfNode(
                    NodeType.ENDORSER,
                    companyNameValue,
                    "STAKE ACCEPT"
                )
                uploadDocument(uploadCertificate, "endorosercert.pem")
                val since = LocalDateTime.now(ZoneOffset.UTC)
                val body = GmailApi.getTextMessageNodeCertificateIssued(userBuyer.email, since)
                assert {
                    textEmail(
                        body,
                        "Dear customerPlease be informed that a new node request status was updated. Node certificate issued and testing is in progress."
                    )
                }
                findAndOpenRequestOfNode(
                    NodeType.ENDORSER,
                    companyNameValue,
                    "CERTIFICATE ISSUED"
                )
                e {
                    setCheckbox(successfullyTested, true)
                    click(save)
                }
                val since1 = LocalDateTime.now(ZoneOffset.UTC)
                val body1 = GmailApi.getTextMessageNodeActivated(userBuyer.email, since1)
                assert {
                    textEmail(
                        body1,
                        "Dear customerPlease be informed that a new node request status was updated." +
                                " Testing successfully accomplished and node activated."
                    )
                }

            }
        }

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                addNodeValidator(ORDERER, userBuyer, mainWallet)
            }
        }

        step("Admin nodes management page check, upload certificate, approve Node and check email message") {
            with(openPage<AtmAdminNodesManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                findAndOpenRequestOfNode(
                    NodeType.ORDERER,
                    companyNameValue,
                    "STAKE ACCEPT"
                )
                uploadDocument(uploadCertificate, "orderer.pem")

                val since = LocalDateTime.now(ZoneOffset.UTC)

                val body = GmailApi.getTextMessageNodeCertificateIssued(userBuyer.email, since)
                assert {
                    textEmail(
                        body,
                        "Dear customerPlease be informed that a new node request status was updated. Node certificate issued and testing is in progress."
                    )
                }

                findAndOpenRequestOfNode(
                    NodeType.ENDORSER,
                    companyNameValue,
                    "CERTIFICATE ISSUED"
                )
                e {
                    setCheckbox(successfullyTested, true)
                    click(save)
                }

                val since1 = LocalDateTime.now(ZoneOffset.UTC)

                val body1 = GmailApi.getTextMessageNodeActivated(userBuyer.email, since1)
                assert {
                    textEmail(
                        body1,
                        "Dear customerPlease be informed that a new node request status was updated." +
                                " Testing successfully accomplished and node activated."
                    )
                }

            }
        }

    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    @TmsLink("ATMCH-4017")
    @Test
    @DisplayName("Validator. Create new node request - check interface")
    fun validatorCreateNewNodeRequestCheckInterface() {

        val userBuyer = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
        val mainWallet = userBuyer.mainWallet

        step("User buy VT token") {
            prerequisite {
                addCurrencyCoinToWallet(userBuyer, "1000", mainWallet)
                openPage<AtmMarketplacePage>(driver) { submit(userBuyer) }.buyTokenNew(
                    CoinType.VT,
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
                    select(nodeType, ENDORSER.toString())
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

                findAndOpenRequestOfNode(
                    NodeType.ENDORSER,
                    companyNameValue,
                    "STAKE ACCEPT"
                )
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

                findAndOpenRequestOfNode(
                    NodeType.ENDORSER,
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
                findActiveNode()
                assert {
                    elementContainingTextPresented("SUBMISSION DATE")
                    elementContainingTextPresented("PAID AMOUNT")
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
                assert {
                    urlEndsWith("/validator/history")
                }
            }

        }
    }
}

