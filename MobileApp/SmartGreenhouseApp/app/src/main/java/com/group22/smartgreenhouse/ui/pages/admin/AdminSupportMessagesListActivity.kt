package com.group22.smartgreenhouse.ui.pages.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
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
import com.group22.smartgreenhouse.data.model.SupportMessage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSupportMessagesListActivity(
    navController: NavHostController,
    viewModel: AdminSupportMessagesListViewModel = viewModel(
        factory = SupportMessageViewModelFactory(LocalContext.current)
    )
) {
    val uiState = viewModel.uiState.collectAsState().value
    var searchQuery by remember { mutableStateOf("") }

    // Handle session expiration
    LaunchedEffect(uiState.error) {
        if (uiState.error == "Session expired") {
            navController.navigate("login") { popUpTo(0) }
        }
        viewModel.refreshMessages()
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
                            text = "Support Messages",
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
                        text = { Text(uiState.error!!) },
                        confirmButton = {
                            Button(onClick = { viewModel.dismissError() }) {
                                Text("OK")
                            }
                        }
                    )
                }

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search messages") },
                    leadingIcon = {
                        androidx.compose.material3.Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.currentFilter == null,
                        onClick = { viewModel.setResponseFilter(null) },
                        label = { Text("All") }
                    )
                    FilterChip(
                        selected = uiState.currentFilter == ResponseFilter.ANSWERED,
                        onClick = { viewModel.setResponseFilter(ResponseFilter.ANSWERED) },
                        label = { Text("Answered") }
                    )
                    FilterChip(
                        selected = uiState.currentFilter == ResponseFilter.UNANSWERED,
                        onClick = { viewModel.setResponseFilter(ResponseFilter.UNANSWERED) },
                        label = { Text("Unanswered") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Filter messages based on available fields
                    val filteredMessages = uiState.messages.filter { message ->
                        message.subject.contains(searchQuery, ignoreCase = true) ||
                                message.createdBy.contains(searchQuery, ignoreCase = true)
                    }

                    itemsIndexed(filteredMessages) { index, message ->
                        if (index == filteredMessages.size - 1) {
                            LaunchedEffect(Unit) {
                                viewModel.loadNextPage()
                            }
                        }
                        SupportMessageItem(message = message) {
                            navController.navigate("support_message_detail/${message.id}")
                        }
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
fun SupportMessageItem(
    message: SupportMessage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (message.isOpened) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status indicator at the top right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (message.isResponsed) Color.Green
                            else Color.Red,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (message.isResponsed) "Answered" else "Unanswered",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Message content
            Text(
                text = message.subject,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (!message.isOpened) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "From: ${message.createdBy}",
                style = MaterialTheme.typography.bodySmall
            )
            message.created?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Created: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}