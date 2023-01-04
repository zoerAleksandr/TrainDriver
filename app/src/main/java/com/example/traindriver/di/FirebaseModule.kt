package com.example.traindriver.di

import com.example.traindriver.R
import com.example.traindriver.ui.util.Constants.SIGN_IN_REQUEST
import com.example.traindriver.ui.util.Constants.SIGN_UP_REQUEST
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val firebaseModule = module {
    single { Identity.getSignInClient(androidContext()) }
    single { Firebase.auth }
    single { Firebase.firestore }

    single(named(SIGN_IN_REQUEST)) {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(androidContext().resources.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    single(named(SIGN_UP_REQUEST)) {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(androidContext().resources.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }
}