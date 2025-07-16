import { create } from "zustand";
import { devtools, subscribeWithSelector } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";
import { v4 as uuidv4 } from "uuid";

const AUTO_HIDE_TIME = 5000;

// Inline NotificationEvent type for this store
export type NotificationEvent = {
  msg: string;
  type: "error" | "success" | "info";
};

interface Notification extends NotificationEvent {
  id: string;
  initTime: number;
}

type NotificationsState = {
  notifications: Notification[];
};

interface NotificationsStoreActions {
  actions: {
    triggerNotificationEvent: (event: NotificationEvent) => void;
    deleteNotification: (notificatonId: string) => void;
  };
}

const getInitialState = (): NotificationsState => {
  return {
    notifications: [],
  };
};

type StoreState = NotificationsState & NotificationsStoreActions;

export const useNotificationsStore = create<StoreState>()(
  devtools(
    subscribeWithSelector(
      immer((set, _get, api) => ({
        ...getInitialState(),
        actions: {
          triggerNotificationEvent: (event) =>
            set((state) => {
              const id = uuidv4();
              const newNotification: Notification = {
                id,
                msg: event.msg,
                type: event.type,
                initTime: Date.now(),
              };
              if (newNotification.type !== "error") {
                setTimeout(() => {
                  api.getState().actions.deleteNotification(newNotification.id);
                }, AUTO_HIDE_TIME);
              }
              state.notifications.push(newNotification);
            }),
          deleteNotification: (notificationId) =>
            set((state) => {
              state.notifications = state.notifications.filter(
                (n: Notification) => n.id !== notificationId
              );
            }),
        },
      }))
    ),
    {
      name: "ns",
    }
  )
);

export const useNotificationsStoreActions = () => {
  return useNotificationsStore((state) => state.actions);
};
