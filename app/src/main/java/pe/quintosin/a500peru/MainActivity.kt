package pe.quintosin.a500peru

import pe.quintosin.a500peru.ui.theme.A500PeruTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import android.widget.Toast
import android.app.role.RoleManager
import android.os.Build
import androidx.compose.foundation.background

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("500_prefs", Context.MODE_PRIVATE)
        setContent {
            A500PeruTheme {
                var isEnabled by remember { mutableStateOf(true) }
                Column(Modifier.fillMaxSize().padding(20.dp).background(androidx.compose.ui.graphics.Color.White)) {
                    Text(
                        "500 PERU - SI VES ESTO YA FUNCIONA",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val rm = getSystemService(RoleManager::class.java)
                            if (!rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                                startActivityForResult(
                                    rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING),
                                    1001
                                )
                            } else {
                                Toast.makeText(this@MainActivity, "Ya activo", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }) { Text("Activar filtro") }
                    Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
                    Text("Si ves este texto, el tema ya está bien.")
                }
            }
        }
    }
}