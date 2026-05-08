// di/src/main/java/com/inwave/di/repository/UserRepositoryModule.kt
package com.inwave.di.repository

import com.inwave.data.repository.user.UserQueryRepositoryImpl
import com.inwave.data.repository.user.UserCommandRepositoryImpl
import com.inwave.di.UserRemoteRepo
import com.inwave.domain.repository.command.UserCommandRepository
import com.inwave.domain.repository.query.UserQueryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {

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