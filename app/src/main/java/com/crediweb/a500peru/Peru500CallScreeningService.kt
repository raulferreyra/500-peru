package pe.quintosin.peru500

import android.telecom.Call
import android.telecom.CallScreeningService
import android.content.Context

class Peru500CallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val incomingNumberRaw = callDetails.handle?.schemeSpecificPart ?: ""

        // Normalizamos: quitamos +51, espacios, guiones
        val number = incomingNumberRaw.replace("+51", "").replace(" ", "").replace("-", "").trim()

        val prefs = getSharedPreferences("500_prefs", Context.MODE_PRIVATE)
        val isBlockingEnabled = prefs.getBoolean("blocking_enabled", true)
        val whitelist = prefs.getStringSet("whitelist", emptySet()) ?: emptySet()

        // ¿Es un número 500? MTC: la serie 500 es comercial.
        // Cubrimos 500xxxxxx y 00500xxxxxx
        val is500 = number.startsWith("500")

        val isWhitelisted = whitelist.contains(number) || whitelist.contains(incomingNumberRaw)

        if (isBlockingEnabled && is500 && !isWhitelisted) {
            // BLOQUEAR: No suena, no notifica, se va al buzón
            val response = CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(false) // dejalo en true si no quieres ni rastro en el registro
                .setSkipNotification(true)
                .build()
            respondToCall(callDetails, response)
        } else {
            // PERMITIR
            val response = CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()
            respondToCall(callDetails, response)
        }
    }
}