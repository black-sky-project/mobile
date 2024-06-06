package blacksky.mobile.web

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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

@Serializable
enum class DegreeDto { Bachelor, Master }

@Serializable
data class CourseDto(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String,
    val degree: DegreeDto,
    @Serializable(with = UUIDSerializer::class) val departmentId: UUID
)

@Serializable
data class UserDto(
    @Serializable(with = UUIDSerializer::class) val id: UUID, val login: String, val name: String
)

@Serializable
data class StudentDto(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val login: String,
    val name: String,
    val acquiringDegree: DegreeDto,
    @Serializable(with = UUIDSerializer::class) val departmentId: UUID
)

@Serializable
data class MentorDto(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val login: String,
    val name: String,
    val bio: String,
    @Serializable(with = UUIDSerializer::class) val departmentId: UUID
)

@Serializable
data class OfferDto(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @Serializable(with = UUIDSerializer::class) val mentorId: UUID,
    @Serializable(with = UUIDSerializer::class) val courseId: UUID,
    val title: String,
    val description: String
)

@Serializable
data class LoginDto(val login: String, val password: String)

@Serializable
data class PostOfferDto(
    @Serializable(with = UUIDSerializer::class) val mentorId: UUID,
    @Serializable(with = UUIDSerializer::class) val courseId: UUID,
    val title: String,
    val description: String
)