import { useLoaderStoreActions } from "@/stores/loader-store";
import { useQueryClient } from "@tanstack/react-query";
import { useState } from "react";

export const useLoaderActions = () => {
  const queryClient = useQueryClient();
  const { updateLoadingState } = useLoaderStoreActions();

  const [, _setIsLoading] = useState(false);

  const setIsLoading = (state: boolean) => {
    const fetchingCount = queryClient.isFetching();
    const mutatingCount = queryClient.isMutating();
    const isLoading = fetchingCount > 0 || mutatingCount > 0 || state;
    _setIsLoading(isLoading);
    updateLoadingState(isLoading);
  };

  return {
    setIsLoading,
  };
};
