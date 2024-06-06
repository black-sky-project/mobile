package blacksky.mobile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blacksky.mobile.exceptions.BadCredentialsException
import blacksky.mobile.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginScreenState(
    val login: String = "",
    val password: String = "",
    val isWaitingForResponse: Boolean = false,
    val usedBadCredentials: Boolean = false,
    val authSuccessful: Boolean = false
) {
    val isButtonActive get() = login.isNotEmpty() && password.isNotEmpty() && !isWaitingForResponse && !usedBadCredentials
}

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState: StateFlow<LoginScreenState> = _uiState.asStateFlow()

    fun updateLogin(newLoginString: String) =
        _uiState.update { it.copy(login = newLoginString, usedBadCredentials = false) }

    fun updatePassword(newPasswordString: String) =
        _uiState.update { it.copy(password = newPasswordString, usedBadCredentials = false) }

    fun login() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isWaitingForResponse = true) }
                AuthService.login(uiState.value.login, uiState.value.password)
                _uiState.update { it.copy(isWaitingForResponse = false, authSuccessful = true) }
            } catch (exception: BadCredentialsException) {
                _uiState.update { it.copy(isWaitingForResponse = false, usedBadCredentials = true) }
            } catch (exception: Exception) {
                TODO("Implement exception handling")
            }
        }
    }
}