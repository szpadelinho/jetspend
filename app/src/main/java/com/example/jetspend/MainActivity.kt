package com.example.jetspend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetspend.ui.theme.JetspendTheme

data class Wydatki(val id: Int, val name: String, val amount: Double, val category: String)

class WydatkiViewModel : ViewModel() {
    private var _wydatki = mutableStateListOf<Wydatki>()
    val wydatki: List<Wydatki> get() = _wydatki

    fun addWydatki(wydatek: Wydatki) {
        _wydatki.add(wydatek)
    }

    fun removeWydatki(wydatek: Wydatki) {
        _wydatki.remove(wydatek)
    }

    fun sumWydatki(): Double {
        return _wydatki.sumOf { it.amount }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetspendTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ExpenseApp()
                }
            }
        }
    }
}

@Composable
fun ExpenseApp(viewModel: WydatkiViewModel = viewModel()) {
    val wydatki by remember { derivedStateOf { viewModel.wydatki } }
    val suma by remember { derivedStateOf { viewModel.sumWydatki() } }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Jetspend")
            Spacer(modifier = Modifier.height(20.dp))
            Text("Łączna suma wydatków: $suma")
            Spacer(modifier = Modifier.height(20.dp))

            ListaWydatkow(wydatki = wydatki, onRemoveWydatki = { viewModel.removeWydatki(it) })

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                viewModel.addWydatki(Wydatki(id = wydatki.size, name = "Sample", amount = 10.0, category = "Food"))
            }) {
                Text("Dodaj nowy wpis")
            }
        }
    }
}

@Composable
fun ListaWydatkow(wydatki: List<Wydatki>, onRemoveWydatki: (Wydatki) -> Unit) {
    LazyColumn {
        items(wydatki) { wydatek ->
            ExpenseItem(wydatek = wydatek, onRemoveWydatki = onRemoveWydatki)
        }
    }
}

@Composable
fun ExpenseItem(wydatek: Wydatki, onRemoveWydatki: (Wydatki) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = wydatek.name)
            Text(text = "${wydatek.amount}")
            Text(text = wydatek.category)
        }
        Button(onClick = { onRemoveWydatki(wydatek) }) {
            Text("Usuń")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    JetspendTheme {
        ExpenseApp()
    }
}