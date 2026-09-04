package com.aiden3630.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiden3630.data.manager.CartManager
import com.aiden3630.domain.model.Product
import com.aiden3630.domain.repository.ShopRepository
import com.aiden3630.presentation.utils.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления корзиной покупок.
 * Связывает пользовательский интерфейс с локальным менеджером корзины и удаленным сервером.
 *
 * @param cartManager Менеджер для управления состоянием корзины в оперативной памяти.
 * @param notificationService Сервис для отправки системных уведомлений.
 * @param shopRepository Репозиторий для синхронизации данных с сервером (Модуль В).
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartManager: CartManager,
    private val notificationService: NotificationService,
    private val shopRepository: ShopRepository // 👈 Добавили репозиторий для работы с сервером
) : ViewModel() {

    // Поток списка товаров в корзине
    val cartItems = cartManager.cartItems

    // Канал для отправки одноразовых событий экрану (Успех/Ошибка)
    private val _cartEvent = Channel<CartEvent>()
    val cartEvent = _cartEvent.receiveAsFlow()

    // Храним текущую сумму, обновляем её автоматически при изменении состава корзины
    val totalSum = cartManager.totalPrice
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = 0
        )

    /**
     * Обработка нажатия на кнопку "Плюс" (Добавить товар).
     * Обновляет данные локально и отправляет запрос на сервер по Swagger.
     */
    fun onPlusClick(product: Product) {
        // 1. Обновляем состояние в интерфейсе мгновенно
        cartManager.addToCart(product)

        // 2. Синхронизируем с сервером (Модуль В, запрос 8)
        viewModelScope.launch {
            try {
                // Вызываем метод уровня абстракции (Domain слой)
                shopRepository.addProductToRemoteCart(
                    productId = product.id.toString(),
                    count = 1 // Отправляем информацию о добавлении единицы товара
                )
            } catch (exception: Exception) {
                // Если возникла сетевая ошибка — показываем красный баннер на 5 секунд
                _cartEvent.send(CartEvent.Error(exception.message ?: "Ошибка синхронизации с сервером"))
            }
        }
    }

    /**
     * Обработка нажатия на кнопку "Минус" (Уменьшить количество).
     */
    fun onMinusClick(product: Product) {
        // Локальное обновление
        cartManager.decreaseQuantity(product)

        // Синхронизация с сервером (Модуль В, запрос 9)
        viewModelScope.launch {
            try {
                shopRepository.addProductToRemoteCart(
                    productId = product.id.toString(),
                    count = -1 // Уменьшаем количество на сервере
                )
            } catch (exception: Exception) {
                _cartEvent.send(CartEvent.Error(exception.message ?: "Ошибка связи с сервером"))
            }
        }
    }

    /**
     * Удаление товара из корзины полностью.
     */
    fun onDeleteClick(product: Product) {
        cartManager.removeFromCart(product)
        // Здесь также можно добавить вызов репозитория для удаления записи на сервере
    }

    /**
     * Полная очистка корзины.
     */
    fun onClearCartClick() {
        cartManager.clearCart()
    }

    /**
     * Финальное оформление заказа.
     * Очищает корзину, шлет уведомление и перенаправляет на главный экран.
     */
    fun checkout() {
        viewModelScope.launch {
            try {
                val currentTotalSum = totalSum.value

                // Проверка: нельзя купить пустоту
                if (cartManager.cartItems.value.isEmpty()) {
                    _cartEvent.send(CartEvent.Error("Ваша корзина пуста"))
                    return@launch
                }

                // Логика оформления заказа (Модуль В, запрос 10)
                // В реальном приложении здесь был бы вызов shopRepository.createOrder(...)

                // Очищаем локальные данные
                cartManager.clearCart()

                // Отправляем системное уведомление пользователю (Модуль Д)
                notificationService.showNotification(
                    title = "Заказ успешно оформлен!",
                    message = "Сумма заказа: $currentTotalSum ₽. Спасибо за покупку в Matule!"
                )

                // Сообщаем UI об успехе для навигации
                _cartEvent.send(CartEvent.OrderSuccess)

            } catch (exception: Exception) {
                // В случае ошибки показываем уведомление на 5 секунд (Модуль В)
                _cartEvent.send(CartEvent.Error(exception.message ?: "Ошибка при оформлении заказа"))
            }
        }
    }
}

/**
 * Варианты событий, которые могут произойти в корзине.
 */
sealed class CartEvent {
    object OrderSuccess : CartEvent()
    data class Error(val message: String) : CartEvent()
}