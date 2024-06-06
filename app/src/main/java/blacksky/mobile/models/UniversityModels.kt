package blacksky.mobile.models

import blacksky.mobile.services.UniversityDto
import java.util.*

interface IId {
    val id: UUID
}

data class University(override val id: UUID, val name: String) : IId {
    constructor(dto: UniversityDto) : this(dto.id, dto.name)
}