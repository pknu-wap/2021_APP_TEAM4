package com.example.wap.ui.todo_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wap.data.MyToDoList
import com.example.wap.repository.TodoRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository
): ViewModel() {

    private val list = mutableListOf<MyToDoList>()

    private val _todoList = MutableLiveData<List<MyToDoList>>()

    val todoList : LiveData<List<MyToDoList>> = _todoList

    init{
        _todoList.value = list
    }
    fun insertTodo(todo: MyToDoList){
        viewModelScope.launch {
            todoRepository.insertTodo(todo)
            list.add(todo)
            _todoList.value = list
        }
    }

    fun getTodos(){
        viewModelScope.launch{
            for(i in 1..todoRepository.loadTodo().size){
                list.add(todoRepository.loadTodo()[i])
            }
            _todoList.value = list
        }
    }

    fun deleteTodo(position: Int){
        viewModelScope.launch {
            todoRepository.deleteTodo(list[position])
            list.removeAt(position)
            _todoList.value = list
        }
    }
}