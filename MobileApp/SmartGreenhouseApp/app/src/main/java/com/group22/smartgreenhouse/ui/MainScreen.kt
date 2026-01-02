package com.group22.smartgreenhouse.ui

import AdminUserListActivity
import HomeActivity
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.data.NavItem
import com.group22.smartgreenhouse.data.UserRole
import com.group22.smartgreenhouse.data.model.AgriProductPrice
import com.group22.smartgreenhouse.data.model.User
import com.group22.smartgreenhouse.ui.pages.admin.AdminHomeActivity
import com.group22.smartgreenhouse.ui.pages.AiTrackerActivity
import com.group22.smartgreenhouse.ui.pages.GreenhouseDetailActivity
import com.group22.smartgreenhouse.ui.pages.admin.AdminDevicesListActivity
import com.group22.smartgreenhouse.ui.pages.admin.AdminSupportMessageDetailActivity
import com.group22.smartgreenhouse.ui.pages.admin.AdminSupportMessagesListActivity
import com.group22.smartgreenhouse.ui.pages.admin.AdminUserDetailActivity
import com.group22.smartgreenhouse.ui.pages.admin.CalculatePriceActivity
import com.group22.smartgreenhouse.ui.pages.admin.MunicipalityListActivity
import com.group22.smartgreenhouse.ui.pages.prices.MunicipalityDetailActivity
import com.group22.smartgreenhouse.ui.pages.prices.PriceCalculationActivity
import com.group22.smartgreenhouse.ui.pages.prices.ProductSelectionActivity

import com.group22.smartgreenhouse.ui.pages.settings.AboutActivity
import com.group22.smartgreenhouse.ui.pages.settings.AccountSettingsActivity
import com.group22.smartgreenhouse.ui.pages.settings.SettingsActivity
import com.group22.smartgreenhouse.ui.pages.settings.SupportActivity
import com.group22.smartgreenhouse.ui.pages.settings.UserSupportMessagesListActivity
import com.group22.smartgreenhouse.ui.theme.BarGreen
import com.group22.smartgreenhouse.util.SessionManager


@SuppressLint("RememberReturnType")
@Composable
fun MainScreen(
    userRole: UserRole,
    navController: NavHostController = rememberNavController(),
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {

    /*val navItemList = listOf(
        NavItem("Home", R.drawable.baseline_home_24, "home"),
        NavItem("AI Tracker", R.drawable.baseline_camera_alt_24, "ai_tracker"),
        NavItem("Settings", R.drawable.baseline_settings_24, "settings")
    )

     */

    val navItemList = remember(userRole) {
        when (userRole) {
            UserRole.ADMIN -> listOf(
                NavItem("Home", R.drawable.baseline_home_24, "home"),
                NavItem("Users", R.drawable.baseline_person_24, "admin_user"),
                NavItem("Devices", R.drawable.baseline_select_all_24, "admin_devices"),
                NavItem("Support", R.drawable.baseline_support_agent_24, "admin_support"),
                NavItem("Settings", R.drawable.baseline_settings_24, "settings")
            )

            UserRole.USER -> listOf(
                NavItem("Home", R.drawable.baseline_home_24, "home"),
                NavItem("Current Prices", R.drawable.baseline_list_alt_24, "current_prices_municipality"),
                NavItem("Calculate", R.drawable.baseline_iso_24, "calculate_price"),
                NavItem("Settings", R.drawable.baseline_settings_24, "settings")
            )
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = BarGreen,
                contentColor = Color.White
            ) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = currentRoute == navItem.route,
                        onClick = {
                            navController.navigate(navItem.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(navItem.icon),
                                contentDescription = navItem.label
                            )
                        },
                        label = { Text(text = navItem.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                if (userRole == UserRole.ADMIN) {
                    val token = SessionManager.jwtToken ?: ""
                    AdminHomeActivity(navController = navController, token = token) // <-- Admin screen
                } else {
                    HomeActivity(navController = navController) // <-- Normal user screen
                }
            }
            composable("ai_tracker") { AiTrackerActivity() }
            composable("settings") { SettingsActivity(navController = navController,onLogout=onLogout) }
            composable("support") {
                val token = SessionManager.jwtToken ?: ""
                SupportActivity(token = token, navController = navController)
            }
            composable("about") { AboutActivity(navController) }
            composable("account_settings") {
                val token = SessionManager.jwtToken ?: return@composable
                AccountSettingsActivity(token, navController)
            }

            composable("greenhouse_detail/{greenhouseId}") { backStackEntry ->
                val greenhouseId = backStackEntry.arguments?.getString("greenhouseId") ?: ""
                GreenhouseDetailActivity(
                    navController = navController,
                    greenhouseId = greenhouseId
                )
            }
            composable("current_prices_municipality") {
                MunicipalityListActivity(navController = navController)
            }
            composable("municipality_detail/{municipalityId}") { backStackEntry ->
                val municipalityId = backStackEntry.arguments?.getString("municipalityId")?.toIntOrNull() ?: 0
                MunicipalityDetailActivity(navController, municipalityId)
            }
            composable("calculate_price") {
                CalculatePriceActivity(navController)
            }
            composable("product_select/{municipalityId}") { backStackEntry ->
                val municipalityId = backStackEntry.arguments?.getString("municipalityId")?.toIntOrNull() ?: 0
                ProductSelectionActivity(navController, municipalityId)
            }
            composable("price_calculating") { backStackEntry ->
                val json = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedProductsJson")
                val selectedProducts: List<AgriProductPrice> = if (!json.isNullOrEmpty()) {
                    Gson().fromJson(json, Array<AgriProductPrice>::class.java).toList()
                } else {
                    emptyList()
                }

                PriceCalculationActivity(
                    navController = navController,
                    selectedProducts = selectedProducts
                )
            }
            // Add to your NavGraph:
            composable("user_notifications") {
                UserSupportMessagesListActivity(navController)
            }


            //Admin
            composable("admin_user") {
                AdminUserListActivity(navController = navController)
            }
            composable(
                "admin_user_detail/{user}",
                arguments = listOf(
                    navArgument("user") { type = NavType.StringType }
                )) { navBackStackEntry ->
                navBackStackEntry?.arguments?.getString("user").let { json ->
                    val user = Gson().fromJson(json, User::class.java)
                    AdminUserDetailActivity(user,navController)
                }

            }
            composable("admin_devices") {
                AdminDevicesListActivity(navController = navController)
            }
            composable("admin_support") {
                AdminSupportMessagesListActivity(navController = navController)
            }

            composable(
                "support_message_detail/{messageId}",
                arguments = listOf(navArgument("messageId") { type = NavType.IntType })
            ) { backStackEntry ->
                val messageId = backStackEntry.arguments?.getInt("messageId") ?: -1
                if (messageId > 0) {  // Only proceed with valid IDs
                    AdminSupportMessageDetailActivity(navController, messageId = messageId)
                } else {
                    // Handle invalid ID case - perhaps navigate back with error
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                        // Optionally show snackbar/error
                    }
                }
            }

        }
    }
}
