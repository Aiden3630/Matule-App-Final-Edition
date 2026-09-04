package com.aiden3630.presentation.main

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiden3630.domain.model.Product
import com.aiden3630.presentation.components.MatuleChip
import com.aiden3630.presentation.components.MatuleSearchField
import com.aiden3630.presentation.components.ProductCard
import com.aiden3630.presentation.theme.*
import com.aiden3630.presentation.R as UiKitR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCartClick: () -> Unit = {},
    cartViewModel: CartViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val searchText by homeViewModel.searchText.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()
    val products by homeViewModel.filteredProducts.collectAsState()

    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartTotal by cartViewModel.totalSum.collectAsState()

    var selectedProductForSheet by remember { mutableStateOf<Product?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val categories = listOf("Все", "Женщинам", "Мужчинам", "Детям")

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MatuleWhite)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // --- 1. Поиск ---
            item {
                Spacer(modifier = Modifier.height(20.dp))
                MatuleSearchField(
                    value = searchText,
                    onValueChange = { homeViewModel.onSearchTextChange(it) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- 2. Баннеры ---
            item {
                Text(text = "Акции и новости", style = Title3, color = MatuleTextGray)
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        BannerItem(
                            title = "Шорты\nВторник",
                            price = "4000 ₽",
                            gradient = Brush.linearGradient(listOf(Color(0xFF97D9F0), Color(0xFF92E9D4))),
                            imageRes = UiKitR.drawable.im_banner_1,
                            onClick = {
                                selectedProductForSheet = products.find { it.id == 2 }
                            }
                        )
                    }
                    item {
                        BannerItem(
                            title = "Рубашка\nВоскресенье",
                            price = "8000 ₽",
                            gradient = Brush.linearGradient(listOf(Color(0xFF76B3FF), Color(0xFFCDE3FF))),
                            imageRes = UiKitR.drawable.im_banner_1,
                            onClick = {
                                selectedProductForSheet = products.find { it.id == 1 }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- 3. Категории ---
            item {
                Text(text = "Каталог описаний", style = Title3, color = MatuleTextGray)
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(categories.size) { index ->
                        MatuleChip(
                            text = categories[index],
                            isSelected = selectedCategory == categories[index],
                            onClick = { homeViewModel.onCategoryChange(categories[index]) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- 4. Товары ---
            items(products) { product ->
                val isProductInCart = cartItems.any { it.product.id == product.id }

                ProductCard(
                    title = product.title,
                    price = "${product.price} ₽",
                    category = product.category,
                    isInCart = isProductInCart,
                    onAddClick = { cartViewModel.onPlusClick(product) },
                    onRemoveClick = { cartViewModel.onDeleteClick(product) },
                    onClick = {
                        selectedProductForSheet = product
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // --- 5. Кнопка Корзины ---
        if (cartTotal > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(10.dp, RoundedCornerShape(12.dp), spotColor = Color(0x40000000))
                    .background(MatuleBlue, RoundedCornerShape(12.dp))
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
                    Text(
                        text = "В корзину",
                        style = Title3.copy(color = MatuleWhite, fontWeight = FontWeight.SemiBold)
                    )
                }
                Text(
                    text = "$cartTotal ₽",
                    style = Title3.copy(color = MatuleWhite, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)
                )
            }
        }

        // --- Шторка ---
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
                    consumption = selectedProductForSheet!!.consumption, // 👈 Передаем расход
                    onDismiss = { selectedProductForSheet = null },
                    onAddToCart ={
                        cartViewModel.onPlusClick(selectedProductForSheet!!)
                        selectedProductForSheet = null
                    }
                )
            }
        }
    }
}

@Composable
fun BannerItem(
    title: String,
    price: String,
    gradient: Brush,
    imageRes: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(270.dp)
            .height(152.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(gradient)
            .clickable { onClick() }
    ) {
        // Слой 1: Текст
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
                .width(140.dp)
                .zIndex(2f)
        ) {
            Text(
                text = title,
                style = Title2.copy(color = MatuleWhite, fontSize = 20.sp),
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = price,
                style = Title2.copy(color = MatuleWhite, fontSize = 20.sp)
            )
        }

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.FillHeight, // Растягиваем по высоте
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .height(200.dp) // Ставим большую высоту
                .offset(x = 4.dp, y = 7.dp) // Сдвигаем в угол
        )
    }
}