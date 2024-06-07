package blacksky.mobile

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import blacksky.mobile.navigation.Screens
import blacksky.mobile.services.AuthService
import blacksky.mobile.ui.LoginScreen
import blacksky.mobile.ui.SelectUniversityScreen
import blacksky.mobile.ui.theme.MobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthService.init(getPreferences(Context.MODE_PRIVATE))

        setContent {
            MobileTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screens.SelectUniversity.route) {
                        composable(Screens.SelectUniversity.route) { SelectUniversityScreen(navController) }
                        composable(Screens.Login.route) { LoginScreen(navController) }
                    }
                }
            }
        }
    }
}