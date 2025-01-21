package de.levithas.aixdroid.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF00FF),
    onPrimary = Color(0xFFAAFFFF),
    primaryContainer = Color(0xFF0000FF),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF0000FF),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFAA00FF),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFFFFF),
    onTertiaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFAAAAFF),
    onPrimary = Color(0xFF003333),
    primaryContainer = Color(0xAA3333FF),
    onPrimaryContainer = Color(0xFF0000FF),
    secondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFAA00FF),
    onSecondaryContainer = Color(0xFF0000FF),
    tertiary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFAAAAFF),
    onTertiaryContainer = Color(0xFF0000FF),
    background = Color(0xFFAAAAAA),
    onBackground = Color(0xFF000000)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

data class CustomColors @OptIn(ExperimentalMaterial3Api::class) constructor(
    val topAppBarColors: TopAppBarColors,
    val addIconButtonColors: IconButtonColors,
    val itemSelectedCard: CardColors,
    val itemCard: CardColors,
    val standardButton: ButtonColors
)


val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current

private val LocalCustomColors = staticCompositionLocalOf<CustomColors> {
    error("No CustomColors defined!")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiXDroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalCustomColors provides CustomColors(
        topAppBarColors = TopAppBarColors(
            containerColor = colorScheme.surfaceVariant,
            scrolledContainerColor = colorScheme.surfaceVariant,
            navigationIconContentColor = colorScheme.primary,
            titleContentColor = colorScheme.primary,
            actionIconContentColor = colorScheme.secondary,
        ),
        itemSelectedCard = CardColors(
            containerColor = colorScheme.errorContainer,
            contentColor = colorScheme.error,
            disabledContainerColor = colorScheme.errorContainer,
            disabledContentColor = colorScheme.error
        ),
        itemCard = CardColors(
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.onPrimary,
            disabledContainerColor = colorScheme.tertiaryContainer,
            disabledContentColor = colorScheme.onTertiary
        ),
        addIconButtonColors = IconButtonColors(
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.primary,
            disabledContainerColor = colorScheme.tertiaryContainer,
            disabledContentColor = colorScheme.tertiary
        ),
        standardButton = ButtonColors(
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.primary,
            disabledContainerColor = colorScheme.tertiaryContainer,
            disabledContentColor = colorScheme.tertiary
        )
    )) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = shapes,
            content = content
        )
    }

}