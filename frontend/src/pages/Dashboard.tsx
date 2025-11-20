import React, { useState, useEffect } from 'react';
import { AlertCircle, Mail, ChevronRight, Users, BookOpen, GraduationCap, Award } from 'lucide-react';
import { COLORS } from '../constants/colors';
import { studentApi, instructorApi, sectionApi, enrollmentApi } from '../services/api';
import { useAuth } from '../contexts/AppContext';
import { ENROLLMENT_STATUS, SECTION_STATUS } from '../types';
import { Badge } from '../components/common/Badge';
import type { ViewType, EnrollmentRes, SectionRes } from '../types';

interface DashboardProps {
    onNavigate: (view: ViewType) => void;
}

export const Dashboard: React.FC<DashboardProps> = ({ onNavigate }) => {
    const { currentUser, isStudent, isInstructor, isAdmin } = useAuth();

    if (!currentUser) return null;

    return (
        <div className="space-y-6">
            {/* User Profile - All Roles */}
            <UserProfileCard />

            {/* Role-specific Content */}
            {isStudent && <StudentContent onNavigate={onNavigate} />}
            {isInstructor && <InstructorContent onNavigate={onNavigate} />}
            {isAdmin && <AdminContent onNavigate={onNavigate} />}
        </div>
    );
};

// User Profile Card (All Roles)
const UserProfileCard: React.FC = () => {
    const { currentUser } = useAuth();

    if (!currentUser) return null;

    return (
        <div className="flex items-center space-x-4">
            <div className="w-16 h-16 rounded-2xl flex items-center justify-center text-3xl"
                 style={{ backgroundColor: COLORS.cream }}>
                {currentUser.userAvatar}
            </div>
            <div>
                <h2 className="text-2xl font-bold" style={{ color: COLORS.dark }}>
                    {currentUser.fullName}
                </h2>
                <div className="flex items-center space-x-3 mt-1 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                    <span>ID: {currentUser.userNumber}</span>
                    <span>•</span>
                    <span className="px-2 py-0.5 bg-emerald-100 text-emerald-700 rounded-full text-xs">
                        ✓ {currentUser.status}
                    </span>
                    <span>•</span>
                    <span>{new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })}</span>
                </div>
            </div>
        </div>
    );
};

// Student Content Component
const StudentContent: React.FC<{ onNavigate: (view: ViewType) => void }> = ({ onNavigate }) => {
    const [activeEnrollments, setActiveEnrollments] = useState<EnrollmentRes[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadEnrollments();
    }, []);

    const loadEnrollments = async () => {
        try {
            const res = await enrollmentApi.getMyEnrollments();

            const active = res.data.filter((e: EnrollmentRes) => e.status === 'ENROLLED');

            setActiveEnrollments(active);
        } catch (err) {
            console.error('Error loading enrollments:', err);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="text-center py-8">Loading...</div>;
    }

    return (
        <>
            {/* My Courses */}
            <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-bold" style={{ color: COLORS.dark }}>
                        My Current Courses ({activeEnrollments.length})
                    </h3>

                    <button
                        className="text-xs cursor-pointer hover:underline font-medium"
                        style={{ color: COLORS.orange }}
                        onClick={() => onNavigate('my-courses')}
                    >
                        View All →
                    </button>
                </div>

                {activeEnrollments.length > 0 ? (
                    <div className="space-y-2">
                        {activeEnrollments.map(enrollment => {
                            const statusConfig = ENROLLMENT_STATUS[enrollment.status as keyof typeof ENROLLMENT_STATUS];

                            return (
                                <div
                                    key={enrollment.id}
                                    className="p-4 rounded-xl cursor-pointer hover:shadow-sm transition-shadow"
                                    style={{ backgroundColor: COLORS.bg + '40', border: `1px solid ${COLORS.cream}` }}
                                    onClick={() => onNavigate('my-courses')}
                                >
                                    <div className="flex justify-between items-start">
                                        <div className="flex-1">
                                            <div className="flex items-center gap-3 mb-2">
                                                <h4 className="font-bold text-base" style={{ color: COLORS.dark }}>
                                                    {enrollment.courseCode} - {enrollment.courseName}
                                                </h4>
                                                <Badge className={statusConfig?.color}>
                                                    {statusConfig?.label}
                                                </Badge>
                                            </div>
                                            <div className="flex items-center gap-4 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                                <span>Section {enrollment.sectionCode}</span>
                                                <span>•</span>
                                                <span>{enrollment.instructorName}</span>
                                                <span>•</span>
                                                <span>{enrollment.hoursPerWeek} hrs/week</span>
                                            </div>
                                        </div>
                                        <ChevronRight size={20} style={{ color: COLORS.dark, opacity: 0.3 }} />
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                ) : (
                    <div className="text-center py-12">
                        <p className="text-sm mb-4" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            No enrolled courses
                        </p>
                        <button
                            className="px-4 py-2 text-white rounded-lg text-sm"
                            style={{ backgroundColor: COLORS.orange }}
                            onClick={() => onNavigate('course-registration')}
                        >
                            Browse Courses
                        </button>
                    </div>
                )}
            </div>

            {/* Tasks & Notifications */}
            <TasksNotificationsPanel role="STUDENT" onNavigate={onNavigate} />
        </>
    );
};

// Instructor Content Component
const InstructorContent: React.FC<{ onNavigate: (view: ViewType) => void }> = ({ onNavigate }) => {
    const [activeSections, setActiveSections] = useState<SectionRes[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadSections();
    }, []);

    const loadSections = async () => {
        try {
            const res = await sectionApi.getMySections();
            const active = res.data.filter((s: SectionRes) => s.status === 'PUBLISHED');

            setActiveSections(active);
        } catch (err) {
            console.error('Error loading sections:', err);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="text-center py-8" style={{ color: COLORS.dark }}>Loading...</div>;
    }

    return (
        <>
            {/* My Teaching */}
            <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-bold" style={{ color: COLORS.dark }}>
                        My Teaching ({activeSections.length})
                    </h3>
                    <button
                        className="text-xs cursor-pointer hover:underline font-medium"
                        style={{ color: COLORS.orange }}
                        onClick={() => onNavigate('my-courses')}
                    >
                        View All →
                    </button>
                </div>

                {activeSections.length > 0 ? (
                    <div className="space-y-2">
                        {activeSections.map(section => {
                            const statusConfig = SECTION_STATUS[section.status as keyof typeof SECTION_STATUS];

                            return (
                                <div
                                    key={section.id}
                                    className="p-4 rounded-xl cursor-pointer hover:shadow-sm transition-shadow"
                                    style={{ backgroundColor: COLORS.bg + '40', border: `1px solid ${COLORS.cream}` }}
                                    onClick={() => onNavigate('my-courses')}
                                >
                                    <div className="flex justify-between items-start">
                                        <div className="flex-1">
                                            <div className="flex items-center gap-3 mb-2">
                                                <h4 className="font-bold text-base" style={{ color: COLORS.dark }}>
                                                    {section.courseCode} - {section.courseName}
                                                </h4>
                                                <Badge className={statusConfig?.color}>
                                                    {statusConfig?.label}
                                                </Badge>
                                            </div>
                                            <div className="flex items-center gap-4 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                                <span>Section {section.sectionCode}</span>
                                                <span>•</span>
                                                <span>{section.schedule}</span>
                                                <span>•</span>
                                                <span>{section.enrolledCount}/{section.capacity} students</span>
                                            </div>
                                        </div>
                                        <ChevronRight size={20} style={{ color: COLORS.dark, opacity: 0.3 }} />
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                ) : (
                    <div className="text-center py-12">
                        <p className="text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            No assigned sections now
                        </p>
                    </div>
                )}
            </div>

            {/* Tasks & Notifications */}
            <TasksNotificationsPanel role="INSTRUCTOR" onNavigate={onNavigate} />
        </>
    );
};

// Admin Content Component
const AdminContent: React.FC<{ onNavigate: (view: ViewType) => void }> = ({ onNavigate }) => {
    const [stats, setStats] = useState({
        totalStudents: 0,
        totalSections: 0,
        totalEnrollments: 0,
        totalInstructors: 0
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadStats();
    }, []);

    const loadStats = async () => {
        try {
            const [studentsRes, sectionsRes, enrollmentsRes, instructorsRes] = await Promise.all([
                studentApi.getAll(),
                sectionApi.getList(),
                enrollmentApi.getList(),
                instructorApi.getAll()
            ]);

            setStats({
                totalStudents: studentsRes.data.length,
                totalSections: sectionsRes.data.length,
                totalEnrollments: enrollmentsRes.data.length,
                totalInstructors: instructorsRes.data.length
            });
        } catch (err) {
            console.error('Error loading stats:', err);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="text-center py-8" style={{ color: COLORS.dark }}>Loading...</div>;
    }

    return (
        <>
            {/* Quick Stats Cards */}
            <div className="grid grid-cols-4 gap-4">
                <StatCard
                    icon={<Users size={24} style={{ color: COLORS.orange }} />}
                    value={stats.totalStudents}
                    label="Students"
                    onClick={() => onNavigate('users-management')}
                />
                <StatCard
                    icon={<BookOpen size={24} style={{ color: COLORS.orange }} />}
                    value={stats.totalSections}
                    label="Sections"
                    onClick={() => onNavigate('courses-management')}
                />
                <StatCard
                    icon={<GraduationCap size={24} style={{ color: COLORS.orange }} />}
                    value={stats.totalEnrollments}
                    label="Enrollments"
                    onClick={() => onNavigate('courses-management')}
                />
                <StatCard
                    icon={<Award size={24} style={{ color: COLORS.orange }} />}
                    value={stats.totalInstructors}
                    label="Instructors"
                    onClick={() => onNavigate('users-management')}
                />
            </div>

            {/* Tasks & Notifications */}
            <TasksNotificationsPanel role="ADMIN" onNavigate={onNavigate} />
        </>
    );
};

// Stat Card Component (Admin)
const StatCard: React.FC<{
    icon: React.ReactNode;
    value: number;
    label: string;
    onClick: () => void
}> = ({ icon, value, label, onClick }) => {
    return (
        <div
            onClick={onClick}
            className="bg-white rounded-2xl p-6 shadow-sm cursor-pointer hover:shadow-md transition-shadow"
            style={{ border: `1px solid ${COLORS.bg}` }}
        >
            <div className="flex items-center justify-between mb-2">
                {icon}
                <span className="text-3xl font-bold" style={{ color: COLORS.dark }}>
                    {value}
                </span>
            </div>
            <div className="text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>{label}</div>
        </div>
    );
};

// Tasks & Notifications Panel (All Roles)
const TasksNotificationsPanel: React.FC<{
    role: 'STUDENT' | 'INSTRUCTOR' | 'ADMIN';
    onNavigate: (view: ViewType) => void;
}> = ({ role }) => {

    // Mock tasks - TODO: Replace with real data
    const studentTasks = [
        { id: 1, title: 'Homework 5', course: 'ESL-LS-L3', dueDate: 'Today', type: 'assignment' },
        { id: 2, title: 'Essay Draft', course: 'ESL-RW-L3', dueDate: 'Tomorrow', type: 'assignment' }
    ];

    const instructorTasks = [
        { id: 1, title: 'Grade Homework', course: 'ESL-LS-L1-A', count: 15, type: 'grading' },
        { id: 2, title: 'Record Attendance', course: 'ESL-LS-L1-B', count: 2, type: 'attendance' }
    ];

    const adminTasks = [
        { id: 1, title: 'Students pending placement test', count: 12, type: 'pending' },
        { id: 2, title: 'Sections below min enrollment', count: 5, type: 'alert' },
        { id: 3, title: 'Instructors at max load', count: 3, type: 'alert' }
    ];

    const notifications = [
        { id: 1, from: 'Teacher Li', message: 'Feedback on your essay', time: '2h ago', unread: true },
        { id: 2, from: 'System', message: 'Grade posted: ESL-LS-L3', time: '1d ago', unread: true }
    ];

    const currentTasks = role === 'STUDENT' ? studentTasks :
        role === 'INSTRUCTOR' ? instructorTasks : adminTasks;

    return (
        <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
            <div className="grid grid-cols-2 gap-6">
                {/* Left: To Do */}
                <div>
                    <h3 className="font-semibold mb-3 flex items-center space-x-2" style={{ color: COLORS.dark }}>
                        <AlertCircle size={18} className="text-red-600" />
                        <span>To Do ({currentTasks.length})</span>
                    </h3>
                    <div className="space-y-2">
                        {currentTasks.map(task => (
                            <div
                                key={task.id}
                                className="p-3 bg-orange-50 rounded-lg cursor-pointer hover:bg-orange-100 transition-colors"
                                style={{ borderLeft: `4px solid ${COLORS.orange}` }}
                            >
                                <h4 className="font-medium text-sm" style={{ color: COLORS.dark }}>
                                    {task.title}
                                    {'count' in task && ` (${task.count})`}
                                </h4>
                                {'course' in task && (
                                    <p className="text-xs" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                        {task.course}
                                    </p>
                                )}
                                {'dueDate' in task && (
                                    <p className="text-xs" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                        Due: {task.dueDate}
                                    </p>
                                )}
                            </div>
                        ))}

                        {currentTasks.length === 0 && (
                            <p className="text-center py-4 text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                No pending tasks
                            </p>
                        )}
                    </div>
                </div>

                {/* Right: Notifications */}
                <div>
                    <h3 className="font-semibold mb-3 flex items-center justify-between" style={{ color: COLORS.dark }}>
                        <span className="flex items-center space-x-2">
                            <Mail size={18} className="text-blue-600" />
                            <span>Notifications</span>
                        </span>
                        {notifications.filter(n => n.unread).length > 0 && (
                            <span className="bg-red-500 text-white text-xs px-2 py-0.5 rounded-full">
                                {notifications.filter(n => n.unread).length}
                            </span>
                        )}
                    </h3>
                    <div className="space-y-2">
                        {notifications.map(notif => (
                            <div
                                key={notif.id}
                                className={`p-3 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors ${
                                    notif.unread ? 'bg-blue-50' : 'bg-white'
                                }`}
                                style={{ border: `1px solid ${COLORS.bg}` }}
                            >
                                <div className="flex items-start space-x-2">
                                    <Mail size={14} className="text-blue-500 mt-0.5" />
                                    <div className="flex-1">
                                        <div className="text-xs font-medium mb-1" style={{ color: COLORS.dark }}>
                                            {notif.from}
                                        </div>
                                        <h4 className="text-sm" style={{ color: COLORS.dark }}>{notif.message}</h4>
                                        <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.5 }}>
                                            {notif.time}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        ))}

                        {notifications.length === 0 && (
                            <p className="text-center py-4 text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                No notifications
                            </p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};