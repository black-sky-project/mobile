package blacksky.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import blacksky.mobile.models.Degree
import blacksky.mobile.navigation.Screens
import blacksky.mobile.viewModels.SelectCourseViewModel
import java.util.UUID
import blacksky.mobile.ui.theme.Purple40
import blacksky.mobile.ui.theme.Pink40

@Composable
fun SelectCourseScreen(
    navController: NavHostController,
    departmentId: UUID,
    modifier: Modifier = Modifier,
    viewModel: SelectCourseViewModel = viewModel()
) {
    viewModel.launch(departmentId)
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
            items(state.courses) {
                Card(
                    modifier = modifier
                        .padding(vertical = 5.dp)
                        .clickable {
                            navController.navigate(
                                "${Screens.SelectCourse.route}/${it.id}"
                            )
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (it.degree) {
                            Degree.Bachelor -> Pink40
                            Degree.Master -> Purple40
                        }
                    )
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