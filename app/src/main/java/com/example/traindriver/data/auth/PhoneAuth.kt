package com.example.traindriver.data.auth

import android.app.Activity
import com.example.traindriver.data.util.ResultState
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit

class PhoneAuth {
    private val auth = Firebase.auth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String

    fun createUserWithPhone(phone: String, activity: Activity): Flow<ResultState<String>> =
        callbackFlow {
            if (phone.isEmpty()) {
                trySend(ResultState.Failure(Throwable(message = "Введите номер телефона")))
            } else {
                trySend(ResultState.Loading)

                val onVerificationCallback =
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                        }

                        override fun onVerificationFailed(p0: FirebaseException) {
                            trySend(ResultState.Failure(p0))
                        }

                        override fun onCodeSent(
                            verificationCode: String,
                            p1: PhoneAuthProvider.ForceResendingToken
                        ) {
                            super.onCodeSent(verificationCode, p1)
                            trySend(ResultState.Success("OTP Sent Successfully"))
                            resendToken = p1
                            storedVerificationId = verificationCode
                        }

                    }

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(onVerificationCallback)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
            awaitClose {
                close()
            }
        }
}