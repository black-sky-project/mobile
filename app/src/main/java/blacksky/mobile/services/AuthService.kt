package blacksky.mobile.services

import android.content.SharedPreferences
import blacksky.mobile.exceptions.BadCredentialsException
import blacksky.mobile.exceptions.NotAuthenticatedException
import blacksky.mobile.web.LoginDto
import blacksky.mobile.web.WebClient

object AuthService {
    private var _token: String? = null
    val token: String
        get() = _token ?: throw NotAuthenticatedException("Not authenticated. Should login")
    private lateinit var _sharedPreferences: SharedPreferences

    fun init(sharedPreferences: SharedPreferences) {
        _sharedPreferences = sharedPreferences
        _token = sharedPreferences.getString("token", null)
    }

    fun isAuthenticated() = _token != null

    suspend fun login(login: String, password: String) = try {
        WebClient.login(LoginDto(login, password)).also { _token = it }
        with(_sharedPreferences.edit()) {
            putString("token", _token)
            apply()
        }
    } catch (exception: BadCredentialsException) {
        _token = null
        throw exception
    }

    suspend fun getMe() = WebClient.getMe()
}