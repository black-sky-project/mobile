package blacksky.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import blacksky.mobile.navigation.Screens
import blacksky.mobile.viewModels.CreateOfferViewModel
import blacksky.mobile.viewModels.SelectOfferViewModel
import java.util.UUID

@Composable
fun CreateOfferScreen(
    navController: NavHostController,
    courseId: UUID,
    modifier: Modifier = Modifier,
    viewModel: CreateOfferViewModel = viewModel()
)
{
    viewModel.launch(courseId)
    val state = viewModel.uiState.collectAsState().value

    when {
        state.error != null -> {
            Column(
                modifier = modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.error)
            }
        }

        state.isLoading -> {
            Column(
                modifier = modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Loading...")
            }
        }

        state.isNeedToAuthorize -> navController.navigate(Screens.Login.route)

        else -> Column(
            modifier.padding(15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card {
                Column(modifier.padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    NamedTextField(
                        modifier,
                        name = "Offer title",
                        onValueChange = viewModel::updateTitle,
                        value = state.offerTitle,
                        isError = false
                    )

                    NamedTextField(
                        modifier,
                        name = "Description",
                        onValueChange = viewModel::updateDescription,
                        value = state.offerDescription,
                        isError = false
                    )

                    Row(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp), horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = viewModel::login) {
                            Text(modifier = modifier.padding(horizontal = 10.dp), text = "Send Offer")
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
}