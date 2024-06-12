package blacksky.mobile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blacksky.mobile.models.Mentor
import blacksky.mobile.models.Offer
import blacksky.mobile.services.AuthService
import blacksky.mobile.services.DataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class OfferInfoScreenState(
    val error: String? = null,
    val isLoading: Boolean = false,
    val isNeedToAuthorize: Boolean = false,
    val offerId: UUID? = null,
    val offer: Offer? = null,
    val mentor: Mentor? = null
)

class OfferInfoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OfferInfoScreenState(isLoading = true))
    val uiState: StateFlow<OfferInfoScreenState> = _uiState.asStateFlow()

    fun launch(offerId: UUID) {
        viewModelScope.launch {
            _uiState.update { it.copy(offerId = offerId) }
            loadOfferInfo() }
    }

    private suspend fun loadOfferInfo() {
        _uiState.update { it.copy(isLoading = true) }
        if (AuthService.isAuthenticated().not()) {
            _uiState.update { it.copy(isLoading = false, isNeedToAuthorize = true) }
            return
        }
        val offer = try {
            _uiState.value.offerId?.let {
                DataService.getOfferById(it).also {
                    _uiState.update { currentState -> currentState.copy(error = null) }
                }
            }
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = "$exception") }
            null
        }
        _uiState.update { it.copy(offer = offer) }
        val mentor = try {
            _uiState.value.offerId?.let {
                offer?.let {
                    DataService.getMentorById(it.mentorId).also {
                        _uiState.update { currentState -> currentState.copy(error = null) }
                    }
                }
            }
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = "$exception") }
            null
        }
        _uiState.update { it.copy(mentor = mentor, isLoading = false) }
    }
}