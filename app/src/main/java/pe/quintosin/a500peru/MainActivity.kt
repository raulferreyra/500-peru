package pe.quintosin.a500peru

import pe.quintosin.a500peru.ui.theme.A500PeruTheme
import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("500_prefs", Context.MODE_PRIVATE)

        setContent {
            A500PeruTheme {
                var blockingEnabled by remember { mutableStateOf(prefs.getBoolean("blocking_enabled", true)) }
                var isPremium by remember { mutableStateOf(prefs.getBoolean("is_premium", false)) }
                var blockedCount by remember { mutableStateOf(prefs.getInt("blocked_count", 0)) }
                var selectedTab by remember { mutableStateOf(0) }
                val whitelist = remember { mutableStateListOf<String>().apply { addAll(prefs.getStringSet("whitelist", emptySet()) ?: emptySet()) } }
                val logs = remember { mutableStateListOf<String>().apply { addAll(prefs.getStringSet("logs", emptySet()) ?: emptySet()) } }

                fun save() {
                    prefs.edit()
                        .putBoolean("blocking_enabled", blockingEnabled)
                        .putBoolean("is_premium", isPremium)
                        .putInt("blocked_count", blockedCount)
                        .putStringSet("whitelist", whitelist.toSet())
                        .putStringSet("logs", logs.toSet())
                        .apply()
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Default.List, null) }, label = { Text("Log") })
                            NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Default.Star, null) }, label = { Text("Whitelist") })
                        }
                    }
                ) { padding ->
                    Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                        // HEADER
                        Text("500 Perú", style = MaterialTheme.typography.headlineMedium)
                        Text(if (isPremium) "Premium Activo - Bloqueo ilimitado" else "Gratis: $blockedCount / 5 bloqueos", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    val rm = getSystemService(RoleManager::class.java)
                                    if (!rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                                        startActivityForResult(rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING), 1001)
                                    } else Toast.makeText(this@MainActivity, "Ya eres filtro activo", Toast.LENGTH_SHORT).show()
                                }
                            }) { Text("Activar filtro") }
                            Switch(checked = blockingEnabled, onCheckedChange = { blockingEnabled = it; save() })
                        }

                        // SUSCRIPCION - solo si no es premium
                        if (!isPremium) {
                            Card(Modifier.fillMaxWidth().padding(vertical = 12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Desbloquea bloqueo ilimitado", style = MaterialTheme.typography.titleSmall)
                                    Text("Gratis solo bloquea 5 llamadas. Pasa a Premium.", style = MaterialTheme.typography.bodySmall)
                                    Spacer(Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(onClick = { isPremium = true; blockedCount = 0; save(); Toast.makeText(this@MainActivity, "Premium Mensual activado (simulado)", Toast.LENGTH_SHORT).show() }) { Text("Mensual S/4.90") }
                                        OutlinedButton(onClick = { isPremium = true; blockedCount = 0; save(); Toast.makeText(this@MainActivity, "Premium Anual activado (simulado)", Toast.LENGTH_SHORT).show() }) { Text("Anual S/29.90") }
                                    }
                                }
                            }
                        }

                        HorizontalDivider()

                        // CONTENIDO DE TABS
                        if (selectedTab == 0) {
                            Text("Llamadas bloqueadas", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top=8.dp))
                            if (logs.isEmpty()) Text("Aún no hay bloqueos.", modifier = Modifier.padding(top=8.dp))
                            LazyColumn {
                                items(logs.toList()) { logEntry ->
                                    val parts = logEntry.split("|")
                                    val num = parts.getOrNull(0) ?: logEntry
                                    val fecha = parts.getOrNull(1) ?: ""
                                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column { Text(num, style = MaterialTheme.typography.bodyLarge); Text(fecha, style = MaterialTheme.typography.bodySmall) }
                                            TextButton(onClick = {
                                                if (!whitelist.contains(num)) { whitelist.add(num); save(); Toast.makeText(this@MainActivity, "$num a whitelist", Toast.LENGTH_SHORT).show() }
                                            }) { Text("Whitelist") }
                                        }
                                    }
                                }
                            }
                        } else {
                            var newNum by remember { mutableStateOf("") }
                            Text("Whitelist - nunca bloquear", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top=8.dp))
                            Row(Modifier.padding(vertical = 8.dp)) {
                                OutlinedTextField(value = newNum, onValueChange = { newNum = it }, label = { Text("Ej: 500123456") }, modifier = Modifier.weight(1f))
                                Spacer(Modifier.width(8.dp))
                                Button(onClick = { if (newNum.isNotBlank()) { whitelist.add(newNum.trim()); newNum = ""; save() } }) { Text("Add") }
                            }
                            LazyColumn {
                                items(whitelist.toList()) { w ->
                                    Card(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                        Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(w); TextButton(onClick = { whitelist.remove(w); save() }) { Text("Quitar") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}