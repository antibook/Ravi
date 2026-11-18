package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_trips")
data class SavedTripEntity(
    @PrimaryKey val id: String,
    val title: String,
    val destinationState: String,
    val citiesCsv: String,
    val dateRange: String,
    val days: Int,
    val adults: Int,
    val children: Int,
    val travelType: String,
    val budgetRange: String,
    val totalBudget: Int,
    val hotelsBudget: Int,
    val transportBudget: Int,
    val foodBudget: Int,
    val ticketsBudget: Int,
    val miscBudget: Int,
    val status: String,
    val isDraft: Boolean,
    val colorKey: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_items")
data class SavedItemEntity(
    @PrimaryKey val itemId: String,
    val title: String,
    val subtitle: String,
    val type: String,
    val colorKey: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "purchased_guides")
data class PurchasedGuideEntity(
    @PrimaryKey val guideId: String,
    val title: String,
    val orderId: String,
    val pricePaid: Int,
    val boughtLabel: String,
    val colorKey: String,
    val purchasedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "taxi_bookings")
data class TaxiBookingEntity(
    @PrimaryKey val bookingCode: String,
    val routeLabel: String,
    val vehicleName: String,
    val driverName: String,
    val vehiclePlate: String,
    val dateTimeLabel: String,
    val passengers: Int,
    val totalAmount: Int,
    val paymentMethod: String,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "manager_bookings")
data class ManagerBookingEntity(
    @PrimaryKey val bookingId: String,
    val managerId: String,
    val managerName: String,
    val stateName: String,
    val city: String,
    val dateRange: String,
    val daysCount: Int,
    val totalPrice: Int,
    val phoneNumber: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val targetRoute: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val author: String,
    val comment: String,
    val rating: Int,
    val verifiedBooking: Boolean,
    val dateLabel: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface TravelGuideDao {
    @Query("SELECT * FROM saved_trips ORDER BY updatedAt DESC")
    fun observeTrips(): Flow<List<SavedTripEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTrip(trip: SavedTripEntity)

    @Query("DELETE FROM saved_trips WHERE id = :id")
    suspend fun deleteTrip(id: String)

    @Query("SELECT * FROM saved_items ORDER BY createdAt DESC")
    fun observeSavedItems(): Flow<List<SavedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedItem(item: SavedItemEntity)

    @Query("DELETE FROM saved_items WHERE itemId = :itemId")
    suspend fun deleteSavedItem(itemId: String)

    @Query("SELECT * FROM purchased_guides ORDER BY purchasedAt DESC")
    fun observePurchasedGuides(): Flow<List<PurchasedGuideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchasedGuide(guide: PurchasedGuideEntity)

    @Query("SELECT * FROM taxi_bookings ORDER BY createdAt DESC")
    fun observeTaxiBookings(): Flow<List<TaxiBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTaxiBooking(booking: TaxiBookingEntity)

    @Query("UPDATE taxi_bookings SET status = :status WHERE bookingCode = :bookingCode")
    suspend fun updateTaxiStatus(bookingCode: String, status: String)

    @Query("SELECT * FROM manager_bookings ORDER BY createdAt DESC")
    fun observeManagerBookings(): Flow<List<ManagerBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManagerBooking(booking: ManagerBookingEntity)

    @Query("SELECT * FROM app_notifications ORDER BY createdAt DESC")
    fun observeNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: String)

    @Query("SELECT * FROM user_reviews ORDER BY createdAt DESC")
    fun observeReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("DELETE FROM saved_trips")
    suspend fun clearTrips()

    @Query("DELETE FROM saved_items")
    suspend fun clearSavedItems()

    @Query("DELETE FROM purchased_guides")
    suspend fun clearPurchases()

    @Query("DELETE FROM taxi_bookings")
    suspend fun clearBookings()

    @Query("DELETE FROM manager_bookings")
    suspend fun clearManagerBookings()

    @Query("DELETE FROM user_reviews")
    suspend fun clearReviews()
}

@Database(
    entities = [
        SavedTripEntity::class,
        SavedItemEntity::class,
        PurchasedGuideEntity::class,
        TaxiBookingEntity::class,
        ManagerBookingEntity::class,
        NotificationEntity::class,
        ReviewEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class TravelGuideDatabase : RoomDatabase() {
    abstract fun dao(): TravelGuideDao

    companion object {
        @Volatile
        private var INSTANCE: TravelGuideDatabase? = null

        fun getInstance(context: Context): TravelGuideDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TravelGuideDatabase::class.java,
                    "travelguide_plus.db"
                ).fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
