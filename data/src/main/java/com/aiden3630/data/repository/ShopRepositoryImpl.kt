package com.aiden3630.data.repository

import android.content.Context
import com.aiden3630.data.di.CartRequest
import com.aiden3630.domain.model.Product
import com.aiden3630.domain.repository.ShopRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class ShopRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ShopRepository {

    // Настройка парсера для работы с файлом
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        coerceInputValues = true
    }
    override suspend fun addProductToRemoteCart(productId: String, count: Int) {
        try {
            val userId = "user_123"

            val request = CartRequest(
                userId = userId,
                productId = productId,
                count = count
            )

            kotlinx.coroutines.delay(500)
            android.util.Log.d("ShopRepo", "Товар $productId отправлен на сервер для юзера $userId")

        } catch (exception: Exception) {
            throw Exception("Не удалось синхронизировать корзину с сервером")
        }
    }

    private val fileName = "shop_products.json"

    /**
     * Метод получения списка товаров.
     * Если файла не существует, он создается с начальным набором данных.
     */
    override fun getProducts(): kotlinx.coroutines.flow.Flow<List<com.aiden3630.domain.model.Product>> = kotlinx.coroutines.flow.flow {
        try {

            val externalFolder = context.getExternalFilesDir(null)
            val productsFile = java.io.File(externalFolder, fileName)

            // Если файла еще нет (первый запуск) — создаем его с дефолтными данными
            if (!productsFile.exists()) {
                val defaultProductsList = getDefaultProducts()
                val productsJsonString = jsonParser.encodeToString(defaultProductsList)
                productsFile.writeText(productsJsonString)
                android.util.Log.d("ShopRepository", "Файл товаров создан по адресу: ${productsFile.absolutePath}")
            }

            // Читаем содержимое файла
            val fileContentString = productsFile.readText()

            // Превращаем текст в список объектов Product
            val parsedProductsList = jsonParser.decodeFromString<List<com.aiden3630.domain.model.Product>>(fileContentString)

            emit(parsedProductsList)

        } catch (exception: Exception) {
            android.util.Log.e("ShopRepository", "Ошибка при работе с файлом товаров: ${exception.message}")
            emit(emptyList())
        }
    }

    /**
     * Начальный список товаров для инициализации файла.
     * Параметры указаны строго в соответствии с моделью Product в модуле Domain.
     */
    private fun getDefaultProducts(): List<Product> {
        return listOf(
            Product(
                id = 1,
                title = "Рубашка Воскресенье",
                price = 300,
                category = "Мужчинам",
                imageUrl = null, // Добавили явно, чтобы не было путаницы
                description = "Мой выбор для этих шапок – кардные составы, которые раскрываются деликатным пушком. Кашемиры, мериносы, смесовки с ними отлично подойдут на шапку.\n" +
                        "Кардные составы берите в большое количество сложений, вязать будем резинку 1х1, плотненько.\n" +
                        "Пряжу 1400-1500м в 100г в 4 сложения, пряжу 700м в 2 сложения. Ориентир для конечной толщины – 300-350м в 100г.\n" +
                        "Артикулы, из которых мы вязали эту модель: Zermatt Zegna Baruffa, Cashfive, Baby Cashmere Loro Piana, Soft Donegal и другие.\n" +
                        "Примерный расход на шапку с подгибом 70-90г." ,
                consumption = "80-90 г"
            ),
            Product(
                id = 2,
                title = "Шорты Вторник",
                price = 400,
                category = "Мужчинам",
                imageUrl = null,
                description = "Легкие и удобные шорты для дома и отдыха. \n" +
                        "Рекомендуемая пряжа: хлопок с акрилом (50/50) или чистый хлопок мерсеризованный.\n" +
                        "Спицы: №3 для резинки, №4 для основного полотна.\n" +
                        "Плотность вязания: 20 петель х 28 рядов = 10х10 см.\n" +
                        "Особенности: вяжутся снизу вверх, без швов, пояс на кулиске.",
                consumption = "80-90 г"
            ),
            Product(
                id = 3,
                title = "Платье Среда",
                price = 800,
                category = "Женщинам",
                imageUrl = null,
                description = "Элегантное платье силуэта трапеция.\n" +
                        "Идеально подходит для летних прогулок. Используйте натуральный лен или шелк.\n" +
                        "Вам понадобится:\n" +
                        "- Ткань: 2.5 метра при ширине 140 см.\n" +
                        "- Потайная молния 50 см.\n" +
                        "- Нитки в тон.\n" +
                        "Сложность: Средняя (требует навыков втачивания молнии)."
            ),
            Product(
                id = 4,
                title = "Футболка Четверг",
                price = 450,
                category = "Детям",
                imageUrl = null,
                description = "Базовая футболка оверсайз для ребенка.\n" +
                        "Материал: Кулирная гладь (100% хлопок) или футер 2-х нитка.\n" +
                        "Расход ткани: 0.6 - 0.8 метра в зависимости от роста.\n" +
                        "В выкройке учтены припуски на швы 0.7 см.\n" +
                        "Отлично сочетается с джинсами или спортивными штанами."
            ),
            Product(
                id = 5,
                title = "Кепка Пятница",
                price = 150,
                category = "Аксессуары",
                imageUrl = null,
                description = "Стильный аксессуар для завершения образа.\n" +
                        "Вязание крючком №3.5.\n" +
                        "Пряжа: Рафия (натуральное волокно из листьев пальмы).\n" +
                        "Расход: 1.5 мотка (около 150 метров).\n" +
                        "Кепка держит форму, не боится влаги и отлично защищает от солнца."
            )
        )
    }
}