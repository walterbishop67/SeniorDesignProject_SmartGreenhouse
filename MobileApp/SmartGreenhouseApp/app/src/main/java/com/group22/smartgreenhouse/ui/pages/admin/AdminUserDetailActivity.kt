package com.group22.smartgreenhouse.ui.pages.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.data.api.AdminApi
import com.group22.smartgreenhouse.data.model.ElectronicCard
import com.group22.smartgreenhouse.data.model.User
import com.group22.smartgreenhouse.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserDetailActivity(
    user: User,
    navController: NavHostController
) {

    val context = LocalContext.current
    val viewModel: UserDetailViewModel = viewModel(
        factory = UserDetailViewModelFactory(context)
    )
    val token = SessionManager.jwtToken.toString()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect states
    val showDialog = viewModel.showDialog.collectAsState().value
    val devices = viewModel.devices.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val snackbarMessage = viewModel.snackbarMessage.collectAsState().value

    // Initial load
    LaunchedEffect(user.id) {
        viewModel.loadUserDevices(user.id, token)
    }


    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            title = { Text("Add New Device") },
            text = { Text("Are you sure you want to add a new device to this user?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.addDeviceForUser(user.id, token) }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissDialog() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.green),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDeviceDialog() },
                containerColor = colorResource(R.color.green), // custom green to match UI
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Device")
            }
        }
    ) { padding ->
        LaunchedEffect(snackbarMessage) {
            snackbarMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.clearSnackbarMessage()
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // User details section
            item {
                UserDetailCard(
                    user = user,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Devices",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Loading/empty state
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (devices.isEmpty()) {
                item {
                    Text(
                        text = "No devices found",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // Correct way to use items with a list
                items(count = devices.size) { index ->
                    DeviceCard(device = devices[index])
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserDetailCard(
    user: User,
    modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = modifier) {
            Text("User ID: ${user.id}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Name: ${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.titleMedium)
            Text("Username: @${user.userName}",
                style = MaterialTheme.typography.bodyMedium)
            Text("Email: ${user.email}",
                style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Role: ", style = MaterialTheme.typography.labelLarge)

                // Role indicator with chip-like appearance
                val isAdmin = user.roles.any { it.contains("admin", ignoreCase = true) }
                val roleText = if (isAdmin) "Admin" else "Basic"
                val containerColor = MaterialTheme.colorScheme.primaryContainer

                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(containerColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = roleText,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }


        }
    }
}

@Composable
fun DevicesList(devices: List<ElectronicCard>) {
    Column {
        devices.forEach { device ->
            DeviceCard(device = device)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DeviceCard(device: ElectronicCard) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Device ID: ${device.id}", style = MaterialTheme.typography.bodyMedium)
            Text("Status: ${device.status ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium)

        }
    }
}
