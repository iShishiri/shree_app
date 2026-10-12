package com.shishir.shree.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shishir.shree.data.ShreeList
import com.shishir.shree.data.Supabase
import com.shishir.shree.ui.theme.CardWhite
import com.shishir.shree.ui.theme.DeepBlue
import com.shishir.shree.ui.theme.SkyBlue
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class ListsViewModel : ViewModel() {
    var lists by mutableStateOf<List<ShreeList>>(emptyList())
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var loading by mutableStateOf(true)
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                lists = Supabase.client.from("lists").select().decodeList<ShreeList>()
            } catch (e: Exception) {
                error = e.message ?: e.toString()
            } finally {
                loading = false
            }
        }
    }
}

@Composable
fun ListsScreen(inner: PaddingValues, vm: ListsViewModel = viewModel()) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(inner)
            .padding(horizontal = 18.dp),
    ) {
        Spacer(Modifier.height(18.dp))
        Text("Our lists", color = DeepBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("live from Supabase", color = DeepBlue.copy(alpha = 0.6f), fontSize = 13.sp)
        Spacer(Modifier.height(16.dp))

        when {
            vm.loading -> Box(Modifier.fillMaxWidth().padding(top = 40.dp), Alignment.Center) {
                CircularProgressIndicator(color = SkyBlue)
            }
            vm.error != null -> ListCard {
                Text("Couldn't load", color = DeepBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Text(vm.error!!, color = DeepBlue.copy(alpha = 0.7f), fontSize = 13.sp)
            }
            else -> Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                vm.lists.forEach { list ->
                    ListCard {
                        Text(list.name, color = DeepBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(list.kind, color = SkyBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
                if (vm.lists.isEmpty()) {
                    ListCard { Text("No lists yet", color = DeepBlue.copy(alpha = 0.7f)) }
                }
            }
        }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun ListCard(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
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
