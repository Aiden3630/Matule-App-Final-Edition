package com.aiden3630.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiden3630.domain.model.Product
import com.aiden3630.presentation.components.ErrorBanner
import com.aiden3630.presentation.components.MatuleChip
import com.aiden3630.presentation.components.MatuleSearchField
import com.aiden3630.presentation.components.ProductCard
import com.aiden3630.presentation.theme.*
import com.aiden3630.presentation.R as UiKitR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    cartViewModel: CartViewModel = hiltViewModel(),
    catalogViewModel: CatalogViewModel = hiltViewModel()
) {
    val searchText by catalogViewModel.searchText.collectAsState()
    val selectedCategory by catalogViewModel.selectedCategory.collectAsState()
    val products by catalogViewModel.filteredProducts.collectAsState()

    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartTotal by cartViewModel.totalSum.collectAsState()

    var selectedProductForSheet by remember { mutableStateOf<Product?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val categories = listOf("Все", "Женщинам", "Мужчинам", "Детям", "Аксессуары")

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MatuleWhite)
                .statusBarsPadding() // Чтобы не наезжало на часы
                .padding(horizontal = 20.dp), // left/right: 20px
            contentPadding = PaddingValues(bottom = 100.dp) // Место под кнопку корзины
        ) {
            // --- 1. Хедер (Поиск + Профиль) ---
            item {
                Spacer(modifier = Modifier.height(16.dp)) // Отступ сверху (top: 72px минус статусбар)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MatuleSearchField(
                        value = searchText,
                        onValueChange = { catalogViewModel.onSearchTextChange(it) },
                        modifier = Modifier.weight(1f) // Поиск занимает всё место
                    )

                    Spacer(modifier = Modifier.width(14.dp)) // Отступ до профиля

                    // Иконка профиля (черная, без фона, как в CSS)
                    Box(
                        modifier = Modifier
                            .size(48.dp) // Зона клика
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            painter = painterResource(id = UiKitR.drawable.ic_profile_black),
                            contentDescription = "Profile",
                            tint = MatuleBlack,
                            modifier = Modifier.size(32.dp) // Размер иконки 32x32
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Отступ до категорий
            }

            // --- 2. Категории ---
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories.size) { index ->
                        MatuleChip(
                            text = categories[index],
                            isSelected = selectedCategory == categories[index],
                            onClick = {
                                catalogViewModel.onCategoryChange(categories[index])
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp)) // Отступ до списка
            }

            // --- 3. Товары ---
            if (products.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), contentAlignment = Alignment.Center) {
                        Text("Ничего не найдено", style = BodyText, color = MatuleTextGray)
                    }
                }
            } else {
                items(products) { product ->
                    val isProductInCart = cartItems.any { cartItem -> cartItem.product.id == product.id }

                    ProductCard(
                        title = product.title,
                        price = "${product.price} ₽",
                        category = product.category,
                        isInCart = isProductInCart,
                        onAddClick = { cartViewModel.onPlusClick(product) },
                        onRemoveClick = { cartViewModel.onDeleteClick(product) },
                        onClick = { selectedProductForSheet = product }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // --- 4. Кнопка Корзины ---
        if (cartTotal > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(10.dp, RoundedCornerShape(10.dp), spotColor = Color(0x40000000))
                    .background(MatuleBlue, RoundedCornerShape(10.dp)) // CSS: radius 10px
                    .clickable { onCartClick() }
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = UiKitR.drawable.ic_cart),
                        contentDescription = null,
                        tint = MatuleWhite,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("В корзину", style = Title3.copy(color = MatuleWhite, fontWeight = FontWeight.SemiBold))
                }
                Text(
                    text = "$cartTotal ₽",
                    style = Title3.copy(color = MatuleWhite, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)
                )
            }
        }

        // --- 5. Баннер ошибки ---
        ErrorBanner(message = errorMsg, onDismiss = { errorMsg = null })

        // --- 6. Шторка ---
        if (selectedProductForSheet != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedProductForSheet = null },
                sheetState = sheetState,
                containerColor = MatuleWhite,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                ProductDetailsSheet(
                    title = selectedProductForSheet!!.title,
                    price = "${selectedProductForSheet!!.price} ₽",
                    description = selectedProductForSheet!!.description,
                    onDismiss = { selectedProductForSheet = null },
                    consumption = selectedProductForSheet!!.consumption,
                    onAddToCart = {
                        cartViewModel.onPlusClick(selectedProductForSheet!!)
                        selectedProductForSheet = null
                    }
                )
            }
        }
    }
}