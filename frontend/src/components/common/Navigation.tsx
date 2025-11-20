import React from 'react';
import {
    Home, BookOpen, Settings, Users, BookMarked, Shield, GraduationCap, LogOut
} from 'lucide-react';
import { NavItem } from './NavItem';
import { COLORS } from '../../constants/colors';
import { useAuth } from '../../contexts/AppContext';
import type { ViewType, UserRole } from '../../types';

interface NavigationProps {
    currentView: ViewType;
    onViewChange: (view: ViewType) => void;
    userRole: UserRole;
    isSuperAdmin?: boolean;
}

export const Navigation: React.FC<NavigationProps> = ({
                                                          currentView,
                                                          onViewChange,
                                                          userRole,
                                                          isSuperAdmin = false
                                                      }) => {
    const { logout } = useAuth();

    const handleLogout = async () => {
        if (window.confirm('Are you sure you want to logout?')) {
            try {
                await logout();
                // No need to redirect, App.tsx will handle showing LoginPage
            } catch (err) {
                console.error('Logout error:', err);
                alert('Logout failed. Please try again.');
            }
        }
    };

    return (
        <div
            className="w-20 h-screen fixed left-0 top-0 flex flex-col items-center py-6 space-y-6 z-50"
            style={{ backgroundColor: COLORS.dark }}
        >
            <div className="text-3xl mb-4">🎓</div>

            {/* Common Navigation (All Roles) */}
            <NavItem
                icon={<Home size={24} />}
                active={currentView === 'dashboard'}
                onClick={() => onViewChange('dashboard')}
            />

            {/* Student Navigation */}
            {userRole === 'STUDENT' && (
                <>
                    <NavItem
                        icon={<BookOpen size={24} />}
                        active={currentView === 'my-courses'}
                        onClick={() => onViewChange('my-courses')}
                    />
                    <NavItem
                        icon={<GraduationCap size={24} />}
                        active={currentView === 'course-registration'}
                        onClick={() => onViewChange('course-registration')}
                    />
                </>
            )}

            {/* Instructor Navigation */}
            {userRole === 'INSTRUCTOR' && (
                <>
                    <NavItem
                        icon={<BookOpen size={24} />}
                        active={currentView === 'my-courses'}
                        onClick={() => onViewChange('my-courses')}
                    />
                </>
            )}

            {/* Admin Navigation */}
            {userRole === 'ADMIN' && (
                <>
                    <NavItem
                        icon={<GraduationCap size={24} />}
                        active={currentView === 'course-registration'}
                        onClick={() => onViewChange('course-registration')}
                    />
                    <NavItem
                        icon={<Users size={24} />}
                        active={currentView === 'users-management'}
                        onClick={() => onViewChange('users-management')}
                    />
                    <NavItem
                        icon={<BookMarked size={24} />}
                        active={currentView === 'courses-management'}
                        onClick={() => onViewChange('courses-management')}
                    />
                </>
            )}

            {/* Super Admin Only */}
            {userRole === 'ADMIN' && isSuperAdmin && (
                <NavItem
                    icon={<Shield size={24} />}
                    active={currentView === 'system-settings'}
                    onClick={() => onViewChange('system-settings')}
                />
            )}

            <div className="flex-1" />

            {/* Profile - All Roles */}
            <NavItem
                icon={<Settings size={24} />}
                active={currentView === 'profile'}
                onClick={() => onViewChange('profile')}
            />

            {/* Logout - All Roles */}
            <NavItem
                icon={<LogOut size={24} />}
                active={false}
                onClick={handleLogout}
            />
        </div>
    );
};