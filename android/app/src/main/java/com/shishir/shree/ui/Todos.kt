package com.shishir.shree.ui

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shishir.shree.data.NewTodo
import com.shishir.shree.data.Supabase
import com.shishir.shree.data.Todo
import com.shishir.shree.ui.theme.CardWhite
import com.shishir.shree.ui.theme.DeepBlue
import com.shishir.shree.ui.theme.SkyBlue
import androidx.glance.appwidget.updateAll
import com.shishir.shree.widget.TodoWidget
import com.shishir.shree.widget.TodoWidgetReceiver
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class TodosViewModel(app: Application) : AndroidViewModel(app) {
    var todos by mutableStateOf<List<Todo>>(emptyList())
        private set
    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var draft by mutableStateOf("")
    var busy by mutableStateOf(false)
        private set

    init { load() }

    private fun sorted(list: List<Todo>) = list.sortedBy { it.done }

    private fun refreshWidget() {
        viewModelScope.launch {
            runCatching { TodoWidget().updateAll(getApplication<Application>()) }
        }
    }

    fun load() {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                todos = sorted(Supabase.client.from("todos").select().decodeList<Todo>())
            } catch (e: Exception) {
                error = e.message ?: e.toString()
            } finally {
                loading = false
            }
        }
    }

    fun add() {
        val text = draft.trim()
        if (text.isEmpty() || busy) return
        viewModelScope.launch {
            busy = true
            error = null
            try {
                Supabase.client.from("todos").insert(NewTodo(text))
                draft = ""
                todos = sorted(Supabase.client.from("todos").select().decodeList<Todo>())
                refreshWidget()
            } catch (e: Exception) {
                error = e.message ?: e.toString()
            } finally {
                busy = false
            }
        }
    }

    fun toggle(todo: Todo) {
        val newDone = !todo.done
        todos = sorted(todos.map { if (it.id == todo.id) it.copy(done = newDone) else it })
        refreshWidget()
        viewModelScope.launch {
            try {
                Supabase.client.from("todos").update({ set("done", newDone) }) {
                    filter { eq("id", todo.id) }
                }
            } catch (e: Exception) {
                error = e.message ?: e.toString()
                load()
            }
        }
    }
}

@Composable
fun TodoSection(vm: TodosViewModel = viewModel()) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("To-dos", color = DeepBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(
                onClick = { pinTodoWidget(context) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text("+ home screen", color = SkyBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = vm.draft,
                onValueChange = { vm.draft = it },
                placeholder = { Text("add a to-do", color = DeepBlue.copy(alpha = 0.4f)) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepBlue,
                    unfocusedBorderColor = SkyBlue,
                    cursorColor = DeepBlue,
                    focusedTextColor = DeepBlue,
                    unfocusedTextColor = DeepBlue,
                ),
            )
            Spacer(Modifier.size(10.dp))
            Button(
                onClick = { vm.add() },
                enabled = !vm.busy && vm.draft.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepBlue, contentColor = CardWhite),
            ) { Text("Add", fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(12.dp))

        when {
            vm.loading -> Box(Modifier.fillMaxWidth().padding(top = 12.dp), Alignment.Center) {
                CircularProgressIndicator(color = SkyBlue)
            }
            vm.todos.isEmpty() -> TodoCard {
                Text("nothing yet — add the first one", color = DeepBlue.copy(alpha = 0.6f), fontSize = 14.sp)
            }
            else -> Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                vm.todos.forEach { todo -> TodoRow(todo) { vm.toggle(todo) } }
            }
        }

        vm.error?.let {
            Text("sync issue: $it", color = Color(0xFFB23A48), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

private fun pinTodoWidget(context: Context) {
    val mgr = AppWidgetManager.getInstance(context)
    val provider = ComponentName(context, TodoWidgetReceiver::class.java)
    if (mgr.isRequestPinAppWidgetSupported) {
        mgr.requestPinAppWidget(provider, null, null)
    }
}

@Composable
private fun TodoRow(todo: Todo, onToggle: () -> Unit) {
    TodoCard(onClick = onToggle) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(20.dp)
                    .background(if (todo.done) SkyBlue else CardWhite, RoundedCornerShape(6.dp))
                    .border(2.5.dp, DeepBlue, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (todo.done) Icon(Icons.Filled.Check, contentDescription = "done", tint = CardWhite, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.size(12.dp))
            Text(
                todo.body,
                color = if (todo.done) DeepBlue.copy(alpha = 0.45f) else DeepBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = if (todo.done) TextDecoration.LineThrough else null,
            )
        }
    }
}

@Composable
private fun TodoCard(onClick: (() -> Unit)? = null, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Box(
            Modifier
                .matchParentSize()
                .offset(3.dp, 3.dp)
                .background(SkyBlue, RoundedCornerShape(14.dp)),
        )
        Column(
            Modifier
                .fillMaxWidth()
                .background(CardWhite, RoundedCornerShape(14.dp))
                .border(2.5.dp, DeepBlue, RoundedCornerShape(14.dp))
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(14.dp),
            content = content,
        )
    }
}
