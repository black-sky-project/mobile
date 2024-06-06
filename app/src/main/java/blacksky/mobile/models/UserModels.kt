package blacksky.mobile.models

import blacksky.mobile.web.MentorDto
import blacksky.mobile.web.StudentDto
import blacksky.mobile.web.UserDto
import java.util.*

interface IUser : IId {
    override val id: UUID
    val login: String
    val name: String
}

data class User(override val id: UUID, override val login: String, override val name: String) : IUser

fun UserDto.toModel() = User(id, login, name)

data class Student(
    override val id: UUID,
    override val login: String,
    override val name: String,
    val acquiringDegree: Degree,
    val departmentId: UUID
) : IUser

fun StudentDto.toModel() = Student(id, login, name, acquiringDegree.toModel(), departmentId)

data class Mentor(
    override val id: UUID,
    override val login: String,
    override val name: String,
    val departmentId: UUID,
    val bio: String
) : IUser

fun MentorDto.toModel() = Mentor(id, login, name, departmentId, bio)