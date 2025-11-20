import React, { useState, useEffect } from 'react';
import { PageHeader } from '../components/common/PageHeader';
import { Button } from '../components/common/Button';
import { COLORS } from '../constants/colors';
import { User, Mail, Phone, Lock, Eye, EyeOff } from 'lucide-react';
import { useAuth } from '../contexts/AppContext';
import { auth } from '../services/firebase';
import { updatePassword, reauthenticateWithCredential, EmailAuthProvider } from 'firebase/auth';
import { profileApi } from '../services/api';

export const ProfileSettings: React.FC = () => {
    const { currentUser } = useAuth();
    const [activeTab, setActiveTab] = useState<'profile' | 'security'>('profile');
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(true);

    const [userProfile, setUserProfile] = useState<any>(null);

    const [passwordForm, setPasswordForm] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });

    const [showPasswords, setShowPasswords] = useState({
        current: false,
        new: false,
        confirm: false
    });

    useEffect(() => {
        if (currentUser) {
            loadProfile();
        }
    }, [currentUser]);

    const loadProfile = async () => {
        if (!currentUser) return;

        try {
            setLoading(true);
            const res = await profileApi.getMyProfile();
            setUserProfile(res.data);
        } catch(err) {
            console.error('Error loading profile:', err);
        } finally {
            setLoading(false);
        }
    }

    const handleSaveProfile = async () => {
        if (!userProfile) return;

        try {
            const updateData: any = {
                phone: userProfile.phone,
                address: userProfile.address,
                emergencyContact: userProfile.emergencyContact,
                emergencyPhone: userProfile.emergencyPhone
            };

            await profileApi.updateMyProfile(updateData);

            setIsEditing(false);
            await loadProfile();
            alert('Profile updated successfully!');
        } catch (err: any) {
            alert('Failed to update profile: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleChangePassword = async () => {
        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
            alert('Passwords do not match!');
            return;
        }
        if (passwordForm.newPassword.length < 8) {
            alert('Password must be at least 8 characters!');
            return;
        }
        if (!passwordForm.currentPassword) {
            alert('Please enter your current password!');
            return;
        }

        try {
            const user = auth.currentUser;
            if (!user || !user.email) {
                alert('User not authenticated');
                return;
            }

            // Step 1: Re-authenticate with current password
            const credential = EmailAuthProvider.credential(
                user.email,
                passwordForm.currentPassword
            );
            await reauthenticateWithCredential(user, credential);

            // Step 2: Update password
            await updatePassword(user, passwordForm.newPassword);

            // Clear form
            setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
            alert('Password changed successfully!');
        } catch (err: any) {
            let errorMsg = 'Failed to change password';

            if (err.code === 'auth/wrong-password' || err.code === 'auth/invalid-credential') {
                errorMsg = 'Current password is incorrect';
            } else if (err.code === 'auth/weak-password') {
                errorMsg = 'Password is too weak';
            } else if (err.code === 'auth/requires-recent-login') {
                errorMsg = 'Please logout and login again before changing password';
            }

            alert(errorMsg);
        }
    };

    if (loading) {
        return (
            <div className="space-y-6">
                <PageHeader title="Profile Settings" />
                <div className="text-center py-12">Loading...</div>
            </div>
        );
    }

    if (!userProfile) {
        return (
            <div className="space-y-6">
                <PageHeader title="Profile Settings" />
                <div className="text-center py-12 text-red-600">Failed to load profile</div>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            <PageHeader title="Profile Settings" />

            <div className="bg-white rounded-3xl shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                {/* Tabs */}
                <div className="flex" style={{ borderBottom: `1px solid ${COLORS.bg}` }}>
                    {[
                        { key: 'profile', label: 'Profile Information', icon: <User size={16} /> },
                        { key: 'security', label: 'Security', icon: <Lock size={16} /> }
                    ].map(tab => (
                        <button
                            key={tab.key}
                            onClick={() => setActiveTab(tab.key as any)}
                            className="px-6 py-4 text-sm font-medium flex items-center space-x-2 cursor-pointer"
                            style={{
                                color: activeTab === tab.key ? COLORS.orange : COLORS.dark + 'AA',
                                borderBottom: activeTab === tab.key ? `2px solid ${COLORS.orange}` : 'none'
                            }}
                        >
                            {tab.icon}
                            <span>{tab.label}</span>
                        </button>
                    ))}
                </div>

                <div className="p-8">
                    {/* Profile Tab */}
                    {activeTab === 'profile' && (
                        <div className="max-w-2xl">
                            {/* Avatar Section */}
                            <div className="flex items-center space-x-6 mb-8 pb-8" style={{ borderBottom: `1px solid ${COLORS.bg}` }}>
                                <div className="w-24 h-24 rounded-2xl flex items-center justify-center text-5xl"
                                     style={{ backgroundColor: COLORS.cream }}>
                                    {userProfile.userAvatar || '👤'}
                                </div>
                                <div>
                                    <h3 className="text-xl font-bold mb-1" style={{ color: COLORS.dark }}>
                                        {userProfile.fullName}
                                    </h3>
                                    <p className="text-sm mb-2" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                        {currentUser?.role} • ID: {userProfile.studentNumber || userProfile.employeeNumber}
                                    </p>
                                </div>
                            </div>

                            {/* Profile Form */}
                            <div className="space-y-4">
                                <div className="grid grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                            First Name
                                        </label>
                                        <input
                                            type="text"
                                            value={userProfile.firstName || ''}
                                            onChange={(e) => setUserProfile({ ...userProfile, firstName: e.target.value })}
                                            disabled={!isEditing}
                                            className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100"
                                            style={{ borderColor: COLORS.bg }}
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                            Last Name
                                        </label>
                                        <input
                                            type="text"
                                            value={userProfile.lastName || ''}
                                            onChange={(e) => setUserProfile({ ...userProfile, lastName: e.target.value })}
                                            disabled={!isEditing}
                                            className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100"
                                            style={{ borderColor: COLORS.bg }}
                                        />
                                    </div>
                                </div>

                                {/* Email - Read only */}
                                <div>
                                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                        <Mail size={16} className="inline mr-2" />
                                        Email
                                    </label>
                                    <input
                                        type="email"
                                        value={userProfile.email || ''}
                                        disabled
                                        className="w-full px-3 py-2 border rounded-lg bg-gray-100 cursor-not-allowed"
                                        style={{ borderColor: COLORS.bg }}
                                    />
                                    <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                        Contact administrator to change email
                                    </p>
                                </div>

                                <div>
                                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                        <Phone size={16} className="inline mr-2" />
                                        Phone
                                    </label>
                                    <input
                                        type="tel"
                                        value={userProfile.phone || ''}
                                        onChange={(e) => setUserProfile({ ...userProfile, phone: e.target.value })}
                                        disabled={!isEditing}
                                        className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100"
                                        style={{ borderColor: COLORS.bg }}
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                        Address
                                    </label>
                                    <input
                                        type="text"
                                        value={userProfile.address || ''}
                                        onChange={(e) => setUserProfile({ ...userProfile, address: e.target.value })}
                                        disabled={!isEditing}
                                        className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100"
                                        style={{ borderColor: COLORS.bg }}
                                    />
                                </div>

                                {/* Student-specific fields */}
                                {currentUser?.role === 'STUDENT' && (
                                    <>
                                        <div>
                                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                                Date of Birth
                                            </label>
                                            <input
                                                type="date"
                                                value={userProfile.dateOfBirth || ''}
                                                onChange={(e) => setUserProfile({ ...userProfile, dateOfBirth: e.target.value })}
                                                disabled={!isEditing}
                                                className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100"
                                                style={{ borderColor: COLORS.bg }}
                                            />
                                        </div>

                                        <div>
                                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                                Gender
                                            </label>
                                            <select
                                                value={userProfile.gender || ''}
                                                onChange={(e) => setUserProfile({ ...userProfile, gender: e.target.value })}
                                                disabled={!isEditing}
                                                className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100 cursor-pointer"
                                                style={{ borderColor: COLORS.bg }}
                                            >
                                                <option value="">Select</option>
                                                <option value="Male">Male</option>
                                                <option value="Female">Female</option>
                                                <option value="Other">Other</option>
                                            </select>
                                        </div>

                                        <div className="grid grid-cols-2 gap-4">
                                            <div>
                                                <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                                    Emergency Contact
                                                </label>
                                                <input
                                                    type="text"
                                                    value={userProfile.emergencyContact || ''}
                                                    onChange={(e) => setUserProfile({ ...userProfile, emergencyContact: e.target.value })}
                                                    disabled={!isEditing}
                                                    className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100"
                                                    style={{ borderColor: COLORS.bg }}
                                                />
                                            </div>
                                            <div>
                                                <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                                    Emergency Phone
                                                </label>
                                                <input
                                                    type="tel"
                                                    value={userProfile.emergencyPhone || ''}
                                                    onChange={(e) => setUserProfile({ ...userProfile, emergencyPhone: e.target.value })}
                                                    disabled={!isEditing}
                                                    className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100"
                                                    style={{ borderColor: COLORS.bg }}
                                                />
                                            </div>
                                        </div>
                                    </>
                                )}

                                {/* Instructor/Admin-specific fields */}
                                {(currentUser?.role === 'INSTRUCTOR' || currentUser?.role === 'ADMIN') && (
                                    <div>
                                        <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                            Office Hours
                                        </label>
                                        <input
                                            type="text"
                                            value={userProfile.officeHours || ''}
                                            onChange={(e) => setUserProfile({ ...userProfile, officeHours: e.target.value })}
                                            disabled={!isEditing}
                                            placeholder="Mon/Wed 2:00-4:00 PM"
                                            className="w-full px-3 py-2 border rounded-lg disabled:bg-gray-100"
                                            style={{ borderColor: COLORS.bg }}
                                        />
                                    </div>
                                )}
                            </div>

                            {/* Action Buttons */}
                            <div className="flex space-x-3 mt-6">
                                {!isEditing ? (
                                    <Button variant="primary" onClick={() => setIsEditing(true)}>
                                        Edit Profile
                                    </Button>
                                ) : (
                                    <>
                                        <Button variant="primary" onClick={handleSaveProfile}>
                                            Save Changes
                                        </Button>
                                        <Button variant="secondary" onClick={() => {
                                            setIsEditing(false);
                                            loadProfile();
                                        }}>
                                            Cancel
                                        </Button>
                                    </>
                                )}
                            </div>
                        </div>
                    )}

                    {/* Security Tab */}
                    {activeTab === 'security' && (
                        <div className="max-w-2xl">
                            <h3 className="text-lg font-bold mb-6" style={{ color: COLORS.dark }}>
                                Change Password
                            </h3>

                            <div className="space-y-4">
                                <div>
                                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                        Current Password
                                    </label>
                                    <div className="relative">
                                        <input
                                            type={showPasswords.current ? 'text' : 'password'}
                                            value={passwordForm.currentPassword}
                                            onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
                                            className="w-full px-3 py-2 pr-10 border rounded-lg"
                                            style={{ borderColor: COLORS.bg }}
                                            placeholder="Enter current password"
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowPasswords({ ...showPasswords, current: !showPasswords.current })}
                                            className="absolute right-3 top-1/2 transform -translate-y-1/2 cursor-pointer"
                                            style={{ color: COLORS.dark, opacity: 0.5 }}
                                        >
                                            {showPasswords.current ? <EyeOff size={18} /> : <Eye size={18} />}
                                        </button>
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                        New Password
                                    </label>
                                    <div className="relative">
                                        <input
                                            type={showPasswords.new ? 'text' : 'password'}
                                            value={passwordForm.newPassword}
                                            onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                                            className="w-full px-3 py-2 pr-10 border rounded-lg"
                                            style={{ borderColor: COLORS.bg }}
                                            placeholder="Enter new password (min 8 characters)"
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowPasswords({ ...showPasswords, new: !showPasswords.new })}
                                            className="absolute right-3 top-1/2 transform -translate-y-1/2 cursor-pointer"
                                            style={{ color: COLORS.dark, opacity: 0.5 }}
                                        >
                                            {showPasswords.new ? <EyeOff size={18} /> : <Eye size={18} />}
                                        </button>
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                        Confirm New Password
                                    </label>
                                    <div className="relative">
                                        <input
                                            type={showPasswords.confirm ? 'text' : 'password'}
                                            value={passwordForm.confirmPassword}
                                            onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                                            className="w-full px-3 py-2 pr-10 border rounded-lg"
                                            style={{ borderColor: COLORS.bg }}
                                            placeholder="Confirm new password"
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowPasswords({ ...showPasswords, confirm: !showPasswords.confirm })}
                                            className="absolute right-3 top-1/2 transform -translate-y-1/2 cursor-pointer"
                                            style={{ color: COLORS.dark, opacity: 0.5 }}
                                        >
                                            {showPasswords.confirm ? <EyeOff size={18} /> : <Eye size={18} />}
                                        </button>
                                    </div>
                                </div>

                                <Button variant="primary" onClick={handleChangePassword}>
                                    Update Password
                                </Button>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* Account Information Card */}
            <div className="bg-white rounded-3xl p-6 shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                <h3 className="text-lg font-bold mb-4" style={{ color: COLORS.dark }}>
                    Account Information
                </h3>
                <div className="grid grid-cols-3 gap-6">
                    <div className="text-center p-4 rounded-lg" style={{ backgroundColor: COLORS.bg + '40' }}>
                        <div className="text-2xl font-bold mb-1" style={{ color: COLORS.dark }}>
                            {currentUser?.role}
                        </div>
                        <div className="text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            Account Type
                        </div>
                    </div>
                    <div className="text-center p-4 rounded-lg" style={{ backgroundColor: COLORS.bg + '40' }}>
                        <div className="text-2xl font-bold mb-1 text-green-600">
                            {userProfile.status}
                        </div>
                        <div className="text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            Account Status
                        </div>
                    </div>
                    <div className="text-center p-4 rounded-lg" style={{ backgroundColor: COLORS.bg + '40' }}>
                        <div className="text-2xl font-bold mb-1" style={{ color: COLORS.dark }}>
                            {userProfile.studentNumber || userProfile.employeeNumber}
                        </div>
                        <div className="text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            User ID
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};