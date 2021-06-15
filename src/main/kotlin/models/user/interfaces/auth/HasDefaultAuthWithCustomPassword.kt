package models.user.interfaces.auth

interface HasDefaultAuthWithCustomPassword : HasEmail {
    val password: String
}