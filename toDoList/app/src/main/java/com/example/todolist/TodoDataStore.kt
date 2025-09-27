package com.example.todolist

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 创建 DataStore
val Context.dataStore by preferencesDataStore("todos_prefs")

class TodoRepository(private val context: Context) {

    private val TODOS_KEY = stringSetPreferencesKey("todos_set")

    // 获取待办列表
    val todosFlow: Flow<List<String>> = context.dataStore.data
        .map { prefs ->
            prefs[TODOS_KEY]?.toList() ?: emptyList()
        }

    // 保存整个列表
    suspend fun saveTodos(todos: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[TODOS_KEY] = todos.toSet()
        }
    }
}
