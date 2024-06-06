package blacksky.mobile.models

import blacksky.mobile.web.CourseDto
import blacksky.mobile.web.DegreeDto
import blacksky.mobile.web.DepartmentDto
import blacksky.mobile.web.UniversityDto
import java.util.*

interface IId {
    val id: UUID
}

data class University(override val id: UUID, val name: String) : IId

fun UniversityDto.toModel() = University(id, name)

data class Department(override val id: UUID, val name: String, val universityId: UUID) : IId

fun DepartmentDto.toModel() = Department(id, name, universityId)

enum class Degree { Bachelor, Master }

fun DegreeDto.toModel() = when (this) {
    DegreeDto.Master -> Degree.Master
    DegreeDto.Bachelor -> Degree.Bachelor
}

data class Course(override val id: UUID, val name: String, val degree: Degree, val departmentId: UUID) : IId

fun CourseDto.toModel() = Course(id, name, degree.toModel(), departmentId)