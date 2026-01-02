package com.group22.smartgreenhouse.ui.pages.prices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.data.model.AgriProductPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSelectionActivity(
    navController: NavHostController,
    municipalityId: Int,
    viewModel: MunicipalityDetailViewModel = viewModel(
        factory = MunicipalityDetailViewModelFactory(LocalContext.current, municipalityId)
    )
) {
    val productPrices = viewModel.productPrices
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    var selectedProducts by remember { mutableStateOf<List<AgriProductPrice>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Products") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedProducts.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                val gson = Gson()
                                val json = gson.toJson(selectedProducts) // Convert list to JSON string
                                navController.currentBackStackEntry?.savedStateHandle?.set("selectedProductsJson", json)

                                navController.navigate("price_calculating") {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Text("Calculate", color = Color.White)
                        }
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (errorMessage != null) {
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissError() },
                        title = { Text("Error") },
                        text = { Text(errorMessage) },
                        confirmButton = {
                            Button(onClick = { viewModel.dismissError() }) {
                                Text("OK")
                            }
                        }
                    )
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn {
                        productPrices?.data?.let { prices ->
                            items(count = prices.size) { index ->
                                val price = prices[index]
                                SelectableProductCard(
                                    price = price,
                                    isSelected = selectedProducts.contains(price),
                                    onSelectionChange = { selected ->
                                        selectedProducts = if (selected) {
                                            selectedProducts + price
                                        } else {
                                            selectedProducts - price
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectableProductCard(
    price: AgriProductPrice,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelectionChange(!isSelected) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = price.agriProductName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Price: ${price.agriProductPrice} TL/unit")
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange
            )
        }
    }
}