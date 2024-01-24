package tech.pacia.notes.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class TokenStore(
    private val dataStore: DataStore<Preferences>,
) {
    private object PreferencesKeys {
        val USER_ACCESS_TOKEN = stringPreferencesKey("user_access_token")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    fun accessToken(): String? {
        return runBlocking { dataStore.data.first()[PreferencesKeys.USER_ACCESS_TOKEN] }
    }

    suspend fun persistToken(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ACCESS_TOKEN] = token
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_ACCESS_TOKEN)
            preferences.remove(PreferencesKeys.USER_EMAIL)
        }
    }
}
