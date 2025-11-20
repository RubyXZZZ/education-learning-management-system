import React from 'react';

interface BadgeProps {
    children: React.ReactNode;
    className?: string;
    style?: React.CSSProperties;
}

export const Badge: React.FC<BadgeProps> = ({
                                                children,
                                                className = 'bg-gray-100 text-gray-600',
                                                style
                                            }) => {
    return (
        <span
            className={`px-2 py-1 rounded-full text-xs font-medium ${className}`}
            style={style}
        >
            {children}
        </span>
    );
};