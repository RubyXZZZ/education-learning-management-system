import React from 'react';
import { COLORS } from '../../constants/colors';
import { Search } from 'lucide-react';

interface SearchBarProps {
    value: string;
    onChange: (value: string) => void;
    placeholder?: string;
}

export const SearchBar: React.FC<SearchBarProps> = ({
                                                        value,
                                                        onChange,
                                                        placeholder = 'Search...'
                                                    }) => {
    return (
        <div className="relative flex-1">
            <Search
                size={18}
                className="absolute left-3 top-1/2 transform -translate-y-1/2"
                style={{ color: COLORS.dark, opacity: 0.5 }}
            />
            <input
                type="text"
                placeholder={placeholder}
                value={value}
                onChange={(e) => onChange(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border-2 rounded-lg focus:outline-none focus:ring-2 text-sm"
                style={{
                    borderColor: COLORS.orange,
                    backgroundColor: 'white',
                    color: COLORS.dark
                }}
            />
        </div>
    );
};