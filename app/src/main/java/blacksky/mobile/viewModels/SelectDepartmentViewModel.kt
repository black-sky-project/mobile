package blacksky.mobile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blacksky.mobile.models.Department
import blacksky.mobile.models.University
import blacksky.mobile.services.AuthService
import blacksky.mobile.services.DataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class SelectDepartmentsScreenState(
    val departments: List<Department> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false,
    val isNeedToAuthorize: Boolean = false,
    val universityId: UUID? = null
)

class SelectDepartmentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelectDepartmentsScreenState(isLoading = true))
    val uiState: StateFlow<SelectDepartmentsScreenState> = _uiState.asStateFlow()

    fun launch(universityId: UUID) {
        viewModelScope.launch {
            _uiState.update { it.copy(universityId = universityId) }
            loadDepartments() }
    }

    private suspend fun loadDepartments() {
        _uiState.update { it.copy(isLoading = true) }
        if (AuthService.isAuthenticated().not()) {
            _uiState.update { it.copy(isLoading = false, isNeedToAuthorize = true) }
            return
        }
        val departments = try {
            _uiState.value.universityId?.let {
                DataService.getDepartmentsByUniversity(it).also {
                    _uiState.update { currentState -> currentState.copy(error = null) }
                }
            } ?: emptyList()
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = "$exception") }
            emptyList()
        }
        _uiState.update { it.copy(departments = departments, isLoading = false) }
    }

    fun setPreviewMode() {
        val departmentsExample = listOf(
            Department(id = UUID.randomUUID(), name = "Mathematics", universityId = UUID.randomUUID()),
            Department(id = UUID.randomUUID(), name = "Mechatronics & Robotics", universityId = UUID.randomUUID()),
            Department(id = UUID.randomUUID(), name = "Physics", universityId = UUID.randomUUID()),
            Department(id = UUID.randomUUID(), name = "Natural Sciences", universityId = UUID.randomUUID()),
            Department(id = UUID.randomUUID(), name = "IT", universityId = UUID.randomUUID()),
            Department(id = UUID.randomUUID(), name = "Economics", universityId = UUID.randomUUID()),
        )
        _uiState.update {
            SelectDepartmentsScreenState(
                departments = departmentsExample, error = null, isLoading = false
            )
        }
    }
}