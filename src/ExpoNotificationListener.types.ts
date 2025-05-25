import { NativeModule } from 'expo';

export type ReceiveNotificationEvent = {
    title: string;
    text: string;
    packageName: string;
    bigText: string;
}

export type NotificationListenerModuleEvents = {
    onReceiveNotification(notification: ReceiveNotificationEvent): void;
}

export declare class ExpoNotificationListenerModule extends NativeModule<NotificationListenerModuleEvents> {
  isNotificationListenerPermissionGranted(): boolean;
  requestPermission();
}
