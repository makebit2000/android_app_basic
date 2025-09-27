package com.example.todolist

import android.content.Context
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
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// --- DataStore Setup ---
val Context.dataStore by preferencesDataStore("todos_prefs")

data class TodoItemData(val text: String, val completed: Boolean)

class TodoRepository(private val context: Context) {

    private val TODOS_KEY = stringSetPreferencesKey("todos_set")

    // Hole die Todo-Liste
    val todosFlow: Flow<List<TodoItemData>> = context.dataStore.data
        .map { prefs ->
            prefs[TODOS_KEY]?.mapNotNull { item ->
                val parts = item.split("|")
                if (parts.size == 2) {
                    val text = parts[0]
                    val completed = parts[1].toBoolean()
                    TodoItemData(text, completed)
                } else null
            } ?: emptyList()
        }

    // Speichere die gesamte Todo-Liste
    suspend fun saveTodos(todos: List<TodoItemData>) {
        context.dataStore.edit { prefs ->
            val stringSet = todos.map { "${it.text}|${it.completed}" }.toSet()
            prefs[TODOS_KEY] = stringSet
        }
    }
}

// --- Main Activity ---
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

// --- TodoApp Composable ---
@Composable
fun TodoApp(repository: TodoRepository) {
    var text by remember { mutableStateOf("") }
    var todos by remember { mutableStateOf(listOf<TodoItemData>()) }

    val scope = rememberCoroutineScope()

    // Lade die Daten beim Start
    LaunchedEffect(Unit) {
        repository.todosFlow.collect { storedTodos ->
            todos = storedTodos
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Eingabefeld + Hinzufügen-Button
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Neue Aufgabe eingeben") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (text.isNotBlank()) {
                    val newTodo = TodoItemData(text, completed = false)
                    todos = todos + newTodo
                    scope.launch {
                        repository.saveTodos(todos)
                    }
                    text = ""
                }
            }) {
                Text("Hinzufügen")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Todo-Liste
        LazyColumn {
            items(todos) { todo ->
                TodoItem(
                    todo = todo,
                    onCheckedChange = { isChecked ->
                        todos = todos.map {
                            if (it.text == todo.text) it.copy(completed = isChecked) else it
                        }
                        scope.launch { repository.saveTodos(todos) }
                    },
                    onDelete = {
                        todos = todos - todo
                        scope.launch { repository.saveTodos(todos) }
                    }
                )
            }
        }
    }
}

// --- TodoItem Composable ---
@Composable
fun TodoItem(
    todo: TodoItemData,
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
                checked = todo.completed,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = todo.text,
                style = if (todo.completed) MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                ) else MaterialTheme.typography.bodyLarge
            )
        }

        Button(onClick = onDelete) {
            Text("Löschen")
        }
    }
}
