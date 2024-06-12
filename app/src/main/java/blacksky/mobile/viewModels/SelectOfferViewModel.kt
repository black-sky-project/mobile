package blacksky.mobile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blacksky.mobile.models.Course
import blacksky.mobile.models.Offer
import blacksky.mobile.services.AuthService
import blacksky.mobile.services.AuthService.getMe
import blacksky.mobile.services.DataService
import blacksky.mobile.services.DataService.getMentorById
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class SelectOffersScreenState(
    val offers: List<Offer> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false,
    val isNeedToAuthorize: Boolean = false,
    val courseId: UUID? = null,
    val amMentor: Boolean = false
)

class SelectOfferViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelectOffersScreenState(isLoading = true))
    val uiState: StateFlow<SelectOffersScreenState> = _uiState.asStateFlow()

    fun launch(courseId: UUID) {

        viewModelScope.launch {
            _uiState.update { it.copy(courseId = courseId) }
            loadOffers() }
    }

    private suspend fun loadOffers() {
        if (AuthService.isAuthenticated().not()) {
            _uiState.update { it.copy(isLoading = false, isNeedToAuthorize = true) }
            return
        }
        val offers = try {
            _uiState.value.courseId?.let {
                DataService.getOffersByCourse(it).also {
                    _uiState.update { currentState -> currentState.copy(error = null) }
                }
            } ?: emptyList()
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = "$exception") }
            emptyList()
        }
        val amMentor = try {
            getMe().let { (getMentorById(it.id) != null) }
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = "$exception") }
            false
        }
        _uiState.update { it.copy(offers = offers, amMentor = amMentor, isLoading = false) }
    }
}