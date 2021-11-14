package com.example.wap

class Datasource {

    fun loadMyToDoList(): MutableList<MyToDoList>{
        return mutableListOf<MyToDoList>(
            MyToDoList("WAP 프로젝트 중간 발표", "2021.11.16"),
            MyToDoList("인프밍 과제", "2021.12.17."),
            MyToDoList("스터디 준비", "2021.11.15")
        )
    }
}