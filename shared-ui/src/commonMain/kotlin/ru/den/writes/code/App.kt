package ru.den.writes.code

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import ru.den.writes.code.di.appModule
import ru.den.writes.code.di.platformModule
import ru.den.writes.code.navigation.AppNavigation
import ru.den.writes.code.ui.components.AppFab
import ru.den.writes.code.ui.components.AppTopBar
import ru.den.writes.code.ui.settings.SettingsViewModel
import ru.den.writes.code.ui.theme.AppTheme
import org.koin.core.KoinApplication as KoinAppDeclaration

expect fun Modifier.fillMaxSizeModifierWithKbdHandling(): Modifier

@Composable
@Preview
fun App(
    koinConfig: KoinAppDeclaration.() -> Unit = {}
) {
    KoinApplication(application = {
        modules(
            platformModule,
            appModule,
        )
        koinConfig()
    }) {
        val settingsViewModel: SettingsViewModel = koinViewModel()
        val useDarkTheme = settingsViewModel.isDarkThemeOverride
            .collectAsState()
            .value ?: isSystemInDarkTheme()

        AppTheme(darkTheme = useDarkTheme) {
            val navController = rememberNavController()

            Scaffold(
                topBar = { AppTopBar(navController) },
                floatingActionButton = { AppFab(navController) }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSizeModifierWithKbdHandling()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                ) {
                    AppNavigation(
                        navController = navController,
                        settingsViewModel = settingsViewModel,
                        isDarkTheme = useDarkTheme
                    )
                }
            }
        }
    }
}
