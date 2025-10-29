"use client";
import { useEffect, useState } from "react";
type User = { id: string; name: string; email: string; role: string };
type Booking = { id: string; vehicleId: string; start: string; end: string; status: string };

export default function UserAppPage() {
  const [user, setUser] = useState<User | null>(null);
  const [bookings, setBookings] = useState<Booking[]>([]);
  useEffect(() => { (async () => {
    const r1 = await fetch("/api/me"); if (r1.ok) setUser((await r1.json()).user);
    const r2 = await fetch("/api/bookings"); if (r2.ok) setBookings((await r2.json()).bookings || []);
  })(); }, []);
  return (
    <main className="p-6">
      <h1 className="text-2xl font-semibold mb-4">User Dashboard</h1>
      {user ? <div className="mb-6">Xin chào, <strong>{user.name}</strong> ({user.role})</div> : <div>Loading…</div>}
      <section>
        <h2 className="text-xl font-medium mb-2">Lịch của bạn</h2>
        {bookings.length === 0 ? (
          <p className="text-muted-foreground">Không có booking nào.</p>
        ) : (
          <ul className="space-y-3">
            {bookings.map(b => (
              <li key={b.id} className="p-3 border rounded-md">
                <div>ID: {b.id} — Vehicle: {b.vehicleId}</div>
                <div>Thời gian: {new Date(b.start).toLocaleString()} → {new Date(b.end).toLocaleString()}</div>
                <div>Trạng thái: {b.status}</div>
              </li>
            ))}
          </ul>
        )}
      </section>
    </main>
  );
}
