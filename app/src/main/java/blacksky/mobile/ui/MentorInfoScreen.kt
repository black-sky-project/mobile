package blacksky.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import blacksky.mobile.navigation.Screens
import blacksky.mobile.viewModels.MentorInfoViewModel
import blacksky.mobile.viewModels.OfferInfoViewModel
import java.util.UUID

@Composable
fun MentorInfoScreen(
    navController: NavHostController,
    mentorId: UUID,
    modifier: Modifier = Modifier,
    viewModel: MentorInfoViewModel = viewModel()
) {
    viewModel.launch(mentorId)
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

        else -> {
            Column(
                modifier = modifier
                    .fillMaxHeight()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Card(modifier = modifier.fillMaxWidth()) {
                    Text(modifier = modifier.padding(10.dp), text = "${state.mentor?.name}")
                }
                Spacer(modifier = modifier.padding(4.dp))
                Card(modifier = modifier.fillMaxWidth())
                {
                    Text(
                        modifier = modifier.padding(vertical = 20.dp, horizontal = 10.dp),
                        text = "${state.mentor?.bio}")
                }
            }
        }
    }
}