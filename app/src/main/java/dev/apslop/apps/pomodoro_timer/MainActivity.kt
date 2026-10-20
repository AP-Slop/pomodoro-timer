package dev.apslop.apps.pomodoro_timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.apslop.apps.pomodoro_timer.ui.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                PomodoroScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    modifier: Modifier = Modifier,
    viewModel: PomodoroViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
    ) { innerPadding ->
        PomodoroContent(
            state = state,
            onToggle = viewModel::toggle,
            onReset = viewModel::reset,
            onSkip = viewModel::skip,
            onClearRounds = viewModel::clearRounds,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun PomodoroContent(
    state: TimerUiState,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onSkip: () -> Unit,
    onClearRounds: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = if (state.phase == Phase.WORK) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(
                    if (state.phase == Phase.WORK) R.string.phase_work else R.string.phase_break
                ),
                style = MaterialTheme.typography.titleLarge,
                color = accent,
            )
            Text(
                text = stringResource(R.string.cycle_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.size(240.dp),
                color = accent,
                strokeWidth = 14.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatMillis(state.remainingMillis),
                    style = MaterialTheme.typography.displayMedium,
                    fontFamily = FontFamily.Monospace,
                )
                Text(
                    text = stringResource(
                        if (state.isRunning) R.string.status_running else R.string.status_paused
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(onClick = onToggle, modifier = Modifier.weight(1f)) {
                Text(stringResource(if (state.isRunning) R.string.pause else R.string.start))
            }
            OutlinedButton(onClick = onReset) {
                Text(stringResource(R.string.reset))
            }
            OutlinedButton(onClick = onSkip) {
                Text(stringResource(R.string.skip))
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.rounds_label),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = stringResource(R.string.rounds_value, state.completedRounds),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                TextButton(onClick = onClearRounds, enabled = state.completedRounds > 0) {
                    Text(stringResource(R.string.clear_rounds))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PomodoroContentPreview() {
    AppTheme {
        PomodoroContent(
            state = TimerUiState(
                phase = Phase.WORK,
                remainingMillis = 12L * 60L * 1000L + 34L * 1000L,
                totalMillis = Phase.WORK.durationMillis,
                isRunning = true,
                completedRounds = 3,
            ),
            onToggle = {},
            onReset = {},
            onSkip = {},
            onClearRounds = {},
        )
    }
}
