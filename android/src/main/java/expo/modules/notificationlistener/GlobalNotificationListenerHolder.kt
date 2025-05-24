package expo.modules.notificationlistener

import android.os.Bundle

object GlobalNotificationListenerHolder {
    var listener: ((bundle: Bundle) -> Unit)? = null
}