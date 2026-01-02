package com.group22.smartgreenhouse.ui.pages.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.group22.smartgreenhouse.R
import kotlin.let
import kotlin.text.isNotBlank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSupportMessageDetailActivity(
    navController: NavHostController,
    viewModel: AdminSupportMessageDetailViewModel = viewModel(
        factory = SupportMessageViewModelFactory(LocalContext.current)
    ),
    messageId: Int
) {
    if (messageId <= 0) {
        Text("Invalid message ID", modifier = Modifier.padding(16.dp))
        return
    }

    val uiState = viewModel.uiState.collectAsState().value
    var responseText by remember { mutableStateOf("") }

    // Pre-fill existing response if available
    LaunchedEffect(uiState.message) {
        uiState.message?.messageResponse?.let {
            responseText = it
        }
    }

    LaunchedEffect(messageId) {
        if (messageId > 0) {
            viewModel.loadMessage(messageId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Message Detail") },
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
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", color = Color.Red)
            } else {
                uiState.message?.let { message ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = message.subject,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("From: ${message.createdBy}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Date: ${message.created}")
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Message Content:",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = message.messageContent,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (message.isResponsed) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Current Response:",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(message.messageResponse ?: "")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Always show response field, regardless of isResponsed status
                    OutlinedTextField(
                        value = responseText,
                        onValueChange = { responseText = it },
                        label = { Text(if (message.isResponsed) "Update response" else "Your response") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (responseText.isNotBlank() && !responseText.equals(message.messageResponse)) {
                                viewModel.submitResponse(messageId, responseText)
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(if (message.isResponsed) "Update Response" else "Submit Response")
                    }
                }
            }
        }
    }
}