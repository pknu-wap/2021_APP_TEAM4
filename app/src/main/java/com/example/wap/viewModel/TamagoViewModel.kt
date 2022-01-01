package com.example.wap.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wap.GameData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class TamagoViewModel: ViewModel() {

    private val gameCollectionRef = Firebase.firestore.collection("game")

    private val level: MutableLiveData<GameData> by lazy{
        MutableLiveData<GameData>().also{
            loadLevel()
        }
    }

    private fun loadLevel() = CoroutineScope(Dispatchers.IO).launch{

        try{
            val querySnapshot = gameCollectionRef.get().await()
            for(document in querySnapshot.documents){
                val game = document.toObject<GameData>()
                game?.let{
                    level.value = game
                }
            }
        }catch(e: Exception){
            e.printStackTrace()
            Log.d("tag", "load data error")
        }
    }
    private fun updateLevel() = CoroutineScope(Dispatchers.IO).launch {

        level.value?.let {
            val gameQuery = gameCollectionRef
                .whereEqualTo("level", it.level)
                .whereEqualTo("progress", it.exp)
                .get()
                .await()

            if (gameQuery.documents.isNotEmpty()) {
                for (document in gameQuery) {
                    try {
                        gameCollectionRef.document(document.id).update("level", levelUp().level).await()
                        gameCollectionRef.document(document.id).update("progress", levelUp().exp)
                            .await()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("tag", "update error")
                    }
                }
            }
        }
    }

    private fun levelUp() : GameData{
        val information = level.value!!

        var level = information.level
        var exp = information.exp
        when {
            level in 1..9 -> {
                exp += 500
            }
            level in 10..30 -> {
                exp += 300
            }
            level > 30 -> {
                exp += 200
            }
        }
        if (exp >= 1000) {
            level += 1
            exp -= 1000
        }
        return GameData(level, exp)
    }
}