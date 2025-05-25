import { requireNativeModule } from 'expo';
import { ExpoNotificationListenerModule } from './ExpoNotificationListener.types';

export default requireNativeModule<ExpoNotificationListenerModule>('ExpoNotificationListener');
