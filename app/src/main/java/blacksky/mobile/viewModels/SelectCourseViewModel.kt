package blacksky.mobile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blacksky.mobile.models.Course
import blacksky.mobile.models.Department
import blacksky.mobile.services.AuthService
import blacksky.mobile.services.DataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class SelectCoursesScreenState(
    val courses: List<Course> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false,
    val isNeedToAuthorize: Boolean = false,
    val departmentId: UUID? = null
)

class SelectCourseViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelectCoursesScreenState(isLoading = true))
    val uiState: StateFlow<SelectCoursesScreenState> = _uiState.asStateFlow()

    fun launch(departmentId: UUID) {
        _uiState.update { it.copy(departmentId = departmentId) }
        viewModelScope.launch { loadCourses() }
    }

    private suspend fun loadCourses() {
        _uiState.update { it.copy(isLoading = true) }
        if (AuthService.isAuthenticated().not()) {
            _uiState.update { it.copy(isLoading = false, isNeedToAuthorize = true) }
            return
        }
        val courses = try {
            _uiState.value.departmentId?.let {
                DataService.getCoursesByDepartment(it).also {
                    _uiState.update { currentState -> currentState.copy(error = null) }
                }
            } ?: emptyList()
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = "$exception") }
            emptyList()
        }
        _uiState.update { it.copy(courses = courses, isLoading = false) }
    }
}