import { EventSubscription } from 'expo-modules-core';
import ExpoNotificationListenerModule from './ExpoNotificationListenerModule';

export type ReceiveNotificationEvent = {
    title: string;
    text: string;
    packageName: string;
    bigText: string;
}

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
