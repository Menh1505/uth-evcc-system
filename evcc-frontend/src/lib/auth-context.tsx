'use client'; 

import { createContext, useContext, useState, ReactNode, useEffect } from 'react'; // <-- THÊM useEffect
import { useRouter } from 'next/navigation';

// Định nghĩa 'User'
interface User {
    username: string;
    email: string;
    role: 'ADMIN' | 'CO_OWNER'; 
}

// Định nghĩa 'AuthContext'
interface AuthContextType {
    user: User | null;
    login: (user: User, token: string) => void;
    logout: () => void;
    isAuthenticated: boolean;
    isAdmin: boolean;
    isLoading: boolean; // <-- THÊM isLoading
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true); // <-- THÊM state loading
    const router = useRouter();

    // *** PHẦN NÂNG CẤP ***
    // Tự động tải user từ localStorage khi F5 (chỉ chạy 1 lần)
    useEffect(() => {
        const storedToken = localStorage.getItem('jwt_token');
        const storedUser = localStorage.getItem('user_info');

        if (storedToken && storedUser) {
            setUser(JSON.parse(storedUser));
        }
        setIsLoading(false); // Báo là đã load xong
    }, []); // Mảng rỗng [] nghĩa là chỉ chạy 1 lần khi mount

    // Hàm đăng nhập (giữ nguyên)
    const login = (userData: User, token: string) => {
        setUser(userData);
        localStorage.setItem('jwt_token', token);
        localStorage.setItem('user_info', JSON.stringify(userData));
        
        if (userData.role === 'ADMIN') {
            router.push('/(protected)/admin');
        } else {
            router.push('/(protected)'); 
        }
    };

    // Hàm đăng xuất (giữ nguyên)
    const logout = () => {
        setUser(null);
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_info');
        router.push('/(auth)/login');
    };

    const isAuthenticated = !!user;
    const isAdmin = user?.role === 'ADMIN';

    const value = {
        user,
        login,
        logout,
        isAuthenticated,
        isAdmin,
        isLoading // <-- Cung cấp isLoading
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

// Hook (giữ nguyên)
export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth phải được dùng bên trong AuthProvider');
    }
    return context;
}