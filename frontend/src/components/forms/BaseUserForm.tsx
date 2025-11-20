import React, { useState, useEffect } from 'react';
import Select from 'react-select';
import { getData } from 'country-list';
import { COLORS } from '../../constants/colors';
import { Badge } from '../common/Badge';
import { Button } from '../common/Button';
import { USER_STATUS, STUDENT_TYPE, DEPARTMENTS } from '../../types';
import type { StudentRes, InstructorRes, AdminRes, StudentType, UserStatus } from '../../types';

type UserType = 'student' | 'instructor' | 'admin';
type UserData = StudentRes | InstructorRes | AdminRes;

interface BaseUserFormProps {
    userType: UserType;
    mode: 'create' | 'edit';
    initialData?: UserData;
    onSubmit: (data: any) => void;
    onCancel: () => void;
}

// Define a unified form data type
interface FormData {
    userNumber: string;
    status: UserStatus;
    firstName: string;
    lastName: string;
    email: string;
    emailVerified: boolean;
    phone: string;
    address: string;
    dateOfBirth: string;
    gender: string;
    // Student-specific
    nationality?: string;
    studentType?: StudentType | '';
    placementLevel?: number | null;
    placementTestDate?: string;
    emergencyContact?: string;
    emergencyPhone?: string;
    // Instructor/Admin-specific
    department?: string;
    officeHours?: string;
    // Admin-specific
    position?: string;
    isSuperAdmin?: boolean;
}

export const BaseUserForm: React.FC<BaseUserFormProps> = ({
                                                              userType,
                                                              mode,
                                                              initialData,
                                                              onSubmit,
                                                              onCancel
                                                          }) => {
    const countryOptions = getData().map(country => ({
        value: country.name,
        label: country.name
    }));

    // Get initial form data based on user type
    const getInitialFormData = (): FormData => {
        if (mode === 'edit' && initialData) {
            if (userType === 'student') {
                const student = initialData as StudentRes;
                return {
                    userNumber: student.studentNumber || '',
                    status: student.status || 'PENDING',
                    firstName: student.firstName || '',
                    lastName: student.lastName || '',
                    email: student.email || '',
                    emailVerified: student.emailVerified || false,
                    phone: student.phone || '',
                    address: student.address || '',
                    dateOfBirth: student.dateOfBirth || '',
                    gender: student.gender || '',
                    nationality: student.nationality || '',
                    studentType: student.studentType || '',
                    placementLevel: student.placementLevel,
                    placementTestDate: student.placementTestDate || '',
                    emergencyContact: student.emergencyContact || '',
                    emergencyPhone: student.emergencyPhone || ''
                };
            } else if (userType === 'instructor') {
                const instructor = initialData as InstructorRes;
                return {
                    userNumber: instructor.employeeNumber || '',
                    status: instructor.status || 'PENDING',
                    firstName: instructor.firstName || '',
                    lastName: instructor.lastName || '',
                    email: instructor.email || '',
                    emailVerified: instructor.emailVerified || false,
                    phone: instructor.phone || '',
                    address: instructor.address || '',
                    dateOfBirth: instructor.dateOfBirth || '',
                    gender: instructor.gender || '',
                    department: instructor.department || '',
                    officeHours: instructor.officeHours || ''
                };
            } else {
                const admin = initialData as AdminRes;
                return {
                    userNumber: admin.employeeNumber || '',
                    status: admin.status || 'PENDING',
                    firstName: admin.firstName || '',
                    lastName: admin.lastName || '',
                    email: admin.email || '',
                    emailVerified: admin.emailVerified || false,
                    phone: admin.phone || '',
                    address: admin.address || '',
                    dateOfBirth: admin.dateOfBirth || '',
                    gender: admin.gender || '',
                    department: admin.department || '',
                    position: admin.position || '',
                    officeHours: admin.officeHours || '',
                    isSuperAdmin: admin.isSuperAdmin || false
                };
            }
        }

        // Create mode defaults
        const baseData: FormData = {
            userNumber: '',
            status: 'PENDING',
            firstName: '',
            lastName: '',
            email: '',
            emailVerified: false,
            phone: '',
            address: '',
            dateOfBirth: '',
            gender: ''
        };

        if (userType === 'student') {
            return {
                ...baseData,
                nationality: '',
                studentType: '',
                placementLevel: null,
                placementTestDate: '',
                emergencyContact: '',
                emergencyPhone: ''
            };
        } else if (userType === 'instructor') {
            return {
                ...baseData,
                department: '',
                officeHours: ''
            };
        } else {
            return {
                ...baseData,
                department: '',
                position: '',
                officeHours: '',
                isSuperAdmin: false
            };
        }
    };

    const [formData, setFormData] = useState<FormData>(getInitialFormData());

    useEffect(() => {
        if (mode === 'edit' && initialData) {
            setFormData(getInitialFormData());
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [mode, initialData]);

    const handleSubmit = () => {
        // Validation
        if (!formData.firstName || !formData.lastName || !formData.email) {
            alert('Please fill in all required fields (First Name, Last Name, Email)');
            return;
        }

        if (userType === 'student' && !formData.studentType) {
            alert('Please select Student Type');
            return;
        }

        if (mode === 'create' && userType !== 'student' && !formData.userNumber) {
            alert('Please fill in Employee Number');
            return;
        }

        // Prepare submit data
        const submitData: any = {
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            phone: formData.phone || null,
            address: formData.address || null,
            dateOfBirth: formData.dateOfBirth || null,
            gender: formData.gender || null
        };

        if (userType === 'student') {
            submitData.nationality = formData.nationality || null;
            submitData.studentType = formData.studentType;
            submitData.placementLevel = formData.placementLevel;
            submitData.placementTestDate = formData.placementTestDate || null;
            submitData.emergencyContact = formData.emergencyContact || null;
            submitData.emergencyPhone = formData.emergencyPhone || null;
        } else {
            submitData.employeeNumber = formData.userNumber;
            submitData.department = formData.department || null;
            submitData.officeHours = formData.officeHours || null;

            if (userType === 'admin') {
                submitData.position = formData.position || null;
                submitData.isSuperAdmin = formData.isSuperAdmin;
            }
        }

        onSubmit(submitData);
    };

    const statusConfig = USER_STATUS[formData.status as keyof typeof USER_STATUS];
    const userTypeLabel = userType.charAt(0).toUpperCase() + userType.slice(1);

    return (
        <div className="space-y-6">
            <h3 className="text-xl font-bold" style={{ color: COLORS.dark }}>
                {mode === 'create'
                    ? `Create ${userTypeLabel}${userType === 'student' ? ' (Walk-in Registration)' : ''}`
                    : `Edit ${userTypeLabel}`
                }
            </h3>

            {/* System Fields */}
            <SystemFields
                userType={userType}
                mode={mode}
                formData={formData}
                setFormData={setFormData}
                statusConfig={statusConfig}
            />

            {/* Basic Information */}
            <BasicInfoFields
                mode={mode}
                formData={formData}
                setFormData={setFormData}
            />

            {/* Student-Specific Fields */}
            {userType === 'student' && (
                <StudentSpecificFields
                    formData={formData}
                    setFormData={setFormData}
                    countryOptions={countryOptions}
                />
            )}

            {/* Instructor/Admin-Specific Fields */}
            {userType !== 'student' && (
                <ProfessionalFields
                    userType={userType}
                    formData={formData}
                    setFormData={setFormData}
                />
            )}

            {/* Admin Role & Permissions */}
            {userType === 'admin' && (
                <AdminRoleFields
                    formData={formData}
                    setFormData={setFormData}
                />
            )}

            {/* Action Buttons */}
            <div className="flex space-x-3 pt-4" style={{ borderTop: `1px solid ${COLORS.bg}` }}>
                <Button
                    variant="primary"
                    onClick={handleSubmit}
                    className="flex-1"
                >
                    {mode === 'create' ? `Create ${userTypeLabel}` : 'Save Changes'}
                </Button>
                <Button
                    variant="secondary"
                    onClick={onCancel}
                    className="flex-1"
                >
                    Cancel
                </Button>
            </div>
        </div>
    );
};

// ==================== Sub-components ====================

interface SystemFieldsProps {
    userType: UserType;
    mode: 'create' | 'edit';
    formData: FormData;
    setFormData: (data: FormData) => void;
    statusConfig?: { value: string; label: string; color?: string };
}

const SystemFields: React.FC<SystemFieldsProps> = ({ userType, mode, formData, setFormData, statusConfig }) => {
    const numberLabel = userType === 'student' ? 'Student Number' : 'Employee Number';
    const numberPlaceholder = userType === 'student' ? 'Auto-generated' :
        userType === 'instructor' ? 'INS001' : 'EMP001';

    return (
        <div>
            <h4 className="text-sm font-bold mb-3" style={{ color: COLORS.dark, opacity: 0.7 }}>
                System Information
            </h4>
            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        {numberLabel} {mode === 'create' && userType !== 'student' && '*'}
                    </label>
                    <input
                        type="text"
                        value={mode === 'create' && userType === 'student' ? 'Auto-generated by system' : formData.userNumber}
                        onChange={(e) => setFormData({ ...formData, userNumber: e.target.value })}
                        disabled={mode === 'edit' || (mode === 'create' && userType === 'student')}
                        className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-50 disabled:text-gray-500 disabled:cursor-not-allowed"
                        placeholder={numberPlaceholder}
                    />
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        {mode === 'create'
                            ? (userType === 'student' ? 'Will be assigned after creation' : 'Required for creation')
                            : `${userType === 'student' ? 'Student' : 'Employee'} number cannot be changed`
                        }
                    </p>
                </div>
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Status
                    </label>
                    <div className="px-3 py-2 border rounded-lg bg-gray-50 cursor-not-allowed" style={{ borderColor: COLORS.bg }}>
                        <Badge className={statusConfig?.color || 'bg-gray-100 text-gray-600'}>
                            {statusConfig?.label || formData.status}
                        </Badge>
                    </div>
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        {mode === 'create' ? 'Initial status after creation' : 'Use action buttons to change status'}
                    </p>
                </div>
            </div>
        </div>
    );
};

interface BasicInfoFieldsProps {
    mode: 'create' | 'edit';
    formData: FormData;
    setFormData: (data: FormData) => void;
}

const BasicInfoFields: React.FC<BasicInfoFieldsProps> = ({ mode, formData, setFormData }) => {
    return (
        <div>
            <h4 className="text-sm font-bold mb-3" style={{ color: COLORS.dark, opacity: 0.7 }}>
                Basic Information
            </h4>
            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Email *
                        {mode === 'edit' && (
                            <span className={`ml-2 text-xs ${formData.emailVerified ? 'text-green-600' : 'text-red-600'}`}>
                                {formData.emailVerified ? '✓ Verified' : '✗ Not verified'}
                            </span>
                        )}
                    </label>
                    <input
                        type="email"
                        value={formData.email}
                        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                        disabled={mode === 'edit'}
                        className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-50 disabled:text-gray-500 disabled:cursor-not-allowed"
                        placeholder="user@example.com"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Phone
                    </label>
                    <input
                        type="tel"
                        value={formData.phone}
                        onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg"
                        placeholder="+1 (555) 123-4567"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        First Name *
                    </label>
                    <input
                        type="text"
                        value={formData.firstName}
                        onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg"
                        placeholder="John"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Last Name *
                    </label>
                    <input
                        type="text"
                        value={formData.lastName}
                        onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg"
                        placeholder="Doe"
                    />
                </div>
                <div className="col-span-2">
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Address
                    </label>
                    <input
                        type="text"
                        value={formData.address}
                        onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg"
                        placeholder="123 Main St, City, State 12345"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Date of Birth
                    </label>
                    <input
                        type="date"
                        value={formData.dateOfBirth}
                        onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Gender
                    </label>
                    <select
                        value={formData.gender}
                        onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg cursor-pointer"
                    >
                        <option value="">Select Gender</option>
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                        <option value="Other">Other</option>
                    </select>
                </div>
            </div>
        </div>
    );
};

interface StudentSpecificFieldsProps {
    formData: FormData;
    setFormData: (data: FormData) => void;
    countryOptions: Array<{ value: string; label: string }>;
}

const StudentSpecificFields: React.FC<StudentSpecificFieldsProps> = ({
                                                                         formData,
                                                                         setFormData,
                                                                         countryOptions
                                                                     }) => {
    // Hardcoded levels (temporary until backend provides API)
    const levels = [
        { value: '1', label: 'Level 1 (A1 - Beginner)' },
        { value: '2', label: 'Level 2 (A2 - Elementary)' },
        { value: '3', label: 'Level 3 (B1 - Intermediate)' },
        { value: '4', label: 'Level 4 (B2 - Upper-Intermediate)' },
        { value: '5', label: 'Level 5 (C1 - Advanced)' },
        { value: '6', label: 'Level 6 (C1 - Proficiency)' }
    ];

    return (
        <>
            {/* Student Type */}
                <div>
                    <h4 className="text-sm font-bold mb-3" style={{ color: COLORS.dark, opacity: 0.7 }}>
                        Student Type *
                    </h4>
                    <select
                        value={formData.studentType || ''}
                        onChange={(e) => setFormData({ ...formData, studentType: e.target.value as StudentType })}
                        className="w-full px-3 py-2 border rounded-lg cursor-pointer"
                        style={{ borderColor: !formData.studentType ? '#ef4444' : COLORS.bg }}
                    >
                        <option value="">Select Student Type</option>
                        {Object.values(STUDENT_TYPE).map((type) => (
                            <option key={type.value} value={type.value}>
                                {type.label}
                            </option>
                        ))}
                    </select>
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        Full-time: Must enroll ≥18 hours | Part-time: &lt;18 hours | Flexible: No restriction
                    </p>
                </div>

                {/* Nationality */}
                <div>
                    <h4 className="text-sm font-bold mb-3" style={{ color: COLORS.dark, opacity: 0.7 }}>
                        Nationality
                    </h4>
                    <Select
                        options={countryOptions}
                        value={countryOptions.find((c) => c.value === formData.nationality)}
                        onChange={(option) => setFormData({ ...formData, nationality: option?.value || '' })}
                        placeholder="Select country..."
                        isClearable
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
                                color: COLORS.dark,
                                cursor: 'pointer'
                            })
                        }}
                    />
                </div>

                {/* Placement Test */}
                <div>
                    <h4 className="text-sm font-bold mb-3" style={{ color: COLORS.dark, opacity: 0.7 }}>
                        Placement Test (Optional)
                    </h4>
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Placement Level
                            </label>
                            <select
                                value={formData.placementLevel || ''}
                                onChange={(e) => setFormData({
                                    ...formData,
                                    placementLevel: e.target.value ? parseInt(e.target.value) : null
                                })}
                                className="w-full px-3 py-2 border rounded-lg cursor-pointer"
                            >
                                <option value="">Not tested yet</option>
                                {levels.map((level) => (
                                    <option key={level.value} value={level.value}>
                                        {level.label}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Test Date
                            </label>
                            <input
                                type="date"
                                value={formData.placementTestDate}
                                onChange={(e) => setFormData({ ...formData, placementTestDate: e.target.value })}
                                className="w-full px-3 py-2 border rounded-lg"
                                disabled={!formData.placementLevel}
                            />
                        </div>
                    </div>
                    <p className="text-xs mt-2" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        Leave empty if student hasn't taken placement test yet. Can be set later.
                    </p>
                </div>

                {/* Emergency Contact */}
                <div>
                    <h4 className="text-sm font-bold mb-3" style={{ color: COLORS.dark, opacity: 0.7 }}>
                        Emergency Contact
                    </h4>
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Contact Name
                            </label>
                            <input
                                type="text"
                                value={formData.emergencyContact}
                                onChange={(e) => setFormData({ ...formData, emergencyContact: e.target.value })}
                                className="w-full px-3 py-2 border rounded-lg"
                                placeholder="Jane Doe"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Contact Phone
                            </label>
                            <input
                                type="tel"
                                value={formData.emergencyPhone}
                                onChange={(e) => setFormData({ ...formData, emergencyPhone: e.target.value })}
                                className="w-full px-3 py-2 border rounded-lg"
                                placeholder="+1 (555) 987-6543"
                            />
                        </div>
                    </div>
                </div>
        </>
    );
};

interface ProfessionalFieldsProps {
    userType: UserType;
    formData: FormData;
    setFormData: (data: FormData) => void;
}

const ProfessionalFields: React.FC<ProfessionalFieldsProps> = ({
                                                                   userType,
                                                                   formData,
                                                                   setFormData
                                                               }) => {
    return (
        <div>
            <h4 className="text-sm font-bold mb-3" style={{ color: COLORS.dark, opacity: 0.7 }}>
                Professional Information
            </h4>
            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Department
                    </label>
                    <select
                        value={formData.department || ''}
                        onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg cursor-pointer"
                    >
                        <option value="">Select Department</option>
                        {DEPARTMENTS.map((dept) => (
                            <option key={dept} value={dept}>{dept}</option>
                        ))}
                    </select>
                </div>
                {userType === 'admin' && (
                    <div>
                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                            Position
                        </label>
                        <input
                            type="text"
                            value={formData.position || ''}
                            onChange={(e) => setFormData({ ...formData, position: e.target.value })}
                            className="w-full px-3 py-2 border rounded-lg"
                            placeholder="System Administrator"
                        />
                    </div>
                )}
                <div className={userType === 'admin' ? 'col-span-2' : 'col-span-1'}>
                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                        Office Hours
                    </label>
                    <input
                        type="text"
                        value={formData.officeHours || ''}
                        onChange={(e) => setFormData({ ...formData, officeHours: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg"
                        placeholder={userType === 'instructor' ? 'Mon/Wed 2:00-4:00 PM, Room 205' : 'Mon-Fri 9:00 AM - 5:00 PM'}
                    />
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        {userType === 'instructor'
                            ? 'When students can meet with you for questions and consultations'
                            : 'When you\'re available for administrative support and consultations'
                        }
                    </p>
                </div>
            </div>
        </div>
    );
};

interface AdminRoleFieldsProps {
    formData: FormData;
    setFormData: (data: FormData) => void;
}

const AdminRoleFields: React.FC<AdminRoleFieldsProps> = ({ formData, setFormData }) => {
    return (
        <div>
            <h4 className="text-sm font-bold mb-3" style={{ color: COLORS.dark, opacity: 0.7 }}>
                Role & Permissions
            </h4>
            <label
                className="flex items-center space-x-3 p-4 rounded-xl cursor-pointer border-2 transition-all"
                style={{
                    backgroundColor: formData.isSuperAdmin ? COLORS.orange + '10' : COLORS.bg + '40',
                    borderColor: formData.isSuperAdmin ? COLORS.orange : 'transparent'
                }}
            >
                <input
                    type="checkbox"
                    checked={formData.isSuperAdmin}
                    onChange={(e) => setFormData({ ...formData, isSuperAdmin: e.target.checked })}
                    className="w-5 h-5 rounded cursor-pointer"
                    style={{ accentColor: COLORS.orange }}
                />
                <div className="flex-1">
                    <div className="font-semibold" style={{ color: COLORS.dark }}>
                        Super Admin
                    </div>
                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        Full system access - Can manage all users, courses, and system settings
                    </p>
                </div>
                {formData.isSuperAdmin && (
                    <span className="px-3 py-1 bg-red-100 text-red-700 rounded-full text-xs font-medium">
                        High Privilege
                    </span>
                )}
            </label>

            {!formData.isSuperAdmin && (
                <p className="text-xs mt-2 px-4" style={{ color: COLORS.dark, opacity: 0.6 }}>
                    Department Admin: Can manage users and courses within their department
                </p>
            )}
        </div>
    );
};