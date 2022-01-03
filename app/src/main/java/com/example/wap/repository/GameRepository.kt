package com.example.wap.repository

import com.example.wap.data.GameData

interface GameRepository {

    suspend fun loadLevel(): GameData

    suspend fun updateLevel(information: GameData){

    }
}