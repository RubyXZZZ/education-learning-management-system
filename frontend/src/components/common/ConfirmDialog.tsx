import React from 'react';
import { COLORS } from '../../constants/colors';
import { AlertTriangle } from 'lucide-react';

interface ConfirmDialogProps {
    isOpen: boolean;
    title: string;
    message: string;
    confirmText?: string;
    cancelText?: string;
    onConfirm: () => void;
    onCancel: () => void;
    variant?: 'default' | 'danger' | 'warning';
    warningNote?: string;
}

export const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
                                                                isOpen,
                                                                title,
                                                                message,
                                                                confirmText = 'Confirm',
                                                                cancelText = 'Cancel',
                                                                onConfirm,
                                                                onCancel,
                                                                variant = 'default',
                                                                warningNote
                                                            }) => {
    if (!isOpen) return null;

    const getConfirmButtonStyle = () => {
        switch (variant) {
            case 'danger':
                return { backgroundColor: '#dc2626', hoverBg: '#b91c1c' };
            case 'warning':
                return { backgroundColor: '#f59e0b', hoverBg: '#d97706' };
            default:
                return { backgroundColor: COLORS.orange, hoverBg: '#d97706' };
        }
    };

    const buttonStyle = getConfirmButtonStyle();

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-2xl p-6 max-w-md w-full mx-4 shadow-xl">
                <h3 className="text-xl font-bold mb-4" style={{ color: COLORS.dark }}>
                    {title}
                </h3>

                <p className="text-sm mb-4" style={{ color: COLORS.dark }}>
                    {message}
                </p>

                {warningNote && (
                    <div className="p-3 bg-yellow-50 border border-yellow-200 rounded-lg mb-4 flex items-start space-x-2">
                        <AlertTriangle size={16} className="text-yellow-600 mt-0.5 flex-shrink-0" />
                        <p className="text-xs text-yellow-700">
                            {warningNote}
                        </p>
                    </div>
                )}

                <div className="flex space-x-3">
                    <button
                        onClick={onConfirm}
                        className="flex-1 px-4 py-2 text-white rounded-lg shadow-sm hover:shadow-md transition-all cursor-pointer"
                        style={{ backgroundColor: buttonStyle.backgroundColor }}
                        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = buttonStyle.hoverBg}
                        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = buttonStyle.backgroundColor}
                    >
                        {confirmText}
                    </button>
                    <button
                        onClick={onCancel}
                        className="flex-1 px-4 py-2 rounded-lg shadow-sm hover:shadow transition-shadow cursor-pointer"
                        style={{ backgroundColor: COLORS.bg, color: COLORS.dark }}
                    >
                        {cancelText}
                    </button>
                </div>
            </div>
        </div>
    );
};