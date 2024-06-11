package blacksky.mobile

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import blacksky.mobile.navigation.Screens
import blacksky.mobile.services.AuthService
import blacksky.mobile.ui.LoginScreen
import blacksky.mobile.ui.SelectUniversityScreen
import blacksky.mobile.ui.theme.MobileTheme
import blacksky.mobile.ui.SelectDepartmentScreen
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthService.init(getPreferences(Context.MODE_PRIVATE))

        setContent {
            MobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screens.SelectUniversity.route
                    ) {
                        composable(Screens.SelectUniversity.route) {
                            SelectUniversityScreen(navController)
                        }
                        composable(Screens.Login.route) {
                            LoginScreen(navController)
                        }
                        composable("${Screens.SelectDepartment.route}/{universityId}") { navBackStackEntry ->
                            val universityId = navBackStackEntry.arguments?.getString("universityId")?.let {UUID.fromString(it)}
                                ?: throw IllegalArgumentException("University expected when navigating to departments")
                            SelectDepartmentScreen(navController = navController, universityId = universityId)
                        }
//                        composable("${Screens.SelectCourse.route}/{departmentId}") { navBackStackEntry ->
//                            val departmentId = navBackStackEntry.arguments?.getString("departmentId")?.let {UUID.fromString(it)}
//                                ?: throw IllegalArgumentException("Department expected when navigating to courses")
//                            SelectCourseScreen(navController = navController, departmentIdId = departmentId)
                    }
                }
            }
        }
    }
}