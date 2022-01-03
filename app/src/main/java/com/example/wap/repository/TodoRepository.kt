package com.example.wap.repository

import com.example.wap.data.MyToDoList

interface TodoRepository {

    suspend fun insertTodo(todo: MyToDoList){

    }

    suspend fun loadTodo(): MutableList<MyToDoList>

    suspend fun  deleteTodo(todo: MyToDoList){

    }
}