import React, { useState, useEffect } from 'react';
import { ArrowLeft, Edit2, Eye, EyeOff, Save, X, Clock, Award } from 'lucide-react';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { DataTable, type ColumnDef } from '../components/common/DataTable';
import { COLORS } from '../constants/colors';
import { assignmentApi, submissionApi } from '../services/api';
import { useAuth } from '../contexts/AppContext';
import type { AssignmentRes, SubmissionRes } from '../types';
import { ASSIGNMENT_TYPE, SUBMISSION_STATUS } from '../types';

interface AssignmentPageProps {
    mode?: 'view' | 'create';
    assignmentId?: string;
    sectionId?: string;
    onBack: () => void;
}

export const AssignmentPage: React.FC<AssignmentPageProps> = ({
                                                                  mode = 'view',
                                                                  assignmentId,
                                                                  sectionId,
                                                                  onBack
                                                              }) => {
    const { currentUser, isInstructor, isStudent } = useAuth();

    // Data
    const [assignment, setAssignment] = useState<AssignmentRes | null>(null);
    const [mySubmission, setMySubmission] = useState<SubmissionRes | null>(null);
    const [allSubmissions, setAllSubmissions] = useState<SubmissionRes[]>([]);
    const [loading, setLoading] = useState(true);

    // View State
    const [viewMode, setViewMode] = useState<'assignment' | 'submission-detail'>('assignment');
    const [selectedSubmissionId, setSelectedSubmissionId] = useState<string | null>(null);
    const [activeTab, setActiveTab] = useState<'details' | 'submissions'>('details');

    // Edit States
    const [isEditing, setIsEditing] = useState(mode === 'create');
    const [editTitle, setEditTitle] = useState('');
    const [editContent, setEditContent] = useState('');
    const [editType, setEditType] = useState<'ASSIGNMENT' | 'QUIZ' | 'DISCUSSION' | 'NOT_GRADED'>('ASSIGNMENT');
    const [editPoints, setEditPoints] = useState('100');
    const [editDueDate, setEditDueDate] = useState('');
    const [editMaxAttempts, setEditMaxAttempts] = useState('3');

    // Submit states
    const [showSubmitForm, setShowSubmitForm] = useState(false);
    const [submissionContent, setSubmissionContent] = useState('');
    const [submitting, setSubmitting] = useState(false);

    // Grading States (inline in table)
    const [grades, setGrades] = useState<Record<string, string>>({});
    const [feedbacks, setFeedbacks] = useState<Record<string, string>>({});

    useEffect(() => {
        if (mode === 'view' && assignmentId) {
            loadData();
        } else if (mode === 'create') {
            setLoading(false);
        }
    }, [assignmentId, mode]);

    const loadData = async () => {
        if (!assignmentId) return;

        try {
            setLoading(true);

            // Load assignment
            const assignmentRes = await assignmentApi.getById(assignmentId);
            setAssignment(assignmentRes.data);

            // Student: load my submission
            if (isStudent && currentUser) {
                try {
                    const submissionRes = await submissionApi.getByStudent(currentUser.id);
                    const mySubmit = submissionRes.data.find((s: SubmissionRes) => s.assignmentId === assignmentId);
                    if (mySubmit) setMySubmission(mySubmit);
                } catch (err) {
                    console.log('No submission');
                }
            }

            // Instructor: load all submissions
            if (isInstructor) {
                const submissionsRes = await submissionApi.getByAssignment(assignmentId);
                setAllSubmissions(submissionsRes.data);

                // Initialize grading inputs
                const gradesInit: Record<string, string> = {};
                const feedbacksInit: Record<string, string> = {};
                submissionsRes.data.forEach((s: SubmissionRes) => {
                    gradesInit[s.studentId] = s.grade !== null ? s.grade.toString() : '';
                    feedbacksInit[s.studentId] = s.feedback || '';
                });
                setGrades(gradesInit);
                setFeedbacks(feedbacksInit);
            }
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            console.error('Error loading:', err);
            alert('Failed to load assignment: ' + errorMsg);
        } finally {
            setLoading(false);
        }
    };

    const handleSave = async () => {
        if (!editTitle.trim()) {
            alert('Title is required');
            return;
        }

        try {
            const data = {
                title: editTitle,
                content: editContent,
                assignmentType: editType,
                submissionType: 'ONLINE_TEXT' as const,
                totalPoints: parseFloat(editPoints),
                dueDate: editDueDate ? new Date(editDueDate).toISOString() : null,
                maxAttempts: parseInt(editMaxAttempts),
                isPublished: false
            };

            if (mode === 'create') {
                await assignmentApi.create({ ...data, courseSectionId: sectionId! });
                alert('Created successfully');
                onBack();
            } else {
                await assignmentApi.update(assignment!.id, data);
                setIsEditing(false);
                await loadData();
                alert('Updated successfully');
            }
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to save: ' + errorMsg);
        }
    };

    const handleTogglePublish = async () => {
        if (!assignment) return;

        try {
            if (assignment.isPublished) {
                await assignmentApi.unpublish(assignment.id);
            } else {
                await assignmentApi.publish(assignment.id);
            }
            await loadData();
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to update: ' + errorMsg);
        }
    };

    const handleSaveGrade = async (studentId: string) => {
        if (!assignment) return;

        const grade = parseFloat(grades[studentId]);
        if (isNaN(grade) || grade < 0 || grade > assignment.totalPoints) {
            alert(`Grade must be 0-${assignment.totalPoints}`);
            return;
        }

        try {
            await submissionApi.grade({
                assignmentId: assignment.id,
                studentId: studentId,
                grade: grade,
                feedback: feedbacks[studentId] || ''
            });
            alert('Grade saved');
            await loadData();
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            alert('Failed to save grade: ' + errorMsg);
        }
    };

    const handleEdit = () => {
        if (!assignment) return;
        setEditTitle(assignment.title);
        setEditContent(assignment.content || '');
        setEditType(assignment.assignmentType);
        setEditPoints(assignment.totalPoints.toString());
        setEditDueDate(assignment.dueDate ? assignment.dueDate.split('T')[0] : '');
        setEditMaxAttempts(assignment.maxAttempts?.toString() || '3');
        setIsEditing(true);
    };

    const handleStudentSubmit = async () => {
        if (!assignment || !currentUser) return;

        if (!submissionContent.trim()) {
            alert('Please enter your submission');
            return;
        }

        try {
            setSubmitting(true);

            await submissionApi.create({
                assignmentId: assignment.id,
                content: submissionContent
            });

            alert('Submission successful!');
            setShowSubmitForm(false);
            setSubmissionContent('');
            await loadData();
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
            console.error('Submission error:', err);
            alert('Failed to submit: ' + errorMsg);
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) {
        return (
            <div className="space-y-6">
                <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>Back</Button>
                <div className="text-center py-12">Loading...</div>
            </div>
        );
    }

    // Submission Detail View
    if (viewMode === 'submission-detail' && selectedSubmissionId) {
        return (
            <SubmissionDetailView
                submissionId={selectedSubmissionId}
                onBack={() => {
                    setViewMode('assignment');
                    setSelectedSubmissionId(null);
                    loadData();
                }}
            />
        );
    }

    // Submissions table columns
    const submissionColumns: ColumnDef[] = [
        {
            key: 'studentName',
            header: 'Student',
            render: (sub) => (
                <div>
                    <div className="font-medium text-sm">{sub.studentName}</div>
                    <div className="text-xs opacity-60">{sub.studentEmail}</div>
                </div>
            )
        },
        {
            key: 'status',
            header: 'Status',
            render: (sub) => (
                <Badge className={SUBMISSION_STATUS[sub.status as keyof typeof SUBMISSION_STATUS]?.color}>
                    {SUBMISSION_STATUS[sub.status as keyof typeof SUBMISSION_STATUS]?.label}
                </Badge>
            )
        },
        {
            key: 'grade',
            header: 'Grade',
            render: (sub) => (
                <div className="flex items-center gap-1">
                    <input
                        type="number"
                        value={grades[sub.studentId] || ''}
                        onChange={(e) => setGrades({ ...grades, [sub.studentId]: e.target.value })}
                        className="w-20 px-2 py-1 border rounded text-sm text-center"
                        style={{ borderColor: COLORS.bg }}
                        max={assignment?.totalPoints}
                        min="0"
                    />
                    <span className="text-sm opacity-60">/ {assignment?.totalPoints}</span>
                </div>
            )
        },
        {
            key: 'feedback',
            header: 'Feedback',
            render: (sub) => (
                <input
                    type="text"
                    value={feedbacks[sub.studentId] || ''}
                    onChange={(e) => setFeedbacks({ ...feedbacks, [sub.studentId]: e.target.value })}
                    className="w-full px-2 py-1 border rounded text-sm"
                    style={{ borderColor: COLORS.bg }}
                    placeholder="Feedback..."
                />
            )
        },
        {
            key: 'actions',
            header: 'Actions',
            render: (sub) => (
                <div className="flex gap-2">
                    {sub.status !== 'MISSING' && (
                        <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => {
                                setSelectedSubmissionId(sub.id);
                                setViewMode('submission-detail');
                            }}
                        >
                            View
                        </Button>
                    )}
                    <Button
                        variant="primary"
                        size="sm"
                        onClick={() => handleSaveGrade(sub.studentId)}
                    >
                        Save
                    </Button>
                </div>
            )
        }
    ];

    return (
        <div className="space-y-6 pb-8">
            {/* Header */}
            <div className="flex justify-between items-center">
                <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>
                    Back to Course
                </Button>

                {isInstructor && !isEditing && (
                    <div className="flex gap-2">
                        <Button variant="secondary" size="sm" icon={<Edit2 size={16} />} onClick={handleEdit}>
                            Edit
                        </Button>
                        <button
                            onClick={handleTogglePublish}
                            className="px-3 py-1.5 rounded-lg flex items-center gap-2 text-sm font-medium"
                            style={{
                                backgroundColor: assignment?.isPublished ? COLORS.orange : COLORS.bg,
                                color: assignment?.isPublished ? 'white' : COLORS.dark
                            }}
                        >
                            {assignment?.isPublished ? <Eye size={16} /> : <EyeOff size={16} />}
                            {assignment?.isPublished ? 'Published' : 'Unpublished'}
                        </button>
                    </div>
                )}

                {isEditing && (
                    <div className="flex gap-2">
                        <Button variant="primary" size="sm" icon={<Save size={16} />} onClick={handleSave}>
                            {mode === 'create' ? 'Create' : 'Save'}
                        </Button>
                        <Button variant="ghost" size="sm" icon={<X size={16} />} onClick={() => mode === 'create' ? onBack() : setIsEditing(false)}>
                            Cancel
                        </Button>
                    </div>
                )}
            </div>

            <div className="bg-white rounded-2xl shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                {/* Header */}
                {!isEditing && (
                    <div className="p-6 border-b" style={{ borderColor: COLORS.bg }}>
                        <h1 className="text-3xl font-bold mb-3" style={{ color: COLORS.dark }}>
                            {assignment?.title}
                        </h1>
                        <div className="flex items-center gap-4 flex-wrap">
                            {assignment && (
                                <Badge className={ASSIGNMENT_TYPE[assignment.assignmentType]?.color}>
                                    {ASSIGNMENT_TYPE[assignment.assignmentType]?.label}
                                </Badge>
                            )}
                            {assignment?.isOverdue && (
                                <span className="px-3 py-1 bg-red-100 text-red-700 rounded-full text-sm font-medium">
                                    Overdue
                                </span>
                            )}
                            <div className="flex items-center gap-2 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                <Clock size={16} />
                                <span>Due: {assignment?.dueDate ? new Date(assignment.dueDate).toLocaleDateString() : 'No due date'}</span>
                            </div>
                            <div className="flex items-center gap-2 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                <Award size={16} />
                                <span>{assignment?.totalPoints} points</span>
                            </div>
                            <div className="flex items-center gap-2 text-sm" style={{ color: COLORS.dark, opacity: 0.7 }}>
                                <span>Max Attempts: {assignment?.maxAttempts || 3}</span>
                            </div>
                        </div>
                    </div>
                )}

                {/* Tabs (Instructor only) */}
                {isInstructor && !isEditing && (
                    <div className="flex border-b" style={{ borderColor: COLORS.bg }}>
                        <button
                            onClick={() => setActiveTab('details')}
                            className="px-6 py-4 text-sm font-medium"
                            style={{
                                color: activeTab === 'details' ? COLORS.orange : COLORS.dark + 'AA',
                                borderBottom: activeTab === 'details' ? `2px solid ${COLORS.orange}` : 'none'
                            }}
                        >
                            Details
                        </button>
                        <button
                            onClick={() => setActiveTab('submissions')}
                            className="px-6 py-4 text-sm font-medium"
                            style={{
                                color: activeTab === 'submissions' ? COLORS.orange : COLORS.dark + 'AA',
                                borderBottom: activeTab === 'submissions' ? `2px solid ${COLORS.orange}` : 'none'
                            }}
                        >
                            Submissions ({allSubmissions.length})
                        </button>
                    </div>
                )}

                <div className="p-6">
                    {isEditing ? (
                        // Edit Form
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium mb-2">Title *</label>
                                <input
                                    type="text"
                                    value={editTitle}
                                    onChange={(e) => setEditTitle(e.target.value)}
                                    className="w-full px-4 py-2 border rounded-lg"
                                    style={{ borderColor: COLORS.bg }}
                                />
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium mb-2">Type</label>
                                    <select
                                        value={editType}
                                        onChange={(e) => setEditType(e.target.value as any)}
                                        className="w-full px-4 py-2 border rounded-lg"
                                        style={{ borderColor: COLORS.bg }}
                                    >
                                        <option value="ASSIGNMENT">Assignment</option>
                                        <option value="QUIZ">Quiz</option>
                                        <option value="DISCUSSION">Discussion</option>
                                        <option value="NOT_GRADED">Not Graded</option>
                                    </select>
                                </div>

                                <div>
                                    <label className="block text-sm font-medium mb-2">Points *</label>
                                    <input
                                        type="number"
                                        value={editPoints}
                                        onChange={(e) => setEditPoints(e.target.value)}
                                        className="w-full px-4 py-2 border rounded-lg"
                                        style={{ borderColor: COLORS.bg }}
                                        min="0"
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-2">Due Date</label>
                                <input
                                    type="date"
                                    value={editDueDate}
                                    onChange={(e) => setEditDueDate(e.target.value)}
                                    className="w-full px-4 py-2 border rounded-lg"
                                    style={{ borderColor: COLORS.bg }}
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                    Max Attempts
                                </label>
                                <input
                                    type="number"
                                    value={editMaxAttempts}
                                    onChange={(e) => setEditMaxAttempts(e.target.value)}
                                    className="w-full px-4 py-2 border rounded-lg text-sm"
                                    style={{ borderColor: COLORS.bg }}
                                    min="1"
                                    max="10"
                                />
                                <p className="text-xs mt-1 opacity-60">
                                    Number of times students can submit (default: 3)
                                </p>
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-2">Instructions</label>
                                <textarea
                                    value={editContent}
                                    onChange={(e) => setEditContent(e.target.value)}
                                    className="w-full px-4 py-3 border rounded-lg resize-none"
                                    style={{ borderColor: COLORS.bg }}
                                    rows={12}
                                />
                            </div>
                        </div>
                    ) : (
                        <>
                            {/* Details Tab */}
                            {activeTab === 'details' && (
                                <div>
                                    <h3 className="font-semibold text-lg mb-3">Instructions</h3>
                                    <div
                                        className="prose max-w-none mb-6"
                                        dangerouslySetInnerHTML={{ __html: assignment?.content || '<p>No instructions</p>' }}
                                    />

                                    {/* Student Submission Section */}
                                    {isStudent && (
                                        <div className="p-4 rounded-xl" style={{ backgroundColor: COLORS.cream }}>
                                            <h3 className="font-semibold text-lg mb-3" style={{ color: COLORS.dark }}>
                                                My Submission
                                            </h3>

                                            {mySubmission ? (
                                                // Has submission
                                                <div>
                                                    <p className="text-sm mb-2">
                                                        Status: <Badge className={SUBMISSION_STATUS[mySubmission.status as keyof typeof SUBMISSION_STATUS]?.color}>
                                                        {SUBMISSION_STATUS[mySubmission.status as keyof typeof SUBMISSION_STATUS]?.label}
                                                    </Badge>
                                                    </p>

                                                    {mySubmission.content && !showSubmitForm && (
                                                        <div className="p-3 bg-white rounded-lg mb-3">
                                                            <p className="text-sm" style={{ whiteSpace: 'pre-wrap' }}>
                                                                {mySubmission.content}
                                                            </p>
                                                        </div>
                                                    )}

                                                    {mySubmission.grade !== null && (
                                                        <div className="text-2xl font-bold mb-3" style={{ color: COLORS.orange }}>
                                                            {mySubmission.grade} / {assignment?.totalPoints}
                                                        </div>
                                                    )}

                                                    {mySubmission.feedback && (
                                                        <div className="p-3 bg-white rounded-lg mb-3">
                                                            <p className="text-sm">{mySubmission.feedback}</p>
                                                        </div>
                                                    )}

                                                    {/* Resubmit */}
                                                    {showSubmitForm ? (
                                                        <div className="space-y-3">
                                                            <textarea
                                                                value={submissionContent}
                                                                onChange={(e) => setSubmissionContent(e.target.value)}
                                                                className="w-full px-4 py-3 border rounded-lg resize-none"
                                                                style={{ borderColor: COLORS.bg }}
                                                                rows={8}
                                                                disabled={submitting}
                                                            />
                                                            <div className="flex gap-2">
                                                                <Button variant="primary" onClick={handleStudentSubmit} disabled={submitting}>
                                                                    {submitting ? 'Submitting...' : 'Submit'}
                                                                </Button>
                                                                <Button variant="ghost" onClick={() => setShowSubmitForm(false)} disabled={submitting}>
                                                                    Cancel
                                                                </Button>
                                                            </div>
                                                        </div>
                                                    ) : (
                                                        mySubmission.status !== 'GRADED' &&
                                                        assignment?.acceptsSubmissions &&
                                                        mySubmission.attemptNumber < (assignment?.maxAttempts || 3) && (
                                                            <Button variant="primary" size="sm" onClick={() => setShowSubmitForm(true)}>
                                                                Resubmit
                                                            </Button>
                                                        )
                                                    )}
                                                </div>
                                            ) : (
                                                // No submission
                                                <div>
                                                    {showSubmitForm ? (
                                                        <div className="space-y-3">
                                                            <textarea
                                                                value={submissionContent}
                                                                onChange={(e) => setSubmissionContent(e.target.value)}
                                                                className="w-full px-4 py-3 border rounded-lg resize-none"
                                                                style={{ borderColor: COLORS.bg }}
                                                                rows={8}
                                                                disabled={submitting}
                                                            />
                                                            <div className="flex gap-2">
                                                                <Button variant="primary" onClick={handleStudentSubmit} disabled={submitting}>
                                                                    Submit
                                                                </Button>
                                                                <Button variant="ghost" onClick={() => setShowSubmitForm(false)}>
                                                                    Cancel
                                                                </Button>
                                                            </div>
                                                        </div>
                                                    ) : (
                                                        <div className="text-center py-6">
                                                            <p className="text-sm mb-4 opacity-60">No submission yet</p>
                                                            <Button variant="primary" onClick={() => setShowSubmitForm(true)}>
                                                                Submit Assignment
                                                            </Button>
                                                        </div>
                                                    )}
                                                </div>
                                            )}
                                        </div>
                                    )}
                                </div>
                            )}

                            {/* Submissions Tab - Grading Table */}
                            {activeTab === 'submissions' && isInstructor && (
                                <DataTable
                                    columns={submissionColumns}
                                    data={allSubmissions}
                                    emptyMessage="No submissions"
                                />
                            )}
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

// Submission Detail - Simple
interface SubmissionDetailViewProps {
    submissionId: string;
    onBack: () => void;
}

const SubmissionDetailView: React.FC<SubmissionDetailViewProps> = ({ submissionId, onBack }) => {
    const [submission, setSubmission] = useState<SubmissionRes | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const load = async () => {
            try {
                const res = await submissionApi.getById(submissionId);
                setSubmission(res.data);
            } catch (err: any) {
                const errorMsg = err.response?.data?.message || err.message || 'Unknown error';
                alert('Failed to load submission: ' + errorMsg);
            } finally {
                setLoading(false);
            }
        };
        load();
    }, [submissionId]);

    if (loading) return <div className="text-center py-12">Loading...</div>;
    if (!submission) return <div className="text-center py-12 text-red-600">Not found</div>;

    return (
        <div className="space-y-6 pb-8">
            <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>
                Back to Submissions
            </Button>

            <div className="bg-white rounded-2xl shadow-sm p-6" style={{ border: `1px solid ${COLORS.bg}` }}>
                {/* Header */}
                <div className="mb-6 pb-6 border-b" style={{ borderColor: COLORS.bg }}>
                    <h2 className="text-2xl font-bold mb-2">{submission.studentName}'s Submission</h2>
                    <div className="flex items-center gap-4 text-sm opacity-70">
                        <span>{submission.studentEmail}</span>
                        <span>•</span>
                        <span>Submitted: {submission.submittedAt ? new Date(submission.submittedAt).toLocaleString() : 'Not submitted'}</span>
                    </div>
                </div>

                {/* Content */}
                <div className="mb-6">
                    <h3 className="font-semibold mb-3">Submission Content</h3>
                    <div className="p-4 rounded-xl" style={{ backgroundColor: COLORS.bg + '40' }}>
                        {submission.content ? (
                            <p style={{ whiteSpace: 'pre-wrap' }}>{submission.content}</p>
                        ) : (
                            <p className="text-center opacity-50">No content</p>
                        )}
                    </div>
                </div>

                {/* Grade */}
                {submission.grade !== null && (
                    <div className="p-4 rounded-xl" style={{ backgroundColor: COLORS.cream }}>
                        <div className="flex items-center justify-between mb-3">
                            <span className="font-medium">Grade</span>
                            <div className="text-3xl font-bold" style={{ color: COLORS.orange }}>
                                {submission.grade} / {submission.totalPoints}
                            </div>
                        </div>
                        {submission.feedback && (
                            <div>
                                <p className="text-xs font-semibold mb-1 opacity-60">Feedback</p>
                                <p className="text-sm">{submission.feedback}</p>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};