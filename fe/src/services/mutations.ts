import {
  createAccount,
  deleteAccount,
  debitAccount,
  creditAccount,
  createTransaction,
} from "./http";
import { useMutationBuilder } from "./mutation-builder";
import { mutationKeys, mutationType } from "./mutation-keys";

export const useCreateAccount = () => {
  return useMutationBuilder({
    mutationKey: [mutationKeys.account, mutationType.post],
    mutationFn: createAccount,
  });
};

export const useDeleteAccount = () => {
  return useMutationBuilder({
    mutationKey: [mutationKeys.account, mutationType.remove],
    mutationFn: deleteAccount,
  });
};

export const useDebitAccount = () => {
  return useMutationBuilder({
    mutationKey: [mutationKeys.account, mutationType.post, "debit"],
    mutationFn: debitAccount,
  });
};

export const useCreditAccount = () => {
  return useMutationBuilder({
    mutationKey: [mutationKeys.account, mutationType.post, "credit"],
    mutationFn: creditAccount,
  });
};

export const useCreateTransaction = () => {
  return useMutationBuilder({
    mutationKey: [mutationKeys.transaction, mutationType.post],
    mutationFn: createTransaction,
  });
};
