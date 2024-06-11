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

data class MentorInfoScreenState(
    val error: String? = null,
    val isLoading: Boolean = false,
    val isNeedToAuthorize: Boolean = false,
    val mentorId: UUID? = null,
    val mentor: Mentor? = null
)

class MentorInfoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MentorInfoScreenState(isLoading = true))
    val uiState: StateFlow<MentorInfoScreenState> = _uiState.asStateFlow()

    fun launch(mentorId: UUID) {
        _uiState.update { it.copy(mentorId = mentorId) }
        viewModelScope.launch { loadMentorInfo() }
    }

    private suspend fun loadMentorInfo() {
        _uiState.update { it.copy(isLoading = true) }
        if (AuthService.isAuthenticated().not()) {
            _uiState.update { it.copy(isLoading = false, isNeedToAuthorize = true) }
            return
        }
        val mentor = try {
            _uiState.value.mentorId?.let {
                DataService.getMentorById(it).also {
                    _uiState.update { currentState -> currentState.copy(error = null) }
                }
            }
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = "$exception") }
            null
        }
        _uiState.update { it.copy(mentor = mentor, isLoading = false) }
    }
}