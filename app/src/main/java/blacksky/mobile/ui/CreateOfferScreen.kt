package blacksky.mobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import blacksky.mobile.viewModels.SelectOfferViewModel
import java.util.UUID

@Composable
fun CreateOfferScreen(
    navController: NavHostController,
    courseId: UUID,
    modifier: Modifier = Modifier,
    viewModel: SelectOfferViewModel = viewModel()
)
{

}