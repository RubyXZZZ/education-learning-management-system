import React, { useState, useEffect } from 'react';
import { Navigation } from './components/common/Navigation';
import { LoginPage } from './pages/LoginPage';
import { Dashboard } from './pages/Dashboard';
import { MyCourses } from './pages/MyCourses';
import { UsersMgmt } from './pages/UsersMgmt';
import { CoursesMgmt } from './pages/CoursesMgmt';
import { CourseEnrollments } from './pages/CourseEnrollments';
import { SystemSettings } from './pages/SystemSettings';
import { ProfileSettings } from './pages/ProfileSettings';
import { CourseRegistration } from './pages/CourseRegistration';
import { useAuth, useSession } from './contexts/AppContext';
import { COLORS } from './constants/colors';
import type { ViewType } from './types';

const App: React.FC = () => {
    const { isAuthenticated, currentUser, loading: authLoading } = useAuth();
    const { loading: sessionsLoading } = useSession();

    const loading = authLoading || sessionsLoading;

    // Always read from localStorage on init
    const [currentView, setCurrentView] = useState<ViewType>(() => {
        const saved = localStorage.getItem('currentView');
        return (saved as ViewType) || 'dashboard';
    });

    const [previousView, setPreviousView] = useState<ViewType>('dashboard');

    const [selectedSectionId, setSelectedSectionId] = useState<string | null>(() => {
        return localStorage.getItem('selectedSectionId') || null;
    });

    // Save currentView to localStorage whenever it changes
    useEffect(() => {
        if (isAuthenticated) {
            localStorage.setItem('currentView', currentView);
        }
    }, [currentView, isAuthenticated]);

    // Save selectedSectionId to localStorage whenever it changes
    useEffect(() => {
        if (selectedSectionId) {
            localStorage.setItem('selectedSectionId', selectedSectionId);
        } else {
            localStorage.removeItem('selectedSectionId');
        }
    }, [selectedSectionId]);

    // Handle user login/logout and user switching
    useEffect(() => {
        if (isAuthenticated && currentUser) {
            const lastSessionUser = localStorage.getItem('lastSessionUser');
            const currentUserId = currentUser.id;

            if (lastSessionUser !== currentUserId) {
                // Different user logged in, reset to dashboard
                setCurrentView('dashboard');
                setSelectedSectionId(null);
                localStorage.setItem('lastSessionUser', currentUserId);
                localStorage.removeItem('currentView');
                localStorage.removeItem('selectedSectionId');
            }
        } else if (!isAuthenticated) {
            // User logged out, clear navigation state
            localStorage.removeItem('currentView');
            localStorage.removeItem('selectedSectionId');
            // Don't remove lastSessionUser - we need it to detect user changes
        }
    }, [isAuthenticated, currentUser]);

    const handleNavigate = (view: ViewType) => {
        setCurrentView(view);
    };

    const handleViewEnrollments = (sectionId: string) => {
        setPreviousView(currentView);
        setSelectedSectionId(sectionId);
        setCurrentView('course-enrollments');
    };

    const handleBackFromEnrollments = () => {
        setSelectedSectionId(null);
        setCurrentView(previousView);
    };

    // Show loading screen during app initialization
    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: COLORS.bg }}>
                <div className="text-center">
                    <div className="text-5xl mb-4">🎓</div>
                    <div className="text-xl font-semibold" style={{ color: COLORS.dark }}>
                        Loading...
                    </div>
                    <p className="text-sm mt-2" style={{ color: COLORS.dark, opacity: 0.6 }}>
                        Initializing your learning portal
                    </p>
                </div>
            </div>
        );
    }

    // Show login page if not authenticated
    if (!isAuthenticated) {
        return <LoginPage />;
    }

    // Main application
    return (
        <div className="min-h-screen bg-white">
            <Navigation
                currentView={currentView}
                onViewChange={setCurrentView}
                userRole={currentUser?.role || 'STUDENT'}
                isSuperAdmin={(currentUser as any)?.isSuperAdmin || false}
            />

            <div className="ml-20 p-8">
                <div className="max-w-7xl mx-auto">
                    {currentView === 'dashboard' && (
                        <Dashboard onNavigate={handleNavigate} />
                    )}

                    {currentView === 'my-courses' && (
                        <MyCourses />
                    )}

                    {currentView === 'course-registration' && (
                        <CourseRegistration onViewEnrollments={handleViewEnrollments} />
                    )}

                    {currentView === 'users-management' && (
                        <UsersMgmt />
                    )}

                    {currentView === 'courses-management' && (
                        <CoursesMgmt onViewEnrollments={handleViewEnrollments} />
                    )}

                    {currentView === 'course-enrollments' && selectedSectionId && (
                        <CourseEnrollments
                            sectionId={selectedSectionId}
                            onBack={handleBackFromEnrollments}
                        />
                    )}

                    {currentView === 'system-settings' && (
                        <SystemSettings />
                    )}

                    {currentView === 'profile' && (
                        <ProfileSettings />
                    )}
                </div>
            </div>
        </div>
    );
};

export default App;