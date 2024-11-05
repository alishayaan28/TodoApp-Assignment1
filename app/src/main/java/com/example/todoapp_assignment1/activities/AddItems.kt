package com.example.todoapp_assignment1.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.todoapp_assignment1.activities.ui.theme.TodoAppAssignment1Theme
import com.example.todoapp_assignment1.dataBase.TodoHelper
import com.example.todoapp_assignment1.models.TodoItems
import com.example.todoapp_assignment1.models.TodoList
import com.example.todoapp_assignment1.ui.theme.lightGrey
import com.example.todoapp_assignment1.ui.theme.poppins
import com.example.todoapp_assignment1.ui.theme.primaryColor
import com.example.todoapp_assignment1.ui.theme.redColor
import com.example.todoapp_assignment1.ui.theme.yellowColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddItems : ComponentActivity() {

    // Declare the variable of database
    private lateinit var todoHelper: TodoHelper

    // id variable for showing item of list in which data is added
    private var uid = 0

    //Declare a new variable to show saved data
    private var todoListItems by mutableStateOf<List<TodoItems>>(emptyList())

    // complete variable to count task completed in the list
    private var completeCount by mutableIntStateOf(0)

    // upcoming Due date
    private var upcomingDate by mutableStateOf("")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        //Initializing Database
       todoHelper = TodoHelper(this)

        // Getting id of list
       uid = intent.getIntExtra("uid", 0)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // show input dialogue
            var dialogue by remember { mutableStateOf(false) }

            // text field
            val inputTxt by remember { mutableStateOf("") }

            // text field
            val dueDate by remember { mutableStateOf<Long?>(null) }

            //Lottie Animation
            val lottieAnimation by rememberLottieComposition(
                spec = LottieCompositionSpec.Asset("emptyList.json")
            )

            TodoAppAssignment1Theme {
                Scaffold(
                    containerColor = Color.White,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "TO DO Items",
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
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.Black
                                    )
                                }
                            },
                            modifier = Modifier.shadow(4.dp)
                        )
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            containerColor = primaryColor,
                            onClick = {
                                dialogue = true
                            },
                            content = {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Add Items",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = "Add Items",
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
                           if(todoListItems.isNotEmpty()){

                               Column {
                                   Row {
                                       Card(
                                           modifier = Modifier
                                               .weight(0.5f)
                                               .aspectRatio(2f)
                                               .padding(
                                                   start = 10.dp,
                                                   end = 10.dp,
                                                   top = 10.dp,
                                                   bottom = 10.dp
                                               ),
                                           colors = CardDefaults.cardColors(lightGrey)
                                       ) {
                                           Text(
                                               text = "Total Items",
                                               modifier = Modifier.padding(8.dp),
                                               color = Color.Black,
                                               style = TextStyle(
                                                   fontFamily = poppins,
                                                   fontWeight = FontWeight.W500,
                                                   fontSize = 16.sp
                                               )

                                           )
                                           Text(
                                               text = "${todoListItems.size}",
                                               modifier = Modifier.padding(5.dp),
                                               color = Color.Black,
                                               style = TextStyle(
                                                   fontFamily = poppins,
                                                   fontWeight = FontWeight.W500,
                                                   fontSize = 16.sp
                                               )

                                           )
                                       }

                                       Card(
                                           modifier = Modifier
                                               .weight(0.5f)
                                               .aspectRatio(2f)
                                               .padding(
                                                   start = 10.dp,
                                                   end = 10.dp,
                                                   top = 10.dp,
                                                   bottom = 10.dp
                                               ),
                                           colors = CardDefaults.cardColors(lightGrey)
                                       ) {
                                           Text(
                                               text = "Completed Items",
                                               modifier = Modifier.padding(8.dp),
                                               color = Color.Black,
                                               style = TextStyle(
                                                   fontFamily = poppins,
                                                   fontWeight = FontWeight.W500,
                                                   fontSize = 16.sp
                                               )

                                           )
                                           Text(
                                               text = "$completeCount",
                                               modifier = Modifier.padding(5.dp),
                                               color = Color.Black,
                                               style = TextStyle(
                                                   fontFamily = poppins,
                                                   fontWeight = FontWeight.W500,
                                                   fontSize = 16.sp
                                               )

                                           )


                                       }

                                   }
                                   Card(
                                       modifier = Modifier
                                           .fillMaxWidth()
                                           .height(100.dp)
                                           .padding(
                                               start = 10.dp, end = 10.dp,
                                               top = 10.dp, bottom = 10.dp
                                           ),
                                       colors = CardDefaults.cardColors(lightGrey)
                                   ) {
                                       Text(
                                           text = "Upcoming Due Date",
                                           modifier = Modifier.padding(8.dp),
                                           color = Color.Black,
                                           style = TextStyle(
                                               fontFamily = poppins,
                                               fontWeight = FontWeight.W500,
                                               fontSize = 16.sp
                                           )

                                       )

                                       Text(
                                           text = upcomingDate,
                                           modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                                           color = Color.Black,
                                           style = TextStyle(
                                               fontFamily = poppins,
                                               fontWeight = FontWeight.W500,
                                               fontSize = 16.sp
                                           )

                                       )
                                   }
                                   ShowItemData()
                               }

                           }else{

                               Column (
                                   modifier = Modifier.fillMaxSize(),
                                   verticalArrangement = Arrangement.Center,
                                   horizontalAlignment = Alignment.CenterHorizontally
                               ) {
                                   LottieAnimation(
                                       composition = lottieAnimation,
                                       iterations = LottieConstants.IterateForever
                                   )
                                   Text(
                                       text = "No Item Found",
                                       modifier = Modifier.padding(4.dp),
                                       color = Color.Black,
                                       style = TextStyle(
                                           fontFamily = poppins,
                                           fontWeight = FontWeight.W500,
                                           fontSize = 16.sp
                                       )
                                   )
                               }
                           }
                        }
                    }
                )
            }
            if(dialogue){
                AddItemDialog(uid, inputTxt, dueDate, dismiss = { dialogue = false}, 1)
                }
        }
    }


    override fun onResume() {
        super.onResume()
        fetchSaveData()
    }

    // Creating a Function to fetch my data
    private fun fetchSaveData(){
        val reverseList = todoHelper.getAllListItems(uid)
        todoListItems =  reverseList.reversed()
        completeCount = todoListItems.count{it.isComplete == 1}
        upcomingDate = todoListItems.filter { it.dueDate.isNotEmpty()}
            .minByOrNull { it.dueDate }?.dueDate ?: "No nearest date found."
    }

    //Show saved items data
    @Composable
    fun ShowItemData(){

        val calender = Calendar.getInstance()
        val currentDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(calender.time)

        LazyColumn  (
            modifier = Modifier.padding(bottom = 75.dp)
        ){
            itemsIndexed(todoListItems){ index, data ->

                val card = when{
                    todoListItems[index].dueDate == currentDate -> yellowColor
                    todoListItems[index].dueDate <= currentDate -> redColor
                    todoListItems[index].dueDate == "null" -> Color.White
                    else -> Color.White
                }
                Log.e("dueDate", todoListItems[index].dueDate)
                Card (
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    colors = CardDefaults.cardColors(card),
                    border = BorderStroke(1.dp, lightGrey)
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        CheckBox(data.id, todoHelper, data.isComplete)
                        Column (
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ){
                            Text(
                                text = todoListItems[index].name,
                                modifier = Modifier.padding(4.dp),
                                color = Color.Black,
                                style = TextStyle(
                                    fontFamily = poppins,
                                    fontWeight = FontWeight.W500,
                                    fontSize = 16.sp
                                )

                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            if(todoListItems[index].dueDate == "null"){
                                Text(
                                    text = "",
                                    modifier = Modifier.padding(4.dp),
                                    color = Color.Black,
                                    style = TextStyle(
                                        fontFamily = poppins,
                                        fontWeight = FontWeight.W500,
                                        fontSize = 16.sp
                                    )

                                )
                            }else{
                                Text(
                                    text = todoListItems[index].dueDate,
                                    modifier = Modifier.padding(4.dp),
                                    color = Color.Black,
                                    style = TextStyle(
                                        fontFamily = poppins,
                                        fontWeight = FontWeight.W500,
                                        fontSize = 16.sp
                                    )

                                )
                            }
                        }
                        PopUpMenu(data.id, todoHelper, todoListItems[index], todoListItems)
                    }
                }

            }
        }

    }

    // PopUp Menu for performing other functionality in my app
    @Composable
    fun PopUpMenu(itemId: Int, todoHandler: TodoHelper, editList: TodoItems, listTodo : List<TodoItems>){

        val extend = remember { mutableStateOf(false) }
        var showDelete by remember { mutableStateOf(false) }
        var editing by remember { mutableStateOf(false) }
        var move by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(
                    Alignment.TopEnd
                )
        ){
            IconButton(
                onClick = {
                    extend.value = !extend.value
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.Black
                )
            }
            DropdownMenu(
                expanded = extend.value,
                onDismissRequest = {
                    extend.value = false
                },
                modifier = Modifier.background(Color.White)
            ) {

                // move item from one list to another
                DropdownMenuItem(
                    text = { Text("Move",
                        style = TextStyle(
                            fontFamily = poppins,
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp
                        )
                    )

                    },
                    onClick = {
                        extend.value = false
                        move = true
                    }
                )

                // edit saved items
                DropdownMenuItem(
                    text = { Text("Edit",
                        style = TextStyle(
                            fontFamily = poppins,
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp
                        )
                    )
                    },
                    onClick = {
                        extend.value = false
                        editing =  true
                    }
                )

                // delete items
                DropdownMenuItem(
                    text = { Text("Delete",
                        style = TextStyle(
                            fontFamily = poppins,
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp
                        )
                    )

                    },
                    onClick = {
                        extend.value = false
                        showDelete = true
                    }
                )
            }
        }

        // Delete Dialogue for confirmation of items to delete
        if(showDelete){
            AlertDialog(
                containerColor = lightGrey,
                onDismissRequest = {
                    showDelete = false
                },
                title = { Text("Are you Sure?",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = poppins,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600
                    )
                )},
                text = { Text("Are you sure you want to delete this item",
                    style = TextStyle(
                        fontFamily = poppins,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )

                )},
                confirmButton = {
                    TextButton(
                        onClick = {
                            todoHandler.deleteListItems(itemId)
                            fetchSaveData()
                            showDelete = false
                        }
                    ) {
                        Text(
                            text = "Yes",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                fontFamily = poppins,
                                color = Color.Black
                            )
                        )

                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDelete = false
                        }
                    ) {
                        Text(
                            text = "No",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                fontFamily = poppins,
                                color = Color.Black
                            )
                        )
                    }
                }
            )
        }

        // show dialog for editing
        if(editing){

            var dateString by remember { mutableStateOf<Long?>(null) }

            // To handle crashing app whe no date is no given
            if(editList.dueDate.isEmpty()){
               dateString = null
            }else{
                 dateString = convertDateToMillis(editList.dueDate)
            }

            AddItemDialog(
                itemId = editList.id,
                input = editList.name,
                date = dateString,
                dismiss = { editing = false },
                isEdit = -1
            )
        }

        // show dialog for move item
        if(move){

            // excluding the current list so the item cannot move to the same list
            val list = todoHandler.getAllList()
            val sortList = listTodo.first{ it.id == itemId}.listId
            val finalList = list.filter { it.id != sortList }
            MoveItemsDialog(itemId = itemId, dismiss = { move = false }, finalList)
        }
    }

    // Add Items Dialogue
    @Composable
    fun AddItemDialog(itemId : Int, input: String, date: Long?, dismiss : () -> Unit, isEdit : Int){

        var inpTxt by remember { mutableStateOf(input)}
        var dueDate by remember { mutableStateOf(date) }

        Dialog(onDismissRequest = {
            dismiss()
        }) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(275.dp)
                    .background(
                        lightGrey,
                        shape = RoundedCornerShape(12.dp)
                    )
            ){
                Row{
                    IconButton(onClick = {
                        dismiss()
                        inpTxt = ""
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Create a Task",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W500,
                            fontFamily = poppins,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(top = 10.dp, end = 10.dp)
                    )
                    Spacer(modifier = Modifier.weight(2f))
                }
                TextFiled(inpTxt) { inpTxt = it }
                DatePickerFieldToModal(selectedDate = dueDate) { selectedDate -> dueDate = selectedDate }
                Button(
                    onClick = {
                       if(isEdit == -1){
                           if(inpTxt.isNotEmpty()){
                               val editDate = dueDate?.let { convertMillisToDate(it) } ?: ""
                               todoHelper.updateListItems(itemId, inpTxt, editDate)
                               dismiss()
                               fetchSaveData()

                           }else{
                               Toast.makeText(this@AddItems, "Please Enter name.",
                                   Toast.LENGTH_SHORT).show()
                           }

                       }else{
                           if(inpTxt.isNotEmpty()){
                               val emptyDate = dueDate?.let { convertMillisToDate(it) } ?: "null"
                               todoHelper.addItems(TodoItems(0, uid, inpTxt, dueDate = emptyDate , 0))
                               dismiss()
                               inpTxt = ""
                               dueDate = null
                               fetchSaveData()
                           }else{
                               Toast.makeText(this@AddItems, "Please Enter name.",
                                   Toast.LENGTH_SHORT).show()
                           }
                       }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    enabled = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(primaryColor)
                ) {
                    if(isEdit == -1){

                        Text(
                            text = "Update",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                fontFamily = poppins,
                                color = Color.White
                            ),
                        )

                    }else{
                        Text(
                            text = "Save",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                fontFamily = poppins,
                                color = Color.White
                            ),
                        )
                    }
                }
            }
        }
    }

    // Move item from one list to another
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MoveItemsDialog(itemId : Int,  dismiss : () -> Unit, allList : List<TodoList>){

        var listId by remember { mutableStateOf(allList.firstOrNull()?.id) }

        // code taken from Android Developers official site of bottom sheet
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = {
               dismiss()
            },
            sheetState = sheetState,
            modifier = Modifier
                .height(500.dp)
                .padding(bottom = 70.dp),
            containerColor = Color.White
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
            ){
                if(allList.isEmpty()){
                    Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        text = "No List Found to move Items.",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            fontFamily = poppins,
                            color = Color.Black
                        )
                    )
                } else{
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        items(allList.size){ index ->
                            val listItem = allList[index]

                            Card (
                                modifier = Modifier.padding(8.dp),
                                colors = CardDefaults.cardColors(lightGrey)
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(10.dp)
                                ){
                                    Text(
                                        text = listItem.listName,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.W500,
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    )
                                    Spacer(modifier = Modifier.weight(1F))
                                    Checkbox(checked = (listId == listItem.id) ,
                                        onCheckedChange = {
                                                checkItem ->
                                            if(checkItem){
                                                listId = listItem.id
                                            }
                                        }
                                    )

                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    colors = ButtonDefaults.buttonColors(primaryColor),
                    modifier = Modifier.padding(end = 20.dp, bottom = 8.dp)
                        .align(Alignment.End),
                    onClick = {
                        val moveItem = todoHelper.moveBuItemID(itemId)
                        if (moveItem != null) {
                            moveItem.listId = listId!!
                            todoHelper.moveListItem(moveItem)
                            dismiss()
                            fetchSaveData()
                        }
                    }) {
                    Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        text = "Move",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            fontFamily = poppins,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }

    // checkBox
    @Composable
    fun CheckBox(itemId : Int , todoHandler : TodoHelper, complete : Int){

        val check = remember { mutableStateOf(complete != 0) }

        Checkbox(
            checked = check.value,
            colors = CheckboxDefaults.colors(primaryColor),
            onCheckedChange = {
                val comVal = if(complete ==0 ){
                    1 // true
                }else{
                    0 // false
                }
                todoHandler.updateCompleteState(itemId, comVal)
                fetchSaveData()
                check.value = comVal != 0
            }
        )
    }

    // TextFiled for take user input
    @Composable
    fun TextFiled(input: String, changeInput: (String) -> Unit){
        OutlinedTextField(
            value = input,
            onValueChange = changeInput,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            ),
            label = {
                Text(
                    "Enter Name",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = poppins
                    )
                )
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp),
        )
    }

    // DatePicker Code Taken from Android Developer Official Site
    @Composable
    fun DatePickerFieldToModal(modifier: Modifier = Modifier, selectedDate: Long?, onDateSelected: (Long?) -> Unit) {

        val showModal = remember { mutableStateOf(false) }

        OutlinedTextField(
            value = selectedDate?.let { convertMillisToDate(it) } ?: "",
            readOnly = true,
            onValueChange = { },
            placeholder = {
                Text("Select Due Date",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = poppins,
                    ),
                ) },
            trailingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Select due date",
                    tint = Color.Black,
                )
            },
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .pointerInput(selectedDate) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showModal.value = true
                        }
                    }
                }
        )

        if (showModal.value) {
            DatePickerModal(
                onDateSelected = { onDateSelected(it) },
                onDismiss = { showModal.value = false }
            )
        }
    }

    private fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    private fun convertDateToMillis(date: String): Long? {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return formatter.parse(date)?.time
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePickerModal(
        onDateSelected: (Long?) -> Unit,
        onDismiss: () -> Unit
    ) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            colors = DatePickerDefaults.colors(Color.White),
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }) {
                    Text("OK",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = poppins,
                            fontWeight = FontWeight.W500
                        ),
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = poppins,
                            fontWeight = FontWeight.W500
                        ),
                    )
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}