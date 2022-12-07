package com.example.traindriver.data.auth

import android.app.Activity
import android.content.res.Resources
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class PhoneAuth : KoinComponent {
    private val auth = Firebase.auth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String
    private val resources: Resources by inject()

    fun createUserWithPhone(phone: String, activity: Activity): Flow<ResultState<String>> =
        callbackFlow {
            val onVerificationCallback =
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {}

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

            if (phone.isEmpty()) {
                trySend(ResultState.Failure(Throwable(message = resources.getString(R.string.empty_number))))
            } else {
                trySend(ResultState.Loading)
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

    fun verifyCode(code: String): Flow<ResultState<FirebaseUser?>> =
        callbackFlow {
            trySend(ResultState.Loading)
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)

            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        trySend(ResultState.Success(task.result?.user))
                    } else {
                        trySend(ResultState.Failure(Throwable(resources.getString(R.string.invalid_code))))
                    }
                }
            awaitClose {
                close()
            }
        }

    fun resendCode(phone: String, activity: Activity): Flow<ResultState<String>> =
        callbackFlow {
            val onVerificationCallback =
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {}

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
            trySend(ResultState.Loading)
            val optionsBuilder = PhoneAuthOptions
                .newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(onVerificationCallback)
            optionsBuilder.setForceResendingToken(resendToken)
            PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
            awaitClose {
                close()
            }
        }
}