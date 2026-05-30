package com.example.lifeledger.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtils {

    fun applyTheme(
        context: Context
    ) {

        val prefs =
            context.getSharedPreferences(
                "ThemePrefs",
                Context.MODE_PRIVATE
            )

        when(
            prefs.getString(
                "theme",
                ThemePreference.SYSTEM
            )
        ) {

            ThemePreference.LIGHT -> {

                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }

            ThemePreference.DARK -> {

                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            }

            else -> {

                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
            }
        }
    }
}