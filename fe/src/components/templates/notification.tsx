import {
  useNotificationsStore,
  useNotificationsStoreActions,
} from "@/stores/notifications-store";
import {
  useWebSocketNotifications,
  useWebSocketNotificationsStore,
} from "@/ws-notifications";
import { useAuth0 } from "@auth0/auth0-react";
import { useEffect } from "react";
import { toast } from "sonner";

export const Notification = () => {
  const { user } = useAuth0();
  useWebSocketNotifications(user?.sub ?? null);

  const websocketNotification = useWebSocketNotificationsStore(
    (state) => state.notification
  );
  const notifications = useNotificationsStore((state) => state.notifications);
  const { deleteNotification } = useNotificationsStoreActions();

  // Show toast for websocket notification
  useEffect(() => {
    if (!websocketNotification) return;
    
    const { type, amount, assetCode } = websocketNotification;
    let message = `New transaction: ${type?.toUpperCase() || "TRANSACTION"}`;

    if (amount && assetCode) {
      message += ` - ${amount} ${assetCode}`;
    }

    if (type === "transfer") {
      message += " (Transfer completed)";
    } else if (type === "deposit") {
      message += " (Deposit received)";
    } else if (type === "withdrawal") {
      message += " (Withdrawal processed)";
    }

    toast.info(message);
  }, [websocketNotification]);

  // Show toast for notifications from the store
  useEffect(() => {
    if (notifications.length === 0) {
      return;
    }
    const item = notifications[-1];

    switch (item.type) {
      case "error": {
        toast.error(item.msg);
        break;
      }
      case "info": {
        toast.info(item.msg);
        break;
      }
      case "success": {
        toast.success(item.msg);
        break;
      }
    }

    deleteNotification(item.id);
  }, [notifications, deleteNotification]);

  return null;
};
