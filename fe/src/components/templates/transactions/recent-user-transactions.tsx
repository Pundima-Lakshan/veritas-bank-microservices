import { useGetTransactions } from "@/services/queries";
import { RecentTransactions } from "./recent-transactions";

export function RecentUserTransactions() {
  const {
    data: transactions,
    isLoading,
    isError,
    error,
  } = useGetTransactions();

  if (isLoading) return <div>Loading transactions...</div>;
  if (isError)
    return (
      <div>Error loading transactions: {error?.message || "Unknown error"}</div>
    );
  if (!transactions || transactions.length === 0)
    return <div>No recent transactions found.</div>;

  return <RecentTransactions transactions={transactions} />;
}
