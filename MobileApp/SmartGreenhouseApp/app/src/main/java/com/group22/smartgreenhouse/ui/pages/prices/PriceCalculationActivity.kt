package com.group22.smartgreenhouse.ui.pages.prices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.data.model.AgriProductPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceCalculationActivity(
    navController: NavHostController,
    selectedProducts: List<AgriProductPrice>,
) {
    // State for quantities (initialize with 1.0 for each product)
    val quantities = remember { mutableStateMapOf<Int, Float>().apply {
        selectedProducts.forEach { product -> this[product.id] = 1.0f }
    }}

    LaunchedEffect(selectedProducts) {
        println("Received products: ${selectedProducts.size}")
    }

    // Calculate totals
    val productTotals = selectedProducts.associate { product ->
        product.id to (quantities[product.id] ?: 1.0f) * product.agriProductPrice
    }
    val grandTotal = productTotals.values.sum()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Price Calculation") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.green),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedProducts) { product ->
                    CalculationItemCard(
                        product = product,
                        quantity = quantities[product.id] ?: 1.0f,
                        onQuantityChange = { newQuantity ->
                            quantities[product.id] = newQuantity
                        },
                        totalPrice = productTotals[product.id] ?: 0f
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Total Price: ${"%.2f".format(grandTotal)} TL",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CalculationItemCard(
    product: AgriProductPrice,
    quantity: Float,
    onQuantityChange: (Float) -> Unit,
    totalPrice: Any
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product name at top
            Text(
                text = product.agriProductName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Price and quantity row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Unit price aligned left
                Text(
                    text = "Unit Price: ${product.agriProductPrice} TL",
                    modifier = Modifier.weight(1f)
                )

                // Quantity field aligned right
                Box(
                    modifier = Modifier.width(150.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    OutlinedTextField(
                        value = "%.1f".format(quantity),
                        onValueChange = {
                            val newValue = it.toFloatOrNull() ?: 1.0f
                            onQuantityChange(newValue)
                        },
                        label = { Text("Quantity") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.End
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Total price
            Text(
                text = "Total: ${"%.2f".format(totalPrice)} TL",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}