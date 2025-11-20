import React, { useState, useEffect } from 'react';
import Select from 'react-select';
import { COLORS } from '../../constants/colors';
import { Button } from '../common/Button';
import type { CourseRes, SessionRes } from '../../types';

interface CourseFormProps {
    mode: 'create' | 'edit';
    initialData?: CourseRes;
    sessions: SessionRes[];
    allCourses: CourseRes[];
    onSubmit: (data: any) => void;
    onCancel: () => void;
}

export const CourseForm: React.FC<CourseFormProps> = ({
                                                          mode,
                                                          initialData,
                                                          sessions,
                                                          allCourses,
                                                          onSubmit,
                                                          onCancel
                                                      }) => {
    const [formData, setFormData] = useState({
        sessionId: '',
        courseCode: '',
        courseName: '',
        courseDescription: '',
        hoursPerWeek: 7,
        isActive: true,
        prerequisiteCourses: [] as string[],
        requiredPlacementLevel: null as number | null,
        allowHigherPlacement: false
    });

    // Filter only UPCOMING and ACTIVE sessions
    const availableSessions = sessions.filter(s =>
        s.status === 'UPCOMING' || s.status === 'ACTIVE'
    );

    // Get unique course codes for prerequisites
    const allCourseCodes = [...new Set(allCourses.map(c => c.courseCode))].sort();

    // Convert to react-select options
    const prerequisiteOptions = allCourseCodes.map(code => ({
        value: code,
        label: code
    }));

    useEffect(() => {
        if (mode === 'edit' && initialData) {
            setFormData({
                sessionId: initialData.sessionId || '',
                courseCode: initialData.courseCode,
                courseName: initialData.courseName,
                courseDescription: initialData.courseDescription || '',
                hoursPerWeek: initialData.hoursPerWeek,
                isActive: initialData.isActive !== undefined ? initialData.isActive : true,
                prerequisiteCourses: initialData.prerequisiteCourses || [],
                requiredPlacementLevel: initialData.requiredPlacementLevel,
                allowHigherPlacement: initialData.allowHigherPlacement || false
            });
        }
    }, [mode, initialData]);

    const handleSubmit = () => {
        if (!formData.courseCode || !formData.courseName) {
            alert('Please fill in Course Code and Name');
            return;
        }

        if (mode === 'create' && !formData.sessionId) {
            alert('Please select a session');
            return;
        }

        if (!formData.hoursPerWeek || formData.hoursPerWeek < 1 || formData.hoursPerWeek > 20) {
            alert('Hours per week must be between 1 and 20');
            return;
        }

        onSubmit(formData);
    };

    // Get selected session info
    const selectedSession = formData.sessionId
        ? sessions.find(s => s.id === formData.sessionId)
        : null;

    return (
        <div className="space-y-6">
            <h3 className="text-2xl font-bold" style={{ color: COLORS.dark }}>
                {mode === 'create' ? 'Create Course' : 'Edit Course'}
            </h3>

            {/* Section 1: Course Information */}
            <div>
                <div className="mb-4 pb-2" style={{ borderBottom: `2px solid ${COLORS.orange}` }}>
                    <h4 className="text-lg font-bold" style={{ color: COLORS.dark }}>
                        Course Information
                    </h4>
                </div>

                <div className="grid grid-cols-2 gap-4">
                    {/* Session Selection */}
                    {mode === 'create' && (
                        <div className="col-span-2">
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Session *
                            </label>
                            <select
                                value={formData.sessionId}
                                onChange={(e) => setFormData({ ...formData, sessionId: e.target.value })}
                                className="w-full px-3 py-2 border rounded-lg cursor-pointer"
                                style={{ borderColor: COLORS.bg }}
                            >
                                <option value="">Select Session</option>
                                {availableSessions.map(s => (
                                    <option key={s.id} value={s.id}>
                                        {s.sessionCode} ({s.startDate} to {s.endDate})
                                    </option>
                                ))}
                            </select>
                            {selectedSession && (
                                <div className="mt-2 p-3 rounded-lg" style={{ backgroundColor: COLORS.cream }}>
                                    <div className="text-xs" style={{ color: COLORS.dark, opacity: 0.8 }}>
                                        <strong>Status:</strong> {selectedSession.status}
                                    </div>
                                </div>
                            )}
                        </div>
                    )}

                    {/* Edit mode: Show session info */}
                    {mode === 'edit' && initialData && (
                        <div className="col-span-2">
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Session
                            </label>
                            <div className="p-3 rounded-lg" style={{ backgroundColor: COLORS.bg + '40' }}>
                                <div className="text-sm font-medium" style={{ color: COLORS.dark }}>
                                    {initialData.sessionCode || 'Not assigned'}
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Course Code */}
                    <div>
                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                            Course Code *
                        </label>
                        <input
                            type="text"
                            value={formData.courseCode}
                            disabled={mode === 'edit'}
                            onChange={(e) => setFormData({ ...formData, courseCode: e.target.value.toUpperCase() })}
                            placeholder="ESL-LS-L1"
                            className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100 disabled:cursor-not-allowed font-mono"
                            style={{ borderColor: COLORS.bg }}
                        />
                        <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            Format: PROGRAM-TYPE-LEVEL (e.g., ESL-LS-L1, BE-MN)
                        </p>
                    </div>

                    {/* Course Name */}
                    <div>
                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                            Course Name *
                        </label>
                        <input
                            type="text"
                            value={formData.courseName}
                            onChange={(e) => setFormData({ ...formData, courseName: e.target.value })}
                            placeholder="Listening & Speaking"
                            className="w-full px-3 py-2 border rounded-lg"
                            style={{ borderColor: COLORS.bg }}
                        />
                    </div>

                    {/* Active Status - Edit mode only */}
                    {mode === 'edit' && (
                        <div>
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Active Status
                            </label>
                            <select
                                value={formData.isActive ? 'true' : 'false'}
                                onChange={(e) => setFormData({ ...formData, isActive: e.target.value === 'true' })}
                                className="w-full px-3 py-2 border rounded-lg cursor-pointer"
                                style={{ borderColor: COLORS.bg }}
                            >
                                <option value="true">Active</option>
                                <option value="false">Inactive</option>
                            </select>
                            <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                Inactive courses cannot create new sections
                            </p>
                        </div>
                    )}

                    {/* Hours per Week */}
                    <div className={mode === 'edit' ? '' : 'col-span-2'}>
                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                            Hours per Week *
                        </label>
                        <input
                            type="number"
                            value={formData.hoursPerWeek}
                            onChange={(e) => setFormData({ ...formData, hoursPerWeek: Number(e.target.value) })}
                            min={1}
                            max={20}
                            className="w-full px-3 py-2 border rounded-lg"
                            style={{ borderColor: COLORS.bg }}
                            placeholder="7"
                        />
                        <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            Typically: 7 hours for main courses (LS/RW), 4 hours for integrated skills (IS)
                        </p>
                    </div>

                    {/* Description */}
                    <div className="col-span-2">
                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                            Description
                        </label>
                        <textarea
                            value={formData.courseDescription}
                            onChange={(e) => setFormData({ ...formData, courseDescription: e.target.value })}
                            className="w-full px-3 py-2 border rounded-lg"
                            rows={3}
                            placeholder="Course description..."
                            style={{ borderColor: COLORS.bg }}
                        />
                    </div>
                </div>
            </div>

            {/* Section 2: Prerequisites */}
            <div>
                <div className="mb-4 pb-2" style={{ borderBottom: `2px solid ${COLORS.orange}` }}>
                    <h4 className="text-lg font-bold" style={{ color: COLORS.dark }}>
                        Prerequisites
                    </h4>
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        Students must meet at least one of the following requirements (OR logic)
                    </p>
                </div>

                <div className="space-y-4">
                    {/* Prerequisite Courses */}
                    <div>
                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                            Completed Courses
                        </label>
                        <Select
                            isMulti
                            options={prerequisiteOptions}
                            value={prerequisiteOptions.filter(opt =>
                                formData.prerequisiteCourses.includes(opt.value)
                            )}
                            onChange={(selected) => {
                                const codes = selected ? selected.map(s => s.value) : [];
                                setFormData({ ...formData, prerequisiteCourses: codes });
                            }}
                            placeholder="Search and select course codes..."
                            isSearchable
                            isClearable
                            closeMenuOnSelect={false}
                            blurInputOnSelect={false}
                            styles={{
                                control: (base) => ({
                                    ...base,
                                    minHeight: '42px',
                                    borderColor: COLORS.bg,
                                    '&:hover': { borderColor: COLORS.orange }
                                }),
                                option: (base, state) => ({
                                    ...base,
                                    backgroundColor: state.isFocused && !state.isSelected ? COLORS.cream :
                                        state.isSelected ? COLORS.lightOrange :
                                            'white',
                                    color: COLORS.dark,
                                    cursor: 'pointer',
                                    fontFamily: 'monospace',
                                    fontWeight: state.isSelected ? 600 : 400,
                                    ':active': {
                                        backgroundColor: state.isSelected ? COLORS.lightOrange : COLORS.cream
                                    }
                                }),
                                multiValue: (base) => ({
                                    ...base,
                                    backgroundColor: COLORS.orange,
                                    borderRadius: '6px',
                                    padding: '2px'
                                }),
                                multiValueLabel: (base) => ({
                                    ...base,
                                    color: 'white',
                                    fontFamily: 'monospace',
                                    fontWeight: 600,
                                    fontSize: '13px'
                                }),
                                multiValueRemove: (base) => ({
                                    ...base,
                                    color: 'white',
                                    ':hover': {
                                        backgroundColor: COLORS.dark,
                                        color: 'white'
                                    }
                                }),
                                menu: (base) => ({
                                    ...base,
                                    zIndex: 100,
                                    marginTop: '4px'
                                }),
                                menuList: (base) => ({
                                    ...base,
                                    paddingTop: '4px',
                                    paddingBottom: '4px'
                                })
                            }}
                            autoFocus={false}
                            openMenuOnFocus={false}
                        />
                        <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            Students must complete ALL selected courses (AND logic)
                        </p>
                    </div>

                    {/* OR Divider */}
                    {formData.prerequisiteCourses.length > 0 && (
                        <div className="flex items-center">
                            <div className="flex-1" style={{ borderTop: `1px solid ${COLORS.bg}` }} />
                            <span className="px-3 text-sm font-medium" style={{ color: COLORS.dark, opacity: 0.5 }}>
                                OR
                            </span>
                            <div className="flex-1" style={{ borderTop: `1px solid ${COLORS.bg}` }} />
                        </div>
                    )}

                    {/* Placement Level Requirement */}
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Required Placement Level
                            </label>
                            <select
                                value={formData.requiredPlacementLevel || ''}
                                onChange={(e) => setFormData({
                                    ...formData,
                                    requiredPlacementLevel: e.target.value ? Number(e.target.value) : null
                                })}
                                className="w-full px-3 py-2 border rounded-lg cursor-pointer"
                                style={{ borderColor: COLORS.bg }}
                            >
                                <option value="">None</option>
                                {[1, 2, 3, 4, 5, 6].map(level => (
                                    <option key={level} value={level}>
                                        Level {level}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Placement Match
                            </label>
                            <select
                                value={formData.allowHigherPlacement ? 'higher' : 'exact'}
                                onChange={(e) => setFormData({
                                    ...formData,
                                    allowHigherPlacement: e.target.value === 'higher'
                                })}
                                disabled={!formData.requiredPlacementLevel}
                                className="w-full px-3 py-2 border rounded-lg cursor-pointer disabled:bg-gray-100 disabled:cursor-not-allowed"
                                style={{ borderColor: COLORS.bg }}
                            >
                                <option value="exact">Exact (=)</option>
                                <option value="higher">Or Higher (≥)</option>
                            </select>
                        </div>
                    </div>

                    {/* Prerequisite Summary */}
                    {(formData.prerequisiteCourses.length > 0 || formData.requiredPlacementLevel) && (
                        <div className="p-4 rounded-lg" style={{ backgroundColor: COLORS.cream }}>
                            <p className="text-sm font-bold mb-2" style={{ color: COLORS.dark }}>
                                Enrollment Requirements:
                            </p>
                            <ul className="text-sm space-y-1" style={{ color: COLORS.dark, opacity: 0.8 }}>
                                {formData.prerequisiteCourses.length > 0 && (
                                    <li className="flex items-start">
                                        <span className="mr-2">•</span>
                                        <span>Completed ALL: {formData.prerequisiteCourses.join(', ')}</span>
                                    </li>
                                )}
                                {formData.prerequisiteCourses.length > 0 && formData.requiredPlacementLevel && (
                                    <li className="flex items-center">
                                        <span className="font-bold mx-2">OR</span>
                                    </li>
                                )}
                                {formData.requiredPlacementLevel && (
                                    <li className="flex items-start">
                                        <span className="mr-2">•</span>
                                        <span>
                                            Placement Level {formData.allowHigherPlacement ? '≥' : '='} {formData.requiredPlacementLevel}
                                        </span>
                                    </li>
                                )}
                            </ul>
                        </div>
                    )}
                </div>
            </div>

            {/* Action Buttons */}
            <div className="flex space-x-3 pt-4" style={{ borderTop: `1px solid ${COLORS.bg}` }}>
                <Button variant="primary" onClick={handleSubmit} className="flex-1">
                    {mode === 'create' ? 'Create Course' : 'Save Changes'}
                </Button>
                <Button variant="secondary" onClick={onCancel} className="flex-1">
                    Cancel
                </Button>
            </div>
        </div>
    );
};