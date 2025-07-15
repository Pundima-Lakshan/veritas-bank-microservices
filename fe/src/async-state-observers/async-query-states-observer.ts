import {
  useQueryClient,
  type QueryCacheNotifyEvent,
} from "@tanstack/react-query";
import { useEffect } from "react";
import {
  useAsyncStateObserversCommon,
  type AsyncStateObserversCommonParams,
} from "./async-state-observers-common";

export const useAsyncQueryStatesObserver = (
  params: AsyncStateObserversCommonParams
) => {
  const queryClient = useQueryClient();
  const { handleEventAction } = useAsyncStateObserversCommon(params);

  useEffect(() => {
    const queryObserverCallback = (event: QueryCacheNotifyEvent) => {
      handleEventAction(event);
    };

    const unsubscribe = queryClient
      .getQueryCache()
      .subscribe(queryObserverCallback);

    return () => {
      unsubscribe();
    };
  }, [handleEventAction, queryClient]);
};
