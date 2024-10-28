package com.example.todoapp_assignment1.dataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todoapp_assignment1.models.TodoItems
import com.example.todoapp_assignment1.models.TodoList

class TodoHelper(context: Context)
    : SQLiteOpenHelper(context, "todoList.db", null, 1 ) {

    override fun onCreate(dataBase: SQLiteDatabase?) {
        dataBase?.execSQL("CREATE TABLE TODOLIST( id INTEGER PRIMARY KEY, listName Text)")
        dataBase?.execSQL("CREATE TABLE TODOIitems( id INTEGER PRIMARY KEY, listId INTEGER, name TEXT, dueDate TEXT, complete INTEGER)")
    }

    override fun onUpgrade(dataBase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        dataBase?.execSQL("DROP TABLE IF EXISTS TODOLIST")
        dataBase?.execSQL("DROP TABLE IF EXISTS TODOIitems")
    }


    // Add Data Function for todoList
    fun addListItems(todoList : TodoList): Long {

        // check same name of list
        val sameNameList = getAllList()
        if (sameNameList.any{it.listName.equals(todoList.listName, ignoreCase = true)}){
            return -1
        }

        val db = writableDatabase
        val values = ContentValues().apply {
         put("listName", todoList.listName)
        }
        val id = db.insert("TODOLIST", null, values)
        db.close()
        return id
    }

    // Get Data Function for todoList
    fun getAllList(): List<TodoList>{
        val itemList = mutableListOf<TodoList>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM TODOLIST", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val listName = cursor.getString(cursor.getColumnIndexOrThrow("listName"))
                itemList.add(TodoList(id, listName))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return itemList
    }


    // Add Data Function for todoItems
    fun addItems(todoItems : TodoItems): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("listId", todoItems.listId)
            put("name", todoItems.name)
            put("dueDate", todoItems.dueDate)
            put("complete", todoItems.isComplete)
        }
        val id = db.insert("TODOLIST", null, values)
        db.close()
        return id
    }



}