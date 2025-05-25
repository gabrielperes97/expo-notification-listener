import { EventSubscription } from 'expo-modules-core';
import ExpoNotificationListenerModule, { ReceiveNotificationEvent } from './ExpoNotificationListenerModule';

export function addNotificationListener(
    listener: (notification: ReceiveNotificationEvent) => void): EventSubscription {
    return ExpoNotificationListenerModule.addListener('onReceiveNotification', listener);
}

export function isNotificationListenerPermissionGranted(): boolean {
    return ExpoNotificationListenerModule.isNotificationListenerPermissionGranted();
}

export function requestPermission() {
    return ExpoNotificationListenerModule.requestPermission();
}
