package expo.modules.notificationlistener

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcel
import android.service.notification.NotificationListenerService.MODE_PRIVATE
import android.util.Base64
import android.util.Log
import androidx.core.content.edit

class NotificationStorage(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("PendingNotifications", MODE_PRIVATE)


    fun save(bundle: Bundle) {
        this.sharedPreferences.edit() {
            val notificationJson =
                bundleToByte(bundle)
            val timestamp = System.currentTimeMillis()
            putString("notification_$timestamp", notificationJson)
        }
    }

    fun popAll(): List<Bundle> {
        val allEntries = sharedPreferences.all

        return allEntries.map { entry: Map.Entry<String, Any?> ->
            val value = entry.value
            if (value is String) {
                val bundle = byteToBundle(value)
                sharedPreferences.edit() { remove(entry.key) }
                return@map bundle
            }
            return@map null
        }.filterNotNull()
    }

    private fun bundleToByte(bundle: Bundle): String? {
        val parcel = Parcel.obtain()
        try {
            parcel.writeBundle(bundle)
            val byteArray = parcel.marshall()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("BundleConverter", "Error when convert parse Bundle to ByteArray", e)
            return null
        } finally {
            parcel.recycle()
        }
    }

    private fun byteToBundle(encodedBundle: String): Bundle? {
        val byteArray = try {
            Base64.decode(encodedBundle, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            Log.e("BundleConverter", "Error when decode base64", e)
            return null
        }

        val parcel = Parcel.obtain()
        try {
            parcel.unmarshall(byteArray, 0, byteArray.size)
            parcel.setDataPosition(0)
            return parcel.readBundle(null)
        } catch (e: Exception) {
            Log.e("BundleConverter", "Error when parse ByteArray to Bundle", e)
            return null
        } finally {
            parcel.recycle()
        }
    }

}