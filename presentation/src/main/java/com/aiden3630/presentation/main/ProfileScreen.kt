package com.aiden3630.presentation.main

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiden3630.presentation.components.MatuleToggle
import com.aiden3630.presentation.theme.*
import com.aiden3630.presentation.R as UiKitR

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Функция для открытия PDF
    fun openPdf(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Фон строго белый
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        // --- 1. ШАПКА (Слева, без аватара) ---
        Spacer(modifier = Modifier.height(30.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = state.name, // Например: Эдуард
                style = Title1.copy(
                    fontSize = 32.sp, // Крупный шрифт как на скрине
                    fontWeight = FontWeight.Bold
                ),
                color = MatuleBlack
            )
            Text(
                text = state.email, // Например: afersfsr@dsfsr.ru
                style = BodyText,
                color = MatuleTextGray
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- 2. МЕНЮ (Без серых боксов) ---

        // Мои заказы
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable {
                    Toast.makeText(context, "В разработке", Toast.LENGTH_SHORT).show()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = UiKitR.drawable.ic_notification), // Твоя 3D иконка
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Мои заказы",
                style = Title3.copy(fontWeight = FontWeight.Medium),
                color = MatuleBlack
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Уведомления
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = UiKitR.drawable.ic_settings), // Иконка шестеренки
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Уведомления",
                style = Title3.copy(fontWeight = FontWeight.Medium),
                color = MatuleBlack,
                modifier = Modifier.weight(1f)
            )

            // Наш тумблер
            MatuleToggle(
                checked = state.isNotificationsEnabled,
                onCheckedChange = { viewModel.toggleNotifications(it) }
            )
        }

        // --- 3. ФУТЕР (Прижимаем вниз и центрируем) ---
        // Используем вес, чтобы вытолкнуть текст вниз при большом экране
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(60.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Политика конфиденциальности",
                style = BodyText.copy(fontWeight = FontWeight.Medium),
                color = MatuleTextGray,
                modifier = Modifier.clickable { openPdf("https://google.com") }
            )

            Text(
                text = "Пользовательское соглашение",
                style = BodyText.copy(fontWeight = FontWeight.Medium),
                color = MatuleTextGray,
                modifier = Modifier.clickable { openPdf("https://google.com") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Выход",
                style = Title3.copy(fontWeight = FontWeight.Medium),
                color = MatuleError, // Красный
                modifier = Modifier.clickable {
                    viewModel.logout()
                    onLogoutClick()
                }
            )
        }

        // Отступ под BottomBar
        Spacer(modifier = Modifier.height(110.dp))
    }
}