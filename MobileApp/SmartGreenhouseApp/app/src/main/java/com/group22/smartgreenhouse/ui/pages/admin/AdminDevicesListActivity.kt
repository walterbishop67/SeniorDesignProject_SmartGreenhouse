package com.group22.smartgreenhouse.ui.pages.admin

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.data.model.ElectronicCard
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.text.contains

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDevicesListActivity(
    navController: NavHostController,
    viewModel: AdminDevicesListViewModel = viewModel(
        factory = AdminDevicesListViewModelFactory(LocalContext.current)
    )
) {
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.refreshDevices()
    }

    // Handle session expiration
    LaunchedEffect(uiState.error) {
        if (uiState.error == "Session expired") {
            navController.navigate("login") { popUpTo(0) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Devices List",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.green)
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
                if (uiState.error != null && uiState.error != "Session expired") {
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissError() },
                        title = { Text("Error") },
                        text = { Text(uiState.error) },
                        confirmButton = {
                            Button(onClick = { viewModel.dismissError() }) {
                                Text("OK")
                            }
                        }
                    )
                }

                // Search bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search device") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Available", "Unavailable", "Error").forEach { filter ->
                        FilterChip(
                            selected = uiState.selectedFilter == filter,
                            onClick = { viewModel.updateFilter(filter) },
                            label = { Text(filter) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filteredDevices = uiState.devices.filter { device ->
                        device.productName?.contains(uiState.searchQuery, ignoreCase = true) == true ||
                                device.id.toString().contains(uiState.searchQuery)
                    }

                    itemsIndexed(filteredDevices) { index, device ->
                        if (index == filteredDevices.size - 1) {
                            LaunchedEffect(Unit) {
                                viewModel.loadNextPage()
                            }
                        }
                        DeviceItem(device = device)
                    }

                    if (uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceItem(device: ElectronicCard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (device.status) {
                "Available" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                "Unavailable" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                "Error" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = device.productName ?: "Device ${device.id}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "ID: ${device.id}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (device.greenHouseId != null) {
                Text(
                    text = "Greenhouse ID: ${device.greenHouseId}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Status: ${device.status}",
                style = MaterialTheme.typography.bodySmall
            )
            if (device.errorState != null) {
                Text(
                    text = "Error State: ${device.errorState}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}