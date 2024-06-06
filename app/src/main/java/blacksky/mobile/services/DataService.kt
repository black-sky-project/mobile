package blacksky.mobile.services

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
    override suspend fun load() = WebClient.getUniversities().map { University(it) }.let { storeLoaded(it) }
}

object DataService {
    private val universityStorage = UniversityStorage()

    suspend fun getUniversities() = universityStorage.getAll()
    suspend fun getUniversityById(id: UUID) = universityStorage.getById(id)
}