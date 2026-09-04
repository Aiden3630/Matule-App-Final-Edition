package com.aiden3630.data.network

import com.aiden3630.data.di.CartRequest
import com.aiden3630.data.di.CartResponse
import com.aiden3630.data.model.SignInRequest
import com.aiden3630.data.model.SignInResponse
import com.aiden3630.data.model.SignUpRequest
import com.aiden3630.data.model.UserDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {


    @POST("collections/users/auth-with-password")
    suspend fun signIn(@Body request: SignInRequest): SignInResponse

    @POST("collections/users/records")
    suspend fun signUp(@Body request: SignUpRequest): UserDto

    @POST("collections/cart/records")
    suspend fun addToCart(@Body request: CartRequest): CartResponse
}