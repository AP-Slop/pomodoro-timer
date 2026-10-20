package dev.apslop.apps.pomodoro_timer

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.apslop.apps.pomodoro_timer.data.RoundStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** 画面に表示するタイマーの状態。 */
data class TimerUiState(
    val phase: Phase = Phase.WORK,
    val remainingMillis: Long = Phase.WORK.durationMillis,
    val totalMillis: Long = Phase.WORK.durationMillis,
    val isRunning: Boolean = false,
    val completedRounds: Int = 0,
) {
    /** 円形プログレス用の進捗（残り時間の割合）。 */
    val progress: Float
        get() = if (totalMillis <= 0L) 0f else (remainingMillis.toFloat() / totalMillis).coerceIn(0f, 1f)
}

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {

    private val roundStore = RoundStore(application)

    private val _state = MutableStateFlow(TimerUiState())
    val state: StateFlow<TimerUiState> = _state.asStateFlow()

    private var ticker: Job? = null

    init {
        viewModelScope.launch {
            roundStore.completedRounds.collect { rounds ->
                _state.update { it.copy(completedRounds = rounds) }
            }
        }
    }

    fun toggle() {
        if (_state.value.isRunning) pause() else start()
    }

    fun start() {
        if (ticker?.isActive == true) return
        _state.update { it.copy(isRunning = true) }
        ticker = viewModelScope.launch {
            var last = SystemClock.elapsedRealtime()
            while (isActive) {
                delay(TICK_MILLIS)
                val now = SystemClock.elapsedRealtime()
                val delta = (now - last).coerceAtLeast(0L)
                last = now
                advance(delta)
            }
        }
    }

    fun pause() {
        ticker?.cancel()
        ticker = null
        _state.update { it.copy(isRunning = false) }
    }

    /** 作業フェーズ（25:00）からやり直す。完了ラウンド数は保持する。 */
    fun reset() {
        pause()
        _state.update {
            it.copy(
                phase = Phase.WORK,
                remainingMillis = Phase.WORK.durationMillis,
                totalMillis = Phase.WORK.durationMillis,
            )
        }
    }

    /** 今のフェーズを飛ばして次へ進む。完了ラウンド数は増やさない。 */
    fun skip() {
        val next = _state.value.phase.next
        _state.update {
            it.copy(
                phase = next,
                remainingMillis = next.durationMillis,
                totalMillis = next.durationMillis,
            )
        }
    }

    fun clearRounds() {
        viewModelScope.launch { roundStore.clearCompletedRounds() }
    }

    private fun advance(deltaMillis: Long) {
        val current = _state.value
        val remaining = current.remainingMillis - deltaMillis
        if (remaining > 0L) {
            _state.update { it.copy(remainingMillis = remaining) }
            return
        }

        val finished = current.phase
        if (finished == Phase.WORK) {
            viewModelScope.launch { roundStore.incrementCompletedRounds() }
        }
        val next = finished.next
        _state.update {
            it.copy(
                phase = next,
                remainingMillis = next.durationMillis,
                totalMillis = next.durationMillis,
            )
        }
    }

    private companion object {
        const val TICK_MILLIS = 250L
    }
}
