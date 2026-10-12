
package com.mad.kitabu.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mad.kitabu.ui.screens.CatalogScreen
import com.mad.kitabu.ui.screens.ReservationsScreen
import com.mad.kitabu.ui.viewmodel.LibraryViewModel

private val Navy = Color(0xFF172B4D)
private val Indigo = Color(0xFF526AA0)
private val Ivory = Color(0xFFF8F7F3)

@Composable
fun KitabuNavigation(
    viewModel: LibraryViewModel
) {
    val navController = rememberNavController()

    val backStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val message by viewModel.message.collectAsStateWithLifecycle()

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        containerColor = Ivory,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = currentRoute == "catalog",
                    onClick = {
                        navController.navigate("catalog") {
                            popUpTo("catalog") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        NavigationSymbol(
                            symbol = "▤",
                            selected = currentRoute == "catalog"
                        )
                    },
                    label = {
                        Text(
                            text = "Explore",
                            fontWeight =
                                if (currentRoute == "catalog")
                                    FontWeight.Bold
                                else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Indigo,
                        selectedTextColor = Navy,
                        indicatorColor = Color(0xFFE8EDF7),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "reservations",
                    onClick = {
                        navController.navigate("reservations") {
                            popUpTo("catalog") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        NavigationSymbol(
                            symbol = "▣",
                            selected = currentRoute == "reservations"
                        )
                    },
                    label = {
                        Text(
                            text = "My Library",
                            fontWeight =
                                if (currentRoute == "reservations")
                                    FontWeight.Bold
                                else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Indigo,
                        selectedTextColor = Navy,
                        indicatorColor = Color(0xFFE8EDF7),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "catalog",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("catalog") {
                CatalogScreen(viewModel)
            }

            composable("reservations") {
                ReservationsScreen(viewModel)
            }
        }
    }
}

@Composable
private fun NavigationSymbol(
    symbol: String,
    selected: Boolean
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(28.dp)
    ) {
        Text(
            text = symbol,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Indigo else Color.Gray
        )
    }
}
