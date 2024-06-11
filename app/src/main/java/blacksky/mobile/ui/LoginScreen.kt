package blacksky.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import blacksky.mobile.navigation.Screens
import blacksky.mobile.viewModels.LoginViewModel

@Composable
@Preview
fun LoginScreenPreview() = LoginScreen(navController = rememberNavController())

@Composable
fun LoginScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsState().value

    if (state.authSuccessful) {
        navController.navigate(Screens.SelectUniversity.route)
        return
    }

    Column(
        modifier.padding(15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card {
            Column(modifier.padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                NamedTextField(
                    modifier,
                    name = "Login",
                    onValueChange = viewModel::updateLogin,
                    value = state.login,
                    isError = false
                )

                NamedTextField(
                    modifier,
                    name = "Password",
                    onValueChange = viewModel::updatePassword,
                    value = state.password,
                    isPassword = true,
                    isError = state.usedBadCredentials
                )

                Row(
                    modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp), horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = viewModel::login, enabled = state.isButtonActive) {
                        Text(modifier = modifier.padding(horizontal = 10.dp), text = "OK")
                    }
                }
            }
        }
    }
}

@Composable
fun NamedTextField(
    modifier: Modifier = Modifier,
    name: String,
    onValueChange: (String) -> Unit,
    value: String,
    isPassword: Boolean = false,
    isError: Boolean
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(name) },
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        isError = isError
    )
}