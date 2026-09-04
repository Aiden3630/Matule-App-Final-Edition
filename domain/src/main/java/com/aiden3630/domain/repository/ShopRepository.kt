package com.aiden3630.domain.repository

import com.aiden3630.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun addProductToRemoteCart(productId: String, count: Int)
}