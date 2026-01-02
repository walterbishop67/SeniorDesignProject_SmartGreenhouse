// AdminMunicipalityListActivity.kt
package com.group22.smartgreenhouse.ui.pages.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.group22.smartgreenhouse.data.model.Municipality
import com.group22.smartgreenhouse.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MunicipalityListActivity(
    navController: NavHostController,
    viewModel: MunicipalityListViewModel = viewModel(
        factory = MunicipalityListViewModelFactory(LocalContext.current)
    )
) {
    val municipalities = viewModel.municipalityList
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    var searchQuery by remember { mutableStateOf("") }

    // Handle session expiration
    LaunchedEffect(errorMessage) {
        if (errorMessage == "Session expired") {
            navController.navigate("login") {
                popUpTo(0)
            }
        }
    }

    fun navigateToMunicipalityDetail(municipality: Municipality) {
        navController.navigate("municipality_detail/${municipality.id}")
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
                            text = "Municipality Management",
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
                if (errorMessage != null && errorMessage != "Session expired") {
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

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search municipalities") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    val filteredMunicipalities = municipalities.filter {
                        it.municipalityName.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredMunicipalities) { municipality ->
                        MunicipalityCard(
                            municipality = municipality,
                            onCardClick = {
                                navigateToMunicipalityDetail(municipality)
                            }
                        )
                    }

                    if (isLoading) {
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
fun MunicipalityCard(municipality: Municipality, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationCity, contentDescription = null)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = municipality.municipalityName,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}