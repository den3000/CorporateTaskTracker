package ru.den.writes.code.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import corporatetasktracker.composeapp.generated.resources.Res
import corporatetasktracker.composeapp.generated.resources.btn_click_me
import corporatetasktracker.composeapp.generated.resources.btn_switch_dark
import corporatetasktracker.composeapp.generated.resources.btn_switch_light
import corporatetasktracker.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ru.den.writes.code.Greeting

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    isDarkTheme: Boolean = false,
) {
    val showContent by viewModel.showContent.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { viewModel.toggleTheme(isDarkTheme) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(
                if (isDarkTheme) stringResource(Res.string.btn_switch_light)
                else stringResource(Res.string.btn_switch_dark)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Основная кнопка (тестовая)
        Button(onClick = { viewModel.toggleContent() }) {
            Text(stringResource(Res.string.btn_click_me))
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(showContent) {
            val greeting = remember { Greeting().greet() }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Compose: $greeting",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}