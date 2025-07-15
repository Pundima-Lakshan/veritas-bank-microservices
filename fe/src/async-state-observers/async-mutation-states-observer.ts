import {
  useQueryClient,
  type MutationCacheNotifyEvent,
} from "@tanstack/react-query";
import { useEffect } from "react";
import {
  useAsyncStateObserversCommon,
  type AsyncStateObserversCommonParams,
} from "./async-state-observers-common";

export interface AsyncMutationStatesObserverParams
  extends AsyncStateObserversCommonParams {
  invalidationConfig: Record<string, Array<Array<string>>>;
}

export const useAsyncMutationStatesObserver = (
  params: AsyncMutationStatesObserverParams
) => {
  const queryClient = useQueryClient();

  const { handleEventAction, handleQueryInvalidation } =
    useAsyncStateObserversCommon({
      ...params,
      cacheType: "mutation",
    });

  useEffect(() => {
    const mutationObserverCallback = (event: MutationCacheNotifyEvent) => {
      if (event.type !== "updated") {
        return;
      }
      handleEventAction(event);
      handleQueryInvalidation(event, queryClient, params.invalidationConfig);
    };

    const unsubscribe = queryClient
      .getMutationCache()
      .subscribe(mutationObserverCallback);

    return () => {
      unsubscribe();
    };
  }, [
    handleEventAction,
    handleQueryInvalidation,
    params.invalidationConfig,
    queryClient,
  ]);
};
