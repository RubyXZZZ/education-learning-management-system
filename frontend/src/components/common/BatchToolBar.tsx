import React from 'react';
import { Button } from './Button';
import { COLORS } from '../../constants/colors';

interface BatchAction {
    label: string;
    icon?: React.ReactNode;
    variant?: 'primary' | 'secondary' | 'ghost' | 'danger';
    onClick: () => void;
}

interface BatchToolbarProps {
    selectedCount: number;
    itemLabel?: string; // e.g., "users", "sections"
    onClear: () => void;
    actions?: BatchAction[];
}

export const BatchToolBar: React.FC<BatchToolbarProps> = ({
                                                              selectedCount,
                                                              itemLabel = 'items',
                                                              onClear,
                                                              actions = []
                                                          }) => {
    if (selectedCount === 0) return null;

    return (
        <div
            className="mb-4 p-4 rounded-xl flex justify-between shadow-sm"
            style={{ backgroundColor: '#FFF7ED', border: `1px solid ${COLORS.lightOrange}` }}
        >
            <div className="flex items-center space-x-4">
                <span className="font-medium" style={{ color: COLORS.dark }}>
                    {selectedCount} {itemLabel} selected
                </span>
                <Button variant="link" onClick={onClear}>
                    Clear
                </Button>
            </div>

            {actions.length > 0 && (
                <div className="flex space-x-3">
                    {actions.map((action, index) => (
                        <Button
                            key={index}
                            variant={action.variant || 'secondary'}
                            size="sm"
                            icon={action.icon}
                            onClick={action.onClick}
                        >
                            {action.label}
                        </Button>
                    ))}
                </div>
            )}
        </div>
    );
};