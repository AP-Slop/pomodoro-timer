package dev.apslop.apps.pomodoro_timer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.pomodoroDataStore: DataStore<Preferences> by preferencesDataStore(name = "pomodoro_prefs")

/** 完了したポモドーロの回数を DataStore Preferences に保存する。 */
class RoundStore(context: Context) {

    private val dataStore = context.applicationContext.pomodoroDataStore

    val completedRounds: Flow<Int> = dataStore.data.map { prefs -> prefs[KEY_COMPLETED_ROUNDS] ?: 0 }

    suspend fun incrementCompletedRounds() {
        dataStore.edit { prefs ->
            prefs[KEY_COMPLETED_ROUNDS] = (prefs[KEY_COMPLETED_ROUNDS] ?: 0) + 1
        }
    }

    suspend fun clearCompletedRounds() {
        dataStore.edit { prefs ->
            prefs[KEY_COMPLETED_ROUNDS] = 0
        }
    }

    private companion object {
        val KEY_COMPLETED_ROUNDS = intPreferencesKey("completed_rounds")
    }
}
