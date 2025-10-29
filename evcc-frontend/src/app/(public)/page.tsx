import Hero from "@/components/marketing/Hero";

export default function HomePage() {
  return (
    <main className="min-h-screen">
      <Hero />

      {/* Giới thiệu */}
      <section id="about-section" className="py-24 bg-background text-center px-6">
        <h2 className="text-3xl font-semibold mb-6">Giới thiệu hệ thống</h2>
        <p className="max-w-3xl mx-auto text-muted-foreground leading-relaxed">
          EV Co-ownership & Cost-sharing giúp cộng đồng cùng sở hữu và vận hành xe điện minh bạch:
          đặt lịch công bằng, ký hợp đồng điện tử, và tự động chia chi phí theo sử dụng thực tế.
        </p>

        <div className="grid md:grid-cols-3 gap-8 mt-16 max-w-5xl mx-auto text-left">
          <div>
            <h3 className="text-xl font-semibold mb-2">Đặt lịch công bằng</h3>
            <p className="text-muted-foreground">Thuật toán fairness đảm bảo quyền lợi theo tỉ lệ sở hữu.</p>
          </div>
          <div>
            <h3 className="text-xl font-semibold mb-2"> Hợp đồng điện tử</h3>
            <p className="text-muted-foreground">Ký số, lưu trữ và truy vết rõ ràng, minh bạch.</p>
          </div>
          <div>
            <h3 className="text-xl font-semibold mb-2">Chia chi phí</h3>
            <p className="text-muted-foreground">Tính toán tự động theo quãng đường, thời gian và giá điện.</p>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-muted py-10 text-center text-sm text-muted-foreground">
        <div className="max-w-4xl mx-auto space-y-2">
          <p><strong>Công ty Cedra Mobility Việt Nam</strong></p>
          <p>Địa chỉ: 123 Nguyễn Văn Linh, Quận 7, TP.HCM</p>
          <p>Email: support@cedra.vn · Hotline: 0901 234 567</p>
          <p>© 2025 Cedra Mobility. All rights reserved.</p>
        </div>
      </footer>
    </main>
  );
}
