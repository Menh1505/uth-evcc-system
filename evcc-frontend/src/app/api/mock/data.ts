export const USERS = [
  { id: "u1", name: "Nguyễn Văn A", email: "a@example.com", role: "user" },
  { id: "u2", name: "Trần Thị B", email: "b@example.com", role: "staff" },
  { id: "u3", name: "Lê Văn Q", email: "q@example.com", role: "admin" },
];

export const VEHICLES = [
  { id: "v1", plate: "EV-001", model: "VinFast VF8", status: "available" },
  { id: "v2", plate: "EV-002", model: "Tesla Model 3", status: "in_use" },
];

export const BOOKINGS = [
  { id: "bk1", userId: "u1", vehicleId: "v1", start: "2025-10-20T09:00:00Z", end: "2025-10-20T12:00:00Z", status: "confirmed" },
  { id: "bk2", userId: "u1", vehicleId: "v2", start: "2025-10-21T14:00:00Z", end: "2025-10-21T16:00:00Z", status: "pending" },
  { id: "bk3", userId: "u2", vehicleId: "v1", start: "2025-10-22T08:00:00Z", end: "2025-10-22T10:00:00Z", status: "completed" },
];

export const CONTRACTS = [
  { id: "c1", title: "E-Contract #001", parties: ["u1","u2"], start: "2025-01-01", status: "active" },
];
