package pages.core.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PageUrl(
    val url: String,
    val authRequired: Boolean = true
)