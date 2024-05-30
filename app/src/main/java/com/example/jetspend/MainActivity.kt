package com.example.jetspend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetspend.ui.theme.JetspendTheme

data class Wydatki(val id: Int, val name: String, val amount: Int, val category: String)

class WydatkiViewModel : ViewModel() {
    private var _wydatki = mutableStateListOf<Wydatki>()
    val wydatki: List<Wydatki> get() = _wydatki

    fun addWydatki(wydatek: Wydatki) {
        _wydatki.add(wydatek)
    }

    fun removeWydatki(wydatek: Wydatki) {
        _wydatki.remove(wydatek)
    }

    fun sortByName() {
        _wydatki.sortBy { it.name }
    }

    fun sortByAmount() {
        _wydatki.sortBy { it.amount }
    }

    fun sortByCategory() {
        _wydatki.sortBy { it.category }
    }

    fun sortById() {
        _wydatki.sortBy { it.id }
    }

    fun sumWydatki(): Int {
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

    var nazwa by remember { mutableStateOf("")}
    var kwota by remember { mutableStateOf("") }
    var kategoria by remember { mutableStateOf("") }
    var menuToggle by remember { mutableStateOf(false)}

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(!menuToggle){
                Text("Jetspend", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(20.dp))
                if(wydatki.isNotEmpty()){
                    HeaderRow(
                        onSortByName = { viewModel.sortByName() },
                        onSortByAmount = { viewModel.sortByAmount() },
                        onSortByCategory = { viewModel.sortByCategory() },
                        onSortById = { viewModel.sortById() }
                    )
                }
                else{
                    Text("Nie posiadasz jeszcze żadnych wpisów.", fontSize = 25.sp, textAlign = TextAlign.Center, lineHeight = 25.sp)
                }
                ListaWydatkow(wydatki = wydatki, onRemoveWydatki = { viewModel.removeWydatki(it) })
                if(wydatki.isNotEmpty()){
                    FooterRow(suma)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { menuToggle = true }) {
                    Text("Dodaj nowy wpis")
                }
            }else{
                Text("Wpisz do kosztorysu", fontSize = 30.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = nazwa,
                    onValueChange = { nazwa = it },
                    label = { Text("Nazwa") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = kwota,
                    onValueChange = { value -> kwota = value.filter { it.isDigit() } },
                    label = { Text("Kwota") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = kategoria,
                    onValueChange = { kategoria = it },
                    label = { Text("Kategoria") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row{
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Button(onClick = {
                            if (nazwa.isNotBlank() && kwota.isNotBlank() && kategoria.isNotBlank()) {
                                val amount = kwota.toIntOrNull()
                                if (amount != null) {
                                    viewModel.addWydatki(
                                        Wydatki(
                                            id = wydatki.size,
                                            name = nazwa,
                                            amount = amount,
                                            category = kategoria
                                        )
                                    )
                                    nazwa = ""
                                    kwota = ""
                                    kategoria = ""
                                    menuToggle = false
                                }
                            }
                        }) {
                            Text("Dodaj")
                        }
                    }
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ){
                        Button(onClick = {menuToggle = false}){
                            Text("Wróć")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderRow(
    onSortByName: () -> Unit,
    onSortByAmount: () -> Unit,
    onSortByCategory: () -> Unit,
    onSortById: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Button(
                onClick = { onSortByName() }
            ) {
                Text(
                    text = "Nazwa",
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Button(
                onClick = { onSortByAmount() }
            ) {
                Text(
                    text = "Kwota",
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Button(
                onClick = { onSortByCategory() }
            ) {
                Text(
                    text = "Kategoria",
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Button(
                onClick = { onSortById() }
            ) {
                Text(
                    text = "ID",
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun FooterRow(suma: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Łączna suma Twoich wydatków: $suma.",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
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
        Text(
            text = wydatek.name,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "${wydatek.amount}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = wydatek.category,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(
            onClick = { onRemoveWydatki(wydatek) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Usuń")
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
