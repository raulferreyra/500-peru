package pe.quintosin.a500peru

import android.telecom.Call
import android.telecom.CallScreeningService
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class Peru500CallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val prefs = getSharedPreferences("500_prefs", Context.MODE_PRIVATE)
        val isPremium = prefs.getBoolean("is_premium", false)
        val blockedCount = prefs.getInt("blocked_count", 0)
        val isEnabled = prefs.getBoolean("blocking_enabled", true)
        val whitelist = prefs.getStringSet("whitelist", emptySet()) ?: emptySet()

        val raw = callDetails.handle?.schemeSpecificPart ?: ""
        val number = raw.replace("+51","").replace(" ","").replace("-","").trim()
        val is500 = number.startsWith("500")

        // Límite gratis: 5
        if (!isEnabled || whitelist.contains(number) || whitelist.contains(raw) || (!isPremium && blockedCount >= 5)) {
            respondToCall(callDetails, CallResponse.Builder().setDisallowCall(false).setRejectCall(false).setSkipCallLog(false).setSkipNotification(false).build())
            return
        }

        if (is500) {
            // Guardar log
            val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            val log = "$number|${sdf.format(Date())}"
            val logs = prefs.getStringSet("logs", emptySet())?.toMutableSet() ?: mutableSetOf()
            logs.add(log)
            prefs.edit().putStringSet("logs", logs).putInt("blocked_count", blockedCount + 1).apply()

            val response = CallResponse.Builder().setDisallowCall(true).setRejectCall(true).setSkipCallLog(false).setSkipNotification(true).build()
            respondToCall(callDetails, response)
        } else {
            respondToCall(callDetails, CallResponse.Builder().setDisallowCall(false).setRejectCall(false).setSkipCallLog(false).setSkipNotification(false).build())
        }
    }
}