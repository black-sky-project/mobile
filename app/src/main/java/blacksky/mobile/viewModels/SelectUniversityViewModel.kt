package blacksky.mobile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blacksky.mobile.models.University
import blacksky.mobile.services.AuthService
import blacksky.mobile.services.DataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class SelectUniversitiesScreenState(
    val universities: List<University> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false,
    val isNeedToAuthorize: Boolean = false
)

class SelectUniversityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelectUniversitiesScreenState(isLoading = true))
    val uiState: StateFlow<SelectUniversitiesScreenState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadUniversities() }
    }

    private suspend fun loadUniversities() {
        _uiState.update { it.copy(isLoading = true) }
        if (AuthService.isAuthenticated().not()) {
            _uiState.update { it.copy(isLoading = false, isNeedToAuthorize = true) }
            return
        }
        val universities = try {
            DataService.getUniversities().also { _uiState.update { it.copy(error = null) } }
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = "$exception") }
            emptyList()
        }
        _uiState.update { it.copy(universities = universities, isLoading = false) }
    }

    fun setPreviewMode() {
        val universitiesExample = listOf(
            University(id = UUID.randomUUID(), name = "Novosibirsk State University"),
            University(id = UUID.randomUUID(), name = "Tomsk State University"),
            University(id = UUID.randomUUID(), name = "Moscow State University"),
            University(id = UUID.randomUUID(), name = "St. Petersburg State University"),
            University(id = UUID.randomUUID(), name = "Far East Federal University"),
            University(id = UUID.randomUUID(), name = "Moscow Institute of Physics and Technology"),
        )
        _uiState.update {
            SelectUniversitiesScreenState(
                universities = universitiesExample, error = null, isLoading = false
            )
        }
    }
}