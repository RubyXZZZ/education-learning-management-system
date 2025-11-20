import React, { useState, useEffect } from 'react';
import { ArrowLeft, Edit2, Eye, EyeOff, Save, X } from 'lucide-react';
import { Button } from '../components/common/Button';
import { COLORS } from '../constants/colors';
import { coursePageApi, moduleApi } from '../services/api';
import { useAuth } from '../contexts/AppContext';
import type { CoursePageRes, ModuleRes } from '../types';

interface CoursePageProps {
    mode?: 'view' | 'create';
    pageId?: string;
    moduleId?: string;
    sectionId?: string;
    onBack: () => void;
}

export const CoursePage: React.FC<CoursePageProps> = ({
                                                          mode = 'view',
                                                          pageId,
                                                          moduleId,
                                                          sectionId,
                                                          onBack
                                                      }) => {
    const { isInstructor } = useAuth();

    const [page, setPage] = useState<CoursePageRes | null>(null);
    const [modules, setModules] = useState<ModuleRes[]>([]);
    const [loading, setLoading] = useState(true);

    // Edit mode
    const [isEditing, setIsEditing] = useState(false);
    const [editTitle, setEditTitle] = useState('');
    const [editBody, setEditBody] = useState('');
    const [editModuleId, setEditModuleId] = useState('');

    useEffect(() => {
        if (mode === 'view' && pageId) {
            loadPage();
        } else if (mode === 'create' && sectionId) {
            loadModulesForCreate();
        }
    }, [pageId, mode, sectionId]);

    const loadPage = async () => {
        if (!pageId) return;

        try {
            setLoading(true);

            const pageRes = await coursePageApi.getById(pageId);
            setPage(pageRes.data);

            // Load modules for dropdown (if instructor)
            if (isInstructor) {
                const modulesRes = await moduleApi.getBySection(pageRes.data.courseSectionId);
                setModules(modulesRes.data);
            }
        } catch (err) {
            console.error('Error loading page:', err);
            alert('Failed to load page: ' + (err instanceof Error ? err.message : ''));
        } finally {
            setLoading(false);
        }
    };

    const loadModulesForCreate = async () => {
        if (!sectionId) return;

        try {
            setLoading(true);
            const modulesRes = await moduleApi.getBySection(sectionId);
            setModules(modulesRes.data);

            // Pre-fill moduleId if provided
            if (moduleId) {
                setEditModuleId(moduleId);
            }

            setIsEditing(true);  // Create mode starts in edit mode
        } catch (err) {
            console.error('Error loading modules:', err);
            alert('Failed to load modules: ' + (err instanceof Error ? err.message : ''));
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = () => {
        if (!page) return;

        setEditTitle(page.title);
        setEditBody(page.body || '');
        setEditModuleId(page.moduleId || '');
        setIsEditing(true);
    };

    const handleSave = async () => {
        if (!editTitle.trim()) {
            alert('Title is required');
            return;
        }

        if (!editModuleId) {
            alert('Please select a module');
            return;
        }

        try {
            if (mode === 'create') {
                // Create new page
                await coursePageApi.create({
                    courseSectionId: sectionId!,
                    moduleId: editModuleId,
                    title: editTitle,
                    body: editBody,
                    isPublished: false
                });
                alert('Page created successfully');
                onBack();
            } else {
                // Update existing page
                await coursePageApi.update(page!.id, {
                    title: editTitle,
                    body: editBody,
                    moduleId: editModuleId
                });
                setIsEditing(false);
                await loadPage();
                alert('Page updated successfully');
            }
        } catch (err) {
            alert(`Failed to ${mode === 'create' ? 'create' : 'update'} page: ` + (err instanceof Error ? err.message : ''));
        }
    };

    const handleCancelEdit = () => {
        if (mode === 'create') {
            onBack();  // Cancel create = go back
        } else {
            setIsEditing(false);
            setEditTitle('');
            setEditBody('');
            setEditModuleId('');
        }
    };

    const handleTogglePublish = async () => {
        if (!page) return;

        try {
            if (page.isPublished) {
                await coursePageApi.unpublish(page.id);
            } else {
                await coursePageApi.publish(page.id);
            }
            await loadPage();
        } catch (err) {
            alert('Failed to update page: ' + (err instanceof Error ? err.message : ''));
        }
    };

    if (loading) {
        return (
            <div className="space-y-6">
                <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>
                    Back to Course
                </Button>
                <div className="text-center py-12">
                    <div className="text-lg" style={{ color: COLORS.dark }}>Loading...</div>
                </div>
            </div>
        );
    }

    if (mode === 'view' && !page) {
        return (
            <div className="space-y-6">
                <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>
                    Back to Course
                </Button>
                <div className="text-center py-12">
                    <div className="text-lg text-red-600">Page not found</div>
                </div>
            </div>
        );
    }

    return (
        <div className="space-y-6 pb-8">
            {/* Header */}
            <div className="flex justify-between items-center">
                <Button variant="ghost" onClick={onBack} icon={<ArrowLeft size={20} />}>
                    Back to Course
                </Button>

                {isInstructor && !isEditing && mode === 'view' && (
                    <div className="flex gap-2">
                        <Button variant="secondary" size="sm" icon={<Edit2 size={16} />} onClick={handleEdit}>
                            Edit
                        </Button>
                        <button
                            onClick={handleTogglePublish}
                            className="px-3 py-1.5 rounded-lg flex items-center gap-2 text-sm font-medium transition-colors"
                            style={{
                                backgroundColor: page?.isPublished ? COLORS.orange : COLORS.bg,
                                color: page?.isPublished ? 'white' : COLORS.dark
                            }}
                        >
                            {page?.isPublished ? <Eye size={16} /> : <EyeOff size={16} />}
                            <span>{page?.isPublished ? 'Published' : 'Unpublished'}</span>
                        </button>
                    </div>
                )}

                {isEditing && (
                    <div className="flex gap-2">
                        <Button variant="primary" size="sm" icon={<Save size={16} />} onClick={handleSave}>
                            {mode === 'create' ? 'Create' : 'Save'}
                        </Button>
                        <Button variant="ghost" size="sm" icon={<X size={16} />} onClick={handleCancelEdit}>
                            Cancel
                        </Button>
                    </div>
                )}
            </div>

            {/* Page Content */}
            <div className="bg-white rounded-2xl shadow-sm" style={{ border: `1px solid ${COLORS.bg}` }}>
                <div className="p-6">
                    {isEditing ? (
                        // Edit/Create Mode
                        <div className="space-y-4">
                            {/* Title Input */}
                            <div>
                                <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                    Title *
                                </label>
                                <input
                                    type="text"
                                    value={editTitle}
                                    onChange={(e) => setEditTitle(e.target.value)}
                                    className="w-full px-4 py-2 border rounded-lg text-base font-semibold"
                                    style={{ borderColor: COLORS.bg, backgroundColor: 'white' }}
                                    placeholder="Page title"
                                />
                            </div>

                            {/* Module Selection */}
                            <div>
                                <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                    Module *
                                </label>
                                <select
                                    value={editModuleId}
                                    onChange={(e) => setEditModuleId(e.target.value)}
                                    className="w-full px-4 py-2 border rounded-lg text-sm"
                                    style={{ borderColor: COLORS.bg, backgroundColor: 'white' }}
                                    required
                                >
                                    <option value="">Select Module</option>
                                    {modules.map(m => (
                                        <option key={m.id} value={m.id}>{m.name}</option>
                                    ))}
                                </select>
                            </div>

                            {/* Body Editor */}
                            <div>
                                <label className="block text-sm font-medium mb-2" style={{ color: COLORS.dark }}>
                                    Content
                                </label>
                                <textarea
                                    value={editBody}
                                    onChange={(e) => setEditBody(e.target.value)}
                                    className="w-full px-4 py-3 border rounded-lg text-sm resize-none font-mono"
                                    style={{ borderColor: COLORS.bg, backgroundColor: 'white' }}
                                    placeholder="Page content (supports HTML)"
                                    rows={15}
                                />
                                <p className="text-xs mt-1" style={{ color: COLORS.dark, opacity: 0.5 }}>
                                    Tip: You can use HTML tags for formatting
                                </p>
                            </div>
                        </div>
                    ) : (
                        // View Mode
                        <div>
                            {/* Page Header */}
                            <div className="mb-6">
                                <h1 className="text-3xl font-bold mb-2" style={{ color: COLORS.dark }}>
                                    {page?.title}
                                </h1>
                                <div className="flex items-center gap-3 text-sm" style={{ color: COLORS.dark, opacity: 0.6 }}>
                                    <span>Module: {page?.moduleName || 'Unknown'}</span>
                                    {isInstructor && (
                                        <>
                                            <span>•</span>
                                            <span>{page?.isPublished ? 'Published' : 'Unpublished'}</span>
                                            <span>•</span>
                                            <span>Updated: {page?.updatedAt ? new Date(page.updatedAt).toLocaleDateString() : ''}</span>
                                        </>
                                    )}
                                </div>
                            </div>

                            {/* Page Body */}
                            <div
                                className="prose max-w-none"
                                style={{ color: COLORS.dark }}
                                dangerouslySetInnerHTML={{ __html: page?.body || '<p>No content</p>' }}
                            />
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};