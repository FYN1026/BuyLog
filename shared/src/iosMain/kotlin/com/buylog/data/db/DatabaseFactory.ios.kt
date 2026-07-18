package com.buylog.data.db

import com.buylog.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual fun createAppDatabase(): AppDatabase = IosAppDatabase()

class IosAppDatabase : AppDatabase {
    override fun productDao(): ProductDao = IosProductDao()
}

class IosProductDao : ProductDao {
    private val products = mutableListOf<Product>()
    override suspend fun insertProduct(product: Product) { products.add(product.copy(id = products.size + 1)) }
    override suspend fun deleteProduct(product: Product) { products.removeAll { it.id == product.id } }
    override fun getAllProducts(): Flow<List<Product>> = flowOf(products.toList())
}
