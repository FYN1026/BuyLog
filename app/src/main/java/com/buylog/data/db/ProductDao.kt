import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.buylog.data.model.Product

@Dao
interface ProductDao {
    @Insert
    suspend fun insertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)
}