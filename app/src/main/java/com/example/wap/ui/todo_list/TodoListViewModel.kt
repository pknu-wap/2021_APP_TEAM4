package com.example.wap.ui.todo_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wap.data.MyToDoList
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class TodoListViewModel: ViewModel() {

    private val todoCollectionRef = Firebase.firestore.collection("todo")

    private val list = mutableListOf<MyToDoList>()

    private val _todoList = MutableLiveData<List<MyToDoList>>()

    val todoList : LiveData<List<MyToDoList>> = _todoList

    init{
        _todoList.value = list
    }

     fun saveTodo(todo: MyToDoList) = CoroutineScope(Dispatchers.IO).launch {
        try {
            todoCollectionRef.add(todo).await()
            list.add(todo)
           viewModelScope.launch {
                _todoList.value = list
            }
        } catch (e: Exception) {
            Log.d("tag", e.message.toString())
        }
    }
    fun loadTodo() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = todoCollectionRef.get().await()
            for (document in querySnapshot.documents) {
                val todo = document.toObject<MyToDoList>()
                todo?.let {
                    list.add(todo)
                }
            }
            viewModelScope.launch{
                _todoList.value = list
            }
        } catch (e: Exception) {
            Log.d("Tag", e.message.toString())
        }
    }

    fun deleteTodo(position: Int) = CoroutineScope(Dispatchers.IO).launch {
        val todoQuery = todoCollectionRef
            .whereEqualTo("deadline", list[position].deadline)
            .whereEqualTo("toDo", list[position].toDo)
            .get()
            .await()
        if(todoQuery.documents.isNotEmpty()){
            for(document in todoQuery){
                try{
                    todoCollectionRef.document(document.id).delete().await()
                    list.removeAt(position)
                    viewModelScope.launch{
                        _todoList.value = list
                    }
                }catch(e: Exception){
                    Log.d("Tag","delete error")
                }
            }
        }
    }

}