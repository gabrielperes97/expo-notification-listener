import { EventSubscription } from 'expo-modules-core';
import ExpoNotificationListenerModule from './ExpoNotificationListener';
import { ReceiveNotificationEvent } from './ExpoNotificationListener.types';

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

export * from './ExpoNotificationListener.types';

export const ExpoNotificationListener = {
    addNotificationListener,
    isNotificationListenerPermissionGranted,
    requestPermission,
};
