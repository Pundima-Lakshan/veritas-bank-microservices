import { create } from "zustand";
import { devtools, subscribeWithSelector } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

type LoaderState = {
  loading: boolean;
};

interface LoaderStoreActions {
  actions: {
    updateLoadingState: (isLoading: boolean) => void;
  };
}

const getInitialState = (): LoaderState => {
  return {
    loading: false,
  };
};

type StoreState = LoaderState & LoaderStoreActions;

export const useLoaderStore = create<StoreState>()(
  devtools(
    subscribeWithSelector(
      immer((set) => ({
        ...getInitialState(),
        actions: {
          updateLoadingState: (isLoading) => {
            set((state) => {
              state.loading = isLoading;
            });
          },
        },
      }))
    ),
    {
      name: "l",
    }
  )
);

export const useLoaderStoreActions = () => {
  return useLoaderStore((state) => state.actions);
};
