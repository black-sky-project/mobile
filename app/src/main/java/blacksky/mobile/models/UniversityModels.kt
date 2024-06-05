package blacksky.mobile.models

import blacksky.mobile.services.UniversityDto
import java.util.*

data class University(val id: UUID, val name: String) {
    constructor(dto: UniversityDto) : this(dto.id, dto.name)
}