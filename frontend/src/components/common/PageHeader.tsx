import React from 'react';
import { COLORS } from '../../constants/colors';

interface PageHeaderProps {
    title: string;
    subtitle?: string;
}

export const PageHeader: React.FC<PageHeaderProps> = ({ title, subtitle }) => (
    <div
        className="rounded-3xl p-4 text-white"
        style={{ backgroundColor: COLORS.dark }}
    >
        <h1 className="text-3xl font-bold">{title}</h1>
        {subtitle && <p style={{ color: COLORS.bg }}>{subtitle}</p>}
    </div>
);