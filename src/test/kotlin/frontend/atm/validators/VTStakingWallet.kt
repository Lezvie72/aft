package frontend.atm.validators

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.CoinType.VT
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmIssuancesPage
import pages.atm.AtmProfilePage
import pages.atm.AtmValidatorPage
import pages.atm.AtmValidatorPage.NodeType.ENDORSER
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.VALIDATORS.NUMBER), Tag(TagNames.Flow.MAIN))
@Tag("Validators")
@Epic("Frontend")
@Story("VT Staking wallet")
@Feature("Validator")
class VTStakingWallet : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_VALIDATOR_WITHOUT_2FA)
    @TmsLink("ATMCH-6129")
    @Test
    @DisplayName("Staking wallet. Interface checking")
    fun stakingWalletInterfaceChecking() {

        val userBuyer = Users.ATM_USER_VALIDATOR_WITHOUT_2FA
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
                    VT,
                    AtmIssuancesPage.OperationType.SELL,
                    AtmIssuancesPage.LimitType.MAX, "11000", itIssuer, itWallet
                )
            }
        }

        AtmProfilePage(driver).logout()

//        openPage<AtmWalletPage>(driver)

        val balanceBefore = step("User check balance before operation") {
            openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(VT, mainWallet.name)
        }

        step("User go ot Validator page check elements and buy Node") {
            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(addNode)
                }
                assert {
                    elementContainingTextPresented("New node request")
                }
                e {
                    select(nodeType, ENDORSER.toString())
                    click(submit)
                }
                assert {
                    elementPresented(stakingWallet)
                    elementContainingTextPresented("All rewards earned by the node will be credited to this wallet")
                    elementContainingTextPresented("AMOUNT TO STAKE")
                    elementContainingTextPresented("AVAILABLE BALANCE")
                }
                assert { stakeStep.isStepSelected() }
                e {
                    select(stakingWallet, mainWallet.name)
                }
                val availableBalance = availableBalance.amount

                MatcherAssert.assertThat(
                    "Expected base balance: $availableBalance, was: $balanceBefore",
                    availableBalance,
                    Matchers.closeTo(balanceBefore, BigDecimal("0.01"))
                )

                e {
                    click(submit)
                    signAndSubmitMessage(userBuyer, mainWallet.secretKey)
                    click(ok)
                }

                val balanceAfter = step("User check balance after operation") {
                    openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(VT, mainWallet.name)
                }

                MatcherAssert.assertThat(
                    "Expected base balance: $balanceAfter, was: $balanceBefore",
                    balanceAfter,
                    Matchers.closeTo(balanceBefore - BigDecimal(1000), BigDecimal("0.01"))
                )
            }


        }

        val balanceBefore1 = step("User check balance before operation") {
            openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(VT, mainWallet.name)
        }
        step("User go ot Validator page check elements, cancel and buy Node") {

            with(openPage<AtmValidatorPage>(driver) { submit(userBuyer) }) {
                e {
                    click(addNode)
                    select(nodeType, ENDORSER.toString())
                    click(submit)
                }
                assert { stakeStep.isStepSelected() }

                e {
                    click(cancel)
                }

                assert {
                    urlEndsWith("/validator/history")
                }
                assert { elementContainingTextPresented("Awaiting payment") }
                findAwaitingNode()
                assert { elementContainingTextPresented("NEW NODE REQUEST") }

                e {
                    select(stakingWallet, mainWallet.name)
                    click(submit)
                    signAndSubmitMessage(userBuyer, mainWallet.secretKey)
                    click(ok)
                }

                val balanceAfter1 = step("User check balance after operation") {
                    openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(VT, mainWallet.name)
                }

                MatcherAssert.assertThat(
                    "Expected base balance: $balanceAfter1, was: $balanceBefore1",
                    balanceAfter1,
                    Matchers.closeTo(balanceBefore1 - BigDecimal(1000), BigDecimal("0.01"))
                )
            }
        }
    }
}
