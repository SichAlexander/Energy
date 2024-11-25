package com.uzhnu.availabilitymonitoring.data.datastorage

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class UserPreferences(
    val uuid: String?
)

class UserPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val USER_UUID = stringPreferencesKey("user_uuid")
    }

    val uuid: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.USER_UUID]
        }

    suspend fun updateUserUuid(uuid: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_UUID] = uuid
        }
    }

    suspend fun clearUserUuid() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_UUID)
        }
    }

    suspend fun fetchInitialPreferences() =
        mapUserPreferences(dataStore.data.first().toPreferences())


    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val uuid = preferences[PreferencesKeys.USER_UUID]
        return UserPreferences(uuid)
    }


    companion object {
        private const val TAG: String = "UserPreferencesRepo"
    }
}

