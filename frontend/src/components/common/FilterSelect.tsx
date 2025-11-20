import React from 'react';
import { COLORS } from '../../constants/colors';

export interface FilterOption {
    value: string;
    label: string;
}

interface FilterSelectProps {
    value: string;
    options: FilterOption[];
    onChange: (value: string) => void;
    disabled?: boolean;
    placeholder?: string;
    width?: string;
}

export const FilterSelect: React.FC<FilterSelectProps> = ({
                                                              value,
                                                              options,
                                                              onChange,
                                                              disabled = false,
                                                              width = '140px'
                                                          }) => {
    return (
        <select
            value={value}
            onChange={(e) => onChange(e.target.value)}
            disabled={disabled}
            className="px-4 py-2 border-2 rounded-lg cursor-pointer font-medium text-sm disabled:opacity-40 disabled:cursor-not-allowed"
            style={{
                borderColor: COLORS.orange,
                backgroundColor: COLORS.bg,
                color: COLORS.dark,
                width
            }}
        >
            {options.map(opt => (
                <option key={opt.value} value={opt.value}>
                    {opt.label}
                </option>
            ))}
        </select>
    );
};