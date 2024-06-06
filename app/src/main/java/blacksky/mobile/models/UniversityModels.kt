package blacksky.mobile.models

import blacksky.mobile.services.DepartmentDto
import blacksky.mobile.services.UniversityDto
import java.util.*

interface IId {
    val id: UUID
}

data class University(override val id: UUID, val name: String) : IId {
    constructor(dto: UniversityDto) : this(dto.id, dto.name)
}

data class Department(override val id: UUID, val name: String, val universityId: UUID) : IId {
    constructor(dto: DepartmentDto) : this(dto.id, dto.name, dto.universityId)
}