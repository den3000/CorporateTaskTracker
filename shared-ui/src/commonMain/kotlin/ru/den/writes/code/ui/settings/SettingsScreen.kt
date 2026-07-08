package ru.den.writes.code.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.den.writes.code.resources.Res
import ru.den.writes.code.resources.btn_click_me
import ru.den.writes.code.resources.btn_switch_dark
import ru.den.writes.code.resources.btn_switch_light
import ru.den.writes.code.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.den.writes.code.Greeting
import ru.den.writes.code.ui.theme.AppTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    isDarkTheme: Boolean = false,
) {
    val showContent by viewModel.showContent.collectAsState()

    SettingsContent(
        isDarkTheme = isDarkTheme,
        showContent = showContent,
        onToggleTheme = { viewModel.toggleTheme(isDarkTheme) },
        onToggleContent = viewModel::toggleContent
    )
}

@Composable
fun SettingsContent(
    isDarkTheme: Boolean,
    showContent: Boolean,
    onToggleTheme: (Boolean) -> Unit = {},
    onToggleContent: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { onToggleTheme(isDarkTheme) },
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

        Button(onClick = onToggleContent) {
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

@Preview
@Composable
fun SettingsContentPreviewLight() {
    AppTheme {
        SettingsContent(
            isDarkTheme = false,
            showContent = false
        )
    }
}

@Preview
@Composable
fun SettingsContentPreviewDark() {
    AppTheme(true) {
        SettingsContent(
            isDarkTheme = true,
            showContent = false
        )
    }
}
