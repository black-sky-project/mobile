package blacksky.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import blacksky.mobile.navigation.Screens
import blacksky.mobile.viewModels.SelectUniversityViewModel

@Composable
@Preview
fun SelectUniversityScreenPreview() =
    SelectUniversityScreen(navController = rememberNavController(),
        viewModel = viewModel<SelectUniversityViewModel>().apply { setPreviewMode() })

@Composable
fun SelectUniversityScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SelectUniversityViewModel = viewModel()
) {
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

        else -> LazyColumn(
            modifier = modifier
                .fillMaxHeight()
                .padding(15.dp),
            userScrollEnabled = true,
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(state.universities) {
                Card(
                    modifier = modifier
                        .padding(vertical = 5.dp)
                        .clickable {
                            navController.navigate(
                                "${Screens.SelectDepartment.route}/${it.id}"
                            )
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Text(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(15.dp), text = it.name
                    )
                }
            }
        }
    }
}