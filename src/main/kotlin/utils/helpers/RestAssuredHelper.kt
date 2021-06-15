package utils.helpers

import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification


//Rest Assured helpers

fun RequestSpecification.kwhen(): RequestSpecification {
    return this.`when`()
}

// allows response.to<Widget>() -> Widget instance
inline fun <reified T> ResponseBodyExtractionOptions.to(): T {
    return this.`as`(T::class.java)
}

inline fun <reified T> ResponseBodyExtractionOptions.toList(): List<T> {
    val response = this.`as`(Array<T>::class.java)
    return response.toList()
}

inline fun <reified T> ResponseBodyExtractionOptions.toList(path: String): List<T> {
    val response = this.jsonPath().getList(path, T::class.java)
    return response.toList()
}