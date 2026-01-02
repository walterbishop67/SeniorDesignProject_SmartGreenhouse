package com.group22.smartgreenhouse.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.util.SessionManager
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenhouseDetailActivity(
    navController: NavController,
    greenhouseId: String,
    viewModel: GreenhouseDetailViewModel = viewModel()
) {
    val token = SessionManager.jwtToken ?: ""
    val coroutineScope = rememberCoroutineScope()
    val expanded = remember { mutableStateOf(false) }
    val greenhouseDetails = viewModel.greenhouseDetails.value
    val isLoading = viewModel.isLoading.value

    val showDeleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = greenhouseId) {
        viewModel.fetchGreenhouseDetails(token, greenhouseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.green),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("My Support Messages")
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { expanded.value = true }
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    expanded.value = false
                                    // navController.navigate("settings")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Greenhouse") },
                                onClick = {
                                    expanded.value = false
                                    showDeleteDialog.value = true
                                }
                            )
                        }
                    }
                }
            )
        }

    ) { innerPadding ->
        if (showDeleteDialog.value) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog.value = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete this greenhouse?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog.value = false
                            coroutineScope.launch {
                                viewModel.deleteGreenhouse(token, greenhouseId)
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog.value = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    greenhouseDetails?.let { details ->
                        SensorCard(
                            icon = Icons.Default.InvertColors,
                            title = "Humidity",
                            value = details.humidity ?: "No data"
                        )

                        Spacer(Modifier.height(16.dp))

                        SensorCard(
                            icon = Icons.Default.LocalFireDepartment,
                            title = "Temperature",
                            value = details.temperature ?: "No data"
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                /*
                Text(
                    text = "My Plants",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(8.dp))

                // Example static list of plants
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlantCard("Plant 1")
                    PlantCard("Plant 2")
                }

                 */
            }
        }
    }
}


@Composable
fun SensorCard(icon: ImageVector, title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAEAEA)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF73C084),
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(text = title, color = Color.Gray, fontSize = 14.sp)
                Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}


/*
@Composable
fun PlantCard(name: String) {
    Card(
        modifier = Modifier
            .size(100.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = name)
        }
    }
}
 */

@Preview(showBackground = true)
@Composable
fun GreenhouseDetailPreview() {
    GreenhouseDetailActivity(
        navController = rememberNavController(),
        greenhouseId = "1"
    )
}