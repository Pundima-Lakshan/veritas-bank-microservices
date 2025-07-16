import { create } from "zustand";
import { devtools } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

interface RegistrationState {
  success?: boolean;
  error?: boolean;
}

type NotificationRegistationsState = {
  notificationRegistrations: Record<string, RegistrationState>;
  count: number;
};

interface NotificationRegistationsStoreActions {
  actions: {
    register: (mutationKey: string, changedState?: RegistrationState) => void;
    unregister: (mutationKey: string, changedState?: RegistrationState) => void;
  };
}

const getInitialState = (): NotificationRegistationsState => {
  return {
    notificationRegistrations: {},
    count: 2,
  };
};

type StoreState = NotificationRegistationsState &
  NotificationRegistationsStoreActions;

export const useNotificationRegistationsStore = create<StoreState>()(
  devtools(
    immer((set) => ({
      ...getInitialState(),
      actions: {
        register: (mutationKey, changedState) =>
          set((state) => {
            state.notificationRegistrations[mutationKey] = {
              ...state.notificationRegistrations[mutationKey],
              ...changedState,
            };
          }),
        unregister: (mutationKey, changedState) =>
          set((state) => {
            state.notificationRegistrations[mutationKey] = {
              ...state.notificationRegistrations[mutationKey],
              ...changedState,
            };
          }),
      },
    })),
    {
      name: "nreg",
    }
  )
);

export const useNotificationRegistationsStoreActions = () => {
  return useNotificationRegistationsStore((state) => state.actions);
};
