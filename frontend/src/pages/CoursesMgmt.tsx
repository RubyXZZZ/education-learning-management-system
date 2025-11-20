import React, { useState, useEffect } from 'react';
import { List } from 'lucide-react';
import { PageHeader } from '../components/common/PageHeader';
import { SearchBar } from '../components/common/SearchBar';
import { FilterSelect } from '../components/common/FilterSelect';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { BatchToolBar } from '../components/common/BatchToolBar';
import { DataTable, TableRenderers, type ColumnDef } from '../components/common/DataTable';
import { Modal } from '../components/common/Modal';
import { COLORS } from '../constants/colors';
import { courseApi, sectionApi, instructorApi } from '../services/api';
import { useSession } from '../contexts/AppContext';
import { SectionForm } from '../components/forms/SectionForm';
import { CourseForm } from '../components/forms/CourseForm';
import { UserDetail } from './UserDetail';
import type { CourseRes, CourseSectionList, SectionRes, InstructorRes } from '../types';
import { SECTION_STATUS, COURSE_FORMAT } from '../types';

interface CoursesMgmtProps {
    onViewEnrollments: (sectionId: string) => void;
}

export const CoursesMgmt: React.FC<CoursesMgmtProps> = ({ onViewEnrollments }) => {
    const { allSessions } = useSession();

    const [courses, setCourses] = useState<CourseRes[]>([]);
    const [courseSections, setCourseSections] = useState<CourseSectionList[]>([]);
    const [instructors, setInstructors] = useState<InstructorRes[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Filters
    const [sessionFilter, setSessionFilter] = useState<string>('');
    const [searchTerm, setSearchTerm] = useState('');

    // View Mode
    const [viewMode, setViewMode] = useState<'list' | 'instructor-detail'>('list');
    const [selectedInstructorId, setSelectedInstructorId] = useState<string | null>(null);

    // Course Modals
    const [showCreateCourseModal, setShowCreateCourseModal] = useState(false);
    const [showEditCourseModal, setShowEditCourseModal] = useState(false);
    const [editingCourse, setEditingCourse] = useState<CourseRes | null>(null);

    // Section Management Modal
    const [showSectionsModal, setShowSectionsModal] = useState(false);
    const [selectedCourse, setSelectedCourse] = useState<CourseRes | null>(null);
    const [showCreateSectionModal, setShowCreateSectionModal] = useState(false);
    const [showEditSectionModal, setShowEditSectionModal] = useState(false);
    const [editingSection, setEditingSection] = useState<SectionRes | null>(null);

    // Batch operations for sections
    const [selectedSectionIds, setSelectedSectionIds] = useState<Set<string>>(new Set());
    const [showBatchStatusModal, setShowBatchStatusModal] = useState(false);
    const [batchTargetStatus, setBatchTargetStatus] = useState<string>('');

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            setLoading(true);
            setError(null);

            const [coursesRes, instructorsRes] = await Promise.all([
                courseApi.getAll(),
                instructorApi.getAll()
            ]);

            setCourses(coursesRes.data);
            setInstructors(instructorsRes.data);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to load data');
            console.error('Error loading data:', err);
        } finally {
            setLoading(false);
        }
    };

    // Filter courses
    const displayedCourses = courses.filter(course => {
        if (sessionFilter && course.sessionCode !== sessionFilter) return false;
        if (searchTerm) {
            const searchLower = searchTerm.toLowerCase();
            if (!course.courseCode.toLowerCase().includes(searchLower) &&
                !course.courseName.toLowerCase().includes(searchLower)) {
                return false;
            }
        }
        return true;
    });

    // Get sections count from course.sections (backend provides this)
    const getSectionsCount = (courseId: string) => {
        const course = courses.find(c => c.id === courseId);
        return course?.sectionsCount || 0;
    };

    const handleCreateCourse = async (formData: any) => {
        try {
            await courseApi.create(formData);
            setShowCreateCourseModal(false);
            await loadData();
            alert('Course created successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to create course: ' + errorMsg);
        }
    };

    const handleEditCourse = async (courseId: string) => {
        try {
            const courseRes = await courseApi.getById(courseId);
            setEditingCourse(courseRes.data);
            setShowEditCourseModal(true);
        } catch (err) {
            alert('Failed to load course: ' + (err instanceof Error ? err.message : ''));
        }
    };

    const handleUpdateCourse = async (formData: any) => {
        if (!editingCourse) return;

        try {
            await courseApi.update(editingCourse.id, formData);
            setShowEditCourseModal(false);
            setEditingCourse(null);
            await loadData();
            alert('Course updated successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to update course: ' + errorMsg);
        }
    };

    const handleDeleteCourse = async (courseId: string) => {
        const sectionsCount = getSectionsCount(courseId);

        if (sectionsCount > 0) {
            alert(`Cannot delete course: ${sectionsCount} section(s) exist. Delete all sections first.`);
            return;
        }

        if (!confirm('Delete this course? This action cannot be undone.')) return;

        try {
            await courseApi.delete(courseId);
            await loadData();
            alert('Course deleted successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to delete course: ' + errorMsg);
        }
    };

    const handleManageSections = async (course: CourseRes) => {
        try {
            setSelectedCourse(course);
            setSelectedSectionIds(new Set());

            // Load sections from backend by course
            const sectionsRes = await sectionApi.getByCourse(course.id);
            setCourseSections(sectionsRes.data);

            setShowSectionsModal(true);
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to load sections: ' + errorMsg);
        }
    };

    const handleCreateSection = async (formData: any) => {
        try {
            const dataWithCourse = {
                ...formData,
                courseId: selectedCourse?.id
            };
            await sectionApi.create(dataWithCourse);
            setShowCreateSectionModal(false);

            // Reload sections for this course
            if (selectedCourse) {
                const sectionsRes = await sectionApi.getByCourse(selectedCourse.id);
                setCourseSections(sectionsRes.data);
            }

            await loadData(); // Reload courses to update section count
            alert('Section created successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to create section: ' + errorMsg);
        }
    };

    const handleEditSection = async (section: CourseSectionList) => {
        try {
            const fullSection = await sectionApi.getById(section.id);
            setEditingSection(fullSection.data);
            setShowEditSectionModal(true);
        } catch (err) {
            alert('Failed to load section: ' + (err instanceof Error ? err.message : ''));
        }
    };

    const handleDeleteSection = async (sectionId: string) => {
        const section = courseSections.find(s => s.id === sectionId);

        if (section?.status !== 'DRAFT') {
            alert('Only DRAFT sections can be deleted');
            return;
        }

        if (section?.enrolledCount > 0) {
            alert(`Cannot delete section: ${section.enrolledCount} student(s) enrolled. Drop all students first.`);
            return;
        }

        if (!confirm('Delete this section? This action cannot be undone.')) return;

        try {
            await sectionApi.delete(sectionId);

            // Reload sections for this course
            if (selectedCourse) {
                const sectionsRes = await sectionApi.getByCourse(selectedCourse.id);
                setCourseSections(sectionsRes.data);
            }

            await loadData(); // Reload courses to update section count
            alert('Section deleted successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to delete section: ' + errorMsg);
        }
    };

    const handleUpdateSection = async (formData: any) => {
        if (!editingSection) return;

        try {
            await sectionApi.update(editingSection.id, formData);
            setShowEditSectionModal(false);
            setEditingSection(null);

            // Reload sections for this course
            if (selectedCourse) {
                const sectionsRes = await sectionApi.getByCourse(selectedCourse.id);
                setCourseSections(sectionsRes.data);
            }

            await loadData();
            alert('Section updated successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to update section: ' + errorMsg);
        }
    };

    const handleToggleSection = (sectionId: string) => {
        const newSelected = new Set(selectedSectionIds);
        if (newSelected.has(sectionId)) {
            newSelected.delete(sectionId);
        } else {
            newSelected.add(sectionId);
        }
        setSelectedSectionIds(newSelected);
    };

    const handleSelectAllSections = () => {
        if (selectedSectionIds.size === courseSections.length && courseSections.length > 0) {
            setSelectedSectionIds(new Set());
        } else {
            setSelectedSectionIds(new Set(courseSections.map(s => s.id)));
        }
    };

    const handleBatchPublish = async () => {
        if (selectedSectionIds.size === 0) {
            alert('Please select sections to publish');
            return;
        }

        if (!confirm(`Publish ${selectedSectionIds.size} section(s)? They will be visible to students.`)) {
            return;
        }

        try {
            for (const id of selectedSectionIds) {
                await sectionApi.update(id, { status: 'PUBLISHED' });
            }

            setSelectedSectionIds(new Set());

            // Reload sections for this course
            if (selectedCourse) {
                const sectionsRes = await sectionApi.getByCourse(selectedCourse.id);
                setCourseSections(sectionsRes.data);
            }

            await loadData();
            alert('Sections published successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to publish sections: ' + errorMsg);
        }
    };

    const handleBatchLockEnrollment = async (locked: boolean) => {
        if (selectedSectionIds.size === 0) {
            alert('Please select sections');
            return;
        }

        const action = locked ? 'lock' : 'unlock';
        if (!confirm(`${locked ? 'Lock' : 'Unlock'} enrollment for ${selectedSectionIds.size} section(s)?`)) {
            return;
        }

        try {
            for (const id of selectedSectionIds) {
                await sectionApi.update(id, { enrollmentLocked: locked });
            }

            setSelectedSectionIds(new Set());

            // Reload sections for this course
            if (selectedCourse) {
                const sectionsRes = await sectionApi.getByCourse(selectedCourse.id);
                setCourseSections(sectionsRes.data);
            }

            await loadData();
            alert(`Enrollment ${action}ed successfully!`);
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert(`Failed to ${action} enrollment: ` + errorMsg);
        }
    };

    const handleBatchStatusChange = async () => {
        if (!batchTargetStatus) {
            alert('Please select a status');
            return;
        }

        try {
            for (const id of selectedSectionIds) {
                await sectionApi.update(id, { status: batchTargetStatus });
            }

            setShowBatchStatusModal(false);
            setSelectedSectionIds(new Set());
            setBatchTargetStatus('');

            // Reload sections for this course
            if (selectedCourse) {
                const sectionsRes = await sectionApi.getByCourse(selectedCourse.id);
                setCourseSections(sectionsRes.data);
            }

            await loadData();
            alert('Status updated successfully!');
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to update status: ' + errorMsg);
        }
    };

    const handleViewInstructorDetail = (instructorId: string) => {
        setSelectedInstructorId(instructorId);
        setViewMode('instructor-detail');
    };

    const handleBackToList = () => {
        setViewMode('list');
        setSelectedInstructorId(null);
    };

    // Show instructor detail view
    if (viewMode === 'instructor-detail' && selectedInstructorId) {
        return <UserDetail userId={selectedInstructorId} userRole="INSTRUCTOR" onBack={handleBackToList} />;
    }

    if (loading) {
        return (
            <div className="space-y-6">
                <PageHeader title="Course Management" />
                <div className="flex items-center justify-center h-64">
                    <div className="text-xl" style={{ color: COLORS.dark }}>Loading...</div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="space-y-6">
                <PageHeader title="Course Management" />
                <div className="flex items-center justify-center h-64">
                    <div className="text-xl text-red-600">Error: {error}</div>
                </div>
            </div>
        );
    }

    // Columns for courses table
    const courseColumns: ColumnDef[] = [
        {
            key: 'courseCode',
            header: 'Course Code',
            render: (course) => (
                <span className="text-sm font-mono font-bold" style={{ color: COLORS.dark }}>
                    {course.courseCode}
                </span>
            )
        },
        {
            key: 'courseName',
            header: 'Course Name',
            className: 'text-sm font-medium'
        },
        {
            key: 'sessionCode',
            header: 'Session',
            render: (course) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {course.sessionCode || course.sessionId || '-'}
                </span>
            )
        },
        { key: 'hoursPerWeek', header: 'Hours/Week', className: 'text-sm' },
        {
            key: 'prerequisiteCourses',
            header: 'Prerequisites',
            render: (course) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {course.prerequisiteCourses && course.prerequisiteCourses.length > 0
                        ? course.prerequisiteCourses.join(', ')
                        : '-'
                    }
                </span>
            )
        },
        {
            key: 'requiredPlacementLevel',
            header: 'Placement',
            render: (course) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {course.requiredPlacementLevel
                        ? `L${course.requiredPlacementLevel}${course.allowHigherPlacement ? '+' : ''}`
                        : '-'
                    }
                </span>
            )
        },
        {
            key: 'isActive',
            header: 'Active',
            render: (course) => (
                <Badge className={course.isActive ? 'bg-green-100 text-green-700' : 'bg-gray-300 text-gray-600'}>
                    {course.isActive ? 'Active' : 'Inactive'}
                </Badge>
            )
        },
        {
            key: 'sections',
            header: 'Sections',
            render: (course) => (
                <button
                    onClick={() => handleManageSections(course)}
                    className="flex items-center space-x-1 px-3 py-1 rounded-lg hover:bg-gray-100 transition-colors"
                    style={{ color: COLORS.orange }}
                >
                    <List size={16} />
                    <span className="font-medium">{getSectionsCount(course.id)}</span>
                </button>
            )
        },
        {
            key: 'actions',
            header: 'Actions',
            render: (course) => {
                const sectionsCount = getSectionsCount(course.id);

                return (
                    <div className="flex space-x-2" onClick={(e) => e.stopPropagation()}>
                        <Button variant="ghost" size="sm" onClick={() => handleEditCourse(course.id)}>
                            Edit
                        </Button>
                        {sectionsCount === 0 && (
                            <Button variant="danger" size="sm" onClick={() => handleDeleteCourse(course.id)}>
                                Delete
                            </Button>
                        )}
                    </div>
                );
            }
        }
    ];

    // Columns for sections table
    const sectionColumns: ColumnDef[] = [
        {
            key: 'sectionCode',
            header: 'Section',
            render: (section) => (
                <span className="text-sm font-mono font-bold" style={{ color: COLORS.dark }}>
                    {section.sectionCode}
                </span>
            )
        },
        {
            key: 'instructorName',
            header: 'Instructor',
            render: (section) => (
                section.instructorName && section.instructorId ? (
                    <button
                        onClick={(e) => {
                            e.stopPropagation();
                            handleViewInstructorDetail(section.instructorId);
                        }}
                        className="hover:underline cursor-pointer font-medium text-sm"
                        style={{ color: COLORS.orange }}
                    >
                        {section.instructorName}
                    </button>
                ) : (
                    <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>-</span>
                )
            )
        },
        {
            key: 'schedule',
            header: 'Schedule',
            render: (section) => (
                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    {section.schedule || '-'}
                </span>
            )
        },
        {
            key: 'location',
            header: 'Location',
            className: 'text-sm'
        },
        {
            key: 'courseFormat',
            header: 'Format',
            render: TableRenderers.badge('courseFormat', COURSE_FORMAT)  // ← 加 'courseFormat'
        },
        {
            key: 'enrolled',
            header: 'Enrolled',
            render: (section) => (
                <button
                    onClick={() => onViewEnrollments(section.id)}
                    className="hover:underline cursor-pointer"
                >
                    <span className="font-medium" style={{ color: COLORS.orange }}>
                        {section.enrolledCount}
                    </span>
                    <span style={{ color: COLORS.dark, opacity: 0.5 }}>
                        /{section.capacity}
                    </span>
                </button>
            )
        },
        {
            key: 'enrollmentLocked',
            header: 'Enrollment',
            render: (section) => (
                section.enrollmentLocked ? (
                    <span className="flex items-center space-x-1 text-xs font-medium" style={{ color: COLORS.orange }}>
                        <span>🔒</span>
                        <span>Locked</span>
                    </span>
                ) : (
                    <span className="text-xs font-medium" style={{ color: COLORS.orange }}>
                        Open
                    </span>
                )
            )
        },
        {
            key: 'status',
            header: 'Status',
            render: TableRenderers.badge('status', SECTION_STATUS)
        },
        {
            key: 'actions',
            header: 'Actions',
            render: (section) => (
                <div className="flex space-x-2" onClick={(e) => e.stopPropagation()}>
                    <Button variant="ghost" size="sm" onClick={() => handleEditSection(section)}>
                        Edit
                    </Button>
                    {section.status === 'DRAFT' && section.enrolledCount === 0 && (
                        <Button variant="danger" size="sm" onClick={() => handleDeleteSection(section.id)}>
                            Delete
                        </Button>
                    )}
                </div>
            )
        }
    ];

    return (
        <div className="space-y-6">
            <PageHeader title="Course Management" />

            <div className="bg-white rounded-3xl shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                <div className="p-6">
                    {/* Filters */}
                    <div className="mb-4 flex items-center gap-3">
                        <FilterSelect
                            value={sessionFilter}
                            options={[
                                { value: '', label: 'All Sessions' },
                                ...allSessions.map(s => ({ value: s.sessionCode, label: s.sessionCode }))
                            ]}
                            onChange={setSessionFilter}
                            width="160px"
                        />

                        <SearchBar
                            value={searchTerm}
                            onChange={setSearchTerm}
                            placeholder="Search courses..."
                        />
                    </div>

                    {/* Header */}
                    <div className="flex justify-between mb-6">
                        <h2 className="text-xl font-bold" style={{ color: COLORS.dark }}>
                            All Courses ({displayedCourses.length})
                        </h2>
                        <Button variant="primary" onClick={() => setShowCreateCourseModal(true)}>
                            + Create Course
                        </Button>
                    </div>

                    {/* Courses Table */}
                    <DataTable
                        columns={courseColumns}
                        data={displayedCourses}
                        emptyMessage="No courses found"
                    />
                </div>
            </div>

            {/* Create Course Modal */}
            <Modal isOpen={showCreateCourseModal} onClose={() => setShowCreateCourseModal(false)} size="xl">
                <CourseForm
                    mode="create"
                    sessions={allSessions}
                    allCourses={courses}
                    onSubmit={handleCreateCourse}
                    onCancel={() => setShowCreateCourseModal(false)}
                />
            </Modal>

            {/* Edit Course Modal */}
            <Modal isOpen={showEditCourseModal} onClose={() => { setShowEditCourseModal(false); setEditingCourse(null); }} size="xl">
                {editingCourse && (
                    <CourseForm
                        mode="edit"
                        initialData={editingCourse}
                        sessions={allSessions}
                        allCourses={courses}
                        onSubmit={handleUpdateCourse}
                        onCancel={() => { setShowEditCourseModal(false); setEditingCourse(null); }}
                    />
                )}
            </Modal>

            {/* Manage Sections Modal */}
            <Modal isOpen={showSectionsModal} onClose={() => { setShowSectionsModal(false); setSelectedSectionIds(new Set()); }} size="full">
                {selectedCourse && (
                    <div className="space-y-6">
                        {/* Header */}
                        <div className="flex justify-between items-start">
                            <h3 className="text-2xl font-bold" style={{ color: COLORS.dark }}>
                                Manage Sections
                            </h3>
                            <Button variant="secondary" onClick={() => { setShowSectionsModal(false); setSelectedSectionIds(new Set()); }}>
                                Close
                            </Button>
                        </div>

                        {/* Course Info Card */}
                        <div className="p-4 rounded-lg" style={{ backgroundColor: COLORS.cream }}>
                            <div className="text-lg font-bold mb-2" style={{ color: COLORS.dark }}>
                                {selectedCourse.courseCode} - {selectedCourse.courseName}
                            </div>
                            <div className="flex items-center gap-3 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                <span>Session: <strong>{selectedCourse.sessionCode || selectedCourse.sessionId}</strong></span>
                                <span>•</span>
                                <span><strong>{selectedCourse.hoursPerWeek}</strong> hours/week</span>
                            </div>

                            {selectedCourse.courseDescription && (
                                <div className="mt-2 text-sm" style={{ color: COLORS.dark, opacity: 0.75 }}>
                                    {selectedCourse.courseDescription}
                                </div>
                            )}

                            {(selectedCourse.prerequisiteCourses?.length > 0 || selectedCourse.requiredPlacementLevel) && (
                                <div className="mt-3 pt-3" style={{ borderTop: `1px solid ${COLORS.lightOrange}` }}>
                                    <div className="text-xs font-semibold mb-2" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                        PREREQUISITES
                                    </div>

                                    <div className="flex flex-wrap items-center gap-2">
                                        {selectedCourse.prerequisiteCourses?.length > 0 && (
                                            <>
                                                <span className="text-xs font-medium" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                                    Courses:
                                                </span>
                                                {selectedCourse.prerequisiteCourses.map((code, idx) => (
                                                    <span
                                                        key={idx}
                                                        className="px-2 py-0.5 rounded text-xs font-mono font-semibold"
                                                        style={{ backgroundColor: COLORS.dark, color: 'white' }}
                                                    >
                                                        {code}
                                                    </span>
                                                ))}
                                                {selectedCourse.requiredPlacementLevel && (
                                                    <span className="text-xs font-bold mx-1" style={{ color: COLORS.orange }}>
                                                        OR
                                                    </span>
                                                )}
                                            </>
                                        )}

                                        {selectedCourse.requiredPlacementLevel && (
                                            <>
                                                <span className="text-xs font-medium" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                                    Placement:
                                                </span>
                                                <span
                                                    className="px-2 py-0.5 rounded text-xs font-bold"
                                                    style={{ backgroundColor: COLORS.orange, color: 'white' }}
                                                >
                                                    L{selectedCourse.requiredPlacementLevel}{selectedCourse.allowHigherPlacement ? '+' : ''}
                                                </span>
                                            </>
                                        )}
                                    </div>
                                </div>
                            )}
                        </div>

                        {/* Sections List */}
                        <div>
                            {/* Batch Operations Toolbar */}
                            <BatchToolBar
                                selectedCount={selectedSectionIds.size}
                                itemLabel="sections"
                                onClear={() => setSelectedSectionIds(new Set())}
                                actions={[
                                    { label: 'Publish', variant: 'primary', onClick: handleBatchPublish },
                                    { label: 'Lock', variant: 'secondary', onClick: () => handleBatchLockEnrollment(true) },
                                    { label: 'Unlock', variant: 'secondary', onClick: () => handleBatchLockEnrollment(false) },
                                    { label: 'Status', variant: 'secondary', onClick: () => setShowBatchStatusModal(true) }
                                ]}
                            />

                            <div className="flex justify-between mb-4">
                                <h4 className="text-lg font-bold" style={{ color: COLORS.dark }}>
                                    Sections ({courseSections.length})
                                </h4>
                                <Button variant="primary" onClick={() => setShowCreateSectionModal(true)}>
                                    + Add Section
                                </Button>
                            </div>

                            <DataTable
                                columns={sectionColumns}
                                data={courseSections}
                                selectedIds={selectedSectionIds}
                                onToggle={handleToggleSection}
                                onSelectAll={handleSelectAllSections}
                                showCheckbox
                                emptyMessage="No sections created yet"
                            />
                        </div>
                    </div>
                )}
            </Modal>

            {/* Create Section Modal */}
            <Modal isOpen={showCreateSectionModal} onClose={() => setShowCreateSectionModal(false)} size="xl">
                {selectedCourse && (
                    <SectionForm
                        mode="create"
                        courses={[selectedCourse]}
                        instructors={instructors}
                        onSubmit={handleCreateSection}
                        onCancel={() => setShowCreateSectionModal(false)}
                    />
                )}
            </Modal>

            {/* Edit Section Modal */}
            <Modal isOpen={showEditSectionModal} onClose={() => { setShowEditSectionModal(false); setEditingSection(null); }} size="xl">
                {editingSection && (
                    <SectionForm
                        mode="edit"
                        initialData={editingSection}
                        courses={courses}
                        instructors={instructors}
                        onSubmit={handleUpdateSection}
                        onCancel={() => { setShowEditSectionModal(false); setEditingSection(null); }}
                    />
                )}
            </Modal>

            {/* Batch Status Modal */}
            {showBatchStatusModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-[70]">
                    <div className="bg-white rounded-2xl p-6 max-w-md w-full mx-4 shadow-xl">
                        <h3 className="text-xl font-bold mb-4" style={{ color: COLORS.dark }}>
                            Change Status
                        </h3>

                        <p className="text-sm mb-6" style={{ color: COLORS.dark }}>
                            Update <strong>{selectedSectionIds.size}</strong> section{selectedSectionIds.size > 1 ? 's' : ''}
                        </p>

                        <select
                            value={batchTargetStatus}
                            onChange={(e) => setBatchTargetStatus(e.target.value)}
                            className="w-full px-4 py-2 border rounded-lg mb-6 cursor-pointer"
                            style={{ borderColor: COLORS.bg }}
                        >
                            <option value="">Select Status</option>
                            <option value="DRAFT">Draft</option>
                            <option value="PUBLISHED">Published</option>
                            <option value="COMPLETED">Completed</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>

                        <div className="flex space-x-3">
                            <Button variant="primary" onClick={handleBatchStatusChange} className="flex-1">
                                Confirm
                            </Button>
                            <Button variant="secondary" onClick={() => { setShowBatchStatusModal(false); setBatchTargetStatus(''); }} className="flex-1">
                                Cancel
                            </Button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};