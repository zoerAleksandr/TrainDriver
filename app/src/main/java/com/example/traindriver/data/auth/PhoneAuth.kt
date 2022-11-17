package com.example.traindriver.data.auth

import android.app.Activity
import android.util.Log
import com.example.traindriver.data.util.ResultState
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class PhoneAuth {
    private val auth = Firebase.auth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String

    fun createUserWithPhone(phone: String, activity: Activity): Flow<ResultState<String>> =
        callbackFlow {
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
            awaitClose {
                close()
            }
        }

//    fun startVerification(phone: String, activity: Activity) {
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phone)
//            .setTimeout(60L, TimeUnit.SECONDS)
//            .setActivity(activity)
//            .setCallbacks(callbacks)
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }
//
//    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//        }
//
//        override fun onVerificationFailed(e: FirebaseException) {
//            Log.d("ZZZ", "onVerificationFailed ${e.message}")
//            when (e) {
//                is FirebaseAuthInvalidCredentialsException -> {
////                    binding.root.showSnack("Номер не существует")
//                }
//                is FirebaseTooManyRequestsException -> {
////                    binding.root.showSnack("Превышена квота")
//                }
//                is FirebaseAuthException -> {
////                    Log.d(TAG, e.message.toString())
////                    binding.root.showSnack("Ошибка авторизации. Используйте другой способ")
//                }
//                is FirebaseApiNotAvailableException -> {
////                    binding.root.showSnack("Отсутствуют Google Play Services")
//                }
//            }
//        }
//
//        override fun onCodeSent(
//            verificationId: String,
//            token: PhoneAuthProvider.ForceResendingToken,
//        ) {
//            storedVerificationId = verificationId
//            resendToken = token
//            Log.d("ZZZ", "onCodeSent")
////            startCheckingCodeFragment(phoneNumber)
//        }
//    }
//
//    // Проверка СМС-Кода
//    private suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//        try {
//            auth.signInWithCredential(credential).await()
//        } catch (e: FirebaseFirestoreException) {
//            Log.d("ZZZ", "signInWithPhoneAuthCredential ${e.message}")
//        }
////            .addOnCompleteListener(this) { task ->
////                if (task.isSuccessful) {
////                    Log.d(TAG, "signInWithCredential:success")
////                    val user = task.result?.user
////                    updateUI(user)
////                } else {
////                    Log.w(TAG, "signInWithCredential:failure", task.exception)
////                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
////                        binding.root.showSnack("Неверный код")
////                    }
////                }
////            }
//    }
}