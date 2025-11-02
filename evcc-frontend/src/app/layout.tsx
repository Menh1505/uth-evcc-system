import "./globals.css";

export const metadata = {
  title: "EV Co-ownership & Cost-sharing",
  description: "Đồng sở hữu xe điện — đặt lịch công bằng, chia chi phí minh bạch.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="vi" className="scroll-smooth">
      <body className="min-h-screen bg-background text-foreground antialiased">
        {children}
      </body>
    </html>
  );
}
