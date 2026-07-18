package com.buylog.data.db

import com.buylog.data.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductDao {
    suspend fun insertProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    fun getAllProducts(): Flow<List<Product>>
}
