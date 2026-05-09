// di/src/main/java/com/inwave/di/repository/UserRepositoryModule.kt
package com.inwave.di.repository

import android.content.Context
import com.invawe.data.repository.user.UserCommandRepositoryImpl
import com.invawe.data.repository.user.UserQueryRepositoryImpl
import com.inwave.di.UserRemoteRepo
import com.inwave.domain.repository.command.UserCommandRepository
import com.inwave.domain.repository.query.UserQueryRepository
import com.inwave.tool.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {
    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    @UserRemoteRepo
    fun provideUserQueryRepository(
        httpClient: HttpClient
    ): UserQueryRepository {
        return UserQueryRepositoryImpl(httpClient)
    }

    @Provides
    @Singleton
    fun provideUserCommandRepository(
        httpClient: HttpClient
    ): UserCommandRepository {
        return UserCommandRepositoryImpl(httpClient)
    }
}