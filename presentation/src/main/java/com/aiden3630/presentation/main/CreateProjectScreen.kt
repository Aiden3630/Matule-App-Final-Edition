package com.aiden3630.presentation.main

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.aiden3630.presentation.components.ErrorBanner
import com.aiden3630.presentation.components.MatuleButton
import com.aiden3630.presentation.components.MatuleDatePicker
import com.aiden3630.presentation.components.MatuleTextField
import com.aiden3630.presentation.theme.*
import com.aiden3630.presentation.utils.convertMillisToDate
import com.aiden3630.presentation.utils.createImageUri
import com.aiden3630.presentation.R as UiKitR
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    projectId: String? = null,
    onBackClick: () -> Unit = {},
    viewModel: CreateProjectViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Состояния полей
    var type by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var dateStart by remember { mutableStateOf("") }
    var dateEnd by remember { mutableStateOf("") }
    var toWhom by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Технические состояния
    var dateFieldType by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showPhotoSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val projectToEdit by viewModel.projectToEdit.collectAsState()

    LaunchedEffect(projectId) {
        if (projectId != null) viewModel.loadProject(projectId)
    }

    LaunchedEffect(projectToEdit) {
        projectToEdit?.let {
            type = it.type
            name = it.name
            dateStart = it.dateStart
            dateEnd = it.dateEnd
            category = it.category
            toWhom = it.toWhom
            source = it.source
        }
    }

    // --- ЛАУНЧЕРЫ ДЛЯ ФОТО ---

    // 1. Галерея
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { selectedImageUri = it }
        }

    // 2. Камера
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && tempCameraUri != null) {
                selectedImageUri = tempCameraUri
            }
        }

    // 3. Запрос разрешения на камеру
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val uri = createImageUri(context)
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Разрешите доступ к камере в настройках", Toast.LENGTH_LONG)
                    .show()
            }
        }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MatuleWhite)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // --- ХЕДЕР ---
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(id = UiKitR.drawable.ic_chevron_left),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).clickable { onBackClick() }
                        .align(Alignment.CenterStart),
                    tint = MatuleBlack
                )
                Text(
                    text = if (projectId == null) "Создать проект" else "Редактировать",
                    style = Title2,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- ПОЛЯ ---
            InputLabel("Тип")
            ProjectDropdown(
                value = type,
                onValueChange = { type = it },
                placeholder = "Выберите тип",
                options = listOf("Вязание", "Шитье", "Творчество")
            )

            Spacer(modifier = Modifier.height(12.dp))

            InputLabel("Название проекта")
            MatuleTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Введите название"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InputLabel("Дата начала")
            MatuleTextField(
                value = dateStart,
                onValueChange = {},
                placeholder = if (dateStart.isEmpty()) "--.--.----" else dateStart,
                readOnly = true,
                onClick = { dateFieldType = "start" })

            Spacer(modifier = Modifier.height(12.dp))

            InputLabel("Дата окончания")
            MatuleTextField(
                value = dateEnd,
                onValueChange = {},
                placeholder = if (dateEnd.isEmpty()) "--.--.----" else dateEnd,
                readOnly = true,
                onClick = { dateFieldType = "end" })

            Spacer(modifier = Modifier.height(12.dp))

            InputLabel("Кому")
            MatuleTextField(
                value = toWhom,
                onValueChange = { toWhom = it },
                placeholder = "Получатель"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InputLabel("Источник описания")
            MatuleTextField(
                value = source,
                onValueChange = { source = it },
                placeholder = "Ссылка или автор"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InputLabel("Категория")
            ProjectDropdown(
                value = category,
                onValueChange = { category = it },
                placeholder = "Выберите категорию",
                options = listOf("Одежда", "Игрушки", "Аксессуары")
            )


            Spacer(modifier = Modifier.height(24.dp))

            // --- БЛОК ФОТО ---
            Box(
                modifier = Modifier.width(202.dp).height(192.dp).clip(RoundedCornerShape(10.dp))
                    .background(MatuleInputBg).clickable { showPhotoSheet = true }
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (projectToEdit?.imageUri != null) {
                    AsyncImage(
                        model = File(projectToEdit!!.imageUri!!),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(id = UiKitR.drawable.ic_plus),
                        contentDescription = null,
                        tint = MatuleTextGray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            MatuleButton(
                text = if (projectId == null) "Подтвердить" else "Сохранить изменения",
                onClick = {
                    viewModel.saveOrUpdate(
                        context,
                        projectId,
                        name,
                        type,
                        dateStart,
                        dateEnd,
                        category,
                        toWhom,
                        source,
                        selectedImageUri
                    ) {
                        Toast.makeText(context, "Сохранено!", Toast.LENGTH_SHORT).show()
                        onBackClick()
                    }
                },
                enabled = name.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(110.dp))
        }
    }

        // --- ВСПОМОГАТЕЛЬНЫЕ ОКНА ---

        if (dateFieldType != null) {
            MatuleDatePicker(
                onDateSelected = { millis ->
                    millis?.let {
                        val date = convertMillisToDate(it)
                        if (dateFieldType == "start") dateStart = date else dateEnd = date
                    }
                },
                onDismiss = { dateFieldType = null }
            )
        }

        if (showPhotoSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPhotoSheet = false },
                sheetState = sheetState,
                containerColor = MatuleWhite
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Загрузить фото", style = Title2)
                    Spacer(Modifier.height(20.dp))
                    MatuleButton(text = "Сделать фото", onClick = {
                        showPhotoSheet = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    })
                    Spacer(Modifier.height(12.dp))
                    MatuleButton(text = "Выбрать из галереи", onClick = {
                        showPhotoSheet = false
                        galleryLauncher.launch("image/*")
                    })
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    ErrorBanner(message = errorMsg, onDismiss = { errorMsg = null })
    }

    @Composable
    private fun InputLabel(text: String) {
        Text(
            text = text,
            style = Caption,
            color = MatuleTextGray,
            modifier = Modifier.padding(bottom = 6.dp)
        )
    }

    @Composable
    private fun ProjectDropdown(
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        options: List<String>
    ) {
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            MatuleTextField(
                value = value,
                onValueChange = {},
                placeholder = placeholder,
                readOnly = true,
                onClick = { expanded = true },
                trailingIcon = {
                    Icon(
                        painterResource(UiKitR.drawable.ic_chevron_down),
                        null,
                        tint = MatuleTextGray
                    )
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MatuleWhite).fillMaxWidth(0.9f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = BodyText) },
                        onClick = { onValueChange(option); expanded = false })
                }
            }
        }

    }