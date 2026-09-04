package com.aiden3630.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aiden3630.presentation.theme.*
import com.aiden3630.presentation.theme.MatuleBlack
import com.aiden3630.presentation.theme.MatuleBlue
import com.aiden3630.presentation.theme.MatuleBlueFocused
import com.aiden3630.presentation.theme.MatuleError
import com.aiden3630.presentation.theme.MatuleInputBg
import com.aiden3630.presentation.theme.MatuleInputIcon
import com.aiden3630.presentation.theme.MatuleInputStroke
import com.aiden3630.presentation.theme.MatulePlaceholder
import com.aiden3630.presentation.theme.MatuleRedBg
import com.aiden3630.presentation.R

@Composable
fun MatuleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false, // Если true, клавиатура не откроется
    onClick: (() -> Unit)? = null // Что делать при клике на поле
) {
    var isFocused by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Для отслеживания нажатия в режиме readOnly
    val interactionSource = remember { MutableInteractionSource() }
    if (readOnly && onClick != null) {
        val isPressed by interactionSource.collectIsPressedAsState()
        if (isPressed) {
            LaunchedEffect(Unit) { onClick() }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    color = if (isError) MatuleRedBg else MatuleInputBg,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    color = when {
                        isError -> MatuleError
                        isFocused -> MatuleBlueFocused
                        else -> MatuleInputStroke
                    },
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(enabled = readOnly && onClick != null) { onClick?.invoke() },
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .padding(end = if (isPassword || trailingIcon != null) 40.dp else 0.dp)
                    .onFocusChanged { isFocused = it.isFocused },
                singleLine = true,
                readOnly = readOnly, // Запрещает клавиатуру
                enabled = !readOnly, // Отключаем стандартное поведение ввода, если readOnly
                textStyle = BodyText.copy(color = MatuleBlack),
                cursorBrush = SolidColor(MatuleBlue),
                visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation('*') else VisualTransformation.None,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = BodyText.copy(color = MatulePlaceholder)
                        )
                    }
                    innerTextField()
                }
            )

            // Иконки
            Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)) {
                if (isPassword) {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(id = if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off),
                            contentDescription = "Toggle password",
                            tint = MatuleInputIcon
                        )
                    }
                } else if (trailingIcon != null) {
                    trailingIcon()
                }
            }
        }

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MatuleError,
                style = Caption,
                modifier = Modifier.padding(start = 14.dp, top = 4.dp)
            )
        }
    }
}