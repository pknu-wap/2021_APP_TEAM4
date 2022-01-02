package com.example.wap.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _level = MutableLiveData<GameData>()

    val level: LiveData<GameData> get() = _level

    fun level(): GameData? {
        return _level.value
    }
    init{
        loadLevel()
    }

    private fun loadLevel() = CoroutineScope(Dispatchers.IO).launch{

        try{
            val querySnapshot = gameCollectionRef.get().await()
            for(document in querySnapshot.documents){
                val game = document.toObject<GameData>()
                game?.let{
                    viewModelScope.launch {
                        _level.value = it
                    }
                }
            }
        }catch(e: Exception){
            e.printStackTrace()
            Log.d("tag", "load data error")
        }
    }
    fun updateLevel() = CoroutineScope(Dispatchers.IO).launch {

        val id = "5ErJmuvrqLCr1rOIuwB6"

        try {
            val information = levelUp()
            Log.d("tag in update", information.exp.toString())
            gameCollectionRef.document(id).update("level", information.level).await()
            gameCollectionRef.document(id).update("exp", information.exp).await()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("tag", "update error")
        }

    }

    private fun levelUp() : GameData{

        val information = _level.value!!

        var nlevel = information.level
        var nexp = information.exp

        when {
            nlevel in 1..9 -> {
                nexp += 50
            }
            nlevel in 10..30 -> {
                nexp += 30
            }
            nlevel > 30 -> {
                nexp += 20
            }
        }
        if (nexp >= 100) {
            nlevel += 1
            nexp -= 100
        }
        val nGameData = GameData(nlevel,nexp)

        viewModelScope.launch {
            _level.value = nGameData
        }
        return nGameData
    }

}