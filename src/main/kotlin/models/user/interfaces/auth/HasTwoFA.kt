package models.user.interfaces.auth

import models.user.interfaces.User

interface HasTwoFA : User, HasDefaultAuth {
    val oAuthSecret: String
}