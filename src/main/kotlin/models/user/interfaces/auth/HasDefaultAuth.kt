package models.user.interfaces.auth

import models.user.interfaces.User

interface HasDefaultAuth {
    var password: String
}