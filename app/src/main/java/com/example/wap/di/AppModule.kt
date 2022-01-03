package com.example.wap.di

import com.example.wap.repository.GameRepository
import com.example.wap.repository.GameRepositoryImpl
import com.example.wap.repository.TodoRepository
import com.example.wap.repository.TodoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoRepository() : TodoRepository{
        return TodoRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideGameRepository() : GameRepository {
        return GameRepositoryImpl()
    }
}