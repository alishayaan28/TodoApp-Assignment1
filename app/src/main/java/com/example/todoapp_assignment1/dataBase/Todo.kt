package com.example.todoapp_assignment1.dataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todoapp_assignment1.models.TodoItems
import com.example.todoapp_assignment1.models.TodoList

class TodoHelper(context: Context)
    : SQLiteOpenHelper(context, "todoList.db", null, 3 ) {

    override fun onCreate(dataBase: SQLiteDatabase?) {
        dataBase?.execSQL("CREATE TABLE TODOLIST( id INTEGER PRIMARY KEY, listName Text)")
        dataBase?.execSQL("CREATE TABLE TODOItems( id INTEGER PRIMARY KEY, listId INTEGER, name TEXT, dueDate TEXT, complete INTEGER)")
    }

    override fun onUpgrade(dataBase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        dataBase?.execSQL("DROP TABLE IF EXISTS TODOLIST")
        dataBase?.execSQL("DROP TABLE IF EXISTS TODOItems")
        onCreate(dataBase)
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
        val id = db.insert("TODOItems", null, values)
        db.close()
        return id
    }

    // Get Data Function for todoItems
    fun getAllListItems(uid: Int): List<TodoItems>{
        val itemList = mutableListOf<TodoItems>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM TODOItems WHERE listId = ?", arrayOf(uid.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val listName = cursor.getInt(cursor.getColumnIndexOrThrow("listId"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val dueDate = cursor.getString(cursor.getColumnIndexOrThrow("dueDate"))
                val complete = cursor.getInt(cursor.getColumnIndexOrThrow("complete"))
                itemList.add(TodoItems(id, listName, name, dueDate, complete))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return itemList
    }

    // Delete Function for todoItems
    fun deleteListItems(itemId: Int): Int {
        writableDatabase.use { database -> return database.delete("TODOItems", "id= ?", arrayOf(itemId.toString()))}
    }

    // Function for check task is completed or not
    fun updateCompleteState(itemId : Int, isComplete : Int) : Int{
        val db = writableDatabase
        val completeState = ContentValues().apply {
            put("complete", isComplete)
        }
        return db.update("TODOItems", completeState, "id= ?", arrayOf(itemId.toString()))
    }

    // Function for check task is completed or not
    fun updateListName(itemId : Int, listName : String) : Int{
        val db = writableDatabase
        val listState = ContentValues().apply {
            put("listName", listName)
        }
        return db.update("TODOLIST", listState, "id= ?", arrayOf(itemId.toString()))
    }

    // Function for check task is completed or not
    fun updateListItems(itemId : Int, itemName : String, dueDate : String) : Int{
        val db = writableDatabase
        val listState = ContentValues().apply {
            put("name", itemName)
            put("dueDate", dueDate)
        }
        return db.update("TODOItems", listState, "id= ?", arrayOf(itemId.toString()))
    }

    // Function for move item from one list to another
    fun moveListItem(item: TodoItems): Int {
        val db = writableDatabase
        val listState = ContentValues().apply {
            put("listId", item.listId)
            put("name", item.name)
            put("dueDate", item.dueDate)
            put("complete", item.isComplete)
        }
        return db.update("TODOItems", listState, "id= ?", arrayOf(item.id.toString()))
    }

    // Get id of list to move item from one list to another
    fun moveBuItemID(uid: Int): TodoItems? {
        var itemList : TodoItems? = null
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM TODOItems WHERE id = ?", arrayOf(uid.toString()))

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val listName = cursor.getInt(cursor.getColumnIndexOrThrow("listId"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val dueDate = cursor.getString(cursor.getColumnIndexOrThrow("dueDate"))
            val complete = cursor.getInt(cursor.getColumnIndexOrThrow("complete"))
            itemList = TodoItems(id, listName, name, dueDate, complete)
        }
        cursor.close()
        db.close()
        return itemList
    }

}