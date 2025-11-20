import React from 'react';
import { COLORS } from '../../constants/colors';
import { Button } from './Button';
import { Badge } from './Badge';

export interface ColumnDef<T = any> {
    key: string;
    header: string;
    width?: string;
    render?: (item: T) => React.ReactNode;
    className?: string;
}

export interface DataTableProps<T = any> {
    columns: ColumnDef<T>[];
    data: T[];
    selectedIds?: Set<string>;
    onToggle?: (id: string) => void;
    onSelectAll?: () => void;
    onRowClick?: (item: T) => void;
    emptyMessage?: string;
    loading?: boolean;
    keyExtractor?: (item: T) => string;
    showCheckbox?: boolean;
    rowClassName?: (item: T) => string;
}

export function DataTable<T extends Record<string, any>>({
                                                             columns,
                                                             data,
                                                             selectedIds = new Set(),
                                                             onToggle,
                                                             onSelectAll,
                                                             onRowClick,
                                                             emptyMessage = 'No data available',
                                                             loading = false,
                                                             keyExtractor = (item) => item.id,
                                                             showCheckbox = false,
                                                             rowClassName
                                                         }: DataTableProps<T>) {

    if (loading) {
        return (
            <div className="text-center py-12">
                <div className="text-lg" style={{ color: COLORS.dark }}>Loading...</div>
            </div>
        );
    }

    if (data.length === 0) {
        return (
            <div className="text-center py-12 rounded-xl" style={{ backgroundColor: COLORS.bg + '40' }}>
                <p className="text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                    {emptyMessage}
                </p>
            </div>
        );
    }

    const allSelected = showCheckbox && data.length > 0 && selectedIds.size === data.length;

    return (
        <div className="overflow-x-auto">
            <table className="w-full">
                <thead style={{ backgroundColor: COLORS.bg + '40' }}>
                <tr>
                    {showCheckbox && (
                        <th className="px-4 py-3 w-12 text-center">
                            <input
                                type="checkbox"
                                checked={allSelected}
                                onChange={onSelectAll}
                                className="w-4 h-4 rounded cursor-pointer"
                                style={{ accentColor: COLORS.orange }}
                            />
                        </th>
                    )}
                    {columns.map((col) => (
                        <th
                            key={col.key}
                            className={`px-4 py-3 text-left text-sm font-semibold ${col.width || ''} ${col.className || ''}`}
                            style={{ color: COLORS.dark }}
                        >
                            {col.header}
                        </th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {data.map((item) => {
                    const id = keyExtractor(item);
                    const isSelected = selectedIds.has(id);
                    const baseRowClass = "border-b hover:bg-gray-50 transition-colors";
                    const customRowClass = rowClassName ? rowClassName(item) : '';

                    return (
                        <tr
                            key={id}
                            className={`${baseRowClass} ${customRowClass} ${onRowClick ? 'cursor-pointer' : ''}`}
                            style={{ borderColor: COLORS.bg }}
                            onClick={() => onRowClick?.(item)}
                        >
                            {showCheckbox && (
                                <td className="px-4 py-3 text-center" onClick={(e) => e.stopPropagation()}>
                                    <input
                                        type="checkbox"
                                        checked={isSelected}
                                        onChange={() => onToggle?.(id)}
                                        className="w-4 h-4 rounded cursor-pointer"
                                        style={{ accentColor: COLORS.orange }}
                                    />
                                </td>
                            )}
                            {columns.map((col) => (
                                <td
                                    key={col.key}
                                    className={`px-4 py-3 text-sm ${col.className || ''}`}
                                    style={{ color: COLORS.dark }}
                                >
                                    {col.render ? col.render(item) : item[col.key]}
                                </td>
                            ))}
                        </tr>
                    );
                })}
                </tbody>
            </table>
        </div>
    );
}

// Helper functions for common render patterns
export const TableRenderers = {

    badge: (fieldKey: string, config: Record<string, { color?: string; label: string; value: string }>) =>
        (item: any) => {
            // Get the actual value from the item using the field key
            const fieldValue = item[fieldKey];

            // Find matching config by value
            const statusKey = Object.keys(config).find(k => config[k].value === fieldValue) || fieldValue;
            const badgeConfig = config[statusKey];

            return (
                <Badge className={badgeConfig?.color || 'bg-gray-100 text-gray-600'}>
                    {badgeConfig?.label || fieldValue}
                </Badge>
            );
        },

    /**
     * Render as clickable link
     * @example
     * { key: 'fullName', header: 'Name', render: TableRenderers.link((item) => handleView(item.id)) }
     */
    link: (onClick: (item: any) => void) =>
        (item: any) => (
            <button
                onClick={(e) => {
                    e.stopPropagation();
                    onClick(item);
                }}
                className="hover:underline cursor-pointer font-medium"
                style={{ color: COLORS.orange }}
            >
                {item.fullName || item.name}
            </button>
        ),

    //Render action buttons
    actions: (buttons: Array<{
        label: string;
        variant?: 'ghost' | 'primary' | 'secondary' | 'danger';
        icon?: React.ReactNode;
        onClick: (item: any) => void;
    }>) =>
        (item: any) => (
            <div className="flex space-x-2" onClick={(e) => e.stopPropagation()}>
                {buttons.map((btn, idx) => (
                    <Button
                        key={idx}
                        variant={btn.variant || 'ghost'}
                        size="sm"
                        icon={btn.icon}
                        onClick={() => btn.onClick(item)}
                    >
                        {btn.label}
                    </Button>
                ))}
            </div>
        )
};