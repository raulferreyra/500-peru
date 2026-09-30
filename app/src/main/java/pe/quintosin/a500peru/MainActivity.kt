package pe.quintosin.a500peru

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("500_prefs", Context.MODE_PRIVATE)

        setContent {
            var isEnabled by remember { mutableStateOf(prefs.getBoolean("blocking_enabled", true)) }
            var whitelistText by remember { mutableStateOf(prefs.getStringSet("whitelist", emptySet())?.joinToString("\n") ?: "") }

            MaterialTheme {
                Column(modifier = Modifier.padding(24.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("500 Perú - Bloqueo Local", style = MaterialTheme.typography.headlineSmall)
                    Text("Bloquea 100% offline todas las llamadas que empiecen con 500 (serie comercial MTC). Sin servidores.")

                    Button(onClick = { requestCallScreeningRole() }) {
                        Text("1. Activar como filtro de llamadas")
                    }

                    Row {
                        Text("Bloqueo activo: ")
                        Switch(checked = isEnabled, onCheckedChange = {
                            isEnabled = it
                            prefs.edit().putBoolean("blocking_enabled", it).apply()
                        })
                    }

                    OutlinedTextField(
                        value = whitelistText,
                        onValueChange = { whitelistText = it },
                        label = { Text("Lista Blanca (un número por línea)") },
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    )

                    Button(onClick = {
                        val set = whitelistText.lines().map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                        prefs.edit().putStringSet("whitelist", set).apply()
                        Toast.makeText(this@MainActivity, "Lista blanca guardada localmente", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Guardar Lista Blanca")
                    }

                    Text("Tu blindaje: Esta app no puede ser reclamada. El bloqueo lo hace el equipo del usuario, no un servidor.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    private fun requestCallScreeningRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                    startActivityForResult(intent, 1001)
                } else {
                    Toast.makeText(this, "Ya eres app de filtro activa", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // En Android 7, 8, 9 se activa en Ajustes > Apps > Acceso especial > ID de llamada y spam
            Toast.makeText(this, "Ve a Ajustes > Apps > Acceso especial > Filtro de llamadas y activa 500 Perú", Toast.LENGTH_LONG).show()
            val intent = Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            startActivity(intent)
        }
    }
}