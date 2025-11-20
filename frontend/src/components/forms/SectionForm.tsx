import React, { useState, useEffect } from 'react';
import Select from 'react-select';
import { COLORS } from '../../constants/colors';
import { Button } from '../common/Button';
import type { SectionRes, CourseRes, InstructorRes, CourseFormat, CourseSectionStatus } from '../../types';

interface SectionFormProps {
    mode: 'create' | 'edit';
    initialData?: SectionRes;
    courses: CourseRes[];
    instructors: InstructorRes[];
    onSubmit: (data: any) => void;
    onCancel: () => void;
}

const DAYS_OPTIONS = [
    { value: 'Monday', label: 'Monday' },
    { value: 'Tuesday', label: 'Tuesday' },
    { value: 'Wednesday', label: 'Wednesday' },
    { value: 'Thursday', label: 'Thursday' },
    { value: 'Friday', label: 'Friday' }
];

export const SectionForm: React.FC<SectionFormProps> = ({
                                                            mode,
                                                            initialData,
                                                            courses,
                                                            instructors,
                                                            onSubmit,
                                                            onCancel
                                                        }) => {
    const [formData, setFormData] = useState<{
        sectionCode: string;
        courseFormat: CourseFormat;
        daysOfWeek: string[];
        startTime: string;
        endTime: string;
        location: string;
        instructorId: string;
        capacity: number;
        minEnrollment: number;
        status: CourseSectionStatus;
        enrollmentLocked: boolean;
    }>({
        sectionCode: '',
        courseFormat: 'IN_PERSON',
        daysOfWeek: [],
        startTime: '',
        endTime: '',
        location: '',
        instructorId: '',
        capacity: 30,
        minEnrollment: 5,
        status: 'DRAFT',
        enrollmentLocked: false
    });

    useEffect(() => {
        if (mode === 'edit' && initialData) {
            setFormData({
                sectionCode: initialData.sectionCode,
                courseFormat: initialData.courseFormat,
                daysOfWeek: initialData.daysOfWeek ? initialData.daysOfWeek.split(',') : [],
                startTime: initialData.startTime || '',
                endTime: initialData.endTime || '',
                location: initialData.location || '',
                instructorId: initialData.instructorId,
                capacity: initialData.capacity,
                minEnrollment: initialData.minEnrollment,
                status: initialData.status,
                enrollmentLocked: initialData.enrollmentLocked || false
            });
        }
    }, [mode, initialData]);

    // Generate schedule preview
    const generateSchedulePreview = (): string => {
        if (formData.daysOfWeek.length === 0 || !formData.startTime || !formData.endTime) {
            return '';
        }

        const dayAbbr: Record<string, string> = {
            'Monday': 'Mon',
            'Tuesday': 'Tue',
            'Wednesday': 'Wed',
            'Thursday': 'Thu',
            'Friday': 'Fri'
        };

        const days = formData.daysOfWeek.map(d => dayAbbr[d]).join('/');
        return `${days} ${formData.startTime}-${formData.endTime}`;
    };

    const handleSubmit = () => {
        if (!formData.sectionCode || !formData.instructorId || !formData.location) {
            alert('Please fill in Section Code, Instructor, and Location');
            return;
        }

        if (formData.daysOfWeek.length === 0) {
            alert('Please select at least one day');
            return;
        }

        if (!formData.startTime || !formData.endTime) {
            alert('Please select start and end time');
            return;
        }

        if (formData.capacity < formData.minEnrollment) {
            alert('Capacity must be greater than or equal to minimum enrollment');
            return;
        }

        // Convert data for backend
        const submitData = {
            sectionCode: formData.sectionCode,
            courseFormat: formData.courseFormat,
            daysOfWeek: formData.daysOfWeek.join(','),
            startTime: formData.startTime,
            endTime: formData.endTime,
            location: formData.location,
            instructorId: formData.instructorId,
            capacity: formData.capacity,
            minEnrollment: formData.minEnrollment,
            status: mode === 'edit' ? formData.status : undefined,
            enrollmentLocked: mode === 'edit' ? formData.enrollmentLocked : undefined
        };

        console.log('Submit data:', submitData);
        onSubmit(submitData);
    };

    const availableInstructors = mode === 'edit'
        ? instructors  // Edit: all
        : instructors.filter(i => i.status === 'ACTIVE'); // Create: only active

    // Instructor options for react-select
    const instructorOptions = availableInstructors.map(inst => ({
        value: inst.id,
        label: `${inst.fullName} (${inst.email})`,
        searchText: `${inst.fullName} ${inst.email} ${inst.employeeNumber}`
    }));

    const displayCourse = mode === 'create' && courses.length === 1
        ? courses[0]
        : mode === 'edit' && initialData
            ? courses.find(c => c.id === initialData.courseId)
            : null;

    const schedulePreview = generateSchedulePreview();

    return (
        <div className="space-y-6">
            <h3 className="text-xl font-bold" style={{ color: COLORS.dark }}>
                {mode === 'create' ? 'Add Section' : 'Edit Section'}
            </h3>

            {/* Course Info - Only show in Edit mode */}
            {mode === 'edit' && displayCourse && (
                <div className="p-4 rounded-lg" style={{ backgroundColor: COLORS.cream }}>
                    <div className="text-sm font-bold mb-1" style={{ color: COLORS.dark }}>
                        {displayCourse.courseCode} - {displayCourse.courseName}
                    </div>
                    <div className="flex flex-wrap gap-x-4 gap-y-1 text-xs" style={{ color: COLORS.dark, opacity: 0.7 }}>
                        <span>Session: {displayCourse.sessionCode}</span>
                        <span>Hours: {displayCourse.hoursPerWeek}/week</span>
                    </div>

                    {displayCourse.prerequisiteCourses && displayCourse.prerequisiteCourses.length > 0 && (
                        <div className="mt-2 pt-2" style={{ borderTop: `1px solid ${COLORS.bg}` }}>
                            <span className="text-xs font-medium" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                Prerequisites: {displayCourse.prerequisiteCourses.join(', ')}
                            </span>
                        </div>
                    )}
                </div>
            )}

            {/* Status & Lock Enrollment - Edit mode only, at top */}
            {mode === 'edit' && (
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                            Status
                        </label>
                        <select
                            value={formData.status}
                            onChange={(e) => setFormData({ ...formData, status: e.target.value as CourseSectionStatus })}
                            className="w-full px-3 py-2 border rounded-lg cursor-pointer"
                            style={{ borderColor: COLORS.bg }}
                        >
                            <option value="DRAFT">Draft</option>
                            <option value="PUBLISHED">Published</option>
                            <option value="COMPLETED">Completed</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                            Enrollment Control
                        </label>
                        <label className="flex items-center space-x-2 cursor-pointer">
                            <input
                                type="checkbox"
                                checked={formData.enrollmentLocked}
                                onChange={(e) => setFormData({ ...formData, enrollmentLocked: e.target.checked })}
                                className="w-4 h-4 rounded cursor-pointer"
                                style={{ accentColor: COLORS.orange }}
                            />
                            <span className="text-sm font-medium" style={{ color: COLORS.dark }}>
                                Lock Enrollment
                            </span>
                        </label>
                        <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            Prevent new student enrollments
                        </p>
                    </div>
                </div>
            )}

            <div className="grid grid-cols-2 gap-4">
                {/* Section Code */}
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Section Code *
                    </label>
                    <input
                        type="text"
                        value={formData.sectionCode}
                        disabled={mode === 'edit'}
                        onChange={(e) => setFormData({ ...formData, sectionCode: e.target.value.toUpperCase() })}
                        placeholder="A"
                        className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100 disabled:cursor-not-allowed"
                        style={{ borderColor: COLORS.bg }}
                    />
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        e.g., A, B, C, or A1, B1
                    </p>
                </div>

                {/* Instructor - Searchable Select */}
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Instructor *
                    </label>
                    <Select
                        options={instructorOptions}
                        value={instructorOptions.find(opt => opt.value === formData.instructorId)}
                        onChange={(selected) => setFormData({ ...formData, instructorId: selected?.value || '' })}
                        placeholder="Search by name or email..."
                        isSearchable
                        isClearable
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
                                color: COLORS.dark,
                                cursor: 'pointer'
                            }),
                            menu: (base) => ({
                                ...base,
                                zIndex: 100
                            })
                        }}
                    />
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        Active instructors with available capacity
                    </p>
                </div>

                {/* Format */}
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Format *
                    </label>
                    <select
                        value={formData.courseFormat}
                        onChange={(e) => setFormData({ ...formData, courseFormat: e.target.value as CourseFormat })}
                        className="w-full px-3 py-2 border rounded-lg cursor-pointer"
                        style={{ borderColor: COLORS.bg }}
                    >
                        <option value="IN_PERSON">In-Person</option>
                        <option value="ONLINE">Online</option>
                        <option value="HYBRID">Hybrid</option>
                    </select>
                </div>

                {/* Location */}
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Location *
                    </label>
                    <input
                        type="text"
                        value={formData.location}
                        onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                        placeholder="Room 201 or Online"
                        className="w-full px-3 py-2 border rounded-lg"
                        style={{ borderColor: COLORS.bg }}
                    />
                </div>

                {/* Days of Week - Multi-select */}
                <div className="col-span-2">
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Days of Week *
                    </label>
                    <Select
                        isMulti
                        options={DAYS_OPTIONS}
                        value={DAYS_OPTIONS.filter(opt => formData.daysOfWeek.includes(opt.value))}
                        onChange={(selected) => {
                            const days = selected ? selected.map(s => s.value) : [];
                            setFormData({ ...formData, daysOfWeek: days });
                        }}
                        placeholder="Select days..."
                        closeMenuOnSelect={false}
                        styles={{
                            control: (base) => ({
                                ...base,
                                minHeight: '42px',
                                borderColor: COLORS.bg,
                                '&:hover': { borderColor: COLORS.orange }
                            }),
                            option: (base, state) => ({
                                ...base,
                                backgroundColor: state.isFocused ? COLORS.cream :
                                    state.isSelected ? COLORS.lightOrange : 'white',
                                color: COLORS.dark,
                                cursor: 'pointer'
                            }),
                            multiValue: (base) => ({
                                ...base,
                                backgroundColor: COLORS.orange,
                                borderRadius: '6px'
                            }),
                            multiValueLabel: (base) => ({
                                ...base,
                                color: 'white',
                                fontWeight: 600
                            }),
                            multiValueRemove: (base) => ({
                                ...base,
                                color: 'white',
                                ':hover': {
                                    backgroundColor: COLORS.dark,
                                    color: 'white'
                                }
                            })
                        }}
                    />
                </div>

                {/* Time Range */}
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Start Time *
                    </label>
                    <input
                        type="time"
                        value={formData.startTime}
                        onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg"
                        style={{ borderColor: COLORS.bg }}
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        End Time *
                    </label>
                    <input
                        type="time"
                        value={formData.endTime}
                        onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg"
                        style={{ borderColor: COLORS.bg }}
                    />
                </div>

                {/* Schedule Preview */}
                {schedulePreview && (
                    <div className="col-span-2">
                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                            Schedule Preview
                        </label>
                        <div className="px-4 py-3 rounded-lg font-medium" style={{ backgroundColor: COLORS.cream, color: COLORS.dark }}>
                            {schedulePreview}
                        </div>
                    </div>
                )}

                {/* Capacity */}
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Capacity *
                    </label>
                    <input
                        type="number"
                        value={formData.capacity}
                        onChange={(e) => setFormData({ ...formData, capacity: Number(e.target.value) })}
                        min={1}
                        max={50}
                        className="w-full px-3 py-2 border rounded-lg"
                        style={{ borderColor: COLORS.bg }}
                    />
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        Maximum students (1-50)
                    </p>
                </div>

                {/* Min Enrollment */}
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Min Enrollment *
                    </label>
                    <input
                        type="number"
                        value={formData.minEnrollment}
                        onChange={(e) => setFormData({ ...formData, minEnrollment: Number(e.target.value) })}
                        min={1}
                        max={formData.capacity}
                        className="w-full px-3 py-2 border rounded-lg"
                        style={{ borderColor: COLORS.bg }}
                    />
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        Minimum to run section
                    </p>
                </div>
            </div>

            {/* Action Buttons */}
            <div className="flex space-x-3 pt-4" style={{ borderTop: `1px solid ${COLORS.bg}` }}>
                <Button variant="primary" onClick={handleSubmit} className="flex-1">
                    {mode === 'create' ? 'Add Section' : 'Save Changes'}
                </Button>
                <Button variant="secondary" onClick={onCancel} className="flex-1">
                    Cancel
                </Button>
            </div>
        </div>
    );
};