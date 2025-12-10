import React, { useState, useEffect } from 'react';
import { PageHeader } from '../components/common/PageHeader';
import { SearchBar } from '../components/common/SearchBar';
import { FilterSelect, type FilterOption } from '../components/common/FilterSelect';
import { Button } from '../components/common/Button';
import { DataTable, TableRenderers, type ColumnDef } from '../components/common/DataTable';
import { BatchToolBar } from '../components/common/BatchToolBar';
import { Modal } from '../components/common/Modal';
import { COLORS } from '../constants/colors';
import { studentApi, instructorApi, adminApi } from '../services/api';
import { USER_STATUS, STUDENT_TYPE } from '../types';
import { UserDetail } from './UserDetail';
import { BaseUserForm } from '../components/forms/BaseUserForm';
import { useAuth } from '../contexts/AppContext';
import { sendPasswordResetEmail } from 'firebase/auth';
import { auth } from '../services/firebase';
import { Mail, Trash2 } from 'lucide-react';
import type { StudentList, InstructorList, AdminList, StudentRes, InstructorRes, AdminRes } from '../types';

export const UsersMgmt: React.FC = () => {
    const { currentUser } = useAuth();
    const [activeTab, setActiveTab] = useState<'students' | 'instructors' | 'admins'>('students');
    const [searchTerm, setSearchTerm] = useState('');
    const [filterField, setFilterField] = useState<'none' | 'status' | 'type'>('none');
    const [statusFilter, setStatusFilter] = useState('ALL');
    const [typeFilter, setTypeFilter] = useState('ALL');

    const [allData, setAllData] = useState<any[]>([]);
    const [students, setStudents] = useState<StudentList[]>([]);
    const [instructors, setInstructors] = useState<InstructorList[]>([]);
    const [admins, setAdmins] = useState<AdminList[]>([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [viewMode, setViewMode] = useState<'list' | 'detail'>('list');
    const [selectedUserId, setSelectedUserId] = useState<string | null>(null);
    const [selectedUserRole, setSelectedUserRole] = useState<'STUDENT' | 'INSTRUCTOR' | 'ADMIN'>('STUDENT');

    const [showEditModal, setShowEditModal] = useState(false);
    const [editingUserData, setEditingUserData] = useState<StudentRes | InstructorRes | AdminRes | null>(null);
    const [editingUserRole, setEditingUserRole] = useState<'student' | 'instructor' | 'admin'>('student');

    const [showCreateModal, setShowCreateModal] = useState(false);
    const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set());
    const [showBatchStatusModal, setShowBatchStatusModal] = useState(false);
    const [batchTargetStatus, setBatchTargetStatus] = useState<string>('');

    const isSuperAdmin = currentUser?.role === 'ADMIN' && (currentUser as any).isSuperAdmin;

    // Search filter effect
    useEffect(() => {
        if (!searchTerm) {
            if (activeTab === 'students') setStudents(allData);
            else if (activeTab === 'instructors') setInstructors(allData);
            else setAdmins(allData);
            return;
        }

        const filtered = allData.filter((item: any) =>
            item.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            item.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            item.studentNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            item.employeeNumber?.toLowerCase().includes(searchTerm.toLowerCase())
        );

        if (activeTab === 'students') setStudents(filtered);
        else if (activeTab === 'instructors') setInstructors(filtered);
        else setAdmins(filtered);

    }, [searchTerm, allData, activeTab]);

    const loadData = async () => {
        try {
            setLoading(true);
            setError(null);

            if (activeTab === 'students') {
                let res;
                if (filterField === 'status' && statusFilter !== 'ALL') {
                    res = await studentApi.getByStatus(statusFilter);
                } else if (filterField === 'type' && typeFilter !== 'ALL') {
                    res = await studentApi.getByType(typeFilter);
                } else {
                    res = await studentApi.getList();
                }
                setAllData(res.data);
                setStudents(res.data);

            } else if (activeTab === 'instructors') {
                let res;
                if (statusFilter !== 'ALL') {
                    res = await instructorApi.getByStatus(statusFilter);
                } else {
                    res = await instructorApi.getList();
                }
                setAllData(res.data);
                setInstructors(res.data);

            } else if (activeTab === 'admins') {
                let res;
                if (statusFilter !== 'ALL') {
                    res = await adminApi.getByStatus(statusFilter);
                } else {
                    res = await adminApi.getList();
                }
                setAllData(res.data);
                setAdmins(res.data);
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to load data');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [activeTab]);

    useEffect(() => {
        loadData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [statusFilter, typeFilter, filterField]);

    const handleViewDetail = (id: string, role: 'Student' | 'Instructor' | 'Admin') => {
        setSelectedUserId(id);
        setSelectedUserRole(role.toUpperCase() as any);
        setViewMode('detail');
    };

    const handleBackToList = () => {
        setViewMode('list');
        setSelectedUserId(null);
    };

    const handleCreateUser = async (formData: any) => {
        try {
            if (activeTab === 'students') {
                await studentApi.create(formData);
            } else if (activeTab === 'instructors') {
                await instructorApi.create(formData);
            } else {
                await adminApi.create(formData);
            }

            setShowCreateModal(false);
            loadData();
            alert(`${activeTab.slice(0, -1)} created successfully!`);
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to create: ' + errorMsg);
        }
    };

    const handleEditUser = async (id: string, role: 'Student' | 'Instructor' | 'Admin') => {
        try {
            const roleMap = {
                'Student': 'student',
                'Instructor': 'instructor',
                'Admin': 'admin'
            } as const;

            setEditingUserRole(roleMap[role]);

            const apiMap = {
                'Student': studentApi,
                'Instructor': instructorApi,
                'Admin': adminApi
            };

            const res = await apiMap[role].getById(id);
            setEditingUserData(res.data);
            setShowEditModal(true);
        } catch (err) {
            alert('Failed to load user data: ' + (err instanceof Error ? err.message : ''));
        }
    };

    const handleUpdateUser = async (formData: any) => {
        if (!editingUserData) return;

        try {
            const apiMap = {
                'student': studentApi,
                'instructor': instructorApi,
                'admin': adminApi
            };

            await apiMap[editingUserRole].update(editingUserData.id, formData);

            setShowEditModal(false);
            setEditingUserData(null);
            loadData();
            alert('Updated successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to update: ' + errorMsg);
        }
    };

    const handleToggle = (id: string) => {
        const newSelected = new Set(selectedIds);
        if (newSelected.has(id)) newSelected.delete(id);
        else newSelected.add(id);
        setSelectedIds(newSelected);
    };

    const handleSelectAll = () => {
        const currentList = activeTab === 'students' ? students :
            activeTab === 'instructors' ? instructors : admins;
        const allIds = currentList.map((item: any) => item.id);

        if (selectedIds.size === allIds.length) setSelectedIds(new Set());
        else setSelectedIds(new Set(allIds));
    };

    const handleBatchStatus = async () => {
        if (!batchTargetStatus) {
            alert('Please select a status');
            return;
        }

        try {
            setLoading(true);
            setShowBatchStatusModal(false);

            const apiMap = {
                'students': studentApi,
                'instructors': instructorApi,
                'admins': adminApi
            };

            const methodMap: Record<string, string> = {
                'ACTIVE': 'reactivate',
                'INACTIVE': 'deactivate',
                'SUSPENDED': 'suspend'
            };

            const method = methodMap[batchTargetStatus];

            if (!method) {
                alert('Invalid status: ' + batchTargetStatus);
                return;
            }

            const promises = Array.from(selectedIds).map(id =>
                (apiMap[activeTab] as any)[method](id)
            );

            await Promise.all(promises);

            setSelectedIds(new Set());
            await loadData();
            alert(`Successfully updated ${promises.length} ${activeTab} to ${batchTargetStatus}`);
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to update status: ' + errorMsg);
        } finally {
            setLoading(false);
        }
    };

    const handleBatchDelete = async () => {
        if (selectedIds.size === 0) return;

        if (!confirm(`Delete ${selectedIds.size} ${activeTab}?\n\nThis action cannot be undone. All related data will be permanently deleted.`)) {
            return;
        }

        try {
            setLoading(true);

            const apiMap = {
                'students': studentApi,
                'instructors': instructorApi,
                'admins': adminApi
            };

            const deletePromises = Array.from(selectedIds).map(id =>
                apiMap[activeTab].delete(id)
            );

            await Promise.all(deletePromises);

            setSelectedIds(new Set());
            await loadData();
            alert(`Successfully deleted ${deletePromises.length} ${activeTab}`);
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to delete: ' + errorMsg);
        } finally {
            setLoading(false);
        }
    };

    const handleBatchSendActivation = async () => {
        if (selectedIds.size === 0) return;

        const selectedUsers = (activeTab === 'students' ? students :
            activeTab === 'instructors' ? instructors : admins)
            .filter((u: any) => selectedIds.has(u.id));

        const pendingUsers = selectedUsers.filter((u: any) => u.status === 'PENDING');

        if (pendingUsers.length === 0) {
            alert('No pending users selected');
            return;
        }

        if (!confirm(`Send activation email to ${pendingUsers.length} pending users?`)) return;

        try {
            let successCount = 0;
            let failCount = 0;

            for (const user of pendingUsers) {
                try {
                    await sendPasswordResetEmail(auth, user.email);
                    successCount++;
                } catch (err) {
                    console.error(`Failed to send to ${user.email}:`, err);
                    failCount++;
                }
            }

            alert(`✅ Sent: ${successCount}\n❌ Failed: ${failCount}`);
            setSelectedIds(new Set());

        } catch (err) {
            alert('Failed to send emails');
        }
    };

    if (viewMode === 'detail' && selectedUserId) {
        return <UserDetail userId={selectedUserId} userRole={selectedUserRole} onBack={handleBackToList} />;
    }

    if (loading) {
        return (
            <div className="space-y-6">
                <PageHeader title="User Management" />
                <div className="flex items-center justify-center h-64">
                    <div className="text-xl" style={{ color: COLORS.dark }}>Loading...</div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="space-y-6">
                <PageHeader title="User Management" />
                <div className="flex items-center justify-center h-64">
                    <div className="text-xl text-red-600">Error: {error}</div>
                </div>
            </div>
        );
    }

    const currentData = activeTab === 'students' ? students :
        activeTab === 'instructors' ? instructors : admins;

    const statusFilterOptions: FilterOption[] = [
        { value: 'ALL', label: 'All' },
        ...Object.values(USER_STATUS).map(s => ({ value: s.value, label: s.label }))
    ];

    const typeFilterOptions: FilterOption[] = [
        { value: 'ALL', label: 'All' },
        ...Object.values(STUDENT_TYPE).map(t => ({ value: t.value, label: t.label }))
    ];

    const filterFieldOptions: FilterOption[] = [
        { value: 'none', label: 'Filter' },
        { value: 'status', label: 'Status' },
        { value: 'type', label: 'Type' }
    ];

    // Define columns based on active tab
    const getColumns = (): ColumnDef[] => {
        if (activeTab === 'students') {
            return [
                {
                    key: 'studentNumber',
                    header: 'Student Number',
                    render: (item) => (
                        <span className="text-sm font-mono" style={{ color: COLORS.dark, opacity: 0.7 }}>
                            {item.studentNumber}
                        </span>
                    )
                },
                {
                    key: 'fullName',
                    header: 'Name',
                    render: TableRenderers.link((item) => handleViewDetail(item.id, 'Student'))
                },
                { key: 'email', header: 'Email', className: 'text-sm' },
                {
                    key: 'studentType',
                    header: 'Student Type',
                    render: TableRenderers.badge('studentType', STUDENT_TYPE)
                },
                {
                    key: 'placementLevel',
                    header: 'Placement',
                    render: (item) => (
                        <span className="text-sm" style={{ color: COLORS.dark }}>
                            {item.placementLevel ? `L${item.placementLevel}` : 'N/A'}
                        </span>
                    )
                },
                { key: 'totalHoursEnrolled', header: 'Hours', className: 'text-sm font-medium' },
                { key: 'enrolledCounts', header: 'Enrolled', className: 'text-sm' },
                {
                    key: 'status',
                    header: 'Status',
                    render: TableRenderers.badge('status',USER_STATUS)
                },
                {
                    key: 'actions',
                    header: 'Actions',
                    render: TableRenderers.actions([
                        { label: 'Edit', variant: 'ghost', onClick: (item) => handleEditUser(item.id, 'Student') }
                    ])
                }
            ];
        } else if (activeTab === 'instructors') {
            return [
                {
                    key: 'employeeNumber',
                    header: 'Employee Number',
                    render: (item) => (
                        <span className="text-sm font-mono" style={{ color: COLORS.dark, opacity: 0.7 }}>
                            {item.employeeNumber}
                        </span>
                    )
                },
                {
                    key: 'fullName',
                    header: 'Name',
                    render: TableRenderers.link((item) => handleViewDetail(item.id, 'Instructor'))
                },
                { key: 'email', header: 'Email', className: 'text-sm' },
                { key: 'department', header: 'Department', className: 'text-sm' },
                { key: 'teachingCounts', header: 'Teaching', className: 'text-sm' },
                {
                    key: 'status',
                    header: 'Status',
                    render: TableRenderers.badge('status',USER_STATUS)
                },
                {
                    key: 'actions',
                    header: 'Actions',
                    render: TableRenderers.actions([
                        { label: 'Edit', variant: 'ghost', onClick: (item) => handleEditUser(item.id, 'Instructor') }
                    ])
                }
            ];
        } else {
            return [
                {
                    key: 'employeeNumber',
                    header: 'Employee Number',
                    render: (item) => (
                        <span className="text-sm font-mono" style={{ color: COLORS.dark, opacity: 0.7 }}>
                            {item.employeeNumber}
                        </span>
                    )
                },
                {
                    key: 'fullName',
                    header: 'Name',
                    render: TableRenderers.link((item) => handleViewDetail(item.id, 'Admin'))
                },
                { key: 'email', header: 'Email', className: 'text-sm' },
                { key: 'department', header: 'Department', className: 'text-sm' },
                { key: 'position', header: 'Position', className: 'text-sm' },
                {
                    key: 'isSuperAdmin',
                    header: 'Type',
                    render: (item) => (
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${item.isSuperAdmin ? 'bg-red-100 text-red-700' : 'bg-blue-100 text-blue-700'}`}>
                            {item.isSuperAdmin ? 'Super Admin' : 'Admin'}
                        </span>
                    )
                },
                {
                    key: 'status',
                    header: 'Status',
                    render: TableRenderers.badge('status',USER_STATUS)
                },
                {
                    key: 'actions',
                    header: 'Actions',
                    render: TableRenderers.actions([
                        { label: 'Edit', variant: 'ghost', onClick: (item) => handleEditUser(item.id, 'Admin') }
                    ])
                }
            ];
        }
    };

    return (
        <div className="space-y-6">
            <PageHeader title="User Management" />

            <div className="bg-white rounded-3xl shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                {/* Tabs */}
                <div className="flex" style={{ borderBottom: `1px solid ${COLORS.bg}` }}>
                    {[
                        { key: 'students', label: 'Students' },
                        { key: 'instructors', label: 'Instructors' },
                        ...(isSuperAdmin ? [{ key: 'admins', label: 'Admins' }] : [])
                    ].map(tab => (
                        <button
                            key={tab.key}
                            onClick={() => {
                                setActiveTab(tab.key as any);
                                setSearchTerm('');
                                setFilterField('none');
                                setStatusFilter('ALL');
                                setTypeFilter('ALL');
                                setSelectedIds(new Set());
                            }}
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
                    {/* Batch Toolbar */}
                    <BatchToolBar
                        selectedCount={selectedIds.size}
                        itemLabel={activeTab}
                        onClear={() => setSelectedIds(new Set())}
                        actions={[
                            {
                                label: 'Change Status',
                                variant: 'secondary',
                                onClick: () => setShowBatchStatusModal(true)
                            },
                            {
                                label: 'Send Activation',
                                icon: <Mail size={16} />,
                                variant: 'secondary',
                                onClick: handleBatchSendActivation
                            },
                            {
                                label: 'Delete',
                                icon: <Trash2 size={16} />,
                                variant: 'danger',
                                onClick: handleBatchDelete
                            }
                        ]}
                    />

                    {/* Search and Filters */}
                    <div className="mb-4 flex space-x-3">
                        <SearchBar value={searchTerm} onChange={setSearchTerm} />

                        {activeTab === 'students' && (
                            <>
                                <FilterSelect
                                    value={filterField}
                                    options={filterFieldOptions}
                                    onChange={(val) => {
                                        setFilterField(val as any);
                                        setStatusFilter('ALL');
                                        setTypeFilter('ALL');
                                    }}
                                />

                                <FilterSelect
                                    value={filterField === 'status' ? statusFilter : typeFilter}
                                    options={filterField === 'status' ? statusFilterOptions : typeFilterOptions}
                                    onChange={(val) => {
                                        if (filterField === 'status') setStatusFilter(val);
                                        else setTypeFilter(val);
                                    }}
                                    disabled={filterField === 'none'}
                                />
                            </>
                        )}

                        {(activeTab === 'instructors' || activeTab === 'admins') && (
                            <FilterSelect
                                value={statusFilter}
                                options={statusFilterOptions}
                                onChange={setStatusFilter}
                            />
                        )}
                    </div>

                    {/* Header */}
                    <div className="flex justify-between mb-6">
                        <h2 className="text-xl font-bold" style={{ color: COLORS.dark }}>
                            {activeTab === 'students' && `All Students (${students.length})`}
                            {activeTab === 'instructors' && `All Instructors (${instructors.length})`}
                            {activeTab === 'admins' && `All Admins (${admins.length})`}
                        </h2>
                        <Button
                            variant="primary"
                            onClick={() => {
                                if (activeTab === 'admins' && !isSuperAdmin) {
                                    alert('Only Super Admin can create admins');
                                    return;
                                }
                                setShowCreateModal(true);
                            }}
                        >
                            + Add {activeTab === 'students' ? 'Student' : activeTab === 'instructors' ? 'Instructor' : 'Admin'}
                        </Button>
                    </div>

                    {/* Data Table */}
                    <DataTable
                        columns={getColumns()}
                        data={currentData}
                        selectedIds={selectedIds}
                        onToggle={handleToggle}
                        onSelectAll={handleSelectAll}
                        showCheckbox
                        emptyMessage="No data"
                    />
                </div>
            </div>

            {/* Create Modal */}
            <Modal isOpen={showCreateModal} onClose={() => setShowCreateModal(false)}>
                <BaseUserForm
                    userType={activeTab === 'students' ? 'student' : activeTab === 'instructors' ? 'instructor' : 'admin'}
                    mode="create"
                    onSubmit={handleCreateUser}
                    onCancel={() => setShowCreateModal(false)}
                />
            </Modal>

            {/* Edit Modal */}
            <Modal isOpen={showEditModal} onClose={() => { setShowEditModal(false); setEditingUserData(null); }}>
                {editingUserData && (
                    <BaseUserForm
                        userType={editingUserRole}
                        mode="edit"
                        initialData={editingUserData}
                        onSubmit={handleUpdateUser}
                        onCancel={() => { setShowEditModal(false); setEditingUserData(null); }}
                    />
                )}
            </Modal>

            {/* Batch Status Modal */}
            {showBatchStatusModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-2xl p-6 max-w-md w-full mx-4 shadow-xl">
                        <h3 className="text-xl font-bold mb-4" style={{ color: COLORS.dark }}>Change Status</h3>
                        <p className="text-sm mb-6" style={{ color: COLORS.dark }}>
                            Update <strong>{selectedIds.size}</strong> {activeTab}?
                        </p>
                        <select className="w-full px-4 py-2 border rounded-lg mb-6 cursor-pointer"
                                value={batchTargetStatus}
                                onChange={(e) => setBatchTargetStatus(e.target.value)}
                                style={{ borderColor: COLORS.bg }}>
                            <option value="">Select Status</option>
                            {Object.values(USER_STATUS).map(s => (
                                <option key={s.value} value={s.value}>{s.label}</option>
                            ))}
                        </select>
                        <div className="flex space-x-3">
                            <Button variant="primary" onClick={handleBatchStatus} className="flex-1">
                                Confirm
                            </Button>
                            <Button variant="secondary" onClick={() => setShowBatchStatusModal(false)} className="flex-1">
                                Cancel
                            </Button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};