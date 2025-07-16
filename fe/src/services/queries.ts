import { getAccounts, checkAssetAvailability, getTransactions } from "./http";
import { useQueryBuilder } from "./query-builder";
import { queryKeys } from "./query-keys";

export const useGetAccounts = () => {
  return useQueryBuilder({
    queryKey: [queryKeys.accounts],
    queryFn: getAccounts,
  });
};

export const useCheckAssetAvailability = (assetCodes: string | string[]) => {
  return useQueryBuilder({
    queryKey: [queryKeys.assetAvailability, assetCodes],
    queryFn: () => checkAssetAvailability(assetCodes),
  });
};

export const useGetTransactions = () => {
  return useQueryBuilder({
    queryKey: [queryKeys.transactions],
    queryFn: getTransactions,
  });
};
