import React, { useState, useEffect } from 'react';
import { PageHeader } from '../components/common/PageHeader';
import { Button } from '../components/common/Button';
import { DataTable, type ColumnDef } from '../components/common/DataTable';
import { Modal } from '../components/common/Modal';
import { COLORS } from '../constants/colors';
import { sessionApi } from '../services/api';
import { SessionForm } from '../components/forms/SessionForm';
import { SESSION_STATUS } from '../types';
import type { SessionRes } from '../types';
import { Settings } from 'lucide-react';
import { useAuth } from '../contexts/AppContext';

export const SystemSettings: React.FC = () => {
    const { currentUser } = useAuth();
    const [activeTab, setActiveTab] = useState<'sessions' | 'email'>('sessions');
    const isSuperAdmin = currentUser?.role === 'ADMIN' && (currentUser as any).isSuperAdmin;

    if (!isSuperAdmin) {
        return (
            <div className="space-y-6">
                <PageHeader title="System Settings" />
                <div className="flex items-center justify-center h-64">
                    <div className="text-center">
                        <div className="text-xl font-bold text-red-600 mb-2">Access Denied</div>
                        <p className="text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            This page requires Super Admin privileges.
                        </p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            <PageHeader title="System Settings" subtitle="Super Admin Only" />

            <div className="bg-white rounded-3xl shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                {/* Tabs */}
                <div className="flex" style={{ borderBottom: `1px solid ${COLORS.bg}` }}>
                    {[
                        { key: 'sessions', label: 'Session Management' },
                        { key: 'email', label: 'Email Settings' }
                    ].map(tab => (
                        <button
                            key={tab.key}
                            onClick={() => setActiveTab(tab.key as any)}
                            className="px-6 py-4 text-sm font-medium cursor-pointer"
                            style={{
                                color: activeTab === tab.key ? COLORS.orange : COLORS.dark + 'AA',
                                borderBottom: activeTab === tab.key ? `2px solid ${COLORS.orange}` : 'none'
                            }}
                        >
                            {tab.label}
                        </button>
                    ))}
                </div>

                <div className="p-6">
                    {activeTab === 'sessions' && <SessionManagementTab />}
                    {activeTab === 'email' && <EmailSettingsTab />}
                </div>
            </div>
        </div>
    );
};

// Session Management Tab
const SessionManagementTab: React.FC = () => {
    const [sessions, setSessions] = useState<SessionRes[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [editingSession, setEditingSession] = useState<SessionRes | null>(null);

    useEffect(() => {
        loadSessions();
    }, []);

    const loadSessions = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await sessionApi.getAll();
            setSessions(response.data);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to load sessions');
            console.error('Error loading sessions:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmitSession = async (formData: any) => {
        try {
            await sessionApi.create(formData);
            setShowCreateModal(false);
            await loadSessions();
            alert('Session created successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to create session: ' + errorMsg);
        }
    };

    const handleManageSession = (session: SessionRes) => {
        if (session.status !== 'UPCOMING') {
            alert('Only UPCOMING sessions can be edited');
            return;
        }

        setEditingSession(session);
        setShowEditModal(true);
    };

    const handleUpdateSession = async (data: any) => {
        if (!editingSession) return;

        try {
            await sessionApi.update(editingSession.id, data);
            setShowEditModal(false);
            setEditingSession(null);
            await loadSessions();
            alert('Session updated successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to update session: ' + errorMsg);
        }
    };

    if (loading) {
        return (
            <div className="text-center py-8">
                <div className="text-lg" style={{ color: COLORS.dark }}>Loading term/sessions...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="text-center py-8">
                <div className="text-lg text-red-600 mb-2">Error: {error}</div>
                <Button variant="primary" onClick={loadSessions}>
                    Retry
                </Button>
            </div>
        );
    }

    // Define columns
    const sessionColumns: ColumnDef[] = [
        {
            key: 'sessionCode',
            header: 'Session Code',
            render: (session) => (
                <span className="text-sm font-mono font-bold" style={{ color: COLORS.dark }}>
                    {session.sessionCode}
                </span>
            )
        },
        {
            key: 'period',
            header: 'Period',
            render: (session) => (
                <span className="text-sm" style={{ color: COLORS.dark }}>
                    {session.startDate} ~ {session.endDate}
                </span>
            )
        },
        {
            key: 'status',
            header: 'Status',
            render: (session) => {
                const statusConfig = SESSION_STATUS[session.status as keyof typeof SESSION_STATUS];
                return (
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusConfig?.color || 'bg-gray-100 text-gray-600'}`}>
                        {statusConfig?.label || session.status}
                    </span>
                );
            }
        },
        {
            key: 'actions',
            header: 'Actions',
            render: (session) => {
                const canEdit = session.status === 'UPCOMING';

                return canEdit ? (
                    <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleManageSession(session)}
                    >
                        Edit
                    </Button>
                ) : (
                    <span className="text-sm text-gray-400">Locked</span>
                );
            }
        }
    ];

    return (
        <div>
            <div className="flex justify-between mb-4">
                <h3 className="text-lg font-bold" style={{ color: COLORS.dark }}>
                    Academic Sessions ({sessions.length})
                </h3>
                <Button variant="primary" onClick={() => setShowCreateModal(true)}>
                    + Create Session
                </Button>
            </div>

            {sessions.length === 0 ? (
                <div className="text-center py-12 rounded-xl" style={{ backgroundColor: COLORS.bg + '40' }}>
                    <p className="text-sm mb-4" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        No sessions found. Create your first session to get started.
                    </p>
                    <Button variant="primary" onClick={() => setShowCreateModal(true)}>
                        Create First term/Session
                    </Button>
                </div>
            ) : (
                <DataTable
                    columns={sessionColumns}
                    data={sessions}
                    emptyMessage="No sessions found"
                />
            )}

            {/* Create Modal */}
            <Modal isOpen={showCreateModal} onClose={() => setShowCreateModal(false)} size="xl">
                <SessionForm onSubmit={handleSubmitSession} onCancel={() => setShowCreateModal(false)} />
            </Modal>

            {/* Edit Modal */}
            <Modal isOpen={showEditModal} onClose={() => { setShowEditModal(false); setEditingSession(null); }} size="xl">
                {editingSession && (
                    <SessionForm
                        mode="edit"
                        initialData={editingSession}
                        onSubmit={handleUpdateSession}
                        onCancel={() => { setShowEditModal(false); setEditingSession(null); }}
                    />
                )}
            </Modal>
        </div>
    );
};

// Email Settings Tab (Placeholder)
const EmailSettingsTab: React.FC = () => {
    return (
        <div className="text-center py-16">
            <div className="inline-flex items-center justify-center w-20 h-20 rounded-full mb-4"
                 style={{ backgroundColor: COLORS.cream }}>
                <Settings size={40} style={{ color: COLORS.orange }} />
            </div>
            <h3 className="text-2xl font-bold mb-2" style={{ color: COLORS.dark }}>
                Email Settings
            </h3>
            <p className="text-sm mb-6" style={{ color: COLORS.dark, opacity: 0.6 }}>
                This feature is under development
            </p>
            <div className="inline-block px-6 py-2 rounded-full text-sm font-medium"
                 style={{ backgroundColor: COLORS.orange + '20', color: COLORS.orange }}>
                Coming Soon
            </div>
        </div>
    );
};