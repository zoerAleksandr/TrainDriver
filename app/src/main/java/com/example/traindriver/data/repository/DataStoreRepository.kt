package com.example.traindriver.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "train_driver_pref")

class DataStoreRepository(context: Context) {

    private object PreferencesKey {
        val isRegistered = booleanPreferencesKey(name = "is_registered")
        val uid = stringPreferencesKey(name = "uid")
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
}