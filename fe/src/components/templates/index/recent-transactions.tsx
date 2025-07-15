import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from "@/components/ui/table";

interface Transaction {
  date: string;
  description: string;
  amount: number;
  balance: number;
}

interface RecentTransactionsProps {
  transactions: Transaction[];
}

const transactions = [
  {
    date: "2024-06-01",
    description: "Deposit",
    amount: 1000,
    balance: 12345.67,
  },
  // ...more transactions
];

export function RecentTransactions() {
  return (
    <Card className="md:col-span-3">
      <CardHeader>
        <CardTitle>Recent Transactions</CardTitle>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Date</TableHead>
              <TableHead>Description</TableHead>
              <TableHead>Amount</TableHead>
              <TableHead>Balance</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {transactions.slice(0, 10).map((tx, idx) => (
              <TableRow key={idx}>
                <TableCell>{tx.date}</TableCell>
                <TableCell>{tx.description}</TableCell>
                <TableCell>${tx.amount.toLocaleString()}</TableCell>
                <TableCell>${tx.balance.toLocaleString()}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
