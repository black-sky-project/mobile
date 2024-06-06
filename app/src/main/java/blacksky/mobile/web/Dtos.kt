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