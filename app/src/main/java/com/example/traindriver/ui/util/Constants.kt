package com.example.traindriver.ui.util

import android.content.res.Resources
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Constants: KoinComponent {
    private val res: Resources by inject()
    const val USERS = "users"

    const val DISPLAY_NAME = "displayName"
    const val EMAIL = "email"
    const val PHOTO_URL = "photoUrl"
    const val CREATED_AT = "createAt"

    const val SIGN_IN_REQUEST = "signInRequest"
    const val SIGN_UP_REQUEST = "signUpRequest"

    const val ANONYMOUS_SIGN_IN_WORKER = "anonymousSignIn"

    const val NETWORK_CONNECTED = "networkConnected"

    const val ROUTE = "route"

    const val DURATION_CROSSFADE = 500
}