import React, { useState, useEffect } from 'react';
import {ChevronRight, Users, BookOpen, GraduationCap, Award } from 'lucide-react';
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
