package com.daveleeds.arrowdemo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.daveleeds.arrowdemo.screens.WrestlerEditScreen
import com.daveleeds.arrowdemo.screens.WrestlerList
import com.daveleeds.arrowdemo.theme.darkScheme
import com.daveleeds.arrowdemo.theme.lightScheme
import com.daveleeds.arrowdemo.viewmodel.WrestlerEditStatus
import com.daveleeds.arrowdemo.viewmodel.WrestlerEditUiState
import com.daveleeds.arrowdemo.viewmodel.WrestlerEditViewModel
import com.daveleeds.arrowdemo.viewmodel.WrestlerListViewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    navController: NavHostController = rememberNavController(),
    darkTheme: Boolean = true,
) {
    MaterialTheme(colorScheme = if (darkTheme) darkScheme else lightScheme) {
        Surface(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Routes.WrestlerList
            ) {
                composable<Routes.WrestlerList> {
                    val viewModel: WrestlerListViewModel = viewModel { WrestlerListViewModel() }
                    val uiState by viewModel.uiState.collectAsState()

                    WrestlerList(
                        uiState = uiState,
                        onWrestlerChosen = { id -> navController.navigate(Routes.WrestlerEdit(id)) },
                        onRetry = { viewModel.refresh() }
                    )
                }

                composable<Routes.WrestlerEdit> { backStackEntry ->
                    val id = backStackEntry.toRoute<Routes.WrestlerEdit>().id

                    val viewModel: WrestlerEditViewModel = viewModel { WrestlerEditViewModel(id = id) }
                    val uiState: WrestlerEditUiState by viewModel.uiState.collectAsState()

                    WrestlerEditScreen(
                            uiState = uiState,
                            onSetName = { name -> viewModel.setName(name) },
                            onSetAge = { age -> viewModel.setAge(age) },
                            onSetWeight = { weight -> viewModel.setWeight(weight) },
                            onSetCity = { city -> viewModel.setCity(city) },
                            onSetCountry = { country -> viewModel.setCountry(country) },
                            onSaved = { wrestler ->
                                viewModel.save(wrestler)
                                navController.navigate(Routes.WrestlerList)
                            },
                            onBack = { navController.popBackStack() }
                        )
                }
            }
        }
    }
}

object Routes {
    @Serializable object WrestlerList
    @Serializable data class WrestlerEdit(val id: Int)
}
