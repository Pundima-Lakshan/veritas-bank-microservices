import { create } from "zustand";
import { devtools, subscribeWithSelector } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

// State for the auth store
interface AuthState {
  accessToken: string | null;
}

// Actions for the auth store
interface AuthStoreActions {
  actions: {
    setAccessToken: (token: string) => void;
    clearAccessToken: () => void;
  };
}

const getInitialState = (): AuthState => ({
  accessToken: null,
});

type StoreState = AuthState & AuthStoreActions;

export const useAuthStore = create<StoreState>()(
  devtools(
    subscribeWithSelector(
      immer((set) => ({
        ...getInitialState(),
        actions: {
          setAccessToken: (token) => {
            set((state) => {
              state.accessToken = token;
            });
          },
          clearAccessToken: () => {
            set((state) => {
              state.accessToken = null;
            });
          },
        },
      }))
    ),
    {
      name: "auth",
    }
  )
);

export const useAuthStoreActions = () => {
  return useAuthStore((state) => state.actions);
};
