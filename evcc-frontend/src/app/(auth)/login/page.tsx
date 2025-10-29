import { Card } from "@/components/ui/card";
import LoginForm from "@/components/auth/LoginForm";

export default function LoginPage() {
  return (
    <div className="min-h-[calc(100vh-64px)] grid place-items-center p-4">
      <Card className="w-full max-w-sm p-6">
        <h1 className="text-2xl font-semibold mb-1">Đăng nhập</h1>
        <p className="text-sm text-muted-foreground mb-6">
          Truy cập hệ thống đồng sở hữu & chia sẻ chi phí.
        </p>
        <LoginForm />
      </Card>
    </div>
  );
}
