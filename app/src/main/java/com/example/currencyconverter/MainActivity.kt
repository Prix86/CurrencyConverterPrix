package com.example.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.currencyconverter.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterApp()
        }
    }
}

class MainViewModel : ViewModel() {
    var currencyRates by mutableStateOf(emptyMap<String, Double>())
    var baseCurrency by mutableStateOf("MXN")
    var convertedAmount by mutableStateOf(0.0)
    var showResult by mutableStateOf(false)

    fun updateConvertedAmount(amount: Double) {
        convertedAmount = amount
    }

    init {
        viewModelScope.launch {
            delay(1000)
            currencyRates = mapOf(
                "USD" to 0.058656,
                "ARS" to 52.42480,
                "JPY" to 9.233741,
                "KRW" to 80.7688,
                "CAD" to 0.080504,
                "GBP" to 0.046208,
                "CNY" to 0.425217,
                "EUR" to 0.0543266
            )
        }
    }
}

@Composable
fun CurrencyConverterApp(viewModel: MainViewModel = viewModel()) {
    var amount by remember { mutableStateOf("") }
    var targetCurrency by remember { mutableStateOf("USD") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(end = 4.dp)
                )
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto en ${viewModel.baseCurrency}") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            CurrencyDropdown(
                currencies = viewModel.currencyRates.keys.toList(),
                selectedCurrency = targetCurrency,
                onCurrencySelected = { targetCurrency = it }
            )

            Button(onClick = {
                val baseCurrencyRate = viewModel.currencyRates[viewModel.baseCurrency] ?: 1.0
                val targetCurrencyRate = viewModel.currencyRates[targetCurrency] ?: 1.0
                val amountValue = amount.toDoubleOrNull()

                if (amountValue != null) {
                    val convertedAmountValue = amountValue / baseCurrencyRate * targetCurrencyRate
                    viewModel.updateConvertedAmount(convertedAmountValue)
                    viewModel.showResult = true
                    keyboardController?.hide()
                } else {
                  
                }
            }) {
                Text("Convertir")
            }

            if (viewModel.showResult) {
                Text(
                    text = "Monto Convertido: $${viewModel.convertedAmount} $targetCurrency",
                    style = MaterialTheme.typography.h6,
                    color = Color.Green // Cambiar el color del texto a verde
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.tipocambio),
                contentDescription = "imagentipocambio",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

@Composable
fun CurrencyDropdown(
    currencies: List<String>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Column {
            Text("Convertir a: ")
            OutlinedButton(onClick = { expanded = true }) {
                Text(text = selectedCurrency)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                Column {
                    currencies.forEach { currency ->
                        DropdownMenuItem(onClick = {
                            onCurrencySelected(currency)
                            expanded = false
                        }) {
                            Text(text = currency)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrencyConverterApp() {
    CurrencyConverterApp()
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrencyDropdown() {
    CurrencyDropdown(
        currencies = listOf("USD", "EUR", "GBP"),
        selectedCurrency = "USD",
        onCurrencySelected = {}
    )
}
