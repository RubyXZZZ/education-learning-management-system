import React, { useState, useEffect } from 'react';
import { ArrowLeft, Mail, Phone, MapPin } from 'lucide-react';
import { PageHeader } from '../components/common/PageHeader';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { DataTable, type ColumnDef } from '../components/common/DataTable';
import { Modal } from '../components/common/Modal';
import { COLORS } from '../constants/colors';
import { studentApi, instructorApi, adminApi } from '../services/api';
import { USER_STATUS, STUDENT_TYPE, ENROLLMENT_STATUS, SECTION_STATUS } from '../types';
import { BaseUserForm } from '../components/forms/BaseUserForm';
import type { StudentRes, InstructorRes, AdminRes, EnrollmentRes, SectionRes } from '../types';

interface UserDetailProps {
    userId: string;
    userRole: 'STUDENT' | 'INSTRUCTOR' | 'ADMIN';
    onBack: () => void;
}

export const UserDetail: React.FC<UserDetailProps> = ({ userId, userRole, onBack }) => {
    const [user, setUser] = useState<StudentRes | InstructorRes | AdminRes | null>(null);
    const [enrollments, setEnrollments] = useState<EnrollmentRes[]>([]);
    const [sections, setSections] = useState<SectionRes[]>([]);
    const [loading, setLoading] = useState(true);
    const [showMoreMenu, setShowMoreMenu] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);

    useEffect(() => {
        loadUserData();
    }, [userId, userRole]);

    const loadUserData = async () => {
        try {
            setLoading(true);

            let userRes;
            if (userRole === 'STUDENT') {
                userRes = await studentApi.getWithEnrollments(userId);
                setEnrollments(userRes.data.enrollments || []);
            } else if (userRole === 'INSTRUCTOR') {
                userRes = await instructorApi.getWithSections(userId);
                setSections(userRes.data.sections || []);
            } else {
                userRes = await adminApi.getById(userId);
            }

            setUser(userRes.data);
        } catch (err) {
            console.error('Error loading user:', err);
        } finally {
            setLoading(false);
        }
    };

    const activeEnrollments = enrollments.filter(e => e.status !== 'DROPPED');

    const handleUpdateUser = async (formData: any) => {
        try {
            const api = userRole === 'STUDENT' ? studentApi :
                userRole === 'INSTRUCTOR' ? instructorApi : adminApi;

            await api.update(userId, formData);

            setShowEditModal(false);
            await loadUserData();
            alert('Updated successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to update: ' + errorMsg);
        }
    };

    const handleStatusChange = async (action: 'suspend' | 'reactivate' | 'deactivate') => {
        if (!user) return;

        const confirmMsg = `${action.charAt(0).toUpperCase() + action.slice(1)} ${user.fullName}?`;
        if (!confirm(confirmMsg)) return;

        try {
            const api = userRole === 'STUDENT' ? studentApi :
                userRole === 'INSTRUCTOR' ? instructorApi : adminApi;

            await (api as any)[action](userId);
            alert(`User ${action}d successfully`);
            loadUserData();
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed: ' + errorMsg);
        }
    };

    if (loading) {
        return (
            <div className="space-y-6">
                <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>Back</Button>
                <div className="text-center py-12">Loading...</div>
            </div>
        );
    }

    if (!user) {
        return (
            <div className="space-y-6">
                <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>Back</Button>
                <div className="text-center py-12 text-red-600">User not found</div>
            </div>
        );
    }

    const statusConfig = USER_STATUS[user.status as keyof typeof USER_STATUS];
    const typeConfig = STUDENT_TYPE[(user as StudentRes).studentType];
    const isStudent = userRole === 'STUDENT';
    const isInstructor = userRole === 'INSTRUCTOR';
    const isAdmin = userRole === 'ADMIN';

    // Enrollments Table Columns (Student)
    const enrollmentColumns: ColumnDef[] = [
        {
            key: 'courseCode',
            header: 'Course',
            className: 'text-sm font-medium'
        },
        {
            key: 'sectionCode',
            header: 'Section',
            className: 'text-sm'
        },
        {
            key: 'sessionCode',
            header: 'Session',
            render: (e) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {e.sessionCode}
                </span>
            )
        },
        {
            key: 'instructorName',
            header: 'Instructor',
            render: (e) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {e.instructorName}
                </span>
            )
        },
        {
            key: 'status',
            header: 'Status',
            render: (e) => {
                const config = ENROLLMENT_STATUS[e.status as keyof typeof ENROLLMENT_STATUS];
                return (
                    <Badge className={config?.color}>
                        {config?.label}
                    </Badge>
                );
            }
        },
        {
            key: 'finalGrade',
            header: 'Grade',
            render: (e) => (
                <span className="text-sm font-medium" style={{ color: COLORS.dark }}>
                    {e.finalGrade ? `${e.finalGrade.toFixed(0)} (${e.letterGrade})` : '-'}
                </span>
            )
        }
    ];

    // Sections Table Columns (Instructor)
    const sectionColumns: ColumnDef[] = [
        {
            key: 'courseCode',
            header: 'Course',
            className: 'text-sm font-medium'
        },
        {
            key: 'sectionCode',
            header: 'Section',
            className: 'text-sm'
        },
        {
            key: 'sessionCode',
            header: 'Session',
            render: (s) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {s.sessionCode}
                </span>
            )
        },
        {
            key: 'schedule',
            header: 'Schedule',
            render: (s) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {s.schedule}
                </span>
            )
        },
        {
            key: 'location',
            header: 'Location',
            render: (s) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {s.location}
                </span>
            )
        },
        {
            key: 'enrolled',
            header: 'Enrolled',
            render: (s) => (
                <span className="text-sm">
                    <span className="font-medium" style={{ color: COLORS.orange }}>
                        {s.enrolledCount}
                    </span>
                    <span style={{ color: COLORS.dark, opacity: 0.5 }}>
                        /{s.capacity}
                    </span>
                </span>
            )
        },
        {
            key: 'status',
            header: 'Status',
            render: (s) => {
                const config = SECTION_STATUS[s.status as keyof typeof SECTION_STATUS];
                return (
                    <Badge className={config?.color}>
                        {config?.label}
                    </Badge>
                );
            }
        }
    ];

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex justify-between items-center">
                <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>
                    Back
                </Button>

                <div className="flex space-x-2">
                    <Button variant="primary" onClick={() => setShowEditModal(true)}>
                        Edit Profile
                    </Button>

                    <div className="relative">
                        <Button variant="secondary" onClick={() => setShowMoreMenu(!showMoreMenu)}>
                            More
                        </Button>

                        {showMoreMenu && (
                            <>
                                <div className="fixed inset-0 z-10" onClick={() => setShowMoreMenu(false)} />
                                <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border z-20"
                                     style={{ borderColor: COLORS.bg }}>
                                    {user.status === 'ACTIVE' && (
                                        <button
                                            onClick={() => { setShowMoreMenu(false); handleStatusChange('suspend'); }}
                                            className="w-full px-4 py-2 text-left text-sm hover:bg-gray-50 cursor-pointer"
                                        >
                                            Suspend
                                        </button>
                                    )}
                                    {user.status === 'SUSPENDED' && (
                                        <button
                                            onClick={() => { setShowMoreMenu(false); handleStatusChange('reactivate'); }}
                                            className="w-full px-4 py-2 text-left text-sm hover:bg-gray-50 cursor-pointer"
                                        >
                                            Reactivate
                                        </button>
                                    )}
                                    {user.status === 'ACTIVE' && (
                                        <button
                                            onClick={() => { setShowMoreMenu(false); handleStatusChange('deactivate'); }}
                                            className="w-full px-4 py-2 text-left text-sm hover:bg-red-50 text-red-600 cursor-pointer"
                                        >
                                            Deactivate
                                        </button>
                                    )}
                                </div>
                            </>
                        )}
                    </div>
                </div>
            </div>

            <PageHeader title={`${userRole} Profile`} />

            {/* User Header Card */}
            <div className="bg-white rounded-3xl p-8 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                <div className="flex items-start justify-between">
                    <div className="flex items-center space-x-6">
                        <div className="w-24 h-24 rounded-2xl flex items-center justify-center text-5xl"
                             style={{ backgroundColor: COLORS.cream }}>
                            {user.userAvatar || '👤'}
                        </div>
                        <div>
                            <h1 className="text-3xl font-bold mb-2" style={{ color: COLORS.dark }}>
                                {user.fullName}
                            </h1>
                            <div className="space-y-1 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                <div className="flex items-center space-x-2">
                                    <Mail size={14} />
                                    <span>{user.email}</span>
                                    {user.emailVerified && <span className="text-green-600">✓</span>}
                                </div>
                                {user.phone && (
                                    <div className="flex items-center space-x-2">
                                        <Phone size={14} />
                                        <span>{user.phone}</span>
                                    </div>
                                )}
                                {user.address && (
                                    <div className="flex items-center space-x-2">
                                        <MapPin size={14} />
                                        <span>{user.address}</span>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>

                    <div className="text-right">
                        <Badge className={statusConfig?.color}>
                            {statusConfig?.label || user.status}
                        </Badge>
                        <div className="text-sm mt-2" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            ID: {(user as any).studentNumber || (user as any).employeeNumber}
                        </div>
                    </div>
                </div>
            </div>

            {/* Student-specific content */}
            {isStudent && (
                <>
                    {/* Personal Information */}
                    <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                        <h3 className="text-lg font-bold mb-4" style={{ color: COLORS.dark }}>
                            Personal Information
                        </h3>
                        <div className="grid grid-cols-3 gap-4">
                            <InfoCard label="Student Type" value={typeConfig?.label || '-'} />
                            <InfoCard label="Date of Birth" value={(user as StudentRes).dateOfBirth || 'N/A'} />
                            <InfoCard label="Gender" value={(user as StudentRes).gender || 'N/A'} />
                            <InfoCard label="Nationality" value={(user as StudentRes).nationality || 'N/A'} />
                            <InfoCard label="Emergency Contact" value={(user as StudentRes).emergencyContact || 'N/A'} />
                            <InfoCard label="Emergency Phone" value={(user as StudentRes).emergencyPhone || 'N/A'} />
                        </div>
                    </div>

                    {/* Enrollments */}
                    <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                        <h3 className="text-lg font-bold mb-4" style={{ color: COLORS.dark }}>
                            Enrollments ({activeEnrollments.length})
                        </h3>
                        {activeEnrollments.length > 0 ? (
                            <DataTable
                                columns={enrollmentColumns}
                                data={activeEnrollments}
                                emptyMessage="No enrollments"
                            />
                        ) : (
                            <p className="text-center py-8 text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                No enrollments
                            </p>
                        )}
                    </div>
                </>
            )}

            {/* Instructor-specific content */}
            {isInstructor && (
                <>
                    <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                        <h3 className="text-lg font-bold mb-4" style={{ color: COLORS.dark }}>
                            Professional Information
                        </h3>
                        <div className="grid grid-cols-3 gap-4">
                            <InfoCard label="Employee Number" value={(user as InstructorRes).employeeNumber} />
                            <InfoCard label="Date of Birth" value={(user as InstructorRes).dateOfBirth || 'N/A'} />
                            <InfoCard label="Gender" value={(user as InstructorRes).gender || 'N/A'} />
                            <InfoCard label="Department" value={(user as InstructorRes).department || 'N/A'} />
                            <InfoCard label="Teaching Sections" value={`${(user as InstructorRes).teachingCounts}/3`} />
                            <InfoCard label="Office Hours" value={(user as InstructorRes).officeHours || 'N/A'} colSpan={3} />
                        </div>
                    </div>

                    {/* Teaching Sections */}
                    <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                        <h3 className="text-lg font-bold mb-4" style={{ color: COLORS.dark }}>
                            Teaching Sections ({sections.length})
                        </h3>
                        {sections.length > 0 ? (
                            <DataTable
                                columns={sectionColumns}
                                data={sections}
                                emptyMessage="No sections"
                            />
                        ) : (
                            <p className="text-center py-8 text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                No sections assigned
                            </p>
                        )}
                    </div>
                </>
            )}

            {/* Admin-specific content */}
            {isAdmin && (
                <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                    <h3 className="text-lg font-bold mb-4" style={{ color: COLORS.dark }}>
                        Administrative Information
                    </h3>
                    <div className="grid grid-cols-3 gap-4">
                        <InfoCard label="Employee Number" value={(user as AdminRes).employeeNumber} />
                        <InfoCard label="Date of Birth" value={(user as AdminRes).dateOfBirth || 'N/A'} />
                        <InfoCard label="Gender" value={(user as AdminRes).gender || 'N/A'} />
                        <InfoCard label="Department" value={(user as AdminRes).department || 'N/A'} />
                        <InfoCard label="Position" value={(user as AdminRes).position || 'N/A'} />
                        <InfoCard label="Admin Type" value={(user as AdminRes).isSuperAdmin ? 'Super Admin' : 'Admin'} />
                        <InfoCard label="Office Hours" value={(user as AdminRes).officeHours || 'N/A'} colSpan={2} />
                    </div>
                </div>
            )}

            {/* Edit Modal */}
            <Modal isOpen={showEditModal} onClose={() => setShowEditModal(false)} size="xl">
                <BaseUserForm
                    userType={userRole.toLowerCase() as 'student' | 'instructor' | 'admin'}
                    mode="edit"
                    initialData={user}
                    onSubmit={handleUpdateUser}
                    onCancel={() => setShowEditModal(false)}
                />
            </Modal>
        </div>
    );
};

// Helper Component
const InfoCard: React.FC<{ label: string; value: string; colSpan?: number }> = ({ label, value, colSpan = 1 }) => (
    <div className={`p-4 rounded-lg col-span-${colSpan}`} style={{ backgroundColor: COLORS.bg + '40' }}>
        <div className="text-xs mb-1" style={{ color: COLORS.dark, opacity: 0.6 }}>{label}</div>
        <div className="text-sm font-medium" style={{ color: COLORS.dark }}>{value}</div>
    </div>
);