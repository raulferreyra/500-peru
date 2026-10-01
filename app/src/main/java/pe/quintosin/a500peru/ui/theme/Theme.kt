package pe.quintosin.a500peru.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulTecnologia,
    onPrimary = Color.White,
    primaryContainer = AzulTecnologia,
    onPrimaryContainer = Color.White,
    secondary = Naranja,
    onSecondary = Color.White,
    secondaryContainer = Naranja.copy(alpha=0.15f),
    onSecondaryContainer = AzulTecnologia,
    background = FondoClaro,
    surface = Color.White,
    error = Color(0xFFD32F2F)
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulClaro,
    onPrimary = Color.White,
    secondary = Naranja,
    onSecondary = Color.White,
    background = AzulOscuro,
    surface = AzulTecnologia
)

@Composable
fun A500PeruTheme(
    darkTheme: Boolean = false, // forzamos claro para tu marca
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}