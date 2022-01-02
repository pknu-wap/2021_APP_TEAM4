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

class GameViewModel: ViewModel() {

    private val gameCollectionRef = Firebase.firestore.collection("game")

    private val _level = MutableLiveData<GameData>()

    val level: LiveData<GameData> get() = _level

    init{
        loadLevel()
    }

    fun loadLevel() = CoroutineScope(Dispatchers.IO).launch{

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
}