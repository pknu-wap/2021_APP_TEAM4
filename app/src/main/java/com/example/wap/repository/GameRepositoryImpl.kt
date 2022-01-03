package com.example.wap.repository

import android.util.Log
import com.example.wap.data.GameData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class GameRepositoryImpl: GameRepository {

    private val gameCollectionRef = Firebase.firestore.collection("game")

    override suspend fun loadLevel(): GameData {
        var level = GameData(0,0)
        try{
            val querySnapshot = gameCollectionRef.get().await()
            for(document in querySnapshot.documents){
                val game = document.toObject<GameData>()
                game?.let{
                    level = it
                }
            }
        }catch(e: Exception){
            e.printStackTrace()
            Log.d("tag", "load data error")
        }
        return level
    }

    override suspend fun updateLevel(information: GameData){
        val id = "5ErJmuvrqLCr1rOIuwB6"

        try {
            Log.d("tag in update", information.exp.toString())
            gameCollectionRef.document(id).update("level", information.level).await()
            gameCollectionRef.document(id).update("exp", information.exp).await()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("tag", "update error")
        }
    }
}