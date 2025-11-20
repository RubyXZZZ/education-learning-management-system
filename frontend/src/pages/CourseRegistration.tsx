import React, { useState, useEffect } from 'react';
import { Clock, MapPin, Users, BookOpen, X } from 'lucide-react';
import { PageHeader } from '../components/common/PageHeader';
import { FilterSelect } from '../components/common/FilterSelect';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { ConfirmDialog } from '../components/common/ConfirmDialog';
import { COLORS } from '../constants/colors';
import { sectionApi, enrollmentApi } from '../services/api';
import { useSession, useAuth } from '../contexts/AppContext';
import { COURSE_FORMAT } from '../types';
import type { SectionRes, EnrollmentRes } from '../types';

interface CourseRegistrationProps {
    onViewEnrollments?: (sectionId: string) => void;
}

export const CourseRegistration: React.FC<CourseRegistrationProps> = ({ onViewEnrollments }) => {
    const { allSessions } = useSession();
    const { currentUser, isStudent } = useAuth();

    const [sections, setSections] = useState<SectionRes[]>([]);
    const [myEnrollments, setMyEnrollments] = useState<EnrollmentRes[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [sessionFilter, setSessionFilter] = useState<string>('');

    const [showEnrollConfirm, setShowEnrollConfirm] = useState(false);
    const [selectedSection, setSelectedSection] = useState<SectionRes | null>(null);

    const [showDropConfirm, setShowDropConfirm] = useState(false);
    const [droppingEnrollment, setDroppingEnrollment] = useState<EnrollmentRes | null>(null);

    useEffect(() => {
        // Set default to current/upcoming session
        if (allSessions.length > 0 && !sessionFilter) {
            const current = allSessions.find(s => s.status === 'ACTIVE');
            const upcoming = allSessions.find(s => s.status === 'UPCOMING');
            setSessionFilter((current || upcoming)?.sessionCode || allSessions[0].sessionCode);
        }
    }, [allSessions, sessionFilter]);

    useEffect(() => {
        if (sessionFilter) {
            loadData();
        }
    }, [sessionFilter]);

    const loadData = async () => {
        try {
            setLoading(true);
            setError(null);

            // Load available sections
            const sectionsRes = await sectionApi.getEnrollable();

            // Filter sections by selected session
            const filteredSections = sectionsRes.data.filter((s: SectionRes) =>
                s.sessionCode === sessionFilter
            );
            setSections(filteredSections);

            // Only load enrollments if user is a student
            if (isStudent) {
                const enrollmentsRes = await enrollmentApi.getMyEnrollments();

                // Filter my enrollments by selected session and ENROLLED status
                const currentEnrollments = enrollmentsRes.data.filter((e: EnrollmentRes) =>
                    e.sessionCode === sessionFilter && e.status === 'ENROLLED'
                );
                setMyEnrollments(currentEnrollments);
            } else {
                setMyEnrollments([]);
            }

        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to load courses');
            console.error('Error loading data:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleEnrollClick = (section: SectionRes) => {
        setSelectedSection(section);
        setShowEnrollConfirm(true);
    };

    const confirmEnroll = async () => {
        if (!selectedSection || !currentUser) return;

        try {
            await enrollmentApi.enroll({
                studentId: currentUser.id,
                courseSectionId: selectedSection.id
            });

            setShowEnrollConfirm(false);
            setSelectedSection(null);
            await loadData();
            alert('Enrolled successfully!');
        } catch (err: any) {
            alert('Failed to enroll: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleDropClick = (enrollment: EnrollmentRes) => {
        setDroppingEnrollment(enrollment);
        setShowDropConfirm(true);
    };

    const confirmDrop = async () => {
        if (!droppingEnrollment) return;

        try {
            await enrollmentApi.drop({
                enrollmentId: droppingEnrollment.id,
                dropReason: 'Dropped by student'
            });

            setShowDropConfirm(false);
            setDroppingEnrollment(null);
            await loadData();
            alert('Course dropped successfully!');
        } catch (err: any) {
            alert('Failed to drop course: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleViewEnrollments = (sectionId: string) => {
        if (onViewEnrollments) {
            onViewEnrollments(sectionId);
        }
    };

    // Calculate total hours enrolled
    const totalHoursEnrolled = myEnrollments.reduce((sum, e) => sum + e.hoursPerWeek, 0);

    // Group sections by course code
    const groupedSections = sections.reduce((acc, section) => {
        const key = section.courseCode;
        if (!acc[key]) {
            acc[key] = [];
        }
        acc[key].push(section);
        return acc;
    }, {} as Record<string, SectionRes[]>);

    const displayedGroups = Object.entries(groupedSections);

    if (loading) {
        return (
            <div className="space-y-6">
                <PageHeader title="Course Registration" />
                <div className="flex items-center justify-center h-64">
                    <div className="text-xl" style={{ color: COLORS.dark }}>Loading...</div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="space-y-6">
                <PageHeader title="Course Registration" />
                <div className="flex items-center justify-center h-64">
                    <div className="text-xl text-red-600">Error: {error}</div>
                </div>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            <PageHeader title="Course Registration" />

            {/* Session Filter */}
            <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        <label className="text-sm font-medium" style={{ color: COLORS.dark }}>
                            Session:
                        </label>
                        <FilterSelect
                            value={sessionFilter}
                            options={allSessions.map(s => ({
                                value: s.sessionCode,
                                label: `${s.sessionCode} (${s.startDate} - ${s.endDate})`
                            }))}
                            onChange={setSessionFilter}
                            width="320px"
                        />
                    </div>

                    <div className="text-right">
                        <div className="text-sm font-medium" style={{ color: COLORS.dark }}>
                            {displayedGroups.length} Courses Available
                        </div>
                        <div className="text-xs" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            {sections.length} Sections
                        </div>
                    </div>
                </div>
            </div>

            {/* My Current Enrollments - Student Only */}
            {isStudent && myEnrollments.length > 0 && (
                <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                    <div className="flex items-center justify-between mb-4">
                        <h3 className="text-lg font-bold" style={{ color: COLORS.dark }}>
                            My Enrolled Courses ({myEnrollments.length})
                        </h3>
                        <div className="text-sm font-medium" style={{ color: COLORS.orange }}>
                            {totalHoursEnrolled} hours/week
                        </div>
                    </div>

                    <div className="space-y-3">
                        {myEnrollments.map(enrollment => (
                            <div
                                key={enrollment.id}
                                className="p-4 rounded-xl flex items-center justify-between"
                                style={{ backgroundColor: COLORS.cream }}
                            >
                                <div className="flex-1">
                                    <div className="flex items-center gap-3 mb-2">
                                        <h4 className="font-bold text-base" style={{ color: COLORS.dark }}>
                                            {enrollment.courseCode} - Section {enrollment.sectionCode}
                                        </h4>
                                        <span className="text-xs px-2 py-0.5 rounded-full bg-orange-100 font-medium" style={{ color: COLORS.orange }}>
                                            {enrollment.hoursPerWeek} hrs/week
                                        </span>
                                    </div>
                                    <div className="flex items-center gap-4 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                        <div className="flex items-center gap-1">
                                            <BookOpen size={14} />
                                            <span>{enrollment.instructorName}</span>
                                        </div>
                                        <span>•</span>
                                        <span>Enrolled: {new Date(enrollment.enrolledTime).toLocaleDateString()}</span>
                                    </div>
                                </div>

                                <Button
                                    variant="danger"
                                    size="sm"
                                    icon={<X size={16} />}
                                    onClick={() => handleDropClick(enrollment)}
                                >
                                    Drop
                                </Button>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Available Courses */}
            <div className="space-y-4">
                <h3 className="text-lg font-bold" style={{ color: COLORS.dark }}>
                    Available Courses
                </h3>

                {displayedGroups.length > 0 ? (
                    displayedGroups.map(([courseCode, courseSections]) => {
                        const firstSection = courseSections[0];

                        return (
                            <div key={courseCode} className="bg-white rounded-3xl shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                                {/* Course Header */}
                                <div className="p-6 pb-4" style={{ borderBottom: `1px solid ${COLORS.bg}` }}>
                                    <div className="flex items-center space-x-3 mb-2">
                                        <h3 className="text-xl font-bold" style={{ color: COLORS.dark }}>
                                            {firstSection.courseCode} - {firstSection.courseName}
                                        </h3>
                                        <Badge className="bg-orange-100 text-orange-600">
                                            {firstSection.hoursPerWeek} hrs/week
                                        </Badge>
                                    </div>

                                    {/* Description */}
                                    {firstSection.courseDescription && (
                                        <p className="text-sm mb-3" style={{ color: COLORS.dark, opacity: 0.75 }}>
                                            <span className="text-sm font-semibold mr-1">Description:</span>
                                            {firstSection.courseDescription}
                                        </p>
                                    )}

                                    {/* Prerequisites */}
                                    {(firstSection.prerequisiteCourses?.length > 0 || firstSection.requiredPlacementLevel) && (
                                        <div className="p-3 rounded-lg" style={{ backgroundColor: COLORS.cream }}>
                                            <div className="text-xs font-semibold mb-2" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                                ENROLLMENT REQUIREMENTS
                                            </div>
                                            <div className="flex flex-wrap items-center gap-2">
                                                {firstSection.prerequisiteCourses?.length > 0 && (
                                                    <>
                                                        <span className="text-xs font-medium" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                                            Courses:
                                                        </span>
                                                        {firstSection.prerequisiteCourses.map((code, idx) => (
                                                            <span
                                                                key={idx}
                                                                className="px-2 py-0.5 rounded text-xs font-mono font-semibold"
                                                                style={{ backgroundColor: COLORS.dark, color: 'white' }}
                                                            >
                                                                {code}
                                                            </span>
                                                        ))}
                                                        {firstSection.requiredPlacementLevel && (
                                                            <span className="text-xs font-bold mx-1" style={{ color: COLORS.orange }}>
                                                                OR
                                                            </span>
                                                        )}
                                                    </>
                                                )}
                                                {firstSection.requiredPlacementLevel && (
                                                    <>
                                                        <span className="text-xs font-medium" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                                            Placement:
                                                        </span>
                                                        <span
                                                            className="px-2 py-0.5 rounded text-xs font-bold"
                                                            style={{ backgroundColor: COLORS.orange, color: 'white' }}
                                                        >
                                                            L{firstSection.requiredPlacementLevel}+
                                                        </span>
                                                    </>
                                                )}
                                            </div>
                                        </div>
                                    )}
                                </div>

                                {/* Available Sections */}
                                <div className="p-6 pt-4">
                                    <h4 className="text-sm font-semibold mb-3" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                        Available Sections ({courseSections.length})
                                    </h4>

                                    <div className="space-y-3">
                                        {courseSections.map(section => (
                                            <div
                                                key={section.id}
                                                className="p-4 rounded-xl hover:shadow-sm transition-shadow"
                                                style={{ backgroundColor: COLORS.bg + '40', border: `1px solid ${COLORS.bg}` }}
                                            >
                                                <div className="flex justify-between items-start">
                                                    <div className="flex-1">
                                                        <div className="flex items-center space-x-3 mb-2">
                                                            <span className="text-base font-bold" style={{ color: COLORS.dark }}>
                                                                Section {section.sectionCode}
                                                            </span>
                                                            <Badge className={COURSE_FORMAT[section.courseFormat]?.color}>
                                                                {COURSE_FORMAT[section.courseFormat]?.label}
                                                            </Badge>
                                                        </div>

                                                        <div className="grid grid-cols-2 gap-3 text-sm">
                                                            <div className="flex items-center space-x-2" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                                                <BookOpen size={14} />
                                                                <span>{section.instructorName}</span>
                                                            </div>
                                                            <div className="flex items-center space-x-2" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                                                <Clock size={14} />
                                                                <span>{section.schedule}</span>
                                                            </div>
                                                            <div className="flex items-center space-x-2" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                                                <MapPin size={14} />
                                                                <span>{section.location}</span>
                                                            </div>
                                                            <div className="flex items-center space-x-2" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                                                <Users size={14} />
                                                                <span>
                                                                    {section.enrolledCount}/{section.capacity} enrolled
                                                                    {section.availableSeats && section.availableSeats > 0 && (
                                                                        <span className="ml-1 font-medium" style={{ color: COLORS.orange }}>
                                                                            ({section.availableSeats} seats left)
                                                                        </span>
                                                                    )}
                                                                </span>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    {/* Action Button - Different for each role */}
                                                    {isStudent && (
                                                        <Button
                                                            variant="primary"
                                                            onClick={() => handleEnrollClick(section)}
                                                            disabled={!section.openForEnrollment || section.availableSeats === 0}
                                                        >
                                                            {section.availableSeats === 0 ? 'Full' : 'Enroll'}
                                                        </Button>
                                                    )}

                                                    {!isStudent && (
                                                        <Button
                                                            variant="secondary"
                                                            onClick={() => handleViewEnrollments(section.id)}
                                                        >
                                                            View Enrollments
                                                        </Button>
                                                    )}
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        );
                    })
                ) : (
                    <div className="bg-white rounded-3xl p-12 text-center shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                        <p className="text-lg mb-2" style={{ color: COLORS.dark }}>
                            No courses available for enrollment
                        </p>
                        <p className="text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            Try selecting a different session or check back later
                        </p>
                    </div>
                )}
            </div>

            {/* Enroll Confirmation Dialog - Student Only */}
            {selectedSection && (
                <ConfirmDialog
                    isOpen={showEnrollConfirm}
                    title="Confirm Enrollment"
                    message={`Enroll in ${selectedSection.courseCode} - ${selectedSection.courseName}?`}
                    confirmText="Enroll Now"
                    cancelText="Cancel"
                    variant="default"
                    warningNote={`Section ${selectedSection.sectionCode} | ${selectedSection.instructorName} | ${selectedSection.schedule} | ${selectedSection.hoursPerWeek} hours/week`}
                    onConfirm={confirmEnroll}
                    onCancel={() => {
                        setShowEnrollConfirm(false);
                        setSelectedSection(null);
                    }}
                />
            )}

            {/* Drop Confirmation Dialog - Student Only */}
            {droppingEnrollment && (
                <ConfirmDialog
                    isOpen={showDropConfirm}
                    title="Drop Course"
                    message={`Drop ${droppingEnrollment.courseCode} - ${droppingEnrollment.courseName}?`}
                    confirmText="Drop Course"
                    cancelText="Cancel"
                    variant="danger"
                    warningNote="This action will remove you from the course. You may re-enroll if seats are available."
                    onConfirm={confirmDrop}
                    onCancel={() => {
                        setShowDropConfirm(false);
                        setDroppingEnrollment(null);
                    }}
                />
            )}
        </div>
    );
};