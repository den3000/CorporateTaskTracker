package ru.den.writes.code.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = koinViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToTask: (Int) -> Unit,
    onNavigateToSettings: () -> Unit
) {
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
        Text(
            text = "Список корпоративных задач",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { onNavigateToTask(1) }) {
            Text("Открыть задачу №1")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onNavigateToTask(42) }) {
            Text("Открыть задачу №42")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNavigateToSettings) {
            Text("Настройки (Смена темы) ⚙️")
        }
    }
}
