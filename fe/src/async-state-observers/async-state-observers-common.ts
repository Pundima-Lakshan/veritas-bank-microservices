import type { mutationKeys } from "@/services/mutation-keys";
import { useNotificationRegistationsStore } from "@/stores/notification-registations-store";
import {
  useNotificationsStoreActions,
  type NotificationEvent,
} from "@/stores/notifications-store";
import {
  type MutationCacheNotifyEvent,
  type QueryCacheNotifyEvent,
  type QueryClient,
} from "@tanstack/react-query";
import { useCallback } from "react";

export type AsyncStateObserversCommonParams = {
  cacheType?: "query" | "mutation";
  setIsLoading: (isLoading: boolean) => void;
};

export const useAsyncStateObserversCommon = ({
  setIsLoading,
  cacheType,
}: AsyncStateObserversCommonParams) => {
  const { notifications } = useGetNotifications();

  const handleEventAction = useCallback(
    (event: QueryCacheNotifyEvent | MutationCacheNotifyEvent) => {
      if (event.type !== "updated") {
        return;
      }

      /**
       * action that starts the query fetching (query only)
       */
      if (event.action.type === "fetch") {
        setIsLoading(true);
        // notifications.loading('Fetching...');
        return;
      }

      /**
       * action that starts the mutation pending (mutation only)
       */
      if (event.action.type === "pending") {
        setIsLoading(true);
        // notifications.loading('Pending...');
        return;
      }

      /**
       * final result after retires if any result in success
       */
      if (event.action.type === "success") {
        setIsLoading(false);
        if (cacheType === "mutation") {
          notifications(event, "success")?.show({
            msg: "Action Successfull",
            type: "success",
          });
        }
        return;
      }

      /**
       * final result after retries if any result in failure
       */
      if (event.action.type === "error") {
        setIsLoading(false);
        if (event.action.error.status === 401) {
          notifications(event, "error")?.show({
            msg: "You are not authenticated",
            type: "error",
          });
          console.error("Unauthorized");
          // void removeUser();
        } else {
          notifications(event, "error")?.show({
            msg:
              event.action.error.content ??
              event.action.error.message ??
              "Error",
            type: "error",
          });
          console.error(event.action.error);
        }

        return;
      }

      /**
       * when errors occur and retry is enabled and is not the last retry
       */
      if (event.action.type === "failed") {
        setIsLoading(false);
        notifications(event, "error")?.show({
          msg: "Something went wrong",
          type: "error",
        });
        return;
      }

      /**
       * when there is no internet connection or for some other reason
       */
      if (event.action.type === "pause") {
        setIsLoading(false);
        notifications(event, "warning")?.show({
          msg: "No internet connection",
          type: "error",
        });
        return;
      }

      /**
       * after connection is restored
       */
      if (event.action.type === "continue") {
        setIsLoading(true);
        return;
      }

      /**
       * when invalidate action is called
       */
      if (event.action.type === "invalidate") {
        setIsLoading(true);
        return;
      }

      /**
       * wasn't able to find (query only)
       */
      if (event.action.type === "setState") {
        return;
      }
    },
    [cacheType, notifications, setIsLoading]
  );

  const handleQueryInvalidation = (
    event: MutationCacheNotifyEvent,
    queryClient: QueryClient,
    invalidationConfig: Record<string, Array<Array<string>>>
  ) => {
    if (event.type !== "updated") {
      return;
    }

    if (event.action.type === "success") {
      const mutationKeyElement = event.mutation.options.mutationKey?.[0] as
        | keyof typeof mutationKeys
        | undefined;
      if (!mutationKeyElement) {
        return;
      }
      invalidationConfig[mutationKeyElement]?.forEach((queryKey) => {
        void queryClient.invalidateQueries({
          queryKey: queryKey,
        });
      });
    }
  };

  return {
    handleEventAction,
    handleQueryInvalidation,
  };
};

const isPartialMutationEvent = (
  event: MutationCacheNotifyEvent | QueryCacheNotifyEvent
): event is MutationCacheNotifyEvent => {
  return (
    "mutation" in event &&
    typeof event.mutation === "object" &&
    event.mutation !== null &&
    "options" in event.mutation &&
    typeof event.mutation.options === "object" &&
    event.mutation.options !== null
  );
};

const isPartialQueryEvent = (
  event: MutationCacheNotifyEvent | QueryCacheNotifyEvent
): event is MutationCacheNotifyEvent => {
  return "query" in event;
};

const useGetNotifications = () => {
  const notificationRegistrationStore = useNotificationRegistationsStore;
  const { triggerNotificationEvent } = useNotificationsStoreActions();
  const _notifications = {
    show: (event: NotificationEvent) => {
      return triggerNotificationEvent(event);
    },
  };

  const notifications = (
    event: MutationCacheNotifyEvent | QueryCacheNotifyEvent,
    type: "success" | "error" | "warning"
  ) => {
    if (isPartialQueryEvent(event)) {
      return _notifications;
    }

    if (!isPartialMutationEvent(event)) return;

    const { notificationRegistrations } =
      notificationRegistrationStore.getState();
    const eventMutationKey = event.mutation?.options.mutationKey;
    const registrationStatus = eventMutationKey
      ? notificationRegistrations[JSON.stringify(eventMutationKey)]
      : undefined;

    if (!registrationStatus) {
      return _notifications;
    }
    if (type === "error" && registrationStatus.error) {
      return undefined;
    }
    if (type === "success" && registrationStatus.success) {
      return undefined;
    }
    return _notifications;
  };

  return { notifications };
};
