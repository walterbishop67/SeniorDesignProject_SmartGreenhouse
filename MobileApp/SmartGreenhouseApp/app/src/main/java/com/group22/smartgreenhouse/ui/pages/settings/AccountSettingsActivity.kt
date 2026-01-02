package com.group22.smartgreenhouse.ui.pages.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.group22.smartgreenhouse.ui.theme.BarGreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import com.group22.smartgreenhouse.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsActivity(
    token: String,
    navController: NavHostController
) {
    val ctx = LocalContext.current
    val viewModel: AccountSettingsActivityViewModel = viewModel(
        factory = AccountSettingsViewModelFactory(ctx)
    )
    val uiState by viewModel.state

    val cur = remember { mutableStateOf("") }
    val new = remember { mutableStateOf("") }
    val cfm = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadUserInfo(token)
    }

    /* toast on success/error */
    LaunchedEffect(uiState) {
        when (uiState) {
            is AccountSettingsActivityViewModel.UiState.Success -> {
                Toast.makeText(ctx, (uiState as AccountSettingsActivityViewModel.UiState.Success).msg,
                    Toast.LENGTH_SHORT).show()
                navController.popBackStack()          // close page
            }
            is AccountSettingsActivityViewModel.UiState.Error -> {
                Toast.makeText(ctx, (uiState as AccountSettingsActivityViewModel.UiState.Error).msg,
                    Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Settings") },
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
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val userInfo = viewModel.userInfo.value

            if (userInfo != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Account Info", fontSize = 16.sp, color = Color.Gray)
                    Text("Name: ${userInfo.firstName} ${userInfo.lastName}")
                    Text("Username: ${userInfo.userName}")
                    Text("Email: ${userInfo.email}")
                }
            }
            Text("Change Password", fontSize = 16.sp, color = Color.Gray)
            OutlinedTextField(
                value = cur.value, onValueChange = { cur.value = it },
                label = { Text("Current password") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = new.value, onValueChange = { new.value = it },
                label = { Text("New password") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cfm.value, onValueChange = { cfm.value = it },
                label = { Text("Confirm new password") }, modifier = Modifier.fillMaxWidth()
            )

            val enabled = cur.value.isNotBlank() && new.value.isNotBlank() && cfm.value.isNotBlank()
                    && uiState !is AccountSettingsActivityViewModel.UiState.Loading

            Button(
                onClick = { viewModel.change(token, cur.value, new.value, cfm.value) },
                enabled = enabled,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = BarGreen)
            ) {
                if (uiState is AccountSettingsActivityViewModel.UiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp)
                    )
                } else Text("Save", color = Color.White)
            }
        }
    }
}
