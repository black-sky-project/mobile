package blacksky.mobile.web

import blacksky.mobile.exceptions.BadCredentialsException
import blacksky.mobile.services.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

object WebClient {
    private const val BASE_URL = "http://147.45.158.234:8080/api/v1"
    private const val FORBIDDEN = 403

    private val client = OkHttpClient()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    suspend fun getUniversities() = getList<UniversityDto>("$BASE_URL/universities/get/list")
    suspend fun getDepartments() = getList<DepartmentDto>("$BASE_URL/departments/get/list")
    suspend fun getCourses() = getList<CourseDto>("$BASE_URL/courses/get/list")
    suspend fun getStudents() = getList<StudentDto>("$BASE_URL/users/get/students")
    suspend fun getMentors() = getList<MentorDto>("$BASE_URL/users/get/mentors")
    suspend fun getOffers() = getList<OfferDto>("$BASE_URL/offers/list")
    suspend fun getMe() = getOne<UserDto>("$BASE_URL/auth/getMe")

    suspend fun login(loginDto: LoginDto) = scope.async {
        val request = Request.Builder().url("$BASE_URL/auth/login")
            .post(Json.encodeToString(loginDto).toRequestBody("application/json; charset=utf-8".toMediaType())).build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful.not()) {
                if (response.code == FORBIDDEN) throw BadCredentialsException("Bad login or password")
                throw IOException("Request failed: $response")
            }
            response.body?.string()
        } ?: throw IOException("Empty body received")
    }.await()

    private suspend inline fun <reified T : Any> getList(url: String) = scope.async {
        val request = Request.Builder().url(url).addHeader("Token", AuthService.token).build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful.not()) throw IOException("Request failed: $response")
            response.body?.string()
        }?.let {
            try {
                Json.decodeFromString<List<T>>(it)
            } catch (exception: SerializationException) {
                throw IOException("Bad JSON received $exception")
            } catch (exception: IllegalArgumentException) {
                throw IOException("Bad type of received body: $exception}")
            } catch (exception: Exception) {
                throw IOException("Failed to deserialize: $exception")
            }
        } ?: throw IOException("Empty body received")
    }.await()

    private suspend inline fun <reified T : Any> getOne(url: String) = scope.async {
        val request = Request.Builder().url(url).addHeader("Token", AuthService.token).build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful.not()) throw IOException("Request failed: $response")
            response.body?.string()
        }?.let {
            try {
                Json.decodeFromString<T>(it)
            } catch (exception: SerializationException) {
                throw IOException("Bad JSON received $exception")
            } catch (exception: IllegalArgumentException) {
                throw IOException("Bad type of received body: $exception}")
            } catch (exception: Exception) {
                throw IOException("Failed to deserialize: $exception")
            }
        } ?: throw IOException("Empty body received")
    }.await()
}