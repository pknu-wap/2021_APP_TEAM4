package com.example.wap.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wap.data.GameData
import com.example.wap.repository.GameRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository
): ViewModel() {

    private val _level = MutableLiveData<GameData>()

    val level: LiveData<GameData> get() = _level

    init{
        loadLevel()
    }

    fun loadLevel(){
        viewModelScope.launch {
            _level.value =  gameRepository.loadLevel()
        }
    }
    fun updateLevel() {
        viewModelScope.launch {
            gameRepository.updateLevel(levelUp())
        }
    }

    private fun levelUp() : GameData {

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