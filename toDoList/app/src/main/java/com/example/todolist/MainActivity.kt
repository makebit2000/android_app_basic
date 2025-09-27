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

    // 每个 todo 的完成状态
    var completedStates by remember { mutableStateOf(mapOf<String, Boolean>()) }

    val scope = rememberCoroutineScope()

    // 启动时从 DataStore 读取
    LaunchedEffect(Unit) {
        repository.todosFlow.collectLatest { storedTodos ->
            todos = storedTodos
            // 仅在 completedStates 为空时初始化，保留已有状态
            completedStates = storedTodos.associateWith { existing ->
                completedStates[existing] ?: false
            }
        }
    }

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
                    // 只为新条目添加状态，保留旧状态
                    completedStates = completedStates + (text to false)
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
                TodoItem(
                    todo = todo,
                    completed = completedStates[todo] ?: false,
                    onCheckedChange = { isChecked ->
                        completedStates = completedStates + (todo to isChecked)
                    },
                    onDelete = {
                        todos = todos - todo
                        completedStates = completedStates - todo
                        scope.launch {
                            repository.saveTodos(todos)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: String,
    completed: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Checkbox(
                checked = completed,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = todo,
                style = if (completed) MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                ) else MaterialTheme.typography.bodyLarge
            )
        }

        Button(onClick = onDelete) {
            Text("删除")
        }
    }
}
