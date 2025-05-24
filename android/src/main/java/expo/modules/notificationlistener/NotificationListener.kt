package expo.modules.notificationlistener

import android.app.Notification
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.os.bundleOf
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

class NotificationListener : NotificationListenerService() {

    private var componentName: ComponentName? = null
    private val context
        get() = requireNotNull(this.applicationContext)

    private val storage = NotificationStorage(context)

    private val seenNotificationHashes = ConcurrentHashMap<String, Long>()
    private val deduplicationWindowMillis = 2 * 60 * 1000L // 2 minutes

    private fun sendEvent(bundle: Bundle) {
        GlobalNotificationListenerHolder.listener?.let {
            Log.d("NotificationListener", "Sending new notification using listener")
            it(bundle)
        } ?: run {
            Log.d("NotificationListener", "Listener is null, saving notification locally")
            storage.save(bundle)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let {
            requestRebind(it)
            toggleNotificationListenerService(it)
        }

        return START_REDELIVER_INTENT
    }
    
    private fun toggleNotificationListenerService(componentName: ComponentName) {
        val pm = packageManager

        pm.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    override fun onListenerConnected() {
        Log.d("NotificationListener", "NotificationListenerService connected")
        super.onListenerConnected()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()

        Log.d("NotificationListener", "NotificationListenerService disconnected")

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let {
            requestRebind(it)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val now = System.currentTimeMillis()
        seenNotificationHashes.entries.removeIf { now - it.value > deduplicationWindowMillis }

        val packageName = sbn.packageName ?: ""
        val extras = sbn.notification?.extras
        val title = extras?.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras?.getString(Notification.EXTRA_TEXT) ?: ""
        val bigText = extras?.getString(Notification.EXTRA_BIG_TEXT) ?: ""

        val hash = generateNotificationHash(packageName, title, text, bigText)
        if (seenNotificationHashes.containsKey(hash)) {
            Log.d("NotificationListener", "Duplicate notification ignored: hash=$hash")
            return
        }

        seenNotificationHashes[hash] = now

        Log.d("NotificationListener", "Notification posted: $packageName, Title: $title, Text: $text, bigText: $bigText")
        this.sendEvent(bundleOf("title" to title, "text" to text, "bigText" to bigText, "packageName" to packageName))
    }

    private fun generateNotificationHash(vararg parts: String): String {
        val input = parts.joinToString("|")
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}