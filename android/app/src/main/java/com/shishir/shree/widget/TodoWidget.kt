package com.shishir.shree.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.shishir.shree.MainActivity
import com.shishir.shree.R
import com.shishir.shree.data.Supabase
import com.shishir.shree.data.Todo
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val WDeep = Color(0xFF15528A)
private val WCard = Color(0xFFFFFFFF)
private val WDone = Color(0x7315528A)

private val AppJson = Json { ignoreUnknownKeys = true }
private val TodosKey = stringPreferencesKey("todos_json")

private val IdKey = ActionParameters.Key<String>("todo_id")
private val DoneKey = ActionParameters.Key<Boolean>("todo_done")

private fun decode(json: String?): List<Todo>? =
    json?.let { runCatching { AppJson.decodeFromString<List<Todo>>(it) }.getOrNull() }

class ToggleTodoAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val id = parameters[IdKey] ?: return
        val done = parameters[DoneKey] ?: false
        // Optimistic: flip the done flag in the widget's own state and re-render instantly.
        updateAppWidgetState(context, glanceId) { prefs ->
            val todos = decode(prefs[TodosKey]) ?: return@updateAppWidgetState
            val updated = todos.map { if (it.id == id) it.copy(done = !done) else it }.sortedBy { it.done }
            prefs[TodosKey] = AppJson.encodeToString(updated)
        }
        TodoWidget().update(context, glanceId)
        // Sync to the database in the background.
        runCatching {
            Supabase.client.from("todos").update({ set("done", !done) }) { filter { eq("id", id) } }
        }
    }
}

class TodoWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val json = prefs[TodosKey]
            // First load only: fetch into state. Once state exists, render it (so an
            // optimistic toggle isn't overwritten by a re-fetch). App edits refresh via syncAll.
            if (json == null) {
                LaunchedEffect(Unit) {
                    runCatching {
                        val todos = Supabase.client.from("todos").select().decodeList<Todo>().sortedBy { it.done }
                        updateAppWidgetState(context, id) { it[TodosKey] = AppJson.encodeToString(todos) }
                    }
                }
            }
            WidgetBody(decode(json), json == null)
        }
    }

    companion object {
        /** Fetch from Supabase, write into every widget instance's state, and re-render. */
        suspend fun syncAll(context: Context) {
            val todos = runCatching {
                Supabase.client.from("todos").select().decodeList<Todo>().sortedBy { it.done }
            }.getOrNull() ?: return
            val json = AppJson.encodeToString(todos)
            GlanceAppWidgetManager(context).getGlanceIds(TodoWidget::class.java).forEach { gid ->
                updateAppWidgetState(context, gid) { it[TodosKey] = json }
            }
            TodoWidget().updateAll(context)
        }
    }
}

@Composable
private fun WidgetBody(todos: List<Todo>?, loading: Boolean) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_frame))
            .padding(14.dp),
    ) {
        Text(
            "श्री · to-dos  ›",
            style = TextStyle(color = ColorProvider(WDeep), fontSize = 15.sp, fontWeight = FontWeight.Bold),
            modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>()),
        )
        Spacer(GlanceModifier.height(12.dp))
        when {
            loading -> Text("loading…", style = TextStyle(color = ColorProvider(WDeep), fontSize = 13.sp))
            todos.isNullOrEmpty() -> Text(
                "nothing yet — tap to add",
                style = TextStyle(color = ColorProvider(WDeep), fontSize = 13.sp),
            )
            else -> Column {
                todos.take(15).forEach { t -> TodoLine(t) }
            }
        }
    }
}

@Composable
private fun TodoLine(todo: Todo) {
    Box(GlanceModifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(ImageProvider(R.drawable.widget_item))
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clickable(
                    actionRunCallback<ToggleTodoAction>(
                        actionParametersOf(IdKey to todo.id, DoneKey to todo.done),
                    ),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = GlanceModifier
                    .size(20.dp)
                    .background(ImageProvider(if (todo.done) R.drawable.widget_check_on else R.drawable.widget_check_off)),
                contentAlignment = Alignment.Center,
            ) {
                if (todo.done) {
                    Text("✓", style = TextStyle(color = ColorProvider(WCard), fontSize = 13.sp, fontWeight = FontWeight.Bold))
                }
            }
            Spacer(GlanceModifier.width(12.dp))
            Text(
                todo.body,
                style = TextStyle(
                    color = ColorProvider(if (todo.done) WDone else WDeep),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (todo.done) TextDecoration.LineThrough else TextDecoration.None,
                ),
            )
        }
    }
}

class TodoWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodoWidget()
}
