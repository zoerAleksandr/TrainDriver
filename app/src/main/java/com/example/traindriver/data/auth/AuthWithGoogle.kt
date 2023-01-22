package com.example.traindriver.data.auth

import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.screen.signin_screen.OneTapSignInResponse
import com.example.traindriver.ui.screen.signin_screen.SignInWithGoogleResponse
import com.example.traindriver.ui.util.Constants
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class AuthWithGoogle : KoinComponent {
    private val oneTapClient: SignInClient by inject()
    private val signInRequest: BeginSignInRequest by inject(named(Constants.SIGN_IN_REQUEST))
    private val signUpRequest: BeginSignInRequest by inject(named(Constants.SIGN_UP_REQUEST))
    private val auth: FirebaseAuth by inject()
    private val db: FirebaseFirestore by inject()
    private val dataStore: DataStoreRepository by inject()

    fun oneTap(): Flow<OneTapSignInResponse> = callbackFlow {
        trySend(ResultState.Loading(null))
        try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            trySend(ResultState.Success(signInResult))
        } catch (e: Exception) {
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                trySend(ResultState.Success(signUpResult))
            } catch (e: Exception) {
                trySend(ResultState.Failure(e))
            }
        }
        awaitClose { close() }
    }

    fun signIn(googleCredential: AuthCredential)
            : Flow<SignInWithGoogleResponse> = callbackFlow {
        trySend(ResultState.Loading(null))
        try {
            val authResult = auth.signInWithCredential(googleCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            if (isNewUser) {
                addUserToFirestore()
            }
            authResult.user?.let { user ->
                dataStore.saveUid(user.uid)
            }
            trySend(ResultState.Success(true))
        } catch (e: Exception) {
            trySend(ResultState.Failure(e))
        }
        awaitClose { close() }
    }

    private suspend fun addUserToFirestore() {
        auth.currentUser?.apply {
            val user = toUser()
            db.collection(Constants.USERS).document(uid).set(user).await()
        }
    }
}

fun FirebaseUser.toUser() = mapOf(
    Constants.DISPLAY_NAME to displayName,
    Constants.EMAIL to email,
    Constants.PHOTO_URL to photoUrl?.toString(),
    Constants.CREATED_AT to FieldValue.serverTimestamp()
)