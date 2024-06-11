package blacksky.mobile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blacksky.mobile.models.Course
import blacksky.mobile.models.Offer
import blacksky.mobile.services.AuthService
import blacksky.mobile.services.DataService
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
    val courseId: UUID? = null
)

class SelectOfferViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelectOffersScreenState(isLoading = true))
    val uiState: StateFlow<SelectOffersScreenState> = _uiState.asStateFlow()

    fun launch(courseId: UUID) {
        _uiState.update { it.copy(courseId = courseId) }
        viewModelScope.launch { loadOffers() }
    }

    private suspend fun loadOffers() {
        _uiState.update { it.copy(isLoading = true) }
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
        _uiState.update { it.copy(offers = offers, isLoading = false) }
    }
}