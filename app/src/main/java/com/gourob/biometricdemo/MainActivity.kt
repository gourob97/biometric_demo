package com.gourob.biometricdemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gourob.biometricdemo.ui.theme.BiometricDemoTheme

class MainActivity : FragmentActivity() {
    @SuppressLint("ContextCastToActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            BiometricDemoTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavGraph(navController)
                }
            }
        }
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun AuthRoute(navController: NavController) {
    val context = LocalContext.current
    val authenticator = BiometricAuthenticator(context)
    val activity = context as FragmentActivity

    var info by remember { mutableStateOf("") }
    AuthScreen(
        info = info,
        onButtonClick = {
            authenticator.promptBiometricAuth(
                title = "Authenticate",
                subTitle = "Please authenticate via biometric",
                negativeButtonText = "Cancel",
                onSuccess = {
                    info = "Success"
                },
                onFailure = {
                    info = "Failed"
                },
                onError = {
                    info = it  + "onError"
                },
                activity = activity,
                onRegisterBiometric = {
                    navController.navigate(BiometricSettingDialog)
                }
            )
        }
    )
}

@Composable
fun AuthScreen(
    info: String,
    onButtonClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Fingerprint Icon",
            modifier = Modifier
                .size(64.dp)
                .clickable {
                    onButtonClick()
                }
        )

        Spacer(Modifier.height(20.dp))

        Text(info)

    }
}


@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    AuthScreen(info = "Info will be here") {}
}