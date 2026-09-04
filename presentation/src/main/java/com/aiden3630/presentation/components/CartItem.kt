package com.aiden3630.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiden3630.presentation.theme.*
import com.aiden3630.presentation.R as UiKitR

@Composable
fun CartItem(
    title: String,
    price: String,
    count: Int,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(138.dp) // Высота по CSS
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFFE4E8F5)
            )
            .background(MatuleWhite, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- ВЕРХНЯЯ ЧАСТЬ ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Название
                Text(
                    text = title,
                    style = Headline.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = MatuleBlack,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Крестик (Удалить)
                Icon(
                    painter = painterResource(id = UiKitR.drawable.ic_close), // Используем крестик (ic_close или ic_delete)
                    contentDescription = "Delete",
                    tint = MatuleGrayIcon, // Серый цвет, как на скрине
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDeleteClick() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- НИЖНЯЯ ЧАСТЬ ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Цена
                Text(
                    text = price,
                    style = Title3.copy(fontWeight = FontWeight.SemiBold),
                    color = MatuleBlack
                )

                Spacer(modifier = Modifier.weight(1f))

                // Текст "1 штук"
                Text(
                    text = "$count штук",
                    style = BodyText.copy(fontWeight = FontWeight.Normal),
                    color = MatuleBlack
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Блок кнопок +/- (Серый фон, скругление 8dp)
                Row(
                    modifier = Modifier
                        .height(32.dp)
                        .background(MatuleInputBg, RoundedCornerShape(8.dp)), // Фон #F5F5F9
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Минус
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onMinusClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = UiKitR.drawable.ic_minus), // Или просто черточка
                            contentDescription = "-",
                            tint = MatuleGrayIcon,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Разделитель (вертикальная черта)
                    VerticalDivider(
                        modifier = Modifier.height(16.dp),
                        color = Color(0xFFEBEBEB)
                    )

                    // Плюс
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onPlusClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = UiKitR.drawable.ic_plus),
                            contentDescription = "+",
                            tint = MatuleGrayIcon,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}