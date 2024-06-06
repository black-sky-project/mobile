package blacksky.mobile.services

import blacksky.mobile.models.Department
import blacksky.mobile.models.IId
import blacksky.mobile.models.University
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

internal abstract class Storage<T> where T : IId {
    private val loadCooldown = 1.minutes

    protected val cache = mutableMapOf<UUID, T>()
    private var lastLoad: ComparableTimeMark? = null
    protected val mutex = Mutex()

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
    override suspend fun load() = WebClient.getUniversities().map { University(it) }.let { storeLoaded(it) }
}

internal class DepartmentStorage : Storage<Department>() {
    override suspend fun load() = WebClient.getDepartments().map { Department(it) }.let { storeLoaded(it) }
}

object DataService {
    private val universityStorage = UniversityStorage()
    private val departmentStorage = DepartmentStorage()

    suspend fun getUniversities() = universityStorage.getAll()
    suspend fun getUniversityById(id: UUID) = universityStorage.getById(id)

    suspend fun getDepartments() = departmentStorage.getAll()
    suspend fun getDepartmentById(id: UUID) = departmentStorage.getById(id)
    suspend fun getDepartmentsByUniversity(universityId: UUID) =
        departmentStorage.getAll().filter { it.universityId == universityId }
}