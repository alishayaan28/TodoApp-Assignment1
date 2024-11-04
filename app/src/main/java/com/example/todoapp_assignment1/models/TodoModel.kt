package com.example.todoapp_assignment1.models

data class  TodoList(
    val id : Int,
    val listName : String,
)

data class TodoItems(
    val id : Int,
    var listId : Int,
    val name : String,
    val dueDate : String,
    val isComplete : Int
)