import {
  useAsyncMutationStatesObserver,
  type AsyncMutationStatesObserverParams,
} from "./async-mutation-states-observer";
import { useAsyncQueryStatesObserver } from "./async-query-states-observer";
import type { AsyncStateObserversCommonParams } from "./async-state-observers-common";
import { useLoaderActions } from "./loader-actions";

export type AsyncStateObserversProps = Omit<
  AsyncStateObserversCommonParams,
  "cacheType" | "channel" | "setIsLoading"
> &
  Omit<
    AsyncMutationStatesObserverParams,
    "cacheType" | "channel" | "setIsLoading"
  >;

export const AsyncStateObservers = ({ ...rest }: AsyncStateObserversProps) => {
  const { setIsLoading } = useLoaderActions();

  useAsyncQueryStatesObserver({
    setIsLoading,
  });
  useAsyncMutationStatesObserver({
    ...rest,
    setIsLoading,
  });

  return <></>;
};
