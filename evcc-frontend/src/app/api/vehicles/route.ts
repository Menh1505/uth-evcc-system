import { NextResponse } from "next/server";
import { VEHICLES } from "@/app/api/mock/data";
export async function GET() { return NextResponse.json({ vehicles: VEHICLES }); }
