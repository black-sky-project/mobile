package blacksky.mobile.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.util.*

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

@Serializable
data class UniversityDto(@Serializable(with = UUIDSerializer::class) val id: UUID, val name: String)

@Serializable
data class DepartmentDto(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String,
    @Serializable(with = UUIDSerializer::class) val universityId: UUID
)

object WebClient {
    private const val BASE_URL = "http://147.45.158.234:8080/api/v1"
    private const val TOKEN = "0d2a4c89-61d9-4dff-af79-ca8b44141a62"
    private val client = OkHttpClient()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    suspend fun getUniversities() = getList<UniversityDto>("$BASE_URL/universities/get/list")

    suspend fun getDepartments() = getList<DepartmentDto>("$BASE_URL/departments/get/list")

    private suspend fun <T> getList(url: String) = scope.async {
        val request = Request.Builder().url(url).addHeader("Token", TOKEN).build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful.not()) throw IOException("Request failed: $response")
            response.body?.string()
        }?.let { Json.decodeFromString<List<T>>(it) } ?: throw IOException("Empty body received")
    }.await()
}