package com.example.traindriver.ui.util

import android.content.res.Resources
import com.example.traindriver.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SnackbarMessage: KoinComponent {
    private val res: Resources by inject()
    val CONNECTING_TO_SERVER_MSG = res.getString(R.string.connecting_to_server)
    val SMS_SEND_MSG = res.getString(R.string.sms_send)
    val AUTO_LOGIN_MSG = res.getString(R.string.auto_login)
    val ERROR_TRY_AGAIN_MSG = res.getString(R.string.error_try_again)
    val CHECKING_CODE_MSG = res.getString(R.string.checking_code)
}