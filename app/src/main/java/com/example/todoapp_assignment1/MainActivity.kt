package com.example.todoapp_assignment1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp_assignment1.activities.AddItems
import com.example.todoapp_assignment1.dataBase.TodoHelper
import com.example.todoapp_assignment1.models.TodoList
import com.example.todoapp_assignment1.ui.theme.TodoAppAssignment1Theme
import com.example.todoapp_assignment1.ui.theme.lightGrey
import com.example.todoapp_assignment1.ui.theme.poppins
import com.example.todoapp_assignment1.ui.theme.primaryColor


class MainActivity : ComponentActivity() {

    // Declare the variable of database
    private lateinit var todoHelper: TodoHelper

    //Declare a new variable to show saved data
    private var todoList by mutableStateOf<List<TodoList>>(emptyList())


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializing DataBase
        todoHelper = TodoHelper(this)

        enableEdgeToEdge()
        setContent {

            // variable for display alert dialogue
            var showDialog by remember { mutableStateOf(false) }

            //TextField variable to access the text written
            val inputText by remember { mutableStateOf("") }

            TodoAppAssignment1Theme {
                Scaffold(
                    containerColor = Color.White,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "TO DO List",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.W500,
                                        fontFamily = poppins
                                    )
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.White,
                                titleContentColor = Color.Black
                            ),
                            modifier = Modifier.shadow(4.dp)
                        )
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            containerColor = primaryColor,
                            onClick = {
                                showDialog = true
                            },
                            content = {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Add List",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = "Add New List",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W500,
                                            fontFamily = poppins,
                                            color = Color.White
                                        )
                                    )
                                }
                            },
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        )
                    },
                    content = {
                        paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)){
                           Column {

                               if(todoList.isEmpty()){
                                   Column{
                                       Text(
                                           text = "No Data Found",
                                           modifier = Modifier.padding(4.dp),
                                           color = Color.Black,
                                           style = TextStyle(
                                               fontFamily = poppins,
                                               fontWeight = FontWeight.W500,
                                               fontSize = 16.sp
                                           )

                                       )
                                   }

                               }else{

                                   Text(
                                       text = "My Lists",
                                       modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp),
                                       color = Color.Black,
                                       style = TextStyle(
                                           fontFamily = poppins,
                                           fontWeight = FontWeight.W600,
                                           fontSize = 18.sp
                                       )

                                   )
                                   Card (
                                       modifier = Modifier
                                           .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 75.dp)
                                           .fillMaxWidth(),
                                       colors = CardDefaults.cardColors(
                                           containerColor = lightGrey
                                       )
                                   ) {
                                       DisplaySaveData()
                                   }


                               }


                           }
                        }
                    }
                )
            }

            // Alert Dialogue
         if(showDialog){
             AddListDialogue(itemId = 0, inpTxt = inputText, dialog = { showDialog = false }, 1)
         }

        }
    }


    override fun onResume() {
        super.onResume()
        fetchSaveData()
    }

    // Creating a Function to fetch my data
    private fun fetchSaveData(){
        val reverseList = todoHelper.getAllList()
        todoList =  reverseList.reversed()
    }

    // Making a composable function for displaying of saved data
    @Composable
    fun DisplaySaveData(){

        var editing by remember { mutableStateOf(false) }
        var currentIndex by remember { mutableIntStateOf(-1) }

        LazyColumn {
            itemsIndexed(todoList){ index, item ->
                Column (
                    modifier = Modifier.clickable(
                        onClick = {
                            val intent = Intent(this@MainActivity, AddItems::class.java)
                            intent.putExtra("uid", item.id)
                            startActivity(intent)
                        }
                    )
                ){
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 12.dp, bottom = 12.dp )
                    ) {
                        Text(
                            text = todoList[index].listName,
                            modifier = Modifier.padding(4.dp),
                            color = Color.Black,
                            style = TextStyle(
                                fontFamily = poppins,
                                fontWeight = FontWeight.W500,
                                fontSize = 16.sp
                            )

                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                editing = true
                                currentIndex = index
                            }
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "forward",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    if(index < todoList.lastIndex ) HorizontalDivider()
                }
            }
        }
        if(editing && currentIndex in todoList.indices){

            AddListDialogue(itemId = todoList[currentIndex].id, inpTxt = todoList[currentIndex].listName, dialog = { editing = false }, -1)

        }
    }

    @Composable
    fun AddListDialogue(itemId: Int , inpTxt : String, dialog : () -> Unit, isEdit : Int){

        var inputText by remember { mutableStateOf(inpTxt) }

        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = {
                dialog()
            },
            confirmButton = {
               if (isEdit == -1){
                   TextButton(
                       onClick = {
                           if (inputText.isNotEmpty()) {
                               todoHelper.updateListName(itemId, inputText)
                               fetchSaveData()
                               dialog()
                           } else {
                               Toast.makeText(this@MainActivity, "This Field cannot be empty.",Toast.LENGTH_SHORT).show()
                           }
                       }) {
                       Text(
                           text = "Update",
                           style = TextStyle(
                               fontSize = 12.sp,
                               fontWeight = FontWeight.W500,
                               fontFamily = poppins,
                               color = primaryColor
                           )
                       )

                   }
               } else {
                   TextButton(
                       onClick = {
                           val data = todoHelper.addListItems(TodoList(0, inputText))
                           if( inputText.isNotEmpty()){
                               if (data != -1L){
                                   inputText = ""
                                   dialog()
                                   fetchSaveData()
                               }else{
                                   Toast.makeText(this@MainActivity, "List with same name Already Exit. " +
                                           "Please write another name.",Toast.LENGTH_SHORT).show()
                               }
                           }
                           else{
                               Toast.makeText(this@MainActivity, "Please Enter List name.",Toast.LENGTH_SHORT).show()
                           }
                       }
                   ) {
                       Text(
                           text = "Add List",
                           style = TextStyle(
                               fontSize = 12.sp,
                               fontWeight = FontWeight.W500,
                               fontFamily = poppins,
                               color = primaryColor
                           )
                       )

                   }
               }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                        },
                        label = {
                            Text(
                                "Enter List Name",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = poppins
                                )
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 16.sp),
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        inputText = ""
                        dialog()
                    }
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W500,
                            fontFamily = poppins,
                            color = Color.Black
                        )
                    )

                }
            }
        )
    }

}
