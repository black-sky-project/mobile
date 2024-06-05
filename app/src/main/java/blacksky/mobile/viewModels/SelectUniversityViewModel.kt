package blacksky.mobile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blacksky.mobile.models.University
import blacksky.mobile.services.WebClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class SelectUniversitiesScreenState(
    val universities: List<University> = emptyList(), val error: String? = null, val isLoading: Boolean = false
)

class SelectUniversityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelectUniversitiesScreenState(isLoading = true))
    val uiState: StateFlow<SelectUniversitiesScreenState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { updateUniversities() }
    }

    private suspend fun updateUniversities() {
        _uiState.update { it.copy(isLoading = true) }
        val universities = try {
            WebClient.getUniversities().map { University(it) }.also { _uiState.update { it.copy(error = null) } }
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