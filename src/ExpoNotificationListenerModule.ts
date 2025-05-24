import { NativeModule, requireNativeModule } from 'expo';

declare class ExpoNotificationListenerModule extends NativeModule {
  isNotificationListenerPermissionGranted(): boolean;
  requestPermission();
}

export default requireNativeModule<ExpoNotificationListenerModule>('ExpoNotificationListener');
