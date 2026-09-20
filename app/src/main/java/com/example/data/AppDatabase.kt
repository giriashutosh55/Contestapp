package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.model.ContestMode
import com.example.model.ContestStatus
import com.example.model.MatchStatus
import com.example.model.TransactionType
import com.example.model.UserRole
import kotlinx.coroutines.flow.Flow

// ---------------- Entities ----------------

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val phone: String,
    val email: String,
    val level: Int,
    val xp: Int,
    val points: Int,
    val matchesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val role: UserRole,
    val isSuspended: Boolean,
    val isBlocked: Boolean,
    val avatar: String
)

@Entity(tableName = "contests")
data class ContestEntity(
    @PrimaryKey val id: String,
    val name: String,
    val gameId: String,
    val gameName: String,
    val contestMode: ContestMode,
    val durationSeconds: Int,
    val maxPlayers: Int,
    val rewardPoints: Int,
    val entryFeePoints: Int,
    val minimumLevel: Int,
    val maximumLevel: Int,
    val status: ContestStatus,
    val createdBy: String,
    val tier: String,
    val activeParticipants: Int
)

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
    val contestId: String,
    val contestName: String,
    val gameId: String,
    val gameName: String,
    val durationSeconds: Int,
    val rewardPoints: Int,
    val status: MatchStatus,
    val playerAId: String,
    val playerAName: String,
    val playerAScore: Int,
    val playerBId: String,
    val playerBName: String,
    val playerBScore: Int,
    val serverStartTime: Long,
    val serverEndTime: Long,
    val winnerId: String?,
    val isDraw: Boolean,
    val riskScore: Int
)

@Entity(tableName = "reward_transactions")
data class RewardTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: TransactionType,
    val amount: Int,
    val referenceType: String,
    val referenceId: String,
    val status: String,
    val timestamp: Long
)

@Entity(tableName = "disputes")
data class DisputeEntity(
    @PrimaryKey val id: String,
    val matchId: String,
    val reporterId: String,
    val reporterName: String,
    val reason: String,
    val description: String,
    val status: String,
    val adminNotes: String,
    val timestamp: Long
)

// ---------------- DAOs ----------------

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET points = points + :delta, matchesPlayed = matchesPlayed + 1, wins = wins + :winDelta, losses = losses + :lossDelta, draws = draws + :drawDelta, xp = xp + :xpDelta WHERE id = :userId")
    suspend fun recordMatchResult(userId: String, delta: Int, winDelta: Int, lossDelta: Int, drawDelta: Int, xpDelta: Int)

    @Query("UPDATE users SET isSuspended = :suspended, isBlocked = :blocked WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, suspended: Boolean, blocked: Boolean)
}

@Dao
interface ContestDao {
    @Query("SELECT * FROM contests ORDER BY id DESC")
    fun getAllContests(): Flow<List<ContestEntity>>

    @Query("SELECT * FROM contests WHERE status = 'OPEN' OR status = 'LIVE'")
    fun getActiveContests(): Flow<List<ContestEntity>>

    @Query("SELECT * FROM contests WHERE id = :contestId")
    suspend fun getContestById(contestId: String): ContestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContest(contest: ContestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContests(contests: List<ContestEntity>)

    @Query("UPDATE contests SET status = :status WHERE id = :contestId")
    suspend fun updateContestStatus(contestId: String, status: ContestStatus)
}

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE playerAId = :userId OR playerBId = :userId ORDER BY serverStartTime DESC")
    fun getMatchesForUser(userId: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches ORDER BY serverStartTime DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Query("UPDATE matches SET status = :status WHERE id = :matchId")
    suspend fun updateMatchStatus(matchId: String, status: MatchStatus)
}

@Dao
interface RewardTransactionDao {
    @Query("SELECT * FROM reward_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<RewardTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: RewardTransactionEntity)
}

@Dao
interface DisputeDao {
    @Query("SELECT * FROM disputes ORDER BY timestamp DESC")
    fun getAllDisputes(): Flow<List<DisputeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispute(dispute: DisputeEntity)

    @Query("UPDATE disputes SET status = :status, adminNotes = :notes WHERE id = :disputeId")
    suspend fun resolveDispute(disputeId: String, status: String, notes: String)
}

// ---------------- Type Converters ----------------

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name
    @TypeConverter
    fun toUserRole(value: String): UserRole = enumValueOf(value)

    @TypeConverter
    fun fromContestMode(value: ContestMode): String = value.name
    @TypeConverter
    fun toContestMode(value: String): ContestMode = enumValueOf(value)

    @TypeConverter
    fun fromContestStatus(value: ContestStatus): String = value.name
    @TypeConverter
    fun toContestStatus(value: String): ContestStatus = enumValueOf(value)

    @TypeConverter
    fun fromMatchStatus(value: MatchStatus): String = value.name
    @TypeConverter
    fun toMatchStatus(value: String): MatchStatus = enumValueOf(value)

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name
    @TypeConverter
    fun toTransactionType(value: String): TransactionType = enumValueOf(value)
}

// ---------------- Database ----------------

@Database(
    entities = [
        UserEntity::class,
        ContestEntity::class,
        MatchEntity::class,
        RewardTransactionEntity::class,
        DisputeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun contestDao(): ContestDao
    abstract fun matchDao(): MatchDao
    abstract fun rewardTransactionDao(): RewardTransactionDao
    abstract fun disputeDao(): DisputeDao
}
