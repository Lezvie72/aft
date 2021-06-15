package utils

import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.specification.ResponseSpecification
import org.hamcrest.Matchers

object SpecStorage {

    init {
        RestAssured.requestSpecification = RestAssured.given()
            .filter(RequestLoggingFilter())
            .filter(ResponseLoggingFilter())
            .filter(AllureRestAssured())
    }

    private val commonResponseSpec: ResponseSpecification = ResponseSpecBuilder()
        .expectStatusCode(Matchers.lessThan(300))
//        .expectBody("error", isEmptyOrNullString())
//        .expectResponseTime(lessThan(400L), TimeUnit.MILLISECONDS)
        .build()


    fun commonResponseSpec(): ResponseSpecification {
        return RestAssured.expect().spec(commonResponseSpec)
    }


}