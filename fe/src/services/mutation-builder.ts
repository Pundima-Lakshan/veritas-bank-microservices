import {
  type MutationOptions,
  useMutation,
  type UseMutationResult,
} from "@tanstack/react-query";
import { useEffect } from "react";
import type { BaseRestResponse, BaseRestResponseDefaultError } from "./utils";
import { useNotificationRegistationsStoreActions } from "@/stores/notification-registations-store";

type ModifiedMutationOptions<T, R> = MutationOptions<
  BaseRestResponse<T>,
  BaseRestResponseDefaultError<T>,
  R
>;

interface MutationBuilderProps<T, R> {
  mutationKey: ModifiedMutationOptions<T, R>["mutationKey"];
  mutationFn: ModifiedMutationOptions<T, R>["mutationFn"];
}

export const useMutationBuilder = <T, R>({
  mutationKey,
  mutationFn,
}: MutationBuilderProps<T, R>) => {
  const { register, unregister } = useNotificationRegistationsStoreActions();
  const mutationResult = useMutation<
    BaseRestResponse<T>,
    BaseRestResponseDefaultError<T>,
    R
  >({
    mutationKey,
    mutationFn,
  });

  const mutationKeyString = JSON.stringify(mutationKey) as string | undefined;

  useEffect(() => {
    if (!mutationKeyString) {
      return;
    }
    register(mutationKeyString);
    return () => {
      unregister(mutationKeyString);
    };
  }, [mutationKeyString, register, unregister]);

  return {
    ...mutationResult,
    mutationKeyString,
  };
};

export type UseMutationBuilderResult<T, E, V, C> = UseMutationResult<
  T,
  E,
  V,
  C
> & {
  mutationKeyString: string | undefined;
};
