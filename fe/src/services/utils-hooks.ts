import { useQueryClient } from "@tanstack/react-query";
import { useEffect } from "react";
import type { UseMutationBuilderResult } from "./mutation-builder";
import { useNotificationsStoreActions } from "@/stores/notifications-store";
import { useNotificationRegistationsStoreActions } from "@/stores/notification-registations-store";

interface MutationSuccessErrorCallback<T, E, C, V> {
  onSuccess?: (data?: T) => void;
  onError?: () => void;
  successMessage?: string;
  errorMessage?: string;
  mutationResult: UseMutationBuilderResult<T, E, C, V>;
}

export const useMutationSuccessErrorCallback = <T, E, C, V>({
  onSuccess,
  onError,
  errorMessage,
  successMessage,
  mutationResult,
}: MutationSuccessErrorCallback<T, E, C, V>) => {
  const { triggerNotificationEvent } = useNotificationsStoreActions();
  const { status, data, error, mutationKeyString } = mutationResult;

  const { register, unregister } = useNotificationRegistationsStoreActions();

  useEffect(() => {
    if (status === "success") {
      if (successMessage) {
        triggerNotificationEvent({
          msg: successMessage,
          type: "success",
        });
      }
      onSuccess?.(data);
      return;
    }

    if (status === "error") {
      if (errorMessage) {
        triggerNotificationEvent({
          msg: errorMessage,
          type: "success",
        });
      }
      console.error(error);
      onError?.();
      return;
    }
  }, [
    data,
    error,
    errorMessage,
    onError,
    onSuccess,
    status,
    successMessage,
    triggerNotificationEvent,
  ]);

  useEffect(() => {
    if (!mutationKeyString) {
      return;
    }
    register(mutationKeyString, {
      error: !!errorMessage,
      success: !!successMessage,
    });

    return () => {
      unregister(mutationKeyString, {
        error: !!errorMessage,
        success: !!successMessage,
      });
    };
  }, [errorMessage, successMessage, mutationKeyString, register, unregister]);
};

export const useHandleQueriesRefresh = () => {
  const queryClient = useQueryClient();

  const handleQueriesRefresh = () => {
    queryClient.invalidateQueries();
  };

  return {
    handleQueriesRefresh,
  };
};
