package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.atm.*
import pages.atm.AtmAdminKycManagementPage.KycStatus
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Atomyze Users management")
class AtomyzeUsersManagement : BaseTest() {


    @TmsLink("ATMCH-448")
    @Test
    @DisplayName("Set First and Last name in Admin Panel")
    fun setFirstAndLastNameInAdminPanel() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, false)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(userManagement)
            }
            assert {
                urlEndsWith("/user-management")
            }
        }
        with(AtmAdminUserManagementPage(driver)) {
            assert {
                elementWithTextPresented("Id ")
                elementWithTextPresented("First Name ")
                elementWithTextPresented("Last Name ")
                elementWithTextPresented("eMail ")
                elementWithTextPresented("KYC ")
                elementWithTextPresented("2FA ")
                elementWithTextPresented("Created ")
                elementWithTextPresented("Updated ")
//                elementPresented(verifyKYB)
//                elementPresented(verifyKYC)
                elementPresented(search)
                elementPresented(editButton)
//                elementPresented(requestEmailButton)
            }
            e {
                sendKeys(search, user.email.substring(0, 15))
                click(firstRow)
                click(editButton)
            }
            assert {
                elementPresented(firstNameInput)
                elementPresented(lastNameInput)
                elementPresented(saveButton)
                elementPresented(cancelButton)
//                elementPresented(emailIsConfirmedCheckbox)
//                elementPresented(kycCheckbox)
            }
            e {
                sendKeys(firstNameInput, "test")
                sendKeys(lastNameInput, "test")
                click(cancelButton)
                checkNotSaveName(user.email)
                click(firstRow)
                click(editButton)
                sendKeys(firstNameInput, "test")
                sendKeys(lastNameInput, "test")
                click(saveButton)
                checkName(user.email, "test", "test")
            }
        }
    }

    @Disabled("не готов еще функционал, отображаение статусов, KYC и тд.")
    @TmsLink("ATMCH-861")
    @Test
    @DisplayName("User management")
    fun userManagement() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, false)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(userManagement)
            }
            assert {
                urlEndsWith("/user-management")
            }
        }
        with(AtmAdminUserManagementPage(driver)) {
            assert {
                elementWithTextPresented("Id ")
                elementWithTextPresented("First Name ")
                elementWithTextPresented("Last Name ")
                elementWithTextPresented("Status ")
                elementWithTextPresented("KYC ")
                elementWithTextPresented("2FA ")
                elementWithTextPresented("Created ")
                elementWithTextPresented("Updated ")
                elementPresented(verifyKYB)
                elementPresented(verifyKYC)
                elementPresented(search)
                elementPresented(editButton)
                elementPresented(requestEmailButton)
            }
            e {
                sendKeys(search, user.email.substring(0, 15))
                click(firstRow)
                click(editButton)
            }
            assert {
                elementPresented(firstNameInput)
                elementPresented(lastNameInput)
                elementPresented(saveButton)
                elementPresented(cancelButton)
                elementPresented(emailIsConfirmedCheckbox)
                elementPresented(kycCheckbox)
            }
            e {
                sendKeys(firstNameInput, "test")
                sendKeys(lastNameInput, "test")
                click(cancelButton)
                checkNotSaveName(user.email)
                click(editButton)
                sendKeys(firstNameInput, "test")
                sendKeys(lastNameInput, "test")
                click(saveButton)
                checkName(user.email, "test", "test")
            }
        }
    }


    @TmsLink("ATMCH-1578")
    @Test
    @DisplayName("Adm.platform. KYС applications dashboard. Interface")
    fun kysApplicationsDashboardInterface() {
        with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                step("Step 1-2. Check the number of requests with validation status near KYC management") {
                    val countRequests = Regex("""\d+""")
                        .find(kycManagementInSettings.text)
                        ?.value
                        ?.toIntOrNull()
                    assertThat("Count requests not found", countRequests, Matchers.notNullValue())
                }

                step("Step 3-5. Check fields") {
                    assert {
                        elementContainingTextPresented("Email")
                        elementContainingTextPresented("First name")
                        elementContainingTextPresented("Last name")
                        elementContainingTextPresented("KYC status")
                        elementContainingTextPresented("Request Date")
                        elementContainingTextPresented("Modified Date")
                    }
                }

                step("Step 6. Check items sorting") {
                    val beforeEmailIntoFirstRow = wait {
                        untilPresented<TextBlock>(By.xpath("//table//td"), "First email in row").text
                    }
                    wait {
                        untilPresentedAnyWithText<Button>(
                            "Modified Date",
                            "Element Modified Date"
                        )
                    }.let {
                        click(it)
                        wait {
                            until("Email into first row should not be $beforeEmailIntoFirstRow", 10L) {
                                check {
                                    !untilPresented<TextBlock>(By.xpath("//table//td"), "First email in row")
                                        .text
                                        .contains(beforeEmailIntoFirstRow)
                                }
                            }
                        }
                    }
                }

                step("Step 7-8. Check user card") {
                    wait {
                        untilPresented<TextBlock>(By.xpath("//table//td"), "First email in row")
                    }.let {
                        click(it)
                        assert {
                            elementContainingTextPresented("Apply")
                            elementContainingTextPresented("Reset")

                            click(kycStatus)
                            wait {
                                untilPresented(kycFilterBar)
                            }
                            elementContainingTextPresented("All")
                            elementContainingTextPresented("Blocked")
                            elementContainingTextPresented("Not started")
                            elementContainingTextPresented("Red")
                            elementContainingTextPresented("Pending")
                            elementContainingTextPresented("Validating")
                            elementContainingTextPresented("Green")
                            elementContainingTextPresented("Green auto")
                            elementContainingTextPresented("Green employee")
                        }
                    }
                }
            }
        }
    }


    @TmsLink("ATMCH-1677")
    @Test
    @DisplayName("Adm.platform. KY applications dashboard. Filters")
    fun kyApplicationsDashboardFilters() {
        with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                step("Step 1-3. Go to KYC management and check the section of the filters") {
                    assert {
                        elementContainingTextPresented("Apply")
                        elementContainingTextPresented("Reset")

                        elementPresented(searchRequestedDateFrom)
                        elementPresented(searchModifiedDateFrom)
                        elementPresented(searchRequestedDateTo)
                        elementPresented(searchModifiedDateTo)
                        elementPresented(searchField)
                    }
                }

                step("Step 4-5. Fill in input fields Search. Click Apply button applies filters to the registry and reset after") {
                    val beforeEmailIntoFirstRow = wait {
                        untilPresented<TextBlock>(
                            By.xpath("//table//td[2]"),
                            "First some email in row (should be more one)"
                        ).text
                    }
                    sendKeys(searchField, beforeEmailIntoFirstRow)
                    click(applyFiltersButton)

                    wait {
                        untilPresented<TextBlock>(
                            By.xpath("//table//td"),
                            "First email in row after send email in Search field"
                        ).text
                    }.let {
                        assertThat(
                            "In first row should be value $beforeEmailIntoFirstRow but was $it",
                            it.contains(beforeEmailIntoFirstRow)
                        )
                    }

                    click(resetFiltersButton)
                    wait {
                        untilPresented<TextBlock>(
                            By.xpath("//table//td"),
                            "First some email in row (should be more one)"
                        ).text
                    }.let {
                        assertThat(
                            "In first row should be value $beforeEmailIntoFirstRow but was $it",
                            it.contains(beforeEmailIntoFirstRow)
                        )
                    }
                }
                step("Step 6-7. Fill in input fields :request date Click Apply button applies filters to the registry and reset after.") {
                    val date = LocalDate.now().plusDays(1)
                    // Must be european format: DD.MM.YYYY
                    val tomorrow = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date)
                    sendKeys(searchRequestedDateFrom, tomorrow)
                    sendKeys(searchRequestedDateTo, tomorrow)
                    click(applyFiltersButton)

                    assertThat(
                        "After using the filter with date $tomorrow should not be visible some requests",
                        check { isElementContainingTextPresented("User list is empty") }
                    )

                    click(resetFiltersButton)

                    wait {
                        until("User list is empty should be disappeared") {
                            check { !isElementContainingTextPresented("User list is empty") }
                        }
                    }
                }

                step("Step 8-9. Fill in input fields :request date Click Apply button applies filters to the registry and reset after.") {
                    val date = LocalDate.now().plusDays(1)
                    // Must be european format: DD.MM.YYYY
                    val tomorrow = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date)
                    sendKeys(searchModifiedDateFrom, tomorrow)
                    sendKeys(searchModifiedDateTo, tomorrow)
                    click(applyFiltersButton)

                    assertThat(
                        "After using the filter with date $tomorrow should not be visible some requests and reset after",
                        check { isElementContainingTextPresented("User list is empty") }
                    )

                    click(resetFiltersButton)

                    wait {
                        until("User list is empty should be disappeared") {
                            check { !isElementContainingTextPresented("User list is empty") }
                        }
                    }
                }

                step("Step 9-10. Set filter ALL and reset after.") {
                    click(kycStatus)
                    setCheckbox(kycFilterBar.all, true, kycFilterBar.localAttribute)
                    click(applyFiltersButton)
                    val countRequestsBefore = countAllRequestsInBottomSidebar.text.split("of ").last()
                    setCheckbox(kycFilterBar.all, false, kycFilterBar.localAttribute)
                    setCheckbox(kycFilterBar.notStarted, true, kycFilterBar.localAttribute)
                    click(applyFiltersButton)
                    wait {
                        until("Count of requests not should be $countRequestsBefore") {
                            check {
                                !countRequestsBefore.contains(countAllRequestsInBottomSidebar.text.split("of ").last())
                            }
                        }
                    }

                    click(resetFiltersButton)

                    wait {
                        until("Count of requests should be $countRequestsBefore") {
                            check {
                                countRequestsBefore.contains(countAllRequestsInBottomSidebar.text.split("of ").last())
                            }
                        }
                    }
                }

                step("Steps 11-13. Mark checkboxes : status of request Click Apply button applies filters to the registry.") {
                    setCheckbox(kycFilterBar.all, true, kycFilterBar.localAttribute)
                    click(applyFiltersButton)
                    val countRequestsBefore = countAllRequestsInBottomSidebar.text.split("of ").last()

                    setCheckbox(kycFilterBar.all, false, kycFilterBar.localAttribute)
                    setCheckbox(kycFilterBar.red, true, kycFilterBar.localAttribute)
                    click(applyFiltersButton)

                    wait {
                        until("Count of requests should be $countRequestsBefore") {
                            check {
                                !countRequestsBefore.contains(countAllRequestsInBottomSidebar.text.split("of ").last())
                            }
                        }
                    }

                    wait {
                        until("Row with value ${KycStatus.RED}' should appear") {
                            untilPresented<WebElement>(By.xpath("//tbody//*[contains(text(), '${KycStatus.RED}')]"))
                        }.to<Button>("Row with value ${KycStatus.RED}'")
                    }

                    click(resetFiltersButton)
                    setCheckbox(kycFilterBar.all, false, kycFilterBar.localAttribute)
                    setCheckbox(kycFilterBar.green, true, kycFilterBar.localAttribute)
                    click(applyFiltersButton)
                    wait {
                        until("Count of requests should be $countRequestsBefore") {
                            check {
                                !countRequestsBefore.contains(countAllRequestsInBottomSidebar.text.split("of ").last())
                            }
                        }
                    }
                    val countRequestsGreen = countAllRequestsInBottomSidebar.text.split("of ").last()
                    setCheckbox(kycFilterBar.blocked, true, kycFilterBar.localAttribute)
                    click(applyFiltersButton)
                    wait {
                        until("Count of requests should be $countRequestsBefore") {
                            check {
                                !countRequestsGreen.contains(countAllRequestsInBottomSidebar.text.split("of ").last())
                            }
                        }
                    }
                }
            }
        }
    }
}
