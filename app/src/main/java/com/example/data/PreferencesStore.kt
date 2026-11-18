package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "travelguide_prefs")

class PreferencesStore(private val context: Context) {

    private object Keys {
        val HAS_ONBOARDED = booleanPreferencesKey("has_onboarded")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_GUEST = booleanPreferencesKey("is_guest")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val AUTH_PROVIDER = stringPreferencesKey("auth_provider")
        val LANGUAGE = stringPreferencesKey("app_language")
        val SEEDED_DEFAULTS = booleanPreferencesKey("seeded_defaults")
        val NOTIF_ENABLED = booleanPreferencesKey("notif_enabled")
    }

    val userSession: Flow<UserSession> = context.dataStore.data.map { prefs ->
        UserSession(
            isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: false,
            isGuest = prefs[Keys.IS_GUEST] ?: false,
            hasCompletedOnboarding = prefs[Keys.HAS_ONBOARDED] ?: false,
            name = prefs[Keys.USER_NAME] ?: "Amit Verma",
            email = prefs[Keys.USER_EMAIL] ?: "amit@email.com",
            phone = prefs[Keys.USER_PHONE] ?: "+91 98765 43210",
            authProvider = prefs[Keys.AUTH_PROVIDER] ?: "Guest"
        )
    }

    val language: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        if (prefs[Keys.LANGUAGE] == AppLanguage.HINDI.code) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.NOTIF_ENABLED] ?: true
    }

    val hasSeededDefaults: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SEEDED_DEFAULTS] ?: false
    }

    suspend fun markOnboardingCompleted() {
        context.dataStore.edit { it[Keys.HAS_ONBOARDED] = true }
    }

    suspend fun setAuthenticatedUser(
        name: String,
        email: String,
        phone: String,
        provider: String
    ) {
        context.dataStore.edit {
            it[Keys.HAS_ONBOARDED] = true
            it[Keys.IS_LOGGED_IN] = true
            it[Keys.IS_GUEST] = false
            it[Keys.USER_NAME] = name
            it[Keys.USER_EMAIL] = email
            it[Keys.USER_PHONE] = phone
            it[Keys.AUTH_PROVIDER] = provider
        }
    }

    suspend fun continueAsGuest() {
        context.dataStore.edit {
            it[Keys.HAS_ONBOARDED] = true
            it[Keys.IS_LOGGED_IN] = false
            it[Keys.IS_GUEST] = true
            it[Keys.AUTH_PROVIDER] = "Guest"
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[Keys.IS_LOGGED_IN] = false
            it[Keys.IS_GUEST] = false
        }
    }

    suspend fun toggleLanguage(lang: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = lang.code }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIF_ENABLED] = enabled }
    }

    suspend fun markDefaultsSeeded() {
        context.dataStore.edit { it[Keys.SEEDED_DEFAULTS] = true }
    }

    suspend fun resetAllAccountData() {
        context.dataStore.edit {
            it[Keys.IS_LOGGED_IN] = false
            it[Keys.IS_GUEST] = false
            it[Keys.USER_NAME] = "Amit Verma"
            it[Keys.USER_EMAIL] = "amit@email.com"
            it[Keys.USER_PHONE] = "+91 98765 43210"
        }
    }
}
