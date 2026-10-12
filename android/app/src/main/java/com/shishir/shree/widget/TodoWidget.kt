package com.shishir.shree.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
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

private val WDeep = Color(0xFF15528A)
private val WCard = Color(0xFFFFFFFF)
private val WDone = Color(0x7315528A)

class TodoWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val result = runCatching {
            Supabase.client.from("todos").select().decodeList<Todo>().sortedBy { it.done }
        }
        provideContent {
            WidgetBody(result.getOrNull(), result.isFailure)
        }
    }
}

@Composable
private fun WidgetBody(todos: List<Todo>?, failed: Boolean) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_frame))
            .padding(14.dp)
            .clickable(actionStartActivity<MainActivity>()),
    ) {
        Text(
            "श्री · to-dos",
            style = TextStyle(color = ColorProvider(WDeep), fontSize = 15.sp, fontWeight = FontWeight.Bold),
        )
        Spacer(GlanceModifier.height(12.dp))
        when {
            failed -> Text(
                "couldn't load — tap to open",
                style = TextStyle(color = ColorProvider(WDeep), fontSize = 13.sp),
            )
            todos.isNullOrEmpty() -> Text(
                "nothing yet — tap to add",
                style = TextStyle(color = ColorProvider(WDeep), fontSize = 13.sp),
            )
            else -> LazyColumn {
                items(todos) { t -> TodoLine(t) }
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
                .padding(horizontal = 12.dp, vertical = 10.dp),
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
