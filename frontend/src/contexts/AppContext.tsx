import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import type { ReactNode } from 'react';
import { auth } from '../services/firebase';
import { signInWithEmailAndPassword, signOut } from 'firebase/auth';
import { sessionApi, authApi } from '../services/api';
import type { SessionRes } from '../types';

// ==================== Types ====================

interface CurrentUser {
    id: string;
    email: string;
    role: 'STUDENT' | 'INSTRUCTOR' | 'ADMIN';
    fullName: string;
    userNumber: string;
    userAvatar: string;
    status: string;
    isSuperAdmin?: boolean;
    firebaseUid: string;
}

interface LoadingState {
    sessions: boolean;
    auth: boolean;
}

interface ErrorState {
    sessions?: string;
    auth?: string;
}

interface AppContextState {
    // Session
    currentSession: SessionRes | null;
    allSessions: SessionRes[];


    // Auth
    currentUser: CurrentUser | null;

    // UI states
    loading: LoadingState;
    errors: ErrorState;
}

interface AppContextActions {
    // Session actions
    refreshSessions: () => Promise<void>;

    // Auth actions
    login: (email: string, password: string) => Promise<void>;
    logout: () => Promise<void>;

    // Utility
    clearError: (key: keyof ErrorState) => void;
}

type AppContextType = AppContextState & AppContextActions;

// ==================== Context ====================

const AppContext = createContext<AppContextType | undefined>(undefined);

// ==================== Provider ====================

interface AppProviderProps {
    children: ReactNode;
}

export const AppProvider: React.FC<AppProviderProps> = ({ children }) => {
    const [state, setState] = useState<AppContextState>({
        currentSession: null,
        allSessions: [],
        currentUser: null,
        loading: {
            sessions: true,
            auth: true
        },
        errors: {}
    });

    // ==================== Initialization ====================

    useEffect(() => {
        // Wait for Firebase Auth to initialize before loading data
        const unsubscribe = auth.onAuthStateChanged(async (firebaseUser) => {
            if (firebaseUser) {
                console.log('Firebase user authenticated:', firebaseUser.email);

                // Firebase is ready, check if we have user info in localStorage
                const userInfoStr = localStorage.getItem('userInfo');

                if (userInfoStr) {
                    // User is logged in, load all app data
                    await Promise.all([
                        loadCurrentUser(),
                        loadSessions()
                    ]);
                } else {
                    // Firebase user exists but no localStorage (edge case)
                    // Try to get user info from backend
                    await loadCurrentUser();

                    // If successful, load sessions and enums
                    const userStillExists = localStorage.getItem('userInfo');
                    if (userStillExists) {
                        await Promise.all([
                            loadSessions(),
                        ]);
                    }
                }
            } else {
                console.log(' No Firebase user, clearing app state');

                // No Firebase user, clear everything
                setState(prev => ({
                    ...prev,
                    currentUser: null,
                    currentSession: null,
                    allSessions: [],
                    loading: {
                        sessions: false,
                        auth: false
                    }
                }));
            }
        });

        // Cleanup subscription on unmount
        return () => unsubscribe();
    }, []);


    // ==================== Session Management ====================

    const loadSessions = async () => {
        try {

            setState(prev => ({
                ...prev,
                loading: { ...prev.loading, sessions: true },
                errors: { ...prev.errors, sessions: undefined }
            }));

            // Get current session and all sessions from backend
            const [currentRes, allRes] = await Promise.all([
                sessionApi.getCurrent(),
                sessionApi.getAll()
            ]);


            setState(prev => ({
                ...prev,
                currentSession: currentRes.data,
                allSessions: allRes.data,
                loading: { ...prev.loading, sessions: false }
            }));
        } catch (err) {

            const errorMsg = err instanceof Error ? err.message : 'Failed to load sessions';
            console.error('Error loading sessions:', err);

            setState(prev => ({
                ...prev,
                loading: { ...prev.loading, sessions: false },
                errors: { ...prev.errors, sessions: errorMsg }
            }));
        }
    };

    const refreshSessions = useCallback(async () => {
        await loadSessions();
    }, []);



    // ==================== Auth Management ====================

    const loadCurrentUser = async () => {
        try {
            setState(prev => ({
                ...prev,
                loading: { ...prev.loading, auth: true },
                errors: { ...prev.errors, auth: undefined }
            }));

            // Check localStorage for existing session
            const userInfoStr = localStorage.getItem('userInfo');

            if (userInfoStr) {
                const userInfo = JSON.parse(userInfoStr);

                const user: CurrentUser = {
                    id: userInfo.userId,
                    email: userInfo.email,
                    role: userInfo.role,
                    fullName: userInfo.fullName,
                    userNumber: userInfo.userNumber,
                    userAvatar: userInfo.userAvatar || '👤',
                    status: userInfo.status,
                    isSuperAdmin: userInfo.isSuperAdmin || false,
                    firebaseUid: userInfo.firebaseUid || 'unknown'
                };

                setState(prev => ({
                    ...prev,
                    currentUser: user,
                    loading: { ...prev.loading, auth: false }
                }));
            } else {
                setState(prev => ({
                    ...prev,
                    currentUser: null,
                    loading: { ...prev.loading, auth: false }
                }));
            }
        } catch (err) {
            const errorMsg = err instanceof Error ? err.message : 'Failed to load user';
            console.error('Error loading current user:', err);

            setState(prev => ({
                ...prev,
                loading: { ...prev.loading, auth: false },
                errors: { ...prev.errors, auth: errorMsg }
            }));
        }
    };

    const login = useCallback(async (email: string, password: string) => {
        try {
            setState(prev => ({
                ...prev,
                loading: { ...prev.loading, auth: true },
                errors: { ...prev.errors, auth: undefined }
            }));

            // Step 1: Sign in with Firebase
            const userCredential = await signInWithEmailAndPassword(auth, email, password);

            // Step 2: Get Firebase ID token
            const loginToken = await userCredential.user.getIdToken();

            // Step 3: Send to backend for verification
            const response = await authApi.login(loginToken);

            // Step 4: Store user info in localStorage
            localStorage.setItem('userInfo', JSON.stringify(response.data));

            // Step 5: Update context state
            const userInfo = response.data;
            const user: CurrentUser = {
                id: userInfo.userId,
                email: userInfo.email,
                role: userInfo.role,
                fullName: userInfo.fullName,
                userNumber: userInfo.userNumber,
                userAvatar: userInfo.userAvatar || '👤',
                status: userInfo.status,
                isSuperAdmin: userInfo.role === 'ADMIN', // TODO: Get from backend when isSuperAdmin field available
                firebaseUid: userCredential.user.uid
            };

            setState(prev => ({
                ...prev,
                currentUser: user,
                loading: { ...prev.loading, auth: false }
            }));

        } catch (err: any) {
            let errorMsg = 'Login failed. Please try again.';

            // Firebase error codes
            if (err.code === 'auth/invalid-credential' || err.code === 'auth/wrong-password') {
                errorMsg = 'Invalid email or password';
            } else if (err.code === 'auth/user-not-found') {
                errorMsg = 'No account found with this email';
            } else if (err.code === 'auth/too-many-requests') {
                errorMsg = 'Too many failed attempts. Please try again later.';
            } else if (err.code === 'auth/user-disabled') {
                errorMsg = 'This account has been disabled. Please contact administrator.';
            } else if (err.response?.data?.message) {
                // Backend error message
                errorMsg = err.response.data.message;
            }

            setState(prev => ({
                ...prev,
                loading: { ...prev.loading, auth: false },
                errors: { ...prev.errors, auth: errorMsg }
            }));

            throw new Error(errorMsg);
        }
    }, []);

    const logout = useCallback(async () => {
        try {
            // Step 1: Sign out from Firebase
            await signOut(auth);

            // Step 2: Clear localStorage
            localStorage.removeItem('userInfo');

            // Step 3: Clear context state
            setState(prev => ({
                ...prev,
                currentUser: null,
                errors: { ...prev.errors, auth: undefined }
            }));

            console.log('User logged out successfully');

        } catch (err) {
            console.error('Logout error:', err);

            // Even if Firebase logout fails, clear local state
            localStorage.removeItem('userInfo');
            setState(prev => ({ ...prev, currentUser: null }));

            throw err;
        }
    }, []);

    // ==================== Utility ====================

    const clearError = useCallback((key: keyof ErrorState) => {
        setState(prev => ({
            ...prev,
            errors: { ...prev.errors, [key]: undefined }
        }));
    }, []);

    // ==================== Context Value ====================

    const value: AppContextType = {
        // State
        ...state,

        // Actions
        refreshSessions,
        login,
        logout,
        clearError
    };

    return (
        <AppContext.Provider value={value}>
            {children}
        </AppContext.Provider>
    );
};

// ==================== Custom Hooks ====================

/**
 * Main hook to access app context
 */
export const useApp = (): AppContextType => {
    const context = useContext(AppContext);
    if (!context) {
        throw new Error('useApp must be used within AppProvider');
    }
    return context;
};

/**
 * Hook for session-related data
 */
export const useSession = () => {
    const { currentSession, allSessions, refreshSessions, loading, errors } = useApp();

    return {
        currentSession,
        allSessions,
        refreshSessions,
        loading: loading.sessions,
        error: errors.sessions
    };
};

/**
 * Hook for authentication state
 */
export const useAuth = () => {
    const { currentUser, login, logout, loading, errors, clearError } = useApp();

    return {
        currentUser,
        login,
        logout,
        loading: loading.auth,
        error: errors.auth,
        clearError: () => clearError('auth'),

        // Convenience flags
        isAuthenticated: !!currentUser,
        isSuperAdmin: currentUser?.isSuperAdmin || false,
        isAdmin: currentUser?.role === 'ADMIN',
        isInstructor: currentUser?.role === 'INSTRUCTOR',
        isStudent: currentUser?.role === 'STUDENT'
    };
};