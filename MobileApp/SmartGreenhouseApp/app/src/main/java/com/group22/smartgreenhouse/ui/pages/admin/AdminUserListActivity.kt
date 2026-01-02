import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.model.User
import com.group22.smartgreenhouse.data.repository.AdminRepository
import com.group22.smartgreenhouse.ui.pages.admin.AdminUserListViewModelFactory
import kotlin.collections.any
import kotlin.collections.filter
import kotlin.text.contains

// AdminUserListActivity.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserListActivity(
    navController: NavHostController,
    viewModel: AdminUserListViewModel = viewModel(
        factory = AdminUserListViewModelFactory(LocalContext.current)
    )
) {
    val users = viewModel.userList
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    var searchQuery by remember { mutableStateOf("") }

    fun navigateToUser(user: User) {
        val userJson = Gson().toJson(user)
        navController.navigate("admin_user_detail/$userJson")
    }


    // Handle session expiration
    LaunchedEffect(errorMessage) {
        if (errorMessage == "Session expired") {
            navController.navigate("login") {
                popUpTo(0)
            }
        }
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
                            text = "User Management",
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
                    label = { Text("Search users") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    val filteredUsers = users.filter {
                        it.userName.contains(searchQuery, ignoreCase = true) ||
                                it.email.contains(searchQuery, ignoreCase = true)
                    }

                    itemsIndexed(filteredUsers) { index, user ->
                        if (index == filteredUsers.size - 1) {
                            LaunchedEffect(Unit) {
                                viewModel.loadNextPage()
                            }
                        }

                        UserCard(user = user) { user ->
                            navigateToUser(user)
                        }
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
fun UserCard(user: User, onUserClick: (User) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onUserClick(user) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, contentDescription = null)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "@${user.userName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            UserRolesChips(roles = user.roles)
        }
    }
}

@Composable
fun UserRolesChips(roles: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Role indicator with chip-like appearance
        val isAdmin = roles.any { it.contains("admin", ignoreCase = true) }
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


/*
@Composable
fun UserRolesChips(roles: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Determine if user is admin or basic
        val isAdmin = roles.any { it.contains("admin", ignoreCase = true) }
        roles.forEach { role ->
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text(role) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
 */