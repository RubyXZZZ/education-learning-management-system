import React, { useState } from 'react';
import { sendPasswordResetEmail } from 'firebase/auth';
import { auth } from '../services/firebase';
import { useAuth } from '../contexts/AppContext';
import { COLORS } from '../constants/colors';
import { Button } from '../components/common/Button';


export const LoginPage: React.FC = () => {
    const { login, error: authError } = useAuth();

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [showForgotPassword, setShowForgotPassword] = useState(false);
    const [resetEmail, setResetEmail] = useState('');
    const [resetMessage, setResetMessage] = useState('');
    const [resetError, setResetError] = useState('');

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            await login(email, password);
        } catch (err) {
        } finally {
            setLoading(false);
        }
    };

    const handleForgotPassword = async (e: React.FormEvent) => {
        e.preventDefault();
        setResetMessage('');
        setResetError('');

        try {
            await sendPasswordResetEmail(auth, resetEmail);
            setResetMessage('Password reset email sent! Please check your inbox.');

            setTimeout(() => {
                setShowForgotPassword(false);
                setResetMessage('');
            }, 3000);

        } catch (err: any) {
            if (err.code === 'auth/user-not-found') {
                setResetError('No account found with this email');
            } else {
                setResetError('Failed to send reset email. Please try again.');
            }
        }
    };

    // Forgot Password Modal
    if (showForgotPassword) {
        return (
            <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: COLORS.bg }}>
                <div className="bg-white rounded-3xl p-8 shadow-xl max-w-md w-full mx-8" style={{ border: `1px solid ${COLORS.bg}` }}>
                    <div className="text-center mb-6">
                        <div className="text-4xl mb-3">🔒</div>
                        <h2 className="text-2xl font-bold mb-2" style={{ color: COLORS.dark }}>
                            Reset Password
                        </h2>
                        <p className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                            Enter your email to receive reset instructions
                        </p>
                    </div>

                    {resetMessage && (
                        <div className="bg-green-100 text-green-700 p-3 rounded-lg mb-4 text-sm">
                            {resetMessage}
                        </div>
                    )}

                    {resetError && (
                        <div className="bg-red-100 text-red-700 p-3 rounded-lg mb-4 text-sm">
                            {resetError}
                        </div>
                    )}

                    <form onSubmit={handleForgotPassword}>
                        <div className="mb-4">
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Email Address
                            </label>
                            <input
                                type="email"
                                value={resetEmail}
                                onChange={(e) => setResetEmail(e.target.value)}
                                className="w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2"
                                style={{ borderColor: COLORS.bg }}
                                placeholder="your@email.com"
                                required
                            />
                        </div>

                        <Button type="submit" variant="primary" className="w-full mb-3">
                            Send Reset Email
                        </Button>

                        <Button
                            type="button"
                            variant="secondary"
                            className="w-full"
                            onClick={() => {
                                setShowForgotPassword(false);
                                setResetError('');
                                setResetMessage('');
                            }}
                        >
                            Back to Login
                        </Button>
                    </form>
                </div>
            </div>
        );
    }

    // Main Login Page
    return (
        <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: COLORS.bg }}>
            <div className="max-w-md w-full mx-8">
                <div className="bg-white rounded-3xl p-8 shadow-xl" style={{ border: `1px solid ${COLORS.bg}` }}>
                    <div className="text-center mb-8">
                        <div className="text-5xl mb-4">🎓</div>
                        <h2 className="text-3xl font-bold mb-2" style={{ color: COLORS.dark }}>
                            Clara Language School
                        </h2>
                        <p className="text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                            Sign in to access your learning portal
                        </p>
                    </div>

                    {authError && (
                        <div className="bg-red-100 text-red-700 p-3 rounded-lg mb-4 text-sm">
                            {authError}
                        </div>
                    )}

                    <form onSubmit={handleLogin} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Email Address
                            </label>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2"
                                style={{ borderColor: COLORS.bg }}
                                placeholder="your@email.com"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                Password
                            </label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2"
                                style={{ borderColor: COLORS.bg }}
                                placeholder="Enter your password"
                                required
                            />
                        </div>

                        <Button
                            type="submit"
                            variant="primary"
                            disabled={loading}
                            className="w-full"
                        >
                            {loading ? 'Signing in...' : 'Sign In'}
                        </Button>

                        <div className="text-center">
                            <button
                                type="button"
                                onClick={() => setShowForgotPassword(true)}
                                className="text-sm hover:underline"
                                style={{ color: COLORS.orange }}
                            >
                                Forgot password?
                            </button>
                        </div>
                    </form>

                    <div className="mt-6 pt-6 text-center" style={{ borderTop: `1px solid ${COLORS.bg}` }}>
                        <p className="text-xs" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            New student? Please contact the school office for admission
                        </p>
                        <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.6 }}>
                            📞 (555) 123-4567 | 📧 admissions@school.com
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};