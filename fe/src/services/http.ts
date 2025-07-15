import { get, post, remove } from "./communication-service-builder";

const BASE_URL = "http://localhost:8080/api/";

export interface Account {
  id: string;
  accountNumber: string;
  accountHolderName: string;
  balance: number;
  currency: string;
  userId: string;
}

export interface CreateAccountRequest {
  accountHolderName: string;
  currency: string;
}

export interface AssetStatus {
  assetCode: string;
  available: boolean;
  message?: string;
}

export type TransactionType = "deposit" | "withdrawal" | "transfer";

export interface TransactionRequest {
  type: TransactionType;
  amount: number;
  assetCode: string;
  sourceAccountId?: string;
  destinationAccountId?: string;
}

export interface Transaction {
  id: string;
  type: TransactionType;
  amount: number;
  assetCode: string;
  sourceAccountId?: string;
  destinationAccountId?: string;
  transactionTime: string;
}

export const createAccount = (data: CreateAccountRequest) => {
  return post<Account, CreateAccountRequest>(`${BASE_URL}account`, data);
};

export const getAccounts = () => {
  return get<Account[]>(`${BASE_URL}account`);
};

export const deleteAccount = (accountHolderName: string) => {
  return remove(`${BASE_URL}account`, { accountHolderName });
};

export const debitAccount = ({
  accountId,
  amount,
}: {
  accountId: string;
  amount: number;
}) => {
  return post<Account, { amount: number }>(
    `${BASE_URL}account/${accountId}/debit`,
    { amount }
  );
};

export const creditAccount = ({
  accountId,
  amount,
}: {
  accountId: string;
  amount: number;
}) => {
  return post<Account, { amount: number }>(
    `${BASE_URL}account/${accountId}/credit`,
    { amount }
  );
};

export const checkAssetAvailability = (assetCodes: string | string[]) => {
  const codes = Array.isArray(assetCodes) ? assetCodes.join(",") : assetCodes;
  return get<AssetStatus[]>(`${BASE_URL}asset-management`, {
    assetCode: codes,
  });
};

export const createTransaction = (data: TransactionRequest) => {
  return post<Transaction, TransactionRequest>(`${BASE_URL}transaction`, data);
};

export const getTransactions = () => {
  return get<Transaction[]>(`${BASE_URL}transaction`);
};
