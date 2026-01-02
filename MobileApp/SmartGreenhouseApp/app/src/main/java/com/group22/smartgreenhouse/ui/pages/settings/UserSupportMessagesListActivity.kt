package com.group22.smartgreenhouse.ui.pages.settings


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.data.model.SupportMessage
import com.group22.smartgreenhouse.ui.pages.admin.SupportMessageItem
import com.group22.smartgreenhouse.ui.pages.admin.SupportMessageViewModelFactory
import kotlin.collections.isNotEmpty
import kotlin.let
import kotlin.text.isNullOrBlank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSupportMessagesListActivity(
    navController: NavHostController
) {
    val viewModel: UserSupportMessagesListViewModel = viewModel(
        factory = UserSupportMessagesListViewModelFactory(LocalContext.current)
    )


    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Support Messages") },
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Filter buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.messages.isEmpty(),
                    onClick = { viewModel.setResponseFilter(ResponseFilter.ALL) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = uiState.messages.isNotEmpty(),
                    onClick = { viewModel.setResponseFilter(ResponseFilter.ANSWERED) },
                    label = { Text("Answered") }
                )
                FilterChip(
                    selected = uiState.messages.isNotEmpty(),
                    onClick = { viewModel.setResponseFilter(ResponseFilter.UNANSWERED) },
                    label = { Text("Unanswered") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Message list
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = { viewModel.refreshMessages() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Retry")
                    }
                }
                uiState.messages.isEmpty() -> {
                    Text(
                        text = "No messages found",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.messages) { message ->
                            UserSupportMessageItem(
                                message = message,
                                onDelete = { messageId -> viewModel.deleteMessage(messageId) }
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun UserSupportMessageItem(
    message: SupportMessage,
    onDelete: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (message.isOpened) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {

                // 🗑️ Delete button (top right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { onDelete(message.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete message")
                    }
                }

                // Message subject
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

                if (message.isResponsed && !message.messageResponse.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Response:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = message.messageResponse!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // ✅ Status indicator (bottom right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (message.isResponsed) Color.Green else Color.Red,
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
            }
        }
    }
}
