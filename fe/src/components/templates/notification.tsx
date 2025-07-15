import {
  useNotificationsStore,
  useNotificationsStoreActions,
} from "@/stores/notifications-store";
import { useEffect } from "react";
import { toast } from "sonner";

export const Notification = () => {
  const notifications = useNotificationsStore((state) => state.notifications);
  const { deleteNotification } = useNotificationsStoreActions();

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
