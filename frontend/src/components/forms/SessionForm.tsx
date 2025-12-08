import React, { useState, useEffect } from 'react';
import { COLORS } from '../../constants/colors';
import type { SessionRes } from '../../types';

interface SessionFormProps {
    mode?: 'create' | 'edit';
    initialData?: SessionRes;
    onSubmit: (data: any) => void;
    onCancel: () => void;
}

export const SessionForm: React.FC<SessionFormProps> = ({
                                                            mode = 'create',
                                                            initialData,
                                                            onSubmit,
                                                            onCancel
                                                        }) => {
    const [formData, setFormData] = useState({
        sessionCode: '',
        startDate: '',
        endDate: ''
    });

    useEffect(() => {
        if (mode === 'edit' && initialData) {
            setFormData({
                sessionCode: initialData.sessionCode,
                startDate: initialData.startDate,
                endDate: initialData.endDate
            });
        }
    }, [mode, initialData]);

    const handleSubmit = () => {
        if (!formData.sessionCode || !formData.startDate || !formData.endDate) {
            alert('Please fill in all required fields');
            return;
        }

        onSubmit(formData);
    };

    return (
        <div className="space-y-6">
            <h3 className="text-xl font-bold" style={{ color: COLORS.dark }}>
                {mode === 'create' ? 'Create New Session' : 'Edit Session'}
            </h3>

            <div className="grid grid-cols-2 gap-4">
                <div className="col-span-2">
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Session Code *
                    </label>
                    <input
                        type="text"
                        value={formData.sessionCode}
                        onChange={(e) => setFormData({ ...formData, sessionCode: e.target.value })}
                        disabled={mode === 'edit'}
                        className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-50 disabled:text-gray-500 disabled:cursor-not-allowed"
                    />
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        Format: YYYY-S# (e.g., 2025-S1, 2025-S2, ...)
                        {mode === 'edit' && ' • Cannot be changed'}
                    </p>
                </div>

                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Start Date *
                    </label>
                    <input
                        type="date"
                        value={formData.startDate}
                        onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        End Date *
                    </label>
                    <input
                        type="date"
                        value={formData.endDate}
                        onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg"
                    />
                </div>
            </div>

            {mode === 'edit' && (
                <div className="p-3 rounded-lg" style={{ backgroundColor: COLORS.infoBg }}>
                    <p className="text-xs" style={{ color: COLORS.infoText }}>
                        ℹ️ Only UPCOMING sessions can be edited. Once a session becomes ACTIVE, dates are locked.
                    </p>
                </div>
            )}

            <div className="flex space-x-3 pt-4" style={{ borderTop: `1px solid ${COLORS.bg}` }}>
                <button
                    onClick={handleSubmit}
                    type="button"
                    className="flex-1 px-4 py-2 text-white rounded-lg shadow-sm hover:shadow-md transition-shadow cursor-pointer"
                    style={{ backgroundColor: COLORS.orange }}
                >
                    {mode === 'create' ? 'Create Session' : 'Save Changes'}
                </button>
                <button
                    onClick={onCancel}
                    type="button"
                    className="flex-1 px-4 py-2 rounded-lg shadow-sm hover:shadow transition-shadow cursor-pointer"
                    style={{ backgroundColor: COLORS.bg, color: COLORS.dark }}
                >
                    Cancel
                </button>
            </div>
        </div>
    );
};