package com.buylog.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.buylog.data.model.Product
import com.buylog.platform.AndroidPlatformContext
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "products")
data class RoomProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val imageUrl: String,
    val productUrl: String,
    val platform: String,
    val price: String = "",
    val images: String = "",
    val addedTime: Long = 0L
)

@Dao
interface RoomProductDao {
    @Insert suspend fun insertProduct(product: RoomProduct)
    @Delete suspend fun deleteProduct(product: RoomProduct)
    @Query("SELECT * FROM products ORDER BY addedTime DESC")
    fun getAllProducts(): Flow<List<RoomProduct>>
}

@Database(entities = [RoomProduct::class], version = 1)
abstract class RoomAppDatabase : RoomDatabase() {
    abstract fun productDao(): RoomProductDao
}

class AndroidAppDatabase(private val db: RoomAppDatabase) : AppDatabase {
    override fun productDao(): ProductDao = AndroidProductDao(db.productDao())
}

class AndroidProductDao(private val dao: RoomProductDao) : ProductDao {
    override suspend fun insertProduct(product: Product) {
        dao.insertProduct(RoomProduct(
            title = product.title, imageUrl = product.imageUrl, productUrl = product.productUrl,
            platform = product.platform, price = product.price, images = product.images, addedTime = product.addedTime
        ))
    }
    override suspend fun deleteProduct(product: Product) {
        dao.deleteProduct(RoomProduct(id = product.id, title = product.title, imageUrl = product.imageUrl,
            productUrl = product.productUrl, platform = product.platform, price = product.price, images = product.images, addedTime = product.addedTime))
    }
    override fun getAllProducts(): Flow<List<Product>> = dao.getAllProducts().let { flow ->
        kotlinx.coroutines.flow.flow { flow.collect { list -> emit(list.map { it.toDomain() }) } }
    }
    private fun RoomProduct.toDomain() = Product(id = id, title = title, imageUrl = imageUrl, productUrl = productUrl, platform = platform, price = price, images = images, addedTime = addedTime)
}

actual fun createAppDatabase(): AppDatabase {
    val context = AndroidPlatformContext.context ?: throw IllegalStateException("Context not initialized")
    val db = Room.databaseBuilder(context.applicationContext, RoomAppDatabase::class.java, "buylog_database").build()
    return AndroidAppDatabase(db)
}
