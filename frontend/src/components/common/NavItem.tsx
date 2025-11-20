// src/components/common/NavItem.tsx
import React from 'react';
import { COLORS } from '../../constants/colors';

interface NavItemProps {
    icon: React.ReactNode;
    active: boolean;
    onClick: () => void;
    badge?: number;
}

export const NavItem: React.FC<NavItemProps> = ({ icon, active, onClick, badge }) => (
    <button
        onClick={onClick}
        className="w-12 h-12 rounded-xl flex items-center justify-center relative transition-all"
        style={{
            backgroundColor: active ? COLORS.orange : 'transparent',
            color: active ? 'white' : COLORS.bg
        }}
    >
        {icon}
        {badge && badge > 0 && (
            <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 rounded-full text-xs text-white flex items-center justify-center">
        {badge}
      </span>
        )}
    </button>
);