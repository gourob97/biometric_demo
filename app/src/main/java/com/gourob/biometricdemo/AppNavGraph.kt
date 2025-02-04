package com.gourob.biometricdemo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = AuthScreenRoute) {
        composable<AuthScreenRoute> {
            AuthRoute(navController)
        }
        composable<BiometricSettingDialog> {
            val context = LocalContext.current
            fun openSecuritySettings(context: Context) {
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                context.startActivity(intent)
            }

            @RequiresApi(Build.VERSION_CODES.R)
            fun openBiometricSettings(context: Context) {
                val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
                    )
                }
                context.startActivity(intent)
            }

            fun setUpBiometric() {
                if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    openBiometricSettings(context)
                } else {
                    openSecuritySettings(context)
                }
            }

            BiometricSettingDialog(
                onConfirm = {
                    navController.popBackStack()
                    setUpBiometric()
                },
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Serializable
object AuthScreenRoute

@Serializable
object BiometricSettingDialog