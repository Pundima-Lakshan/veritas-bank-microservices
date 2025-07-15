import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

export function QuickActions() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Quick Actions</CardTitle>
      </CardHeader>
      <CardContent>
        <Button>Transfer Money</Button>
        <Button>Deposit Money</Button>
        <Button>Withdraw Money</Button>
      </CardContent>
    </Card>
  );
}
