package com.example.traindriver.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.traindriver.data.util.ResultState
import kotlinx.coroutines.flow.*
import java.io.IOException

operator fun Long?.times(other: Long?): Long? =
    if (this != null && other != null) {
        this * other
    } else {
        null
    }

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "train_driver_pref")

class DataStoreRepository(context: Context) {

    private val oneHourInMillis = 3_600_000L
    private val defaultTimeRest = oneHourInMillis * 3
    private val defaultDieselCoefficient = 0.83
    private val defaultTypeLoco = true

    private object PreferencesKey {
        val isRegistered = booleanPreferencesKey(name = "is_registered")
        val uid = stringPreferencesKey(name = "uid")
        val minTimeRest = longPreferencesKey(name = "minTimeRest")
        val dieselCoefficient = doublePreferencesKey(name = "dieselCoefficient")
        val typeLoco = booleanPreferencesKey(name = "typeLoco")
    }

    private val dataStore = context.dataStore

    private suspend fun saveStateIsRegistered(value: Boolean) {
        dataStore.edit { pref ->
            pref[PreferencesKey.isRegistered] = value
        }
    }

    suspend fun readIsRegisteredState(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[PreferencesKey.isRegistered] ?: false
    }

    suspend fun saveUid(value: String) {
        try {
            dataStore.edit { pref ->
                pref[PreferencesKey.uid] = value
            }
            saveStateIsRegistered(true)
        } catch (e: Exception) {
            saveStateIsRegistered(false)
        }
    }

    suspend fun setMinTimeRest(time: Int): Flow<ResultState<Boolean>> =
        callbackFlow {
            trySend(ResultState.Loading())
            try {
                dataStore.edit { pref ->
                    pref[PreferencesKey.minTimeRest] = time * oneHourInMillis
                }
                trySend(ResultState.Success(true))
            } catch (e: Exception) {
                trySend(ResultState.Failure(e))
            }
        }

    suspend fun getMinTimeRest(): Flow<Long> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { pref ->
                pref[PreferencesKey.minTimeRest] ?: defaultTimeRest
            }
    }

    suspend fun setTypeLoco(typeLoco: Boolean): Flow<ResultState<Boolean>> =
        callbackFlow {
            trySend(ResultState.Loading())
            try {
                dataStore.edit { pref ->
                    pref[PreferencesKey.typeLoco] = typeLoco
                }
                trySend(ResultState.Success(true))
            } catch (e: Exception) {
                trySend(ResultState.Failure(e))
            }
        }

    suspend fun getTypeLoco(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }

            }
            .map { pref ->
                pref[PreferencesKey.typeLoco] ?: defaultTypeLoco
            }
    }

    suspend fun clearUid() {
        dataStore.edit { pref ->
            pref.minusAssign(PreferencesKey.uid)
        }
        saveStateIsRegistered(false)
    }

    fun readUid(): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { pref ->
                pref[PreferencesKey.uid]
            }
    }

    fun readDieselCoefficient(): Flow<Double> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { pref ->
                pref[PreferencesKey.dieselCoefficient] ?: defaultDieselCoefficient
            }
    }

    suspend fun saveDieselCoefficient(coefficient: Double): Flow<ResultState<Boolean>> =
        callbackFlow {
            trySend(ResultState.Loading(null))
            try {
                dataStore.edit { pref ->
                    pref[PreferencesKey.dieselCoefficient] = coefficient
                }
                trySend(ResultState.Success(data = true))
            } catch (e: Exception) {
                trySend(ResultState.Failure(e))
            }
        }
}