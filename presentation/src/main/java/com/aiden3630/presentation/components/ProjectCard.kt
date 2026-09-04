package com.aiden3630.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aiden3630.presentation.theme.*
import java.io.File

@Composable
fun ProjectCard(
    title: String,
    date: String,
    imageUri: String? = null,
    showImage: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFFE4E8F5))
            .background(MatuleWhite, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Заголовок
            Text(
                text = title,
                style = Headline.copy(fontWeight = FontWeight.SemiBold),
                color = MatuleBlack
            )

            if (showImage) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp) // Высота картинки
                        .clip(RoundedCornerShape(8.dp))
                        .background(MatuleInputBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = File(imageUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Нижняя строка (Дата + Кнопка)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = date,
                    style = Caption,
                    color = MatuleTextGray
                )

                Button(
                    onClick = onClick,
                    modifier = Modifier.height(36.dp).width(100.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MatuleBlue),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Открыть",
                        style = Caption.copy(color = MatuleWhite, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}