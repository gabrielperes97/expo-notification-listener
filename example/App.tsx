import { useEffect, useState } from 'react';
import * as ExpoNotificationListener from 'expo-notification-listener';
import { Button, Platform, SafeAreaView, ScrollView, Text, View } from 'react-native';

export default function App() {

  const [notifications, setNotifications] = useState<ExpoNotificationListener.ReceiveNotificationEvent[]>([]);

  const addNotification = (notification: any) => {
    console.log('Notification received:', notification);
    setNotifications((prevNotifications) => [...prevNotifications, notification]);
  };

  useEffect(() => {
    const subscription = ExpoNotificationListener.addNotificationListener((notification) => {
      addNotification(notification)
    });

    return () => {
      subscription.remove();
    };
  }, [addNotification]);

  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Text>Notification listener permission granted: {ExpoNotificationListener.isNotificationListenerPermissionGranted() ? 'Yes' : 'No'}</Text>
      {Platform.OS === 'android' ? (
        <Button
          title="Request Permission"
          onPress={() => ExpoNotificationListener.requestPermission()}
          disabled={ExpoNotificationListener.isNotificationListenerPermissionGranted()}
        />
      ) : (
        <Text>No support to this platform</Text>
      )}
      <SafeAreaView style={{ flex: 1, width: '100%' }}>
        <ScrollView contentContainerStyle={{ padding: 20 }}>
          {notifications.map((notification, index) => (
        <View
          key={index}
          style={{
            marginBottom: 10,
            padding: 10,
            borderWidth: 1,
            borderColor: '#ccc',
            borderRadius: 5,
          }}
        >
          <Text style={{ fontWeight: 'bold' }}>Title: {notification.title || 'N/A'}</Text>
          <Text>Text: {notification.text || 'N/A'}</Text>
          <Text>Package: {notification.packageName || 'N/A'}</Text>
        </View>
          ))}
        </ScrollView>
      </SafeAreaView>
    </View>
  );
}
