package pe.quintosin.a500peru.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import pe.quintosin.a500peru.R

// Si no tienes las fuentes aún, esto usará la default sin crashear
val OrgonFamily = try {
    FontFamily(
        Font(R.font.orgon_regular, FontWeight.Normal),
        Font(R.font.orgon_bold, FontWeight.Bold)
    )
} catch (e: Exception) { FontFamily.Default }

val LatoFamily = try {
    FontFamily(
        Font(R.font.lato_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.lato_bolditalic, FontWeight.Bold, FontStyle.Italic)
    )
} catch (e: Exception) { FontFamily.Default }

val Typography = Typography(
    headlineLarge = TextStyle(fontFamily = OrgonFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = AzulTecnologia),
    headlineMedium = TextStyle(fontFamily = OrgonFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = AzulTecnologia),
    titleMedium = TextStyle(fontFamily = OrgonFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AzulTecnologia),
    bodyLarge = TextStyle(fontFamily = LatoFamily, fontStyle = FontStyle.Italic, fontSize = 16.sp),
    bodySmall = TextStyle(fontFamily = LatoFamily, fontStyle = FontStyle.Italic, fontSize = 12.sp, color = AzulTecnologia.copy(alpha=0.7f))
)