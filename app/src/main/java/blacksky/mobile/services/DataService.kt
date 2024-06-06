package blacksky.mobile.services

import blacksky.mobile.models.*
import blacksky.mobile.web.WebClient
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

internal abstract class Storage<T> where T : IId {
    private val loadCooldown = 1.minutes

    private val cache = mutableMapOf<UUID, T>()
    private var lastLoad: ComparableTimeMark? = null
    private val mutex = Mutex()

    companion object {
        val timeSource = TimeSource.Monotonic
    }

    suspend fun getById(id: UUID): T? = refreshIfNeed().let { mutex.withLock { cache[id] } }

    suspend fun getAll(): List<T> = refreshIfNeed().let { mutex.withLock { cache.values.toList() } }

    protected abstract suspend fun load()

    private suspend fun refreshIfNeed() =
        lastLoad?.let { it.takeIf { timeSource.markNow() - it > loadCooldown }?.let { load() } } ?: load()

    protected suspend fun storeLoaded(entries: List<T>) = mutex.withLock {
        cache.clear()
        entries.forEach { cache[it.id] = it }
    }
}

internal class UniversityStorage : Storage<University>() {
    override suspend fun load() = WebClient.getUniversities().map { it.toModel() }.let { storeLoaded(it) }
}

internal class DepartmentStorage : Storage<Department>() {
    override suspend fun load() = WebClient.getDepartments().map { it.toModel() }.let { storeLoaded(it) }
}

internal class CoursesStorage : Storage<Course>() {
    override suspend fun load() = WebClient.getCourses().map { it.toModel() }.let { storeLoaded(it) }
}

internal class StudentStorage : Storage<Student>() {
    override suspend fun load() = WebClient.getStudents().map { it.toModel() }.let { storeLoaded(it) }
}

internal class MentorStorage : Storage<Mentor>() {
    override suspend fun load() = WebClient.getMentors().map { it.toModel() }.let { storeLoaded(it) }
}

object DataService {
    private val universityStorage = UniversityStorage()
    private val departmentStorage = DepartmentStorage()
    private val coursesStorage = CoursesStorage()
    private val studentStorage = StudentStorage()
    private val mentorStorage = MentorStorage()

    suspend fun getUniversities() = universityStorage.getAll()
    suspend fun getUniversityById(id: UUID) = universityStorage.getById(id)

    suspend fun getDepartments() = departmentStorage.getAll()
    suspend fun getDepartmentById(id: UUID) = departmentStorage.getById(id)
    suspend fun getDepartmentsByUniversity(universityId: UUID) =
        departmentStorage.getAll().filter { it.universityId == universityId }

    suspend fun getCourses() = coursesStorage.getAll()
    suspend fun getCourseById(id: UUID) = coursesStorage.getById(id)
    suspend fun getCoursesByDepartment(departmentId: UUID) =
        coursesStorage.getAll().filter { it.departmentId == departmentId }

    suspend fun getStudents() = studentStorage.getAll()
    suspend fun getStudentById(id: UUID) = studentStorage.getById(id)
    suspend fun getStudentsByDepartment(departmentId: UUID) =
        studentStorage.getAll().filter { it.departmentId == departmentId }

    suspend fun getMentors() = mentorStorage.getAll()
    suspend fun getMentorById(id: UUID) = mentorStorage.getById(id)
    suspend fun getMentorsByDepartment(departmentId: UUID) =
        mentorStorage.getAll().filter { it.departmentId == departmentId }
}