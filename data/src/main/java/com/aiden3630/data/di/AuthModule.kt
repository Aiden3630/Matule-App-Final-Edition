package com.aiden3630.data.di

import android.content.Context
import com.aiden3630.data.manager.JsonDbManager
import com.aiden3630.data.manager.TokenManager
import com.aiden3630.data.network.AuthApi
import com.aiden3630.data.repository.AuthRepositoryImpl
import com.aiden3630.data.repository.ProjectRepositoryImpl
import com.aiden3630.data.repository.ShopRepositoryImpl
import com.aiden3630.domain.repository.AuthRepository
import com.aiden3630.domain.repository.ProjectRepository
import com.aiden3630.domain.repository.ShopRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        tokenManager: TokenManager,
        jsonDbManager: JsonDbManager
    ): AuthRepository {
        return AuthRepositoryImpl(api, tokenManager, jsonDbManager)
    }

    @Provides
    @Singleton
    fun provideShopRepository(
        @ApplicationContext context: Context
    ): ShopRepository {
        return ShopRepositoryImpl(context)
    }
}
/**
 * DTO для запроса на добавление товара в корзину на сервере.
 */
@Serializable
data class CartRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("count") val count: Int
)

/**
 * DTO ответа сервера после добавления в корзину.
 */
@Serializable
data class CartResponse(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("count") val count: Int
)