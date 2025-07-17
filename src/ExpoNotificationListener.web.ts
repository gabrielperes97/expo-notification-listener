import { registerWebModule, NativeModule } from "expo";

import { NotificationListenerModuleEvents } from "./ExpoNotificationListener.types";

class ExpoNotificationListenerModule extends NativeModule<NotificationListenerModuleEvents> {
  isNotificationListenerPermissionGranted(): boolean {
    return false;
  }
  requestPermission() {}
}

export default registerWebModule(ExpoNotificationListenerModule);
