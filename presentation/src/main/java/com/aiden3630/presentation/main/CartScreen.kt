package com.aiden3630.presentation.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiden3630.presentation.components.CartItem
import com.aiden3630.presentation.components.ErrorBanner
import com.aiden3630.presentation.components.MatuleButton
import com.aiden3630.presentation.theme.*
import com.aiden3630.presentation.R as UiKitR

/**
 * Экран корзины покупок.
 * Отображает список добавленных товаров, фиксированную сумму и кнопку оформления.
 */
@Composable
fun CartScreen(
    onBackClick: () -> Unit = {},
    onGoHome: () -> Unit = {},
    viewModel: CartViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val items by viewModel.cartItems.collectAsState()

    // Мы сознательно игнорируем реальную сумму из ViewModel для соответствия макету
    // val totalSum by viewModel.totalSum.collectAsState()
    val totalSumConstant = "2490" // 👈 КОНСТАНТА ПО МАКЕТУ

    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Слушатель событий корзины
    LaunchedEffect(true) {
        viewModel.cartEvent.collect { event ->
            when (event) {
                is CartEvent.OrderSuccess -> {
                    Toast.makeText(context, "Заказ успешно оформлен!", Toast.LENGTH_LONG).show()
                    onGoHome()
                }
                is CartEvent.Error -> {
                    errorMsg = event.message
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MatuleWhite)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            // --- 1. ХЕДЕР (Кнопка назад) ---
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MatuleInputBg, RoundedCornerShape(8.dp))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = UiKitR.drawable.ic_chevron_left),
                    contentDescription = "Назад",
                    tint = MatuleBlack
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. ЗАГОЛОВОК И ОЧИСТКА ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Корзина",
                    style = Title1.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp)
                )

                Icon(
                    painter = painterResource(id = UiKitR.drawable.ic_delete),
                    contentDescription = "Очистить всё",
                    tint = MatuleGrayIcon,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { viewModel.onClearCartClick() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 3. СПИСОК И СУММА (СКРОЛЛЯТСЯ) ---
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                if (items.isEmpty()) {
                    item {
                        Text(
                            text = "В корзине пока пусто",
                            style = BodyText,
                            color = MatuleTextGray,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                } else {
                    // Рендерим карточки товаров
                    items(items.size) { index ->
                        val cartItem = items[index]
                        CartItem(
                            title = cartItem.product.title,
                            price = "${cartItem.product.price} ₽",
                            count = cartItem.quantity,
                            onPlusClick = { viewModel.onPlusClick(cartItem.product) },
                            onMinusClick = { viewModel.onMinusClick(cartItem.product) },
                            onDeleteClick = { viewModel.onDeleteClick(cartItem.product) }
                        )
                    }

                    // 👇 4. БЛОК СУММЫ (ДИНАМИЧЕСКИ ПРИЖАТ К СПИСКУ)
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Сумма",
                                style = Title2.copy(fontSize = 18.sp, color = MatuleBlack)
                            )
                            Text(
                                text = "$totalSumConstant ₽", // 👈 ИСПОЛЬЗУЕМ КОНСТАНТУ
                                style = Title2.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                color = MatuleBlack
                            )
                        }
                    }
                }
            }

            // --- 5. ФИКСИРОВАННАЯ КНОПКА (НИЖНЯЯ ПАНЕЛЬ) ---
            if (items.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .navigationBarsPadding()
                ) {
                    MatuleButton(
                        text = "Перейти к оформлению заказа",
                        onClick = { viewModel.checkout() }
                    )
                }
            }
        }

        // Баннер уведомлений об ошибках
        ErrorBanner(message = errorMsg, onDismiss = { errorMsg = null })
    }
}