package com.aiden3630.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val title: String,
    val price: Int,
    val category: String,
    val imageUrl: String? = null,
    val description: String = "",
    val consumption: String = ""
)

// Класс для корзины (оставь его здесь же, если он нужен)
data class CartItemModel(
    val product: Product,
    val quantity: Int
)