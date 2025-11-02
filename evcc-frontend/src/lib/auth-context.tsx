'use client'; // Bắt buộc dùng 'use client' vì có hook (useState, useContext)

import { createContext, useContext, useState, ReactNode } from 'react';
import { useRouter } from 'next/navigation'; // Dùng 'next/navigation' cho App Router

// Định nghĩa 'User' trông như thế nào
interface User {
    username: string;
    email: string;
    role: 'ADMIN' | 'CO_OWNER'; // Chỉ còn ADMIN và CO_OWNER
}

// Định nghĩa 'AuthContext' sẽ cung cấp những gì
interface AuthContextType {
    user: User | null;         // Thông tin user, null nếu chưa đăng nhập
    login: (user: User, token: string) => void; // Hàm để đăng nhập
    logout: () => void;        // Hàm để đăng xuất
    isAuthenticated: boolean;  // Trạng thái đăng nhập
    isAdmin: boolean;          // Có phải Admin không?
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Đây là 'Nhà cung cấp' (Provider) sẽ bọc toàn bộ ứng dụng
export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const router = useRouter();

    // Hàm đăng nhập
    const login = (userData: User, token: string) => {
        setUser(userData);
        // Lưu token vào localStorage để 'nhớ' đăng nhập
        localStorage.setItem('jwt_token', token);
        // Lưu thông tin user để hiển thị (chào user A)
        localStorage.setItem('user_info', JSON.stringify(userData));
        
        // Điều hướng dựa trên vai trò
        if (userData.role === 'ADMIN') {
            router.push('/(protected)/admin'); // Chuyển đến trang Admin
        } else {
            router.push('/(protected)'); // Trang home chung cho CO_OWNER
        }
    };

    // Hàm đăng xuất
    const logout = () => {
        setUser(null);
        // Xóa khỏi localStorage
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_info');
        // Về trang đăng nhập
        router.push('/(auth)/login');
    };

    const isAuthenticated = !!user;
    const isAdmin = user?.role === 'ADMIN';

    const value = {
        user,
        login,
        logout,
        isAuthenticated,
        isAdmin // Bỏ isStaff
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

// Đây là 'Hook' tùy chỉnh để các component con có thể lấy dữ liệu
export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth phải được dùng bên trong AuthProvider');
    }
    return context;
}