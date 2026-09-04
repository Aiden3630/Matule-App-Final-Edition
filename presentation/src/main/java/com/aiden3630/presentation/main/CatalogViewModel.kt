package com.aiden3630.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiden3630.domain.model.Product
import com.aiden3630.domain.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Все")
    val selectedCategory = _selectedCategory.asStateFlow()

    // Наш отфильтрованный список
    val filteredProducts = combine(_allProducts, _searchText, _selectedCategory) { products, text, category ->
        products.filter { product ->
            val matchesSearch = product.title.contains(text, ignoreCase = true)

            val matchesCategory = when (category) {
                "Все" -> true
                "Женщинам" -> {
                    // Ищем и точное совпадение, и вариант из твоего JSON
                    product.category == "Женщинам" || product.category == "Женская одежда"
                }
                "Мужчинам" -> {
                    product.category == "Мужчинам" || product.category == "Мужская одежда"
                }
                "Детям" -> {
                    product.category == "Детям" || product.category == "Детская одежда"
                }
                "Аксессуары" -> {
                    product.category == "Аксессуары" || product.category == "Аксессуары"
                }
                else -> product.category == category
            }

            matchesSearch && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            shopRepository.getProducts().collect { list ->
                _allProducts.value = list
            }
        }
    }

    fun onSearchTextChange(text: String) { _searchText.value = text }
    fun onCategoryChange(category: String) { _selectedCategory.value = category }
}