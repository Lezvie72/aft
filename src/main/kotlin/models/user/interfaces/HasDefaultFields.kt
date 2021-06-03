package models.user.interfaces


interface HasDefaultFields {
    val email: String
    val project: Int
        get() = 1
}