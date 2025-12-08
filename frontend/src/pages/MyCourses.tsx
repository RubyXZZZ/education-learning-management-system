import React, { useState, useEffect } from 'react';
import { Clock, MapPin, Plus, Mail, Eye, EyeOff, Edit2, Trash2 } from 'lucide-react';
import { PageHeader } from '../components/common/PageHeader';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { DataTable, type ColumnDef } from '../components/common/DataTable';
import { COLORS } from '../constants/colors';
import { enrollmentApi, sectionApi, moduleApi, coursePageApi, assignmentApi, submissionApi } from '../services/api';
import { useAuth } from '../contexts/AppContext';
import { CoursePage } from './CoursePage';
import { AssignmentPage } from './AssignmentPage';
import { UserDetail } from './UserDetail';
import type { EnrollmentRes, SectionRes, ModuleRes, CoursePageRes, AssignmentRes, SubmissionRes } from '../types';
import { ASSIGNMENT_TYPE, SUBMISSION_STATUS } from '../types';

export const MyCourses: React.FC = () => {
    const { currentUser, isStudent, isInstructor } = useAuth();

    // Data
    const [myCourses, setMyCourses] = useState<(EnrollmentRes | SectionRes)[]>([]);
    const [completedCourses, setCompletedCourses] = useState<(EnrollmentRes | SectionRes)[]>([]);
    const [selectedCourse, setSelectedCourse] = useState<EnrollmentRes | SectionRes | null>(null);
    const [selectedCompletedId, setSelectedCompletedId] = useState<string>('');

    const [modules, setModules] = useState<ModuleRes[]>([]);
    const [pages, setPages] = useState<CoursePageRes[]>([]);
    const [selectedPageId, setSelectedPageId] = useState<string | null>(null);
    const [assignments, setAssignments] = useState<AssignmentRes[]>([]);
    const [submissions, setSubmissions] = useState<SubmissionRes[]>([]);
    const [enrolledStudents, setEnrolledStudents] = useState<EnrollmentRes[]>([]);
    const [allSubmissions, setAllSubmissions] = useState<SubmissionRes[]>([]);

    // UI State
    const [activeTab, setActiveTab] = useState<'content' | 'assignments' | 'grades' | 'people' | 'announcements' | 'attendance'>('content');
    const [loading, setLoading] = useState(true);
    const [showCreateModule, setShowCreateModule] = useState(false);
    const [newModuleName, setNewModuleName] = useState('');
    const [newModuleDescription, setNewModuleDescription] = useState('');
    const [editingModuleId, setEditingModuleId] = useState<string | null>(null);
    const [editModuleName, setEditModuleName] = useState('');
    const [editModuleDescription, setEditModuleDescription] = useState('');

    const [viewMode, setViewMode] = useState<'courses' | 'student-detail' | 'page-detail' | 'assignment-detail'>('courses');
    const [createPageModuleId, setCreatePageModuleId] = useState<string | null>(null);
    const [selectedStudentId, setSelectedStudentId] = useState<string | null>(null);
    const [selectedAssignmentId, setSelectedAssignmentId] = useState<string | null>(null);

    useEffect(() => {
        loadMyCourses();
    }, [currentUser]);

    useEffect(() => {
        if (selectedCourse && viewMode === 'courses') {
            loadCourseContent();
        }
    }, [selectedCourse, activeTab]);

    const loadMyCourses = async () => {
        if (!currentUser) return;

        try {
            setLoading(true);
            if (isStudent) {
                const res = await enrollmentApi.getMyEnrollments();

                const active = res.data.filter((e: EnrollmentRes) => e.status === 'ENROLLED');
                const completed = res.data.filter((e: EnrollmentRes) => e.status === 'COMPLETED');

                setMyCourses(active);
                setCompletedCourses(completed);

                if (active.length > 0) {
                    setSelectedCourse(active[0]);
                } else if (completed.length > 0) {
                    setSelectedCourse(completed[0]);
                    setSelectedCompletedId(completed[0].id);
                }
            } else if (isInstructor) {
                const res = await sectionApi.getMySections();
                const active = res.data.filter((s: SectionRes) => s.status === 'PUBLISHED');
                const completed = res.data.filter((s: SectionRes) => s.status === 'COMPLETED');
                setMyCourses(active);
                setCompletedCourses(completed);

                if (active.length > 0) {
                    setSelectedCourse(active[0]);
                }
            }
        } catch (err) {
            console.error('Error loading courses:', err);
        } finally {
            setLoading(false);
        }
    };

    const loadCourseContent = async () => {
        if (!selectedCourse) return;

        const sectionId = 'courseSectionId' in selectedCourse
            ? selectedCourse.courseSectionId
            : selectedCourse.id;

        try {
            setLoading(true);

            if (activeTab === 'content') {
                const [modulesRes, pagesRes] = await Promise.all([
                    isStudent
                        ? moduleApi.getPublishedBySection(sectionId)
                        : moduleApi.getBySection(sectionId),
                    isStudent
                        ? coursePageApi.getPublishedBySection(sectionId)
                        : coursePageApi.getBySection(sectionId)
                ]);

                setModules(modulesRes.data);
                setPages(pagesRes.data);
            }

            if (activeTab === 'assignments') {
                const assignmentsRes = isStudent
                    ? await assignmentApi.getPublishedBySection(sectionId)
                    : await assignmentApi.getBySection(sectionId);

                setAssignments(assignmentsRes.data);
                if (isStudent && currentUser) {
                    try {
                        const submissionsRes = await submissionApi.getByStudent(currentUser.id);
                        setSubmissions(submissionsRes.data);
                    } catch (err) {
                        console.log('No submissions found');
                        setSubmissions([]);
                    }
                }
            }

            if (activeTab === 'grades' && isInstructor) {
                const [assignmentsRes, enrollmentsRes] = await Promise.all([
                    assignmentApi.getBySection(sectionId),
                    enrollmentApi.getBySection(sectionId)
                ]);

                setAssignments(assignmentsRes.data);
                setEnrolledStudents(enrollmentsRes.data);
                const allSubs: SubmissionRes[] = [];
                for (const enrollment of enrollmentsRes.data) {
                    try {
                        const subsRes = await submissionApi.getByStudent(enrollment.studentId);
                        allSubs.push(...subsRes.data);
                    } catch (err) {
                        console.log('No submissions for student:', enrollment.studentId);
                    }
                }
                setAllSubmissions(allSubs);
            }

            if (activeTab === 'people') {
                const enrollmentsRes = await enrollmentApi.getBySection(sectionId);
                setEnrolledStudents(enrollmentsRes.data);
            }

        } catch (err) {
            console.error('Error loading course content:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleCompletedSelect = (courseId: string) => {
        if (!courseId) {
            setSelectedCompletedId('');
            if (myCourses.length > 0) {
                setSelectedCourse(myCourses[0]);
            }
            return;
        }

        const course = completedCourses.find(c => c.id === courseId);
        if (course) {
            setSelectedCompletedId(courseId);
            setSelectedCourse(course);
        }
    };

    const isViewingCompleted = selectedCourse
        ? completedCourses.some(c => c.id === selectedCourse.id)
        : false;


    const handleCreateModule = async () => {
        if (!newModuleName.trim() || !selectedCourse) return;

        const sectionId = 'courseSectionId' in selectedCourse
            ? selectedCourse.courseSectionId
            : selectedCourse.id;

        try {
            const maxOrder = modules.length > 0 ? Math.max(...modules.map(m => m.orderNum)) : 0;

            await moduleApi.create({
                courseSectionId: sectionId,
                name: newModuleName,
                description: newModuleDescription || null,
                orderNum: maxOrder + 1,
                isPublished: false
            });

            setNewModuleName('');
            setNewModuleDescription('');
            setShowCreateModule(false);
            await loadCourseContent();
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to create module: ' + errorMsg);
        }
    };

    const handleToggleModulePublish = async (moduleId: string, currentStatus: boolean) => {
        try {
            if (currentStatus) {
                await moduleApi.unpublish(moduleId);
            } else {
                await moduleApi.publish(moduleId);
            }
            await loadCourseContent();
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to update module: ' + errorMsg);
        }
    };

    const handleEditModule = (module: ModuleRes) => {
        setEditingModuleId(module.id);
        setEditModuleName(module.name);
        setEditModuleDescription(module.description || '');
    };

    const handleUpdateModule = async (moduleId: string) => {
        try {
            await moduleApi.update(moduleId, {
                name: editModuleName,
                description: editModuleDescription || null
            });

            setEditingModuleId(null);
            setEditModuleName('');
            setEditModuleDescription('');
            await loadCourseContent();
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to update module: ' + errorMsg);
        }
    };


    const handleViewPage = (pageId: string) => {
        setSelectedPageId(pageId);
        setViewMode('page-detail');
    };

    const handleBackFromPage = async () => {
        setViewMode('courses');
        setSelectedPageId(null);
        setCreatePageModuleId(null);

        await loadCourseContent();
    };

    const handleViewStudentProfile = (studentId: string) => {
        setSelectedStudentId(studentId);
        setViewMode('student-detail');
    };

    const handleBackToCourses = () => {
        setViewMode('courses');
        setSelectedStudentId(null);
    };

    const handleViewAssignment = (assignmentId: string) => {
        setSelectedAssignmentId(assignmentId);
        setViewMode('assignment-detail');
    };

    const handleBackFromAssignment = async () => {
        setViewMode('courses');
        setSelectedAssignmentId(null);
        await loadCourseContent();
    };

    const calculateStudentGrade = (studentId: string) => {
        const studentSubs = allSubmissions.filter(s => s.studentId === studentId);

        const gradedAssignments = assignments.filter(a =>
            studentSubs.find(s => s.assignmentId === a.id && s.grade !== null)
        );

        const totalPoints = gradedAssignments.reduce((sum, a) => sum + a.totalPoints, 0);

        const earnedPoints = gradedAssignments.reduce((sum, assignment) => {
            const sub = studentSubs.find(s =>
                s.assignmentId === assignment.id && s.grade !== null
            );
            return sum + (sub?.grade || 0);
        }, 0);

        const percentage = totalPoints > 0
            ? parseFloat(((earnedPoints / totalPoints) * 100).toFixed(2))
            : 0;

        return {
            totalPoints,
            earnedPoints,
            percentage,
            percentageDisplay: totalPoints > 0 ? `${percentage.toFixed(1)}%` : '-'  // 添加 %
        };
    };

    const handleFinalizeGrades = async () => {
        if (!confirm('Finalize grades for all students?')) return;

        try {
            setLoading(true);

            for (const enrollment of enrolledStudents) {
                const gradeInfo = calculateStudentGrade(enrollment.studentId);

                await enrollmentApi.complete({
                    enrollmentId: enrollment.id,
                    finalGrade: gradeInfo.percentage,
                    letterGrade: ''
                });
            }

            alert('Grades finalized successfully!');
            await loadCourseContent();

        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to finalize grades: ' + errorMsg);
        } finally {
            setLoading(false);
        }
    };

    const getCourseInfo = () => {
        if (!selectedCourse) return null;

        if ('courseSectionId' in selectedCourse) {
            return {
                id: selectedCourse.courseSectionId,
                code: selectedCourse.courseCode,
                name: selectedCourse.courseName,
                sectionCode: selectedCourse.sectionCode,
                instructor: selectedCourse.instructorName,
                instructorEmail: selectedCourse.instructorEmail,
                sessionCode: selectedCourse.sessionCode,
                schedule: null,
                location: null,
                enrolledCount: 0,
                instructorId: selectedCourse.instructorId
            };
        } else {
            return {
                id: selectedCourse.id,
                code: selectedCourse.courseCode,
                name: selectedCourse.courseName,
                sectionCode: selectedCourse.sectionCode,
                instructor: selectedCourse.instructorName,
                instructorEmail: selectedCourse.instructorEmail,
                sessionCode: selectedCourse.sessionCode,
                schedule: selectedCourse.schedule,
                location: selectedCourse.location,
                enrolledCount: selectedCourse.enrolledCount,
                instructorId: selectedCourse.instructorId
            };
        }
    };

    const courseInfo = getCourseInfo();

    const pagesByModule = pages.reduce((acc, page) => {
        if (!page.moduleId) return acc;
        if (!acc[page.moduleId]) acc[page.moduleId] = [];
        acc[page.moduleId].push(page);
        return acc;
    }, {} as Record<string, CoursePageRes[]>);

    const visibleModules = isStudent
        ? modules.filter(m => m.isPublished)
        : modules;

    const getInstructorInfo = () => {
        if (!courseInfo) return null;

        if ('courseSectionId' in selectedCourse!) {
            const enrollment = selectedCourse as EnrollmentRes;
            return {
                name: enrollment.instructorName,
                email: enrollment.instructorEmail,
                avatar: null,
                officeHours: null
            };
        } else {
            const section = selectedCourse as SectionRes;
            return {
                name: section.instructorName,
                email: section.instructorEmail,
                avatar: null,
                officeHours: null
            };
        }
    };

    const instructorInfo = getInstructorInfo();

    // People Table Columns
    const peopleColumns: ColumnDef[] = [
        {
            key: 'studentName',
            header: 'Student',
            render: (enrollment) => (
                <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full flex items-center justify-center text-lg flex-shrink-0 bg-gray-200">
                        👤
                    </div>
                    <span className="font-medium text-sm" style={{ color: COLORS.dark }}>
                        {enrollment.studentName}
                    </span>
                </div>
            )
        },
        {
            key: 'studentEmail',
            header: 'Email',
            render: (enrollment) => (
                <div className="flex items-center gap-2 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    <Mail size={14} />
                    <span className="truncate">{enrollment.studentEmail}</span>
                </div>
            )
        },
        {
            key: 'actions',
            header: '',
            render: (enrollment) => (
                isInstructor ? (
                    <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => handleViewStudentProfile(enrollment.studentId)}
                    >
                        Profile
                    </Button>
                ) : null
            )
        }
    ];

    // Show student detail if in detail view mode
    if (viewMode === 'student-detail' && selectedStudentId) {
        return <UserDetail userId={selectedStudentId} userRole="STUDENT" onBack={handleBackToCourses} />;
    }


    if (loading && !selectedCourse) {
        return (
            <div className="space-y-6">
                <PageHeader title="My Courses" />
                <div className="flex items-center justify-center h-64">
                    <div className="text-xl" style={{ color: COLORS.dark }}>Loading...</div>
                </div>
            </div>
        );
    }

    if (!selectedCourse || !courseInfo) {
        return (
            <div className="space-y-6">
                <PageHeader title="My Courses" />
                <div className="flex items-center justify-center h-64">
                    <p className="text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        No courses enrolled
                    </p>
                </div>
            </div>
        );
    }

    if (viewMode === 'page-detail') {
        if (selectedPageId) {
            return <CoursePage mode="view" pageId={selectedPageId} onBack={handleBackFromPage} />;
        } else if (createPageModuleId) {
            const sectionId = 'courseSectionId' in selectedCourse!
                ? selectedCourse.courseSectionId
                : selectedCourse.id;

            return (
                <CoursePage
                    mode="create"
                    moduleId={createPageModuleId}
                    sectionId={sectionId}
                    onBack={handleBackFromPage}
                />
            );
        }
    }

    if (viewMode === 'assignment-detail') {
        if (selectedAssignmentId) {
            return <AssignmentPage mode="view" assignmentId={selectedAssignmentId} onBack={handleBackFromAssignment} />;
        } else {
            const sectionId = 'courseSectionId' in selectedCourse!
                ? selectedCourse.courseSectionId
                : selectedCourse.id;
            return <AssignmentPage mode="create" sectionId={sectionId} onBack={handleBackFromAssignment} />;
        }
    }

    return (
        <div className="space-y-6 pb-8">
            <PageHeader title="My Courses" />

            {/* Course Tabs */}
            <div className="bg-white rounded-3xl p-4 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                <div className="flex items-center justify-between gap-4">
                    {/* Left: Current Course Tabs */}
                    {!selectedCompletedId && (
                        <div className="flex items-center gap-2 overflow-x-auto flex-1">
                            {myCourses.map((course) => {
                                const code = 'courseCode' in course ? course.courseCode : '';
                                const section = 'sectionCode' in course ? course.sectionCode : '';
                                const isActive = selectedCourse === course;

                                return (
                                    <button
                                        key={'id' in course ? course.id : ''}
                                        onClick={() => {
                                            setSelectedCourse(course);
                                            setSelectedCompletedId('');
                                        }}
                                        className="px-4 py-2 rounded-xl font-semibold text-sm whitespace-nowrap transition-all"
                                        style={{
                                            backgroundColor: isActive ? COLORS.orange : COLORS.bg + '40',
                                            color: isActive ? 'white' : COLORS.dark
                                        }}
                                    >
                                        {code}-{section}
                                    </button>
                                );
                            })}
                        </div>
                    )}

                    {/* view completed course state, give notice*/}
                    {selectedCompletedId && (
                        <div className="flex-1 px-4 py-2 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                            Viewing completed course
                        </div>
                    )}

                    {/* Right: Completed Courses Dropdown - */}
                    <div className="flex-shrink-0">
                        <select
                            value={selectedCompletedId}
                            onChange={(e) => handleCompletedSelect(e.target.value)}
                            className="px-4 py-2 rounded-xl text-sm border transition-all"
                            style={{
                                borderColor: COLORS.bg,
                                backgroundColor: 'white',
                                minWidth: '250px',
                                color: COLORS.dark
                            }}
                            disabled={completedCourses.length === 0}
                        >
                            <option value="">
                                {completedCourses.length === 0
                                    ? 'No completed courses'
                                    : selectedCompletedId
                                        ? 'View current courses'
                                        : 'View completed course'
                                }
                            </option>
                            {completedCourses.map(course => {
                                const code = 'courseCode' in course ? course.courseCode : '';
                                const section = 'sectionCode' in course ? course.sectionCode : '';
                                const session = 'sessionCode' in course ? course.sessionCode : '';

                                return (
                                    <option key={course.id} value={course.id}>
                                        {code} - {section} ({session})
                                    </option>
                                );
                            })}
                        </select>
                    </div>
                </div>
            </div>

            {/* Course Info Card */}
            <div className="rounded-3xl p-6" style={{ backgroundColor: COLORS.cream }}>
                <h1 className="text-2xl font-bold mb-2 flex items-center gap-2" style={{ color: COLORS.dark }}>
                    {courseInfo?.code} - {courseInfo?.name}
                    {isViewingCompleted && (
                        <span className="text-xs px-3 py-1 bg-gray-100 text-gray-600 rounded-full font-medium">
                            Completed
                        </span>
                    )}
                </h1>
                <p className="text-sm mb-4" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    Section {courseInfo?.sectionCode} • Instructor: {courseInfo?.instructor}
                </p>
                <div className="flex gap-3 flex-wrap">
                    <div className="bg-white px-3 py-1.5 rounded-full text-xs flex items-center gap-1.5" style={{ border: `1px solid ${COLORS.bg}` }}>
                        <Clock size={14} />
                        <span>{courseInfo.sessionCode}</span>
                    </div>

                    {courseInfo.schedule && (
                        <div className="bg-white px-3 py-1.5 rounded-full text-xs flex items-center gap-1.5" style={{ border: `1px solid ${COLORS.bg}` }}>
                            <Clock size={14} />
                            <span>{courseInfo.schedule}</span>
                        </div>
                    )}

                    {courseInfo.location && (
                        <div className="bg-white px-3 py-1.5 rounded-full text-xs flex items-center gap-1.5" style={{ border: `1px solid ${COLORS.bg}` }}>
                            <MapPin size={14} />
                            <span>{courseInfo.location}</span>
                        </div>
                    )}

                    {isInstructor && (
                        <div className="bg-white px-3 py-1.5 rounded-full text-xs" style={{ border: `1px solid ${COLORS.bg}` }}>
                            {courseInfo.enrolledCount} students
                        </div>
                    )}
                </div>
            </div>

            {/* Tabs */}
            <div className="bg-white rounded-2xl shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                <div className="flex border-b" style={{ borderColor: COLORS.bg }}>
                    {[
                        { key: 'content', label: 'Content' },
                        { key: 'assignments', label: 'Assignments' },
                        { key: 'grades', label: 'Grades' },
                        { key: 'people', label: 'People' },
                    ].map(tab => (
                        <button
                            key={tab.key}
                            onClick={() => setActiveTab(tab.key as any)}
                            className="px-6 py-4 text-sm font-medium transition-colors"
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
                    {/* Content Tab */}
                    {activeTab === 'content' && (
                        <div className="space-y-4">
                            {/* Top Action Bar */}
                            {isInstructor && !isViewingCompleted && (
                                <div className="flex justify-end">
                                    <Button
                                        variant="primary"
                                        size="sm"
                                        icon={<Plus size={16} />}
                                        onClick={() => setShowCreateModule(true)}
                                    >
                                        Add Module
                                    </Button>
                                </div>
                            )}

                            {/* Modules List - All Expanded */}
                            {visibleModules.length === 0 && !showCreateModule ? (
                                <div className="text-center py-16">
                                    <p className="text-sm mb-6" style={{ color: COLORS.dark, opacity: 0.5 }}>
                                        No modules created yet
                                    </p>
                                    {isInstructor && !isViewingCompleted && (
                                        <Button
                                            variant="primary"
                                            icon={<Plus size={16} />}
                                            onClick={() => setShowCreateModule(true)}
                                        >
                                            Create First Module
                                        </Button>
                                    )}
                                </div>
                            ) : (
                                <div className="space-y-3">
                                    {visibleModules.map(module => {
                                        const modulePagesData = pagesByModule[module.id] || [];
                                        const isEditing = editingModuleId === module.id;

                                        return (
                                            <div key={module.id} className="rounded-xl" style={{ backgroundColor: COLORS.bg + '40', borderLeft: `4px solid ${COLORS.cream}` }}>
                                                {isEditing ? (
                                                    // Edit Mode
                                                    <div className="p-4 space-y-3">
                                                        <input
                                                            type="text"
                                                            value={editModuleName}
                                                            onChange={(e) => setEditModuleName(e.target.value)}
                                                            className="w-full px-3 py-2 border rounded-lg text-sm font-semibold"
                                                            style={{ borderColor: COLORS.bg, backgroundColor: 'white' }}
                                                            placeholder="Module name"
                                                        />
                                                        <textarea
                                                            value={editModuleDescription}
                                                            onChange={(e) => setEditModuleDescription(e.target.value)}
                                                            className="w-full px-3 py-2 border rounded-lg text-xs resize-none"
                                                            style={{ borderColor: COLORS.bg, backgroundColor: 'white' }}
                                                            placeholder="Description (optional)"
                                                            rows={2}
                                                        />
                                                        <div className="flex gap-2">
                                                            <Button
                                                                variant="primary"
                                                                size="sm"
                                                                onClick={() => handleUpdateModule(module.id)}
                                                            >
                                                                Save
                                                            </Button>
                                                            <Button
                                                                variant="ghost"
                                                                size="sm"
                                                                onClick={() => {
                                                                    setEditingModuleId(null);
                                                                    setEditModuleName('');
                                                                    setEditModuleDescription('');
                                                                }}
                                                            >
                                                                Cancel
                                                            </Button>
                                                        </div>
                                                    </div>
                                                ) : (
                                                    // View Mode - Always Expanded
                                                    <>
                                                        <div className="p-4 flex justify-between items-center">
                                                            {/* Module Title */}
                                                            <div className="flex-1">
                                                                <h4 className="font-semibold text-base" style={{ color: COLORS.dark }}>
                                                                    {module.name}
                                                                </h4>
                                                                {module.description && (
                                                                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                                                        {module.description}
                                                                    </p>
                                                                )}
                                                            </div>

                                                            {/* Action Buttons */}
                                                            {isInstructor && !isViewingCompleted && (
                                                                <div className="flex items-center gap-2">
                                                                    <Button
                                                                        variant="ghost"
                                                                        size="sm"
                                                                        icon={<Plus size={14} />}
                                                                        onClick={() => {
                                                                            setCreatePageModuleId(module.id);
                                                                            setViewMode('page-detail');
                                                                            setSelectedPageId(null);
                                                                        }}
                                                                    >
                                                                        Add Page
                                                                    </Button>

                                                                    <button
                                                                        onClick={() => handleEditModule(module)}
                                                                        className="p-1.5 rounded hover:bg-white transition-colors"
                                                                        title="Edit module"
                                                                    >
                                                                        <Edit2 size={16} style={{ color: COLORS.dark, opacity: 0.6 }} />
                                                                    </button>

                                                                    <button
                                                                        onClick={() => handleToggleModulePublish(module.id, module.isPublished)}
                                                                        className="p-1.5 rounded hover:bg-white transition-colors"
                                                                        title={module.isPublished ? 'Hide from students' : 'Show to students'}
                                                                    >
                                                                        {module.isPublished ? (
                                                                            <Eye size={16} style={{ color: COLORS.orange }} />
                                                                        ) : (
                                                                            <EyeOff size={16} style={{ color: COLORS.dark, opacity: 0.4 }} />
                                                                        )}
                                                                    </button>

                                                                    <button
                                                                        onClick={async () => {
                                                                            if (confirm(`Delete module "${module.name}"?`)) {
                                                                                try {
                                                                                    await moduleApi.delete(module.id);
                                                                                    await loadCourseContent();
                                                                                } catch (err: any) {
                                                                                    const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
                                                                                    alert('Failed to delete: ' + errorMsg);
                                                                                }
                                                                            }
                                                                        }}
                                                                        className="p-1.5 rounded hover:bg-red-50 transition-colors"
                                                                        title="Delete module"
                                                                    >
                                                                        <Trash2 size={16} className="text-red-600" />
                                                                    </button>
                                                                </div>
                                                            )}
                                                        </div>




                                                        {/* Pages List  */}
                                                        <div className="px-4 pb-4 space-y-2">
                                                            {modulePagesData.length > 0 ? (
                                                                modulePagesData.map(page => (
                                                                    <div
                                                                        key={page.id}
                                                                        className="flex justify-between items-center p-3 bg-white rounded-lg cursor-pointer hover:shadow-sm transition-shadow"
                                                                        style={{ border: `1px solid ${COLORS.bg}` }}
                                                                        onClick={() => handleViewPage(page.id)}
                                                                    >
                                                                        <span className="text-sm font-medium" style={{ color: COLORS.dark }}>
                                                                            {page.title}
                                                                        </span>
                                                                        {isInstructor && !isViewingCompleted && (
                                                                            <div className="flex items-center gap-2">
                                                                                <button
                                                                                    onClick={async (e) => {
                                                                                        e.stopPropagation();
                                                                                        try {
                                                                                            if (page.isPublished) {
                                                                                                await coursePageApi.unpublish(page.id);
                                                                                            } else {
                                                                                                await coursePageApi.publish(page.id);
                                                                                            }
                                                                                            await loadCourseContent();
                                                                                        } catch (err: any) {
                                                                                            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
                                                                                            alert('Failed to update: ' + errorMsg);
                                                                                        }
                                                                                    }}
                                                                                    className="p-1.5 rounded hover:bg-gray-100 transition-colors"
                                                                                >
                                                                                    {page.isPublished ? (
                                                                                        <Eye size={14} style={{ color: COLORS.orange }} />
                                                                                    ) : (
                                                                                        <EyeOff size={14} style={{ color: COLORS.dark, opacity: 0.4 }} />
                                                                                    )}
                                                                                </button>

                                                                                <button
                                                                                    onClick={async (e) => {
                                                                                        e.stopPropagation();
                                                                                        if (confirm(`Delete page "${page.title}"?`)) {
                                                                                            try {
                                                                                                await coursePageApi.delete(page.id);
                                                                                                await loadCourseContent();
                                                                                            } catch (err: any) {
                                                                                                const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
                                                                                                alert('Failed to delete: ' + errorMsg);
                                                                                            }
                                                                                        }
                                                                                    }}
                                                                                    className="p-1.5 rounded hover:bg-red-50 transition-colors"
                                                                                >
                                                                                    <Trash2 size={14} className="text-red-600" />
                                                                                </button>
                                                                            </div>
                                                                        )}
                                                                    </div>
                                                                ))
                                                            ) : (
                                                                <p className="text-xs text-center py-6" style={{ color: COLORS.dark, opacity: 0.5 }}>
                                                                    No pages in this module
                                                                </p>
                                                            )}
                                                        </div>
                                                    </>
                                                )}
                                            </div>
                                        );
                                    })}
                                </div>
                            )}

                            {/* Create Module Form */}
                            {isInstructor && !isViewingCompleted && showCreateModule && (
                                <div className="p-4 rounded-xl mt-4 space-y-3" style={{ backgroundColor: 'white', border: `2px solid ${COLORS.orange}` }}>
                                    <h4 className="font-semibold text-sm" style={{ color: COLORS.dark }}>
                                        Create New Module
                                    </h4>
                                    <input
                                        type="text"
                                        placeholder="Module name *"
                                        value={newModuleName}
                                        onChange={(e) => setNewModuleName(e.target.value)}
                                        className="w-full px-4 py-2 border rounded-lg text-sm"
                                        style={{ borderColor: COLORS.bg, backgroundColor: 'white' }}
                                        autoFocus
                                    />
                                    <textarea
                                        placeholder="Description (optional)"
                                        value={newModuleDescription}
                                        onChange={(e) => setNewModuleDescription(e.target.value)}
                                        className="w-full px-4 py-2 border rounded-lg text-sm resize-none"
                                        style={{ borderColor: COLORS.bg, backgroundColor: 'white' }}
                                        rows={3}
                                    />
                                    <div className="flex gap-2">
                                        <Button variant="primary" size="sm" onClick={handleCreateModule}>
                                            Create Module
                                        </Button>
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onClick={() => {
                                                setShowCreateModule(false);
                                                setNewModuleName('');
                                                setNewModuleDescription('');
                                            }}
                                        >
                                            Cancel
                                        </Button>
                                    </div>
                                </div>
                            )}
                        </div>
                    )}

                    {/* Assignments Tab */}
                    {activeTab === 'assignments' && (
                        <div className="space-y-3">
                            {/* Completed course notice */}
                            {isViewingCompleted && (
                                <div className="p-3 rounded-lg bg-blue-50 text-sm text-blue-700 mb-4">
                                    Completed course - view only
                                </div>
                            )}

                            {isInstructor && !isViewingCompleted && (
                                <div className="flex justify-end mb-4">
                                    <Button
                                        variant="primary"
                                        size="sm"
                                        icon={<Plus size={16} />}
                                        onClick={() => {
                                            setSelectedAssignmentId(null);
                                            setViewMode('assignment-detail');
                                        }}
                                    >
                                        Add Assignment
                                    </Button>
                                </div>
                            )}

                            {assignments.length === 0 ? (
                                <p className="text-center py-8 text-sm" style={{ color: COLORS.dark, opacity: 0.5 }}>
                                    No assignments
                                </p>
                            ) : (
                                assignments.map(assignment => {
                                    const mySubmission = isStudent
                                        ? submissions.find(s => s.assignmentId === assignment.id)
                                        : null;
                                    const isOverdue = assignment.isOverdue;

                                    return (
                                        <div
                                            key={assignment.id}
                                            className="p-4 rounded-xl cursor-pointer hover:shadow-sm transition-shadow"
                                            style={{
                                                backgroundColor: COLORS.bg + '40',
                                                borderLeft: isOverdue ? `4px solid #ef4444` : `4px solid ${COLORS.lightOrange}`
                                            }}
                                            onClick={() => handleViewAssignment(assignment.id)}
                                        >
                                            <div className="flex justify-between items-start">
                                                <div className="flex-1">
                                                    <div className="flex items-center gap-2 mb-1">
                                                        <h4 className="font-semibold" style={{ color: COLORS.text }}>
                                                            📝 {assignment.title}
                                                        </h4>
                                                        <Badge className={ASSIGNMENT_TYPE[assignment.assignmentType as keyof typeof ASSIGNMENT_TYPE]?.color}>
                                                            {ASSIGNMENT_TYPE[assignment.assignmentType as keyof typeof ASSIGNMENT_TYPE]?.label}
                                                        </Badge>
                                                        {isOverdue && (
                                                            <span className="text-xs px-2 py-0.5 bg-red-100 text-red-700 rounded-full font-medium">
                                                                Overdue
                                                            </span>
                                                        )}
                                                    </div>

                                                    <div className="flex items-center gap-4 text-xs" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                                        <span>Due: {assignment.dueDate ? new Date(assignment.dueDate).toLocaleDateString() : 'No due date'}</span>
                                                        <span>•</span>
                                                        <span>{assignment.totalPoints} points</span>
                                                        {isInstructor && (
                                                            <>
                                                                <span>•</span>
                                                                <span>{assignment.submissionCount} submitted</span>
                                                            </>
                                                        )}
                                                    </div>
                                                </div>

                                                <div className="flex items-center gap-3">
                                                    {isStudent &&  (
                                                        <div className="text-right">
                                                            {mySubmission ? (
                                                                <Badge className={SUBMISSION_STATUS[mySubmission.status as keyof typeof SUBMISSION_STATUS]?.color}>
                                                                    {SUBMISSION_STATUS[mySubmission.status as keyof typeof SUBMISSION_STATUS]?.label}
                                                                </Badge>
                                                            ) : (
                                                                <span className="text-sm" style={{ color: COLORS.dark, opacity: 0.5 }}>
                                                                    Unsubmitted
                                                                </span>
                                                            )}
                                                        </div>
                                                    )}

                                                    {isInstructor && !isViewingCompleted && (
                                                        <div className="flex gap-2">
                                                            {/* Edit  */}
                                                            <button
                                                                onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    setSelectedAssignmentId(assignment.id);
                                                                    setViewMode('assignment-detail');
                                                                }}
                                                                className="p-1.5 rounded hover:bg-gray-100 transition-colors"
                                                                title="Edit assignment"
                                                            >
                                                                <Edit2 size={14} style={{ color: COLORS.dark, opacity: 0.6 }} />
                                                            </button>

                                                            {/* Publish/Unpublish  */}
                                                            <button
                                                                onClick={async (e) => {
                                                                    e.stopPropagation();
                                                                    try {
                                                                        if (assignment.isPublished) {
                                                                            await assignmentApi.unpublish(assignment.id);
                                                                        } else {
                                                                            await assignmentApi.publish(assignment.id);
                                                                        }
                                                                        await loadCourseContent();
                                                                    } catch (err: any) {
                                                                        const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
                                                                        alert('Failed to update: ' + errorMsg);
                                                                    }
                                                                }}
                                                                className="p-1.5 rounded hover:bg-gray-100 transition-colors"
                                                                title={assignment.isPublished ? 'Unpublish' : 'Publish'}
                                                            >
                                                                {assignment.isPublished ? (
                                                                    <Eye size={14} style={{ color: COLORS.orange }} />
                                                                ) : (
                                                                    <EyeOff size={14} style={{ color: COLORS.dark, opacity: 0.4 }} />
                                                                )}
                                                            </button>

                                                            {/* Delete  */}
                                                            <button
                                                                onClick={async (e) => {
                                                                    e.stopPropagation();
                                                                    if (confirm(`Delete assignment "${assignment.title}"?`)) {
                                                                        try {
                                                                            await assignmentApi.delete(assignment.id);
                                                                            await loadCourseContent();
                                                                        } catch (err: any) {
                                                                            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
                                                                            alert('Failed to delete: ' + errorMsg);
                                                                        }
                                                                    }
                                                                }}
                                                                className="p-1.5 rounded hover:bg-red-50 transition-colors"
                                                                title="Delete assignment"
                                                            >
                                                                <Trash2 size={14} className="text-red-600" />
                                                            </button>
                                                        </div>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    );
                                })
                            )}
                        </div>
                    )}

                    {/* Grades Tab */}
                    {activeTab === 'grades' && (
                        <div>
                            {/* Instructor View */}
                            {isInstructor && (
                                <>
                                    {/* Action Bar */}
                                    <div className="flex justify-end mb-4">
                                        <Button
                                            variant="primary"
                                            size="sm"
                                            onClick={handleFinalizeGrades}
                                        >
                                            Publish Final Grades
                                        </Button>
                                    </div>

                                    {/* Grades Table */}
                                    <div className="overflow-x-auto">
                                        <table className="w-full">
                                            <thead style={{ backgroundColor: COLORS.bg + '40' }}>
                                            <tr>
                                                <th className="px-4 py-3 text-left text-sm font-semibold">Student</th>
                                                {assignments.map(assignment => (
                                                    <th key={assignment.id} className="px-3 py-3 text-center text-sm font-semibold min-w-[120px]">
                                                        <div className="truncate">{assignment.title}</div>
                                                        <div className="text-xs opacity-60 font-normal">{assignment.totalPoints}pts</div>
                                                    </th>
                                                ))}
                                                <th className="px-4 py-3 text-center text-sm font-semibold min-w-[100px]">Total</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {enrolledStudents.map(enrollment => {
                                                const gradeInfo = calculateStudentGrade(enrollment.studentId);

                                                return (
                                                    <tr key={enrollment.studentId} className="border-b" style={{ borderColor: COLORS.bg }}>
                                                        <td className="px-4 py-3 text-sm font-medium" style={{ color: COLORS.dark }}>
                                                            {enrollment.studentName}
                                                        </td>
                                                        {assignments.map(assignment => {
                                                            const sub = allSubmissions.find(
                                                                s => s.studentId === enrollment.studentId && s.assignmentId === assignment.id
                                                            );
                                                            return (
                                                                <td key={assignment.id} className="px-3 py-3 text-center">
                                                                    {sub && sub.grade !== null && sub.grade !== undefined ? (
                                                                        <span className="font-semibold" style={{ color: COLORS.orange }}>
                                                            {sub.grade}
                                                        </span>
                                                                    ) : (
                                                                        <span className="opacity-40">-</span>
                                                                    )}
                                                                </td>
                                                            );
                                                        })}
                                                        <td className="px-4 py-3 text-center">
                                                            <span className="text-lg font-bold" style={{ color: COLORS.orange }}>
                                                                {gradeInfo.percentageDisplay}
                                                            </span>
                                                        </td>
                                                    </tr>
                                                );
                                            })}
                                            </tbody>
                                        </table>
                                    </div>
                                </>
                            )}

                            {/* Student View */}
                            {isStudent && (
                                <div>
                                    {assignments.map(assignment => {
                                        const mySubmission = submissions.find(s => s.assignmentId === assignment.id);

                                        return (
                                            <div key={assignment.id} className="flex justify-between p-4 rounded-xl mb-3" style={{ backgroundColor: COLORS.bg + '40' }}>
                                                <h4 className="font-medium" style={{ color: COLORS.dark }}>
                                                    {assignment.title}
                                                </h4>
                                                <div className="text-2xl font-bold" style={{ color: COLORS.orange }}>
                                                    {mySubmission?.grade || '-'}
                                                </div>
                                            </div>
                                        );
                                    })}

                                    {/* Final Grade */}
                                    {isStudent
                                        && 'finalGrade' in selectedCourse
                                        && selectedCourse.status === 'COMPLETED'
                                        && selectedCourse.finalGrade !== null && (
                                            <div className="flex justify-between p-4 rounded-xl border-2 mt-3" style={{
                                                backgroundColor: COLORS.cream,
                                                borderColor: COLORS.orange
                                            }}>
                                                <div>
                                                    <h4 className="font-bold" style={{ color: COLORS.dark }}>Course Final Grade</h4>
                                                </div>
                                                <div className="text-2xl font-bold" style={{ color: COLORS.orange }}>
                                                    {selectedCourse.finalGrade}%
                                                </div>
                                            </div>
                                        )}
                                </div>
                            )}
                        </div>
                    )}

                    {/* People Tab */}
                    {activeTab === 'people' && (
                        <div className="space-y-6">
                            {/* Instructor Info */}
                            <div>
                                <h3 className="font-semibold mb-3 text-sm" style={{ color: COLORS.dark }}>Instructor</h3>
                                <div className="p-3 rounded-lg flex items-center gap-6" style={{ backgroundColor: COLORS.bg + '40' }}>
                                    {/* Left: Avatar + Name */}
                                    <div className="flex items-center gap-3 flex-1">
                                        <div className="w-10 h-10 rounded-full flex items-center justify-center text-lg flex-shrink-0 bg-gray-200">
                                            {instructorInfo?.avatar || '👤'}
                                        </div>
                                        <span className="font-semibold text-sm" style={{ color: COLORS.dark }}>
                                            {instructorInfo?.name}
                                        </span>
                                    </div>

                                    {/* Middle: Email */}
                                    <div className="flex items-center gap-2 text-sm flex-1" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                        <Mail size={14} />
                                        <span>{instructorInfo?.email}</span>
                                    </div>

                                    {/* Right: Office Hours */}
                                    <div className="flex items-center gap-2 text-sm flex-1 justify-end" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                        {instructorInfo?.officeHours ? (
                                            <>
                                                <Clock size={14} />
                                                <span>{instructorInfo.officeHours}</span>
                                            </>
                                        ) : (
                                            <span style={{ opacity: 0.5 }}>-</span>
                                        )}
                                    </div>
                                </div>
                            </div>

                            {/* Students List */}
                            <div>
                                <h3 className="font-semibold mb-3 text-sm" style={{ color: COLORS.dark }}>
                                    Students ({enrolledStudents.length})
                                </h3>

                                {enrolledStudents.length === 0 ? (
                                    <p className="text-center py-8 text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                        No students enrolled yet
                                    </p>
                                ) : (
                                    <DataTable
                                        columns={peopleColumns}
                                        data={enrolledStudents}
                                        emptyMessage="No students"
                                    />
                                )}
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};