package com.group22.smartgreenhouse.ui.pages.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.ui.pages.settings.SupportActivityViewModel.SendState
import com.group22.smartgreenhouse.ui.theme.BarGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportActivity(
    token: String,
    navController: NavHostController,
) {
    val title = remember { mutableStateOf("") }
    val desc  = remember { mutableStateOf("") }

    val context = LocalContext.current
    val viewModel: SupportActivityViewModel = viewModel(
        factory = SupportViewModelFactory(token, context)
    )
    val snackbarHost = remember { SnackbarHostState() }
    val uiState = viewModel.state.value

    LaunchedEffect(uiState) {
        if (uiState is SendState.Success) {
            Toast
                .makeText(context, "Ticket sent successfully!", Toast.LENGTH_SHORT)
                .show()

            title.value = ""
            desc.value  = ""

            navController.popBackStack("settings", inclusive = false)
        } else if (uiState is SendState.Error){
            Toast
                .makeText(context, "Sending failed", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // ─── Top bar ────────────────────────────────────────────────────────────────
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = { Text("Support Center") },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create Support Ticket", fontSize = 18.sp)

            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = desc.value,
                onValueChange = { desc.value = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                maxLines = 10
            )

            // Submit button
            val buttonEnabled =
                title.value.isNotBlank() &&
                        desc.value .isNotBlank() &&
                        uiState !is SendState.Sending

            Button(
                onClick = {
                    viewModel.sendMessage(token, title.value, desc.value)
                },
                enabled = buttonEnabled,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BarGreen),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                if (uiState is SendState.Sending) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Submit Ticket", color = Color.White)
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SupportActivityPreview() {
    // Dummy token for preview
    val fakeToken = "preview‑token"

    // NavController required by the composable
    val navController = rememberNavController()

    // Render the screen
    SupportActivity(
        token = fakeToken,
        navController = navController
    )
}

