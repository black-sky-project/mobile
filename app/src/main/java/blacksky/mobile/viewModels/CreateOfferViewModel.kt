package blacksky.mobile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blacksky.mobile.exceptions.BadCredentialsException
import blacksky.mobile.models.Mentor
import blacksky.mobile.services.AuthService
import blacksky.mobile.services.AuthService.getMe
import blacksky.mobile.services.DataService
import blacksky.mobile.web.OfferDto
import blacksky.mobile.web.PostOfferDto
import blacksky.mobile.web.WebClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class CreateOfferScreenState(
    val error: String? = null,
    val isLoading: Boolean = false,
    val isNeedToAuthorize: Boolean = false,
    val courseId: UUID? = null,
    val mentorId: UUID? = null,
    val offerTitle: String = "",
    val offerDescription: String = ""
)

class CreateOfferViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CreateOfferScreenState(isLoading = true))
    val uiState: StateFlow<CreateOfferScreenState> = _uiState.asStateFlow()

    fun updateTitle(newTitleString: String) =
        _uiState.update { it.copy(offerTitle = newTitleString) }

    fun updateDescription(newDescriptionString: String) =
        _uiState.update { it.copy(offerDescription = newDescriptionString) }

    fun launch(courseId: UUID) {
        _uiState.update { it.copy(courseId = courseId) }
        viewModelScope.launch { loadMentorId() }
    }

    fun login() {
        viewModelScope.launch {
            WebClient.postOffer(
                PostOfferDto(
                    uiState.value.mentorId!!,
                    uiState.value.courseId!!,
                    uiState.value.offerTitle,
                    uiState.value.offerDescription
                )
            )
        }
    }

    private suspend fun loadMentorId() {
        _uiState.update { it.copy(isLoading = true) }
        if (AuthService.isAuthenticated().not()) {
            _uiState.update { it.copy(isLoading = false, isNeedToAuthorize = true) }
            return
        }
        val mentorId: UUID? = try {
            getMe().let {
                if (DataService.getMentorById(it.id) != null)
                    it.id
                else
                    null
            }
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = "$exception") }
            null
        }
        if (mentorId != null)
            _uiState.update { it.copy(mentorId = mentorId, isLoading = false) }
        else
            _uiState.update {
                it.copy(
                    error = "You must be a mentor to proceed",
                    isLoading = false
                )
            }
    }
}