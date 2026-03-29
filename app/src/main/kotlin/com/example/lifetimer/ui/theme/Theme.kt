package com.mercangel.lifetimer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary              = Purple40,
    onPrimary            = Neutral99,
    primaryContainer     = Purple90,
    onPrimaryContainer   = Purple10,
    secondary            = Teal40,
    onSecondary          = Neutral99,
    secondaryContainer   = Teal90,
    onSecondaryContainer = Teal40,
    tertiary             = Orange40,
    onTertiary           = Neutral99,
    tertiaryContainer    = Orange90,
    onTertiaryContainer  = Orange40,
    error                = Red40,
    onError              = Neutral99,
    errorContainer       = Red90,
    onErrorContainer     = Red40,
    background           = Neutral99,
    onBackground         = Neutral10,
    surface              = Neutral99,
    onSurface            = Neutral10,
    surfaceVariant       = NeutralVariant90,
    onSurfaceVariant     = NeutralVariant30,
    outline              = NeutralVariant30
)

private val DarkColorScheme = darkColorScheme(
    primary              = Purple80,
    onPrimary            = Purple20,
    primaryContainer     = Purple30,
    onPrimaryContainer   = Purple90,
    secondary            = Teal80,
    onSecondary          = Teal40,
    secondaryContainer   = Teal40,
    onSecondaryContainer = Teal90,
    tertiary             = Orange80,
    onTertiary           = Orange40,
    tertiaryContainer    = Orange40,
    onTertiaryContainer  = Orange90,
    error                = Red80,
    onError              = Red40,
    errorContainer       = Red40,
    onErrorContainer     = Red90,
    background           = Neutral10,
    onBackground         = Neutral90,
    surface              = Neutral10,
    onSurface            = Neutral90,
    surfaceVariant       = NeutralVariant30,
    onSurfaceVariant     = NeutralVariant80,
    outline              = NeutralVariant80
)

@Composable
fun LifeTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
