package com.example.traindriver.domain.use_case

import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.screen.signin_screen.OneTapSignInResponse
import com.example.traindriver.ui.screen.signin_screen.SignInWithGoogleResponse
import com.example.traindriver.ui.util.Constants.CREATED_AT
import com.example.traindriver.ui.util.Constants.DISPLAY_NAME
import com.example.traindriver.ui.util.Constants.EMAIL
import com.example.traindriver.ui.util.Constants.PHOTO_URL
import com.example.traindriver.ui.util.Constants.SIGN_IN_REQUEST
import com.example.traindriver.ui.util.Constants.SIGN_UP_REQUEST
import com.example.traindriver.ui.util.Constants.USERS
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

class SignInWithGoogleUseCase : KoinComponent {
    private val oneTapClient: SignInClient by inject()
    private val signInRequest: BeginSignInRequest by inject(named(SIGN_IN_REQUEST))
    private val signUpRequest: BeginSignInRequest by inject(named(SIGN_UP_REQUEST))
    private val auth: FirebaseAuth by inject()
    private val db: FirebaseFirestore by inject()
    private val dataStore: DataStoreRepository by inject()

    fun oneTapSignInWithGoogle(): Flow<OneTapSignInResponse> = callbackFlow {
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

    fun firebaseSignInWithGoogle(googleCredential: AuthCredential): Flow<SignInWithGoogleResponse> =
        callbackFlow {
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
            db.collection(USERS).document(uid).set(user).await()
        }
    }
}

fun FirebaseUser.toUser() = mapOf(
    DISPLAY_NAME to displayName,
    EMAIL to email,
    PHOTO_URL to photoUrl?.toString(),
    CREATED_AT to FieldValue.serverTimestamp()
)