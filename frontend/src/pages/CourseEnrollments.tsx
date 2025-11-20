import React, { useState, useEffect } from 'react';
import { ArrowLeft, UserPlus, UserMinus } from 'lucide-react';
import Select from 'react-select';
import { PageHeader } from '../components/common/PageHeader';
import { SearchBar } from '../components/common/SearchBar';
import { FilterSelect } from '../components/common/FilterSelect';
import { Button } from '../components/common/Button';
import { DataTable, TableRenderers, type ColumnDef } from '../components/common/DataTable';
import { Modal } from '../components/common/Modal';
import { COLORS } from '../constants/colors';
import { sectionApi, enrollmentApi, studentApi } from '../services/api';
import { ENROLLMENT_STATUS } from '../types';
import type { SectionRes, EnrollmentRes, StudentList } from '../types';

interface CourseEnrollmentProps {
    sectionId: string;
    onBack: () => void;
}

export const CourseEnrollments: React.FC<CourseEnrollmentProps> = ({ sectionId, onBack }) => {
    const [section, setSection] = useState<SectionRes | null>(null);
    const [enrollments, setEnrollments] = useState<EnrollmentRes[]>([]);
    const [students, setStudents] = useState<StudentList[]>([]);
    const [loading, setLoading] = useState(true);

    const [searchTerm, setSearchTerm] = useState('');
    const [statusFilter, setStatusFilter] = useState<string>('ACTIVE');

    const [showAddModal, setShowAddModal] = useState(false);
    const [selectedStudentId, setSelectedStudentId] = useState('');

    const [showDropModal, setShowDropModal] = useState(false);
    const [droppingEnrollmentId, setDroppingEnrollmentId] = useState('');
    const [dropReason, setDropReason] = useState('');

    useEffect(() => {
        loadData();
    }, [sectionId]);

    const loadData = async () => {
        try {
            setLoading(true);
            const [sectionRes, enrollmentsRes, studentsRes] = await Promise.all([
                sectionApi.getById(sectionId),
                enrollmentApi.getAllBySectionForAdmin(sectionId),
                studentApi.getList()
            ]);
            setSection(sectionRes.data);
            setEnrollments(enrollmentsRes.data);
            setStudents(studentsRes.data);
        } catch (err) {
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleAddStudent = async () => {
        if (!selectedStudentId) {
            alert('Please select a student');
            return;
        }

        try {
            await enrollmentApi.enroll({
                studentId: selectedStudentId,
                courseSectionId: sectionId
            });
            setShowAddModal(false);
            setSelectedStudentId('');
            await loadData();
            alert('Student enrolled successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed: ' + errorMsg);
        }
    };

    const handleDropStudent = async () => {
        try {
            await enrollmentApi.drop({
                enrollmentId: droppingEnrollmentId,
                dropReason: dropReason || 'Dropped by admin'
            });
            setShowDropModal(false);
            setDroppingEnrollmentId('');
            setDropReason('');
            await loadData();
            alert('Student dropped successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed: ' + errorMsg);
        }
    };

    // Only filter out currently ENROLLED students (allow re-enrolling DROPPED students)
    const enrolledIds = new Set(
        enrollments
            .filter(e => e.status === 'ENROLLED')
            .map(e => e.studentId)
    );
    const availableStudents = students.filter(s => !enrolledIds.has(s.id));

    const studentOptions = availableStudents.map(s => ({
        value: s.id,
        label: `${s.fullName} (${s.studentNumber})`
    }));

    const enrolledCount = enrollments.filter(e => e.status === 'ENROLLED').length;
    const completedCount = enrollments.filter(e => e.status === 'COMPLETED').length;
    const droppedCount = enrollments.filter(e => e.status === 'DROPPED').length;
    const activeCount = enrolledCount + completedCount;

    // Filter enrollments by status
    const statusFilteredEnrollments = enrollments.filter(e => {
        if (statusFilter === 'ALL') return true;
        if (statusFilter === 'ACTIVE') {
            return e.status === 'ENROLLED' || e.status === 'COMPLETED';
        }
        return e.status === statusFilter;
    });

    // Filter by search term
    const filteredEnrollments = statusFilteredEnrollments.filter(e => {
        if (!searchTerm) return true;
        const search = searchTerm.toLowerCase();
        return (
            e.studentNumber?.toLowerCase().includes(search) ||
            e.studentName?.toLowerCase().includes(search) ||
            e.studentEmail?.toLowerCase().includes(search)
        );
    });

    if (loading) {
        return (
            <div className="space-y-6">
                <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>Back</Button>
                <div className="text-center py-12">Loading...</div>
            </div>
        );
    }

    if (!section) return null;

    // Define columns
    const enrollmentColumns: ColumnDef[] = [
        {
            key: 'studentNumber',
            header: 'Student #',
            render: (e) => (
                <span className="text-sm font-mono" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {e.studentNumber}
                </span>
            )
        },
        {
            key: 'studentName',
            header: 'Name',
            className: 'text-sm font-medium'
        },
        {
            key: 'studentEmail',
            header: 'Email',
            render: (e) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {e.studentEmail || '-'}
                </span>
            )
        },
        {
            key: 'enrolledTime',
            header: 'Enrolled Date',
            render: (e) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {e.enrolledTime ? new Date(e.enrolledTime).toLocaleDateString() : '-'}
                </span>
            )
        },
        {
            key: 'status',
            header: 'Status',
            render: TableRenderers.badge('status',ENROLLMENT_STATUS)
        },
        {
            key: 'finalGrade',
            header: 'Grade',
            render: (e) => (
                <span className="text-sm font-medium" style={{ color: COLORS.dark }}>
                    {e.finalGrade ? e.finalGrade.toFixed(0) : '-'}
                </span>
            )
        },
        {
            key: 'actions',
            header: 'Actions',
            render: (e) => (
                e.status === 'ENROLLED' ? (
                    <Button
                        variant="danger"
                        size="sm"
                        icon={<UserMinus size={14} />}
                        onClick={() => {
                            setDroppingEnrollmentId(e.id);
                            setShowDropModal(true);
                        }}
                    >
                        Drop
                    </Button>
                ) : null
            )
        }
    ];

    return (
        <div className="space-y-6">
            <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>
                Back
            </Button>

            <PageHeader title="Section Enrollments" />

            {/* Section Info */}
            <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                <div className="flex justify-between">
                    <div>
                        <h2 className="text-2xl font-bold mb-2" style={{ color: COLORS.dark }}>
                            {section.courseCode} - {section.courseName}
                        </h2>
                        <div className="flex gap-4 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                            <span>Section {section.sectionCode}</span>
                            <span>•</span>
                            <span>{section.sessionCode}</span>
                            <span>•</span>
                            <span>{section.instructorName}</span>
                            <span>•</span>
                            <span>{section.schedule}</span>
                        </div>
                    </div>
                    <div className="text-right">
                        <div className="text-3xl font-bold" style={{ color: COLORS.orange }}>
                            {section.enrolledCount}/{section.capacity}
                        </div>
                        <div className="text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            Active Students
                        </div>
                    </div>
                </div>
            </div>

            {/* Enrollments Table */}
            <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                {/* Search and Filter */}
                <div className="mb-4 flex items-center gap-3">
                    <FilterSelect
                        value={statusFilter}
                        options={[
                            { value: 'ACTIVE', label: `Active (${activeCount})` },
                            { value: 'DROPPED', label: `Dropped (${droppedCount})` },
                            { value: 'ALL', label: `All (${enrollments.length})` }
                        ]}
                        onChange={setStatusFilter}
                        width="180px"
                    />
                    <SearchBar
                        value={searchTerm}
                        onChange={setSearchTerm}
                        placeholder="Search students..."
                    />
                </div>

                {/* Header */}
                <div className="flex justify-between mb-6">
                    <h3 className="text-xl font-bold" style={{ color: COLORS.dark }}>
                        Students ({filteredEnrollments.length})
                    </h3>
                    <Button variant="primary" icon={<UserPlus size={16} />} onClick={() => setShowAddModal(true)}>
                        Add Student
                    </Button>
                </div>

                {/* Table */}
                {filteredEnrollments.length > 0 ? (
                    <DataTable
                        columns={enrollmentColumns}
                        data={filteredEnrollments}
                        emptyMessage="No students"
                    />
                ) : (
                    <div className="text-center py-12">
                        <p className="text-sm mb-4" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            {statusFilter === 'ACTIVE' ? 'No active students' :
                                statusFilter === 'DROPPED' ? 'No dropped students' :
                                    'No students'}
                        </p>
                        {statusFilter === 'ACTIVE' && (
                            <Button variant="primary" icon={<UserPlus size={16} />} onClick={() => setShowAddModal(true)}>
                                Add First Student
                            </Button>
                        )}
                    </div>
                )}
            </div>

            {/* Add Student Modal */}
            <Modal isOpen={showAddModal} onClose={() => { setShowAddModal(false); setSelectedStudentId(''); }} size="md">
                <h3 className="text-xl font-bold mb-4" style={{ color: COLORS.dark }}>Add Student</h3>

                <div className="mb-6">
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Select Student
                    </label>
                    <Select
                        options={studentOptions}
                        value={studentOptions.find(opt => opt.value === selectedStudentId)}
                        onChange={(selected) => setSelectedStudentId(selected?.value || '')}
                        placeholder="Search by name or number..."
                        isSearchable
                        styles={{
                            control: (base) => ({
                                ...base,
                                minHeight: '42px',
                                borderColor: COLORS.bg,
                                '&:hover': { borderColor: COLORS.orange }
                            }),
                            option: (base, state) => ({
                                ...base,
                                backgroundColor: state.isFocused ? COLORS.cream : 'white',
                                cursor: 'pointer'
                            }),
                            menu: (base) => ({ ...base, zIndex: 100 })
                        }}
                    />
                    <p className="text-xs mt-2" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        {availableStudents.length} students available
                    </p>
                </div>

                <div className="flex space-x-3">
                    <Button variant="primary" onClick={handleAddStudent} className="flex-1">
                        Add Student
                    </Button>
                    <Button variant="secondary" onClick={() => {
                        setShowAddModal(false);
                        setSelectedStudentId('');
                    }} className="flex-1">
                        Cancel
                    </Button>
                </div>
            </Modal>

            {/* Drop Student Modal */}
            <Modal isOpen={showDropModal} onClose={() => {
                setShowDropModal(false);
                setDroppingEnrollmentId('');
                setDropReason('');
            }} size="md">
                <h3 className="text-xl font-bold mb-4" style={{ color: COLORS.dark }}>
                    Drop Student
                </h3>

                <p className="text-sm mb-4" style={{ color: COLORS.dark }}>
                    Remove this student from the section?
                </p>

                <div className="mb-4">
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Reason (Optional)
                    </label>
                    <textarea
                        value={dropReason}
                        onChange={(e) => setDropReason(e.target.value)}
                        placeholder="Enter drop reason (optional)..."
                        rows={3}
                        className="w-full px-3 py-2 border rounded-lg"
                        style={{ borderColor: COLORS.bg }}
                    />
                </div>

                <div className="flex space-x-3">
                    <Button variant="danger" onClick={handleDropStudent} className="flex-1">
                        Drop Student
                    </Button>
                    <Button variant="secondary" onClick={() => {
                        setShowDropModal(false);
                        setDroppingEnrollmentId('');
                        setDropReason('');
                    }} className="flex-1">
                        Cancel
                    </Button>
                </div>
            </Modal>
        </div>
    );
};