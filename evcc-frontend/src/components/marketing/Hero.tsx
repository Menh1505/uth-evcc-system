"use client";

import { useTransition } from "react";
import { useRouter } from "next/navigation";
import { motion } from "framer-motion";

export default function Hero() {
  const router = useRouter();
  const [isPending, startTransition] = useTransition();

  const goLogin = () => {
    startTransition(() => {
      router.push("/login");
    });
  };

  const scrollToAbout = () => {
    const el = document.getElementById("about-section");
    if (el) el.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <section className="relative min-h-[100vh] flex flex-col items-center justify-center text-center overflow-hidden">
      {/* Nền video */}
      <video
        autoPlay
        loop
        muted
        playsInline
        preload="auto"
        className="absolute inset-0 w-full h-full object-cover brightness-[0.55] contrast-[1.1] saturate-[1.15]"
      >
        <source src="/assets/ev-bg.mp4" type="video/mp4" />
      </video>

      {/* Overlay tối nhẹ */}
      <div className="absolute inset-0 bg-gradient-to-b from-black/65 via-black/35 to-black/75" />

      {/* Nội dung */}
      <div className="relative z-10 max-w-4xl px-6 text-white">
        {/* Tiêu đề */}
        <motion.h1
          initial={{ opacity: 0, y: 42 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.9, ease: "easeOut" }}
          className="text-center font-extrabold text-[42px] sm:text-6xl md:text-7xl tracking-tight mb-6 leading-tight bg-gradient-to-r from-blue-400 via-sky-300 to-blue-500 bg-clip-text text-transparent animate-gradient-move drop-shadow-[0_2px_8px_rgba(0,0,0,0.35)]"
        >
            EV Co-ownership & Cost Sharing




          
        </motion.h1>

        {/* Mô tả */}
        <motion.p
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.2 }}
          className="text-base sm:text-lg md:text-xl text-gray-200/90 mb-8 leading-relaxed"
        >
          Đồng sở hữu xe điện — đặt lịch công bằng, check-in/checkout minh bạch,
          tự động chia chi phí và quản lý hợp đồng thông minh.
        </motion.p>

        {/* Nút CTA */}
        <motion.div
          initial={{ opacity: 0, scale: 0.96 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.7, delay: 0.35 }}
          className="flex flex-wrap justify-center gap-4"
        >
          {/* Nút chính có nền xanh nhạt động chuyển */}
          <button
            onClick={goLogin}
            disabled={isPending}
            className="relative px-8 py-3 rounded-xl font-semibold overflow-hidden text-black shadow-lg active:scale-95 disabled:opacity-80 disabled:cursor-not-allowed"
            style={{
              background:
                "linear-gradient(90deg, #d5faff, #b8f1ff, #a9e2ff, #d5faff)",
              backgroundSize: "300% 100%",
              animation: "evccGradient 6s ease-in-out infinite",
            }}
          >
            <span className={`${isPending ? "opacity-0" : "opacity-100"} transition-opacity`}>
              Bắt đầu
            </span>

            {/* shimmer ánh sáng chạy qua */}
            <span
              aria-hidden
              className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/60 to-transparent"
              style={{
                filter: "blur(8px)",
                animation: "evccShimmer 2.4s linear infinite",
              }}
            />

            {/* spinner loading */}
            {isPending && (
              <span className="absolute inset-0 flex items-center justify-center">
                <svg className="h-5 w-5 animate-spin" viewBox="0 0 24 24" fill="none">
                  <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="4"
                  />
                  <path
                    className="opacity-90"
                    d="M4 12a8 8 0 018-8"
                    stroke="currentColor"
                    strokeWidth="4"
                  />
                </svg>
              </span>
            )}
          </button>

          {/* Nút phụ */}
          <button
            onClick={scrollToAbout}
            className="px-8 py-3 rounded-xl font-semibold border border-white/35 bg-white/10 backdrop-blur-sm text-white hover:bg-white/20 transition-all duration-300 active:scale-95"
          >
            Tìm hiểu thêm
          </button>
        </motion.div>
      </div>

      {/* Cuộn để khám phá */}
      <motion.div
        initial={{ opacity: 0, y: 8 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 1, duration: 0.8 }}
        className="absolute bottom-8 z-10 flex flex-col items-center text-gray-200/80"
      >
        <motion.span
          animate={{ opacity: [0.5, 1, 0.5] }}
          transition={{ duration: 1.6, repeat: Infinity }}
          className="text-sm tracking-wide cursor-pointer"
          onClick={scrollToAbout}
        >
          Cuộn để khám phá
        </motion.span>
        <motion.svg
          onClick={scrollToAbout}
          className="mt-1 cursor-pointer"
          width="22"
          height="22"
          viewBox="0 0 24 24"
          fill="none"
          animate={{ y: [0, 6, 0] }}
          transition={{ duration: 1.2, repeat: Infinity, ease: "easeInOut" }}
        >
          <path
            d="M6 9l6 6 6-6"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </motion.svg>
      </motion.div>

      {/* Keyframes cho gradient + shimmer */}
      <style jsx global>{`
        @keyframes evccGradient {
          0% {
            background-position: 0% 0%;
          }
          50% {
            background-position: 100% 0%;
          }
          100% {
            background-position: 0% 0%;
          }
        }
        @keyframes evccShimmer {
          0% {
            transform: translateX(-120%);
          }
          100% {
            transform: translateX(120%);
          }
        }
      `}</style>
    </section>
  );
}


