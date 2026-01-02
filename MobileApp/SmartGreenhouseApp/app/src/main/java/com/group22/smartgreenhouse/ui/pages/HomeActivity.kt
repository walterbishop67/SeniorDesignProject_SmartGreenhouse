import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.group22.smartgreenhouse.R
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group22.smartgreenhouse.data.model.ElectronicCard
import com.group22.smartgreenhouse.ui.pages.HomeActivityViewModel
import com.group22.smartgreenhouse.util.SessionManager
import kotlinx.coroutines.launch

data class Greenhouse(
    val id: String,
    val name: String,
    val imageRes: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeActivity(navController: NavController) {

    val viewModel: HomeActivityViewModel = viewModel()
    //val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJkZXJ5YTEyIiwianRpIjoiMTM1YmQ2ZmMtMjAzZS00ZTkzLTlhMDUtOTBlOGE0NjIwZWRmIiwiZW1haWwiOiJkZXJ5YWNheXJvZ2x1QGdtYWlsLmNvbSIsInVpZCI6IjE4OTEwNzZhLTAxZDctNGEzMi05ZmUyLWMyMjE4MWQ5MjJiNyIsImlwIjoiMTkyLjE2OC4xLjEwMiIsInJvbGVzIjoiQmFzaWMiLCJleHAiOjE3NDUwOTgwMjMsImlzcyI6IkNvcmVJZGVudGl0eSIsImF1ZCI6IkNvcmVJZGVudGl0eVVzZXIifQ.9_uBJHQtg0TtFDAKMYuwhVMWr1vLFjUE4OpTKgF349w"
    val token = SessionManager.jwtToken ?: ""

    LaunchedEffect(Unit) {
        viewModel.loadGreenhouses(token)
        viewModel.loadAvailableDevices(token)
    }

    // Sample data
    val greenhouses = viewModel.greenhouses

    val showAddDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF73C084), // same green
                    titleContentColor = Color.White
                ),
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "My Greenhouses",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {}, // remove nav icon
                actions = {}         // remove settings icon
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog.value = true },
                containerColor = Color(0xFF73C084), // custom green to match UI
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp) // match rounded corner of cards
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Greenhouse")
            }
        }
    ) { innerPadding ->
        if (greenhouses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No greenhouses added yet\nTap + to add one",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(greenhouses) { greenhouse ->
                    GreenhouseCard(
                        greenhouse = greenhouse,
                        onCardClick = {
                            navController.navigate("greenhouse_detail/${greenhouse.id}")
                        },
                    )
                }
            }
        }
    }
    val scope = rememberCoroutineScope()
    if (showAddDialog.value) {
        AddGreenhouseDialog(
            onDismiss = { showAddDialog.value = false },
            onConfirm = { name, type, area, code ->
                showAddDialog.value = false
                scope.launch {
                    val success = viewModel.addGreenhouse(token, name, type, area, code)
                    if (success) {
                        viewModel.loadAvailableDevices(token)
                        viewModel.loadGreenhouses(token)
                    }
                }
            },
            availableDevices = viewModel.availableDevices
        )
    }
}

@Composable
fun GreenhouseCard(
    greenhouse: Greenhouse,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .height(180.dp)
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = greenhouse.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun AddGreenhouseDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String, area: String, code: String) -> Unit,
    availableDevices: List<ElectronicCard>
) {
    val name = remember { mutableStateOf("") }
    val type = remember { mutableStateOf("") }
    val area = remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }
    val selectedDevice = remember { mutableStateOf<ElectronicCard?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Greenhouse") },
        text = {
            Column {
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Name") }
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = type.value,
                    onValueChange = { type.value = it },
                    label = { Text("Type") }
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = area.value,
                    onValueChange = { area.value = it },
                    label = { Text("Area") }
                )
                Spacer(Modifier.height(8.dp))

                // Improved Device Selection
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expanded.value = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedDevice.value?.let { "Device: ${it.id}" } ?: "Select Device",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = if (expanded.value) Icons.Default.ArrowDropUp
                                else Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availableDevices.forEach { device ->
                            DropdownMenuItem(
                                text = { Text("Device ${device.id}") },
                                onClick = {
                                    selectedDevice.value = device
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedDevice.value?.let {
                        onConfirm(name.value, type.value, area.value, it.id.toString())
                    }
                },
                enabled = name.value.isNotBlank() && selectedDevice.value != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeActivity(navController = rememberNavController())
    }
}