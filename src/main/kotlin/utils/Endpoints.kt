package utils

object Endpoints {

    //Sum-sub
    val resources_auth_login = "/resources/auth/login"
    val resources_applicants_applicantId = "/resources/applicants/{applicantId}"
    val resources_applicants = "/resources/applicants/"

    val resources_applicants_applicantId_requiredIdDocsStatus =
        "/resources/applicants/{applicantId}/requiredIdDocsStatus"

    val resources_applicants_applicantId_status_pending = "/resources/applicants/{applicantId}/status/pending"

    val resources_applicants_applicantId_status_testCompleted =
        "/resources/applicants/{applicantId}/status/testCompleted"

}