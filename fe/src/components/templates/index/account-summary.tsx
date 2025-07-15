import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { useAuth0 } from "@auth0/auth0-react";

// Example props: replace with your real data source
const account = {
  name: "John Doe",
  balance: 12345.67,
  number: "1234567890123456",
  type: "Checking",
};

export function AccountSummary() {
  const { user } = useAuth0();
  // Mask account number except last 4 digits
  const maskedNumber = account.number.replace(/\d(?=\d{4})/g, "*");
  return (
    <Card className="md:col-span-1">
      <CardHeader>
        <CardTitle>Account Summary</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <div>
            <span className="font-medium">Name:</span>{" "}
            {user?.name || account.name}
          </div>
          <div>
            <span className="font-medium">Current Balance:</span> $
            {account.balance.toLocaleString()}
          </div>
          <div>
            <span className="font-medium">Account Number:</span> {maskedNumber}
          </div>
          <div>
            <span className="font-medium">Account Type:</span> {account.type}
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
