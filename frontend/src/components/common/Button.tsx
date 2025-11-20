import React from 'react';
import { COLORS } from '../../constants/colors';

interface ButtonProps {
    variant?: 'primary' | 'secondary' | 'ghost' | 'danger' | 'link';
    size?: 'sm' | 'md' | 'lg';
    children: React.ReactNode;
    onClick?: () => void;
    disabled?: boolean;
    icon?: React.ReactNode;
    type?: 'button' | 'submit' | 'reset';
    className?: string;
}

export const Button: React.FC<ButtonProps> = ({
                                                  variant = 'primary',
                                                  size = 'md',
                                                  children,
                                                  onClick,
                                                  disabled = false,
                                                  icon,
                                                  type = 'button',
                                                  className = ''
                                              }) => {
    // Base styles
    const baseStyles = "rounded-lg font-medium transition-all disabled:opacity-50 disabled:cursor-not-allowed";

    // Variant styles
    const variantStyles: Record<string, string> = {
        primary: `${baseStyles} text-white shadow-sm hover:shadow-md cursor-pointer`,
        secondary: `${baseStyles} shadow-sm hover:shadow cursor-pointer`,
        ghost: `${baseStyles} shadow-sm hover:shadow cursor-pointer`,
        danger: `${baseStyles} text-white shadow-sm hover:shadow-md cursor-pointer`,
        link: "text-sm hover:underline cursor-pointer"
    };

    // Size styles
    const sizeStyles: Record<string, string> = {
        sm: "px-3 py-1.5 text-xs",
        md: "px-4 py-2 text-sm",
        lg: "px-6 py-3 text-base"
    };

    // Background colors
    const getBgColor = () => {
        if (variant === 'link') return undefined;

        const bgColors: Record<string, string> = {
            primary: COLORS.orange,
            secondary: COLORS.bg,
            ghost: COLORS.bg,
            danger: COLORS.dark
        };
        return bgColors[variant];
    };

    // Text colors for non-primary variants
    const getTextColor = () => {
        if (variant === 'secondary' || variant === 'ghost') {
            return COLORS.dark;
        }
        if (variant === 'link') {
            return COLORS.orange;
        }
        return undefined;
    };

    return (
        <button
            type={type}
            onClick={onClick}
            disabled={disabled}
            className={`
                ${variantStyles[variant]} 
                ${variant !== 'link' ? sizeStyles[size] : ''} 
                ${icon ? 'flex items-center space-x-2' : ''}
                ${className}
            `.trim()}
            style={{
                backgroundColor: getBgColor(),
                color: getTextColor()
            }}
        >
            {icon && <span className="flex-shrink-0">{icon}</span>}
            <span>{children}</span>
        </button>
    );
};