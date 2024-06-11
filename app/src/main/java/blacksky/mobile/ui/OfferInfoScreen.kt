package blacksky.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import blacksky.mobile.navigation.Screens
import blacksky.mobile.viewModels.OfferInfoViewModel
import blacksky.mobile.viewModels.SelectOfferViewModel
import java.util.UUID

@Preview
@Composable
fun OfferInfoScreenExample() {
    val modifier = Modifier
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Card(modifier = modifier.fillMaxWidth()) {
            Text(modifier = modifier.padding(10.dp), text = "OfferTitle")
        }
        Spacer(modifier = modifier.padding(4.dp))
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround)
        {
            Card(
                modifier = modifier
                    .weight(1f)
                    .padding(end = 2.dp)
            ) {
                Text(
                    modifier = modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                    text = "Mentor: MentorName")
            }
            Card(
                modifier = modifier
                    .weight(1f)
                    .padding(start = 2.dp)
            ) {
                Text(
                    modifier = modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                    text = "Rating: 5.0")
            }
        }
        Spacer(modifier = modifier.padding(4.dp))
        Card(modifier = modifier.fillMaxWidth())
        {
            Text(
                modifier = modifier.padding(vertical = 20.dp, horizontal = 10.dp),
                text = "Description")
        }
    }
}

@Composable
fun OfferInfoScreen(
    navController: NavHostController,
    offerId: UUID,
    modifier: Modifier = Modifier,
    viewModel: OfferInfoViewModel = viewModel()
) {
    viewModel.launch(offerId)

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
                    Text(modifier = modifier.padding(10.dp), text = "${state.offer?.title}")
                }
                Spacer(modifier = modifier.padding(4.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                )
                {
                    Card(
                        modifier = modifier
                            .weight(1f)
                            .padding(end = 2.dp)
                            .clickable {
                                navController.navigate(
                                    "${Screens.MentorInfo.route}/${state.mentor?.id}"
                                )
                            }
                    ) {
                        Text(
                            modifier = modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                            text = "Mentor: ${state.mentor?.name}"
                        )
                    }
                    Card(
                        modifier = modifier
                            .weight(1f)
                            .padding(start = 2.dp)
                    ) {
                        Text(modifier = modifier.padding(vertical = 5.dp, horizontal = 10.dp), text = "Rating: 5.0")
                    }
                }
                Spacer(modifier = modifier.padding(4.dp))
                Card(modifier = modifier.fillMaxWidth())
                {
                    Text(
                        modifier = modifier.padding(vertical = 20.dp, horizontal = 10.dp),
                        text = "${state.offer?.description}")
                }
            }
        }
    }
}