package pe.quintosin.a500peru

import pe.quintosin.a500peru.ui.theme.*
import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("500_prefs", Context.MODE_PRIVATE)
        setContent {
            A500PeruTheme {
                var blockingEnabled by remember { mutableStateOf(prefs.getBoolean("blocking_enabled", true)) }
                var isPremium by remember { mutableStateOf(prefs.getBoolean("is_premium", true)) }
                var blockedCount by remember { mutableStateOf(prefs.getInt("blocked_count", 0)) }
                var selectedTab by remember { mutableStateOf(0) }
                val whitelist = remember { mutableStateListOf<String>().apply { addAll(prefs.getStringSet("whitelist", emptySet()) ?: emptySet()) } }
                val logs = remember { mutableStateListOf<String>().apply { addAll(prefs.getStringSet("logs", emptySet()) ?: emptySet()) } }
                var newNum by remember { mutableStateOf("") }

                fun save() {
                    prefs.edit().putBoolean("blocking_enabled", blockingEnabled).putBoolean("is_premium", isPremium).putInt("blocked_count", blockedCount).putStringSet("whitelist", whitelist.toSet()).putStringSet("logs", logs.toSet()).apply()
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = AzulTecnologia) {
                            NavigationBarItem(selected = selectedTab==0, onClick = { selectedTab=0 }, icon = { Icon(Icons.Default.List, null, tint = if(selectedTab==0) Naranja else androidx.compose.ui.graphics.Color.White) }, label = { Text("Log", color = androidx.compose.ui.graphics.Color.White, fontFamily = OrgonFamily) })
                            NavigationBarItem(selected = selectedTab==1, onClick = { selectedTab=1 }, icon = { Icon(Icons.Default.Star, null, tint = if(selectedTab==1) Naranja else androidx.compose.ui.graphics.Color.White) }, label = { Text("Whitelist", color = androidx.compose.ui.graphics.Color.White, fontFamily = OrgonFamily) })
                        }
                    }
                ) { padding ->
                    Column(Modifier.padding(padding).fillMaxSize().background(FondoClaro)) {
                        // HEADER PREMIUM
                        Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(AzulTecnologia, AzulOscuro))).padding(20.dp)) {
                            Column {
                                Text("500 Perú", fontFamily = OrgonFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = androidx.compose.ui.graphics.Color.White)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null, tint = if(isPremium) Naranja else androidx.compose.ui.graphics.Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(if(isPremium) "Premium Activo • Ilimitado" else "Gratis $blockedCount/5", color = androidx.compose.ui.graphics.Color.White.copy(0.8f), fontSize = 12.sp, fontFamily = LatoFamily)
                                }
                            }
                        }

                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // CARD ACTIVACION
                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(if(blockingEnabled) Naranja.copy(0.15f) else androidx.compose.ui.graphics.Color.Gray.copy(0.15f)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Lock, null, tint = if(blockingEnabled) Naranja else androidx.compose.ui.graphics.Color.Gray)
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text("Filtro activo", fontFamily = OrgonFamily, fontWeight = FontWeight.Bold, color = AzulTecnologia)
                                            Text(if(blockingEnabled) "Bloqueando serie 500" else "Pausado", fontSize = 12.sp, fontFamily = LatoFamily, color = AzulTecnologia.copy(0.6f))
                                        }
                                    }
                                    Switch(checked = blockingEnabled, onCheckedChange = { blockingEnabled=it; save() }, colors = SwitchDefaults.colors(checkedThumbColor = androidx.compose.ui.graphics.Color.White, checkedTrackColor = Naranja, uncheckedTrackColor = androidx.compose.ui.graphics.Color.LightGray))
                                }
                                Button(onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        val rm = getSystemService(RoleManager::class.java)
                                        if (!rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) startActivityForResult(rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING), 1001)
                                        else Toast.makeText(this@MainActivity, "Ya eres filtro", Toast.LENGTH_SHORT).show()
                                    }
                                }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom=16.dp), colors = ButtonDefaults.buttonColors(containerColor = AzulTecnologia), shape = RoundedCornerShape(12.dp)) {
                                    Icon(Icons.Default.Phone, null, modifier=Modifier.size(16.dp)); Spacer(Modifier.width(8.dp)); Text("Activar como filtro de llamadas", fontFamily = OrgonFamily)
                                }
                            }

                            if (!isPremium) {
                                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Naranja)) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text("¡Te quedan ${5-blockedCount} bloqueos gratis!", color = androidx.compose.ui.graphics.Color.White, fontFamily = OrgonFamily, fontWeight = FontWeight.Bold)
                                        Text("Pasa a Premium y bloquea ilimitado + soporte", color = androidx.compose.ui.graphics.Color.White.copy(0.9f), fontSize = 12.sp, fontFamily = LatoFamily)
                                        Row(Modifier.padding(top=12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(onClick = { isPremium=true; save() }, colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.White, contentColor = AzulTecnologia)) { Text("Mensual S/4.90", fontFamily = OrgonFamily, fontSize=12.sp) }
                                            Button(onClick = { isPremium=true; save() }, colors = ButtonDefaults.buttonColors(containerColor = AzulTecnologia, contentColor = androidx.compose.ui.graphics.Color.White)) { Text("Anual S/29.90", fontFamily = OrgonFamily, fontSize=12.sp) }
                                        }
                                    }
                                }
                            }

                            // CONTENIDO TABS
                            if (selectedTab == 0) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Llamadas bloqueadas", fontFamily = OrgonFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AzulTecnologia)
                                    if(logs.isNotEmpty()) AssistChip(onClick = { logs.clear(); save() }, label = { Text("Limpiar") })
                                }

                                if (logs.isEmpty()) {
                                    Column(Modifier.fillMaxWidth().padding(top=40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(Modifier.size(80.dp).clip(RoundedCornerShape(24.dp)).background(Naranja.copy(0.1f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Call, null, tint = Naranja, modifier=Modifier.size(40.dp)) }
                                        Spacer(Modifier.height(12.dp))
                                        Text("Todo tranquilo", fontFamily = OrgonFamily, fontWeight = FontWeight.Bold, color = AzulTecnologia)
                                        Text("Aún no hay bloqueos. Cuando entre un 500, aparecerá aquí.", fontFamily = LatoFamily, fontSize = 13.sp, color = AzulTecnologia.copy(0.5f), modifier = Modifier.padding(horizontal=24.dp))
                                    }
                                } else {
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(logs.toList()) { entry ->
                                            val num = entry.split("|").getOrNull(0) ?: entry
                                            val fecha = entry.split("|").getOrNull(1) ?: ""
                                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)) {
                                                Row(Modifier.padding(14.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                    Column {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.Close, null, tint=Naranja, modifier=Modifier.size(16.dp)); Spacer(Modifier.width(6.dp))
                                                            Text(num, fontFamily = OrgonFamily, fontWeight = FontWeight.Bold, color=AzulTecnologia)
                                                        }
                                                        Text(fecha, fontSize=11.sp, fontFamily = LatoFamily, color=AzulTecnologia.copy(0.5f))
                                                    }
                                                    OutlinedButton(onClick = { if(!whitelist.contains(num)){ whitelist.add(num); save() } }, border = androidx.compose.foundation.BorderStroke(1.dp, Naranja), shape = RoundedCornerShape(8.dp)) { Text("Whitelist", color=Naranja, fontSize=11.sp, fontFamily=OrgonFamily) }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text("Whitelist", fontFamily = OrgonFamily, fontWeight = FontWeight.Bold, fontSize=18.sp, color=AzulTecnologia)
                                Text("Estos números nunca se bloquearán", fontSize=12.sp, fontFamily=LatoFamily, color=AzulTecnologia.copy(0.5f))
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(value=newNum, onValueChange={newNum=it}, label={Text("500123456")}, modifier=Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                                    Spacer(Modifier.width(8.dp))
                                    IconButton(onClick = { if(newNum.isNotBlank()){ whitelist.add(newNum.trim()); newNum=""; save()} }, modifier=Modifier.background(Naranja, RoundedCornerShape(12.dp))) { Icon(Icons.Default.Add, null, tint=androidx.compose.ui.graphics.Color.White) }
                                }
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(whitelist.toList()) { w ->
                                        Card(Modifier.fillMaxWidth(), colors=CardDefaults.cardColors(containerColor=androidx.compose.ui.graphics.Color.White)) {
                                            Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically){
                                                Text(w, fontFamily=OrgonFamily, color=AzulTecnologia); TextButton(onClick={ whitelist.remove(w); save()}){ Text("Quitar", color=Naranja) }
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
}