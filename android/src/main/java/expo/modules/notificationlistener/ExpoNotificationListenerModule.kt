package expo.modules.notificationlistener

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition


class ExpoNotificationListenerModule : Module() {
  private val context
    get() = requireNotNull(appContext.reactContext)

  companion object {
    const val EVENT_ON_RECEIVE_NOTIFICATION = "onReceiveNotification"
  }

  private val listener: (Bundle) -> Unit = { bundle ->
    this.sendEventOnReceveiveNotification(bundle)
  }

  fun sendEventOnReceveiveNotification(bundle: Bundle) {
    Log.d("ExpoNotificationListenerModule", "Sending event: $bundle")
    this.sendEvent(EVENT_ON_RECEIVE_NOTIFICATION, bundle)
  }

  private fun sendPendingNotifications() {
    val storage = NotificationStorage(context)
    val notifications = storage.popAll()

    for (notification in notifications) {
      Log.d("ExpoNotificationListenerModule", "Sending pending notification: $notification")
      this.sendEventOnReceveiveNotification(notification)
    }
  }

  override fun definition() = ModuleDefinition {
    Name("ExpoNotificationListener")

    Events(EVENT_ON_RECEIVE_NOTIFICATION)

    OnStartObserving {
      GlobalNotificationListenerHolder.listener = listener
      Log.d("ExpoNotificationListenerModule", "Adding listener")
      sendPendingNotifications()
    }

    OnStopObserving {
      GlobalNotificationListenerHolder.listener = null
      Log.d("ExpoNotificationListenerModule", "Removing listener")
    }


    Function("isNotificationListenerPermissionGranted") {

      val checkPermissionGranted = {
        val componentName = ComponentName(context, NotificationListener::class.java)
        val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        enabledListeners?.contains(componentName.flattenToString()) ?: false
      }
      return@Function checkPermissionGranted()
    }

    Function("requestPermission") {
      val request = {
        val intent = Intent()
        intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(intent)
      }
      return@Function request()
    }
  }
}
