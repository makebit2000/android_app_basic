package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: TodoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = TodoRepository(this)

        setContent {
            TodoApp(repository)
        }
    }
}

@Composable
fun TodoApp(repository: TodoRepository) {
    var text by remember { mutableStateOf("") }
    var todos by remember { mutableStateOf(listOf<String>()) }

    // 启动时从 DataStore 读取
    LaunchedEffect(Unit) {
        repository.todosFlow.collectLatest { storedTodos ->
            todos = storedTodos
        }
    }

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("输入待办事项") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (text.isNotBlank()) {
                    todos = todos + text
                    scope.launch {
                        repository.saveTodos(todos)
                    }
                    text = ""
                }
            }) {
                Text("添加")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(todos) { todo ->
                TodoItem(todo = todo) {
                    todos = todos - todo
                    scope.launch {
                        repository.saveTodos(todos)
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItem(todo: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(todo)
        Button(onClick = onDelete) {
            Text("删除")
        }
    }
}
