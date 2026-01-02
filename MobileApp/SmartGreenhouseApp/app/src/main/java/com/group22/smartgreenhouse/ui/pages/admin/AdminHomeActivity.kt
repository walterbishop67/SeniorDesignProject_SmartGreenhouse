package com.group22.smartgreenhouse.ui.pages.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.ui.theme.BarGreen

// AdminHomeActivity.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeActivity(navController: NavHostController, token: String) {
    val ctx = LocalContext.current
    val vm: AdminHomeViewModel = viewModel(factory = AdminHomeViewModelFactory(ctx))
    val userState = vm.userState.value
    val cardState = vm.cardState.value

    LaunchedEffect(Unit) { vm.loadData(token) }

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
                            text = "Dashboard",
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
            when {
                userState is AdminHomeViewModel.UserState.Loading ||
                        cardState is AdminHomeViewModel.CarState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BarGreen
                    )
                }
                userState is AdminHomeViewModel.UserState.Error -> {
                    Text(
                        text = (userState as AdminHomeViewModel.UserState.Error).msg,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
                cardState is AdminHomeViewModel.CarState.Error -> {
                    Text(
                        text = (cardState as AdminHomeViewModel.CarState.Error).msg,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // User Stats Section
                        Text(
                            text = "User Statistics",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (userState is AdminHomeViewModel.UserState.Data) {
                            val userData = (userState as AdminHomeViewModel.UserState.Data).dto
                            AdminStatCard(Icons.Default.Person, "Total Users", count = userData.totalUserCount)
                            userData.roleCounts.forEach { (role, count) ->
                                AdminStatCard(
                                    icon = if (role.contains("Admin", ignoreCase = true))
                                        Icons.Default.PersonPin
                                    else
                                        Icons.Default.Person,
                                    title = role,
                                    count = count
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Car Stats Section
                        Text(
                            text = "Electronic Card Statistics",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (cardState is AdminHomeViewModel.CarState.Data) {
                            val carData = (cardState as AdminHomeViewModel.CarState.Data).dto
                            AdminStatCard(Icons.Default.PermDeviceInformation, "Total Cards", count = carData.totalCount)
                            AdminStatCard(Icons.Default.PermDeviceInformation, "Available Cards", count = carData.availableCount)
                            AdminStatCard(Icons.Default.DeviceUnknown, "Unavailable Cards", count = carData.unavailableCount)
                            AdminStatCard(Icons.Default.Error, "Error Cards", count = carData.errorCount)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun AdminStatCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    count: Int
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF73C084)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    subtitle?.let {
                        Text(text = it, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminHomeActivityPreview() {
    val navController = rememberNavController()   // dummy controller for preview

    //AdminHomeActivity(navController = navController)
}

