package com.shishir.shree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shishir.shree.ui.theme.Canvas
import com.shishir.shree.ui.theme.CardWhite
import com.shishir.shree.ui.theme.DeepBlue
import com.shishir.shree.ui.theme.ShreeTheme
import com.shishir.shree.ui.theme.SkyBlue
import com.shishir.shree.ui.theme.SoftBlue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShreeTheme {
                ShreeApp()
            }
        }
    }
}

private data class Dest(val label: String, val icon: ImageVector)

private val destinations = listOf(
    Dest("Today", Icons.Filled.Home),
    Dest("Chat", Icons.Filled.ChatBubble),
    Dest("Memories", Icons.Filled.FavoriteBorder),
    Dest("Lists", Icons.Filled.ListAlt),
    Dest("More", Icons.Filled.MoreHoriz),
)

@Composable
fun ShreeApp() {
    var selected by remember { mutableIntStateOf(0) }
    Scaffold(
        containerColor = Canvas,
        bottomBar = {
            NavigationBar(containerColor = CardWhite) {
                destinations.forEachIndexed { i, d ->
                    NavigationBarItem(
                        selected = selected == i,
                        onClick = { selected = i },
                        icon = { Icon(d.icon, contentDescription = d.label) },
                        label = { Text(d.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepBlue,
                            selectedTextColor = DeepBlue,
                            indicatorColor = SoftBlue,
                            unselectedIconColor = DeepBlue.copy(alpha = 0.45f),
                            unselectedTextColor = DeepBlue.copy(alpha = 0.45f),
                        ),
                    )
                }
            }
        },
    ) { inner ->
        when (selected) {
            0 -> TodayScreen(inner)
            3 -> com.shishir.shree.ui.ListsScreen(inner)
            else -> ComingSoon(inner, destinations[selected].label)
        }
    }
}

@Composable
private fun TodayScreen(inner: PaddingValues) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(inner)
            .padding(horizontal = 18.dp),
    ) {
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(56.dp)
                    .background(CardWhite, RoundedCornerShape(16.dp))
                    .border(2.5.dp, DeepBlue, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text("श्री", color = DeepBlue, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(12.dp))
            Column {
                Text("Shree", color = DeepBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("good morning, Reetu", color = DeepBlue.copy(alpha = 0.6f), fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(18.dp))

        StickerCard {
            Label("today, from shishir")
            Text(
                "good morning, Reetu. go get that Loksewa seat today.",
                color = DeepBlue, fontSize = 15.sp, fontWeight = FontWeight.Medium,
            )
        }
        StickerCard {
            Label("on this day")
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(SoftBlue, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text("a photo of us", color = SkyBlue, fontWeight = FontWeight.Bold)
            }
        }
        com.shishir.shree.ui.TodoSection()
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun ComingSoon(inner: PaddingValues, name: String) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(inner),
        contentAlignment = Alignment.Center,
    ) {
        Text("$name — coming soon", color = DeepBlue.copy(alpha = 0.6f), fontSize = 16.sp)
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text.uppercase(),
        color = SkyBlue,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(5.dp))
}

@Composable
private fun StickerCard(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Box(
            Modifier
                .matchParentSize()
                .offset(4.dp, 4.dp)
                .background(SkyBlue, RoundedCornerShape(14.dp)),
        )
        Column(
            Modifier
                .fillMaxWidth()
                .background(CardWhite, RoundedCornerShape(14.dp))
                .border(2.5.dp, DeepBlue, RoundedCornerShape(14.dp))
                .padding(14.dp),
            content = content,
        )
    }
}
