import { useEffect } from "react";
import { Client, type IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { create } from "zustand";
import { getBearerToken } from "./services/communication-service-builder";
import { useQueryClient } from "@tanstack/react-query";

type Notification = {
  transactionId: string;
  userId: string;
  sourceAccountId?: string;
  destinationAccountId?: string;
  type?: string;
  amount?: string;
  assetCode?: string;
};

interface WebSocketStore {
  notification: Notification | null;
  setNotification: (notification: Notification) => void;
}

export const useWebSocketNotificationsStore = create<WebSocketStore>((set) => ({
  notification: null,
  setNotification: (notification) => set({ notification }),
}));

export function useWebSocketNotifications(userId: string | null) {
  const setNotification = useWebSocketNotificationsStore(
    (state) => state.setNotification,
  );

  const queryClient = useQueryClient();

  useEffect(() => {
    if (!userId) return;
    const client = new Client({
      webSocketFactory: () =>
        new SockJS("http://localhost:39857/ws-notifications", {
          transportOptions: {
            "xhr-streaming": {
              headers: {
                Authorization: getBearerToken(),
              },
            },
          },
        }),
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: getBearerToken(),
      },
      onConnect: () => {
        client.subscribe(
          `/topic/notifications/${userId}`,
          (message: IMessage) => {
            try {
              console.log(
                `subscribing to ws topic /topic/notifications/${userId}`,
              );
              const notif: Notification = JSON.parse(message.body);
              setNotification(notif);
              queryClient.invalidateQueries();
            } catch {
              // handle parse error
            }
          },
        );
      },
    });
    client.activate();
    return () => {
      client.deactivate();
    };
  }, [userId, setNotification]);
}
