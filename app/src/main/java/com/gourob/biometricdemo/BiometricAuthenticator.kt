package com.gourob.biometricdemo

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity


class BiometricAuthenticator(
    private val context: Context,
) {
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private val biometricManager = BiometricManager.from(context)

    private lateinit var biometricPrompt: BiometricPrompt

    private fun isBiometricAuthAvailable(): BiometricAuthStatus {
        val authStatus =
            biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

        return when (authStatus) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAuthStatus.TEMPORARILY_NOT_AVAILABLE
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAuthStatus.READY
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAuthStatus.AVAILABLE_BUT_NOT_ENROLLED
            else -> BiometricAuthStatus.NOT_AVAILABLE
        }
    }

    fun promptBiometricAuth(
        title: String,
        subTitle: String,
        negativeButtonText: String,
        activity: FragmentActivity,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onRegisterBiometric: () -> Unit,
        onFailure: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        when (val availabilityStatus = isBiometricAuthAvailable()) {
            BiometricAuthStatus.READY  -> Unit
            BiometricAuthStatus.AVAILABLE_BUT_NOT_ENROLLED -> {
                onRegisterBiometric()
                return
            }
            else -> {
                onError(availabilityStatus.getMessage())
                return
            }
        }

        biometricPrompt = prepareBiometricPrompt(activity, onSuccess, onFailure, onError)

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subTitle)
            .setNegativeButtonText(negativeButtonText)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun prepareBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onFailure: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ): BiometricPrompt{
        return BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailure()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }
            }
        )
    }
}

enum class BiometricAuthStatus {
    READY,
    NOT_AVAILABLE,
    TEMPORARILY_NOT_AVAILABLE,
    AVAILABLE_BUT_NOT_ENROLLED;

    fun getMessage(): String {
        return when (this) {
            READY -> "Ready"
            NOT_AVAILABLE -> "Not Available"
            TEMPORARILY_NOT_AVAILABLE -> "Temporarily not available"
            AVAILABLE_BUT_NOT_ENROLLED -> "Available but not enrolled"
        }
    }
}