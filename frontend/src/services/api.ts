import axios from 'axios';
import { auth } from '../services/firebase';

// API base URL from environment variables
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// axios instance
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    timeout: 10000
});


// Request Interceptor - Add Firebase Token
api.interceptors.request.use(
    async (config) => {
        const user = auth.currentUser;

        if (user) {
            try {
                const token = await user.getIdToken();
                config.headers.Authorization = `Bearer ${token}`;
            } catch (error) {
                console.error('Failed to get Firebase token:', error);
            }
        }

        return config;
    },
    (error) => Promise.reject(error)
);


// Response Interceptor - Handle 401 and Refresh
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const user = auth.currentUser;

                if (user) {
                    const newToken = await user.getIdToken(true);
                    originalRequest.headers.Authorization = `Bearer ${newToken}`;
                    return api(originalRequest);
                }
            } catch (refreshError) {
                localStorage.removeItem('userInfo');
                window.location.href = '/';
                return Promise.reject(refreshError);
            }
        }

        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
    }
);

// Auth API
export const authApi = {
    login: (loginToken: string) => api.post('/auth/login', { loginToken }),
    getCurrentUser: () => api.get('/auth/me'),
    logout: () => api.post('/auth/logout'),
    health: () => api.get('/auth/health')
};

// Profile API
export const profileApi = {
    getMyProfile: () => api.get('/profile'),
    updateMyProfile: (data: any) => api.put('/profile', data),
};



// Student API
export const studentApi = {
    getAll: () => api.get('/students/all'),
    getList: () => api.get('/students'),
    getById: (id: string) => api.get(`/students/${id}`),
    getByStudentNumber: (studentNumber: string) =>
        api.get(`/students/by-student-number/${studentNumber}`),
    getByType: (studentType: string) => api.get(`/students/type/${studentType}`),
    getByStatus: (status: string) => api.get(`/students/status/${status}`),
    getByPlacementLevel: (placementLevel: number) => api.get(`/students/level/${placementLevel}`),
    getWithEnrollments: (id: string) => api.get(`/students/${id}/with-enrollments`),
    create: (data: any) => api.post('/students', data),
    update: (id: string, data: any) => api.put(`/students/${id}`, data),
    suspend: (id: string) => api.post(`/students/${id}/suspend`),
    reactivate: (id: string) => api.post(`/students/${id}/reactivate`),
    deactivate: (id: string) => api.post(`/students/${id}/deactivate`),
    delete: (id: string) => api.delete(`/students/${id}`),
    sendActivation: (id: string, email: string) => ({ id, email })
};

// Instructor API
export const instructorApi = {
    getAll: () => api.get('/instructors/all'),
    getList: () => api.get('/instructors'),
    getById: (id: string) => api.get(`/instructors/${id}`),
    getByStatus: (status: string) => api.get(`/instructors/status/${status}`),
    getByEmployeeNumber: (employeeNumber: string) =>
        api.get(`/instructors/by-employee/${employeeNumber}`),
    getByDepartment: (department: string) =>
        api.get(`/instructors/department/${department}`),
    getWithSections: (id: string) => api.get(`/instructors/${id}/with-sections`),
    create: (data: any) => api.post('/instructors', data),
    update: (id: string, data: any) => api.put(`/instructors/${id}`, data),
    suspend: (id: string) => api.post(`/instructors/${id}/suspend`),
    reactivate: (id: string) => api.post(`/instructors/${id}/reactivate`),
    deactivate: (id: string) => api.post(`/instructors/${id}/deactivate`),
    delete: (id: string) => api.delete(`/instructors/${id}`),
    sendActivation: (id: string, email: string) => ({ id, email })
};


// Admin API
export const adminApi = {
    getAll: () => api.get('/admins/all'),
    getList: () => api.get('/admins'),
    getById: (id: string) => api.get(`/admins/${id}`),
    getByEmployeeNumber: (employeeNumber: string) =>
        api.get(`/admins/by-employee/${employeeNumber}`),
    getByDepartment: (department: string) =>
        api.get(`/admins/department/${department}`),
    getByStatus: (status: string) => api.get(`/admins/status/${status}`),
    getSuperAdmins: () => api.get('/admins/super-admins'),
    create: (data: any) => api.post('/admins', data),
    update: (id: string, data: any) => api.put(`/admins/${id}`, data),
    promoteSuper: (id: string) => api.post(`/admins/${id}/promote-super`),
    demoteSuper: (id: string) => api.post(`/admins/${id}/demote-super`),
    suspend: (id: string) => api.post(`/admins/${id}/suspend`),
    reactivate: (id: string) => api.post(`/admins/${id}/reactivate`),
    deactivate: (id: string) => api.post(`/admins/${id}/deactivate`),
    delete: (id: string) => api.delete(`/admins/${id}`),
    sendActivation: (id: string, email: string) => ({ id, email })
};

// Course API
export const courseApi = {
    getAll: () => api.get('/courses/all'),
    getById: (id: string) => api.get(`/courses/${id}`),
    getByCourseCode: (courseCode: string) =>
        api.get(`/courses/by-code/${courseCode}`),
    getBySession: (sessionId: string) =>
        api.get(`/courses/session/${sessionId}`),
    getBySessionCode: (sessionCode: string) =>
        api.get(`/courses/session-code/${sessionCode}`),
    getActive: () => api.get('/courses/active'),
    create: (data: any) => api.post('/courses', data),
    update: (id: string, data: any) => api.put(`/courses/${id}`, data),
    activate: (id: string) => api.post(`/courses/${id}/activate`),
    deactivate: (id: string) => api.post(`/courses/${id}/deactivate`),
    delete: (id: string) => api.delete(`/courses/${id}`),
    addSection: (courseId: string, data: any) =>
        api.post(`/courses/${courseId}/sections`, data),
    removeSection: (courseId: string, sectionId: string) =>
        api.delete(`/courses/${courseId}/sections/${sectionId}`)
};

// Section API
export const sectionApi = {
    getAll: () => api.get('/sections/all'),
    getList: () => api.get('/sections'),
    getById: (id: string) => api.get(`/sections/${id}`),
    getByCourse: (courseId: string) => api.get(`/sections/course/${courseId}`),
    getBySession: (sessionId: string) => api.get(`/sections/session/${sessionId}`),
    getByInstructor: (instructorId: string) =>
        api.get(`/sections/instructor/${instructorId}`),
    getByInstructorAndSession: (instructorId: string, sessionId: string) =>
        api.get(`/sections/instructor/${instructorId}/session/${sessionId}`),
    getByStatus: (status: string) => api.get(`/sections/status/${status}`),
    getEnrollable: () => api.get('/sections/enrollable'),
    getFull: () => api.get('/sections/full'),
    getUnderfull: () => api.get('/sections/underfull'),
    create: (data: any) => api.post('/sections', data),
    update: (id: string, data: any) => api.put(`/sections/${id}`, data),
    publish: (id: string) => api.post(`/sections/${id}/publish`),
    // complete: (id: string) => api.post(`/sections/${id}/complete`),
    cancel: (id: string) => api.post(`/sections/${id}/cancel`),
    delete: (id: string) => api.delete(`/sections/${id}`),
    getMySections: () => api.get('/sections/me'),
};

// Enrollment API
export const enrollmentApi = {
    getAll: () => api.get('/enrollments/all'),
    getList: () => api.get('/enrollments'),
    getById: (id: string) => api.get(`/enrollments/${id}`),
    getByStudent: (studentId: string) =>
        api.get(`/enrollments/student/${studentId}`),
    getActiveByStudent: (studentId: string) =>
        api.get(`/enrollments/student/${studentId}/active`),
    getCompletedByStudent: (studentId: string) =>
        api.get(`/enrollments/student/${studentId}/completed`),
    getAllBySectionForAdmin: (sectionId: string) =>
        api.get(`/enrollments/section/${sectionId}/admin`),
    getBySection: (sectionId: string) =>
        api.get(`/enrollments/section/${sectionId}`),
    getByStudentAndSession: (studentNumber: string, sessionCode: string) =>
        api.get(`/enrollments/student/${studentNumber}/session/${sessionCode}`),
    getByStatus: (status: string) => api.get(`/enrollments/status/${status}`),
    enroll: (data: { studentId: string; courseSectionId: string }) =>
        api.post('/enrollments', data),
    // batchEnroll: (data: { studentId: string[]; courseSectionId: string }) =>
    //     api.post('/enrollments/batch', data),
    drop: (data: { enrollmentId: string; dropReason: string }) =>
        api.post('/enrollments/drop', data),
    complete: (data: { enrollmentId: string; finalGrade: number; letterGrade: string }) =>
        api.post('/enrollments/complete', data),
    getMyEnrollments: () => api.get('/enrollments/me'),
};

// Session API
export const sessionApi = {
    getAll: () => api.get('/sessions'),
    getById: (id: string) => api.get(`/sessions/${id}`),
    getByCode: (sessionCode: string) => api.get(`/sessions/by-code/${sessionCode}`),
    getCurrent: () => api.get('/sessions/current'),
    getByDateRange: (startDate: string, endDate: string) =>
        api.get('/sessions/dateRange', { params: { startDate, endDate } }),
    getOpenRegistration: () => api.get('/sessions/open-registration'),
    create: (data: any) => api.post('/sessions', data),
    update: (id: string, data: any) => api.put(`/sessions/${id}`, data)
};

// Module API
export const moduleApi = {
    getAll: () => api.get('/modules'),
    getById: (id: string) => api.get(`/modules/${id}`),
    getBySection: (sectionId: string) => api.get(`/modules/sections/${sectionId}`),
    getPublishedBySection: (sectionId: string) => api.get(`/modules/sections/${sectionId}/published`),
    create: (data: any) => api.post('/modules', data),
    update: (id: string, data: any) => api.put(`/modules/${id}`, data),
    publish: (id: string) => api.post(`/modules/${id}/publish`),
    unpublish: (id: string) => api.post(`/modules/${id}/unpublish`),
    delete: (id: string) => api.delete(`/modules/${id}`)
};

// CoursePage API
export const coursePageApi = {
    getById: (id: string) => api.get(`/pages/${id}`),
    getBySection: (sectionId: string) => api.get(`/pages/sections/${sectionId}`),
    getPublishedBySection: (sectionId: string) => api.get(`/pages/sections/${sectionId}/published`),
    create: (data: any) => api.post('/pages', data),
    update: (id: string, data: any) => api.put(`/pages/${id}`, data),
    publish: (id: string) => api.post(`/pages/${id}/publish`),
    unpublish: (id: string) => api.post(`/pages/${id}/unpublish`),
    delete: (id: string) => api.delete(`/pages/${id}`)
};

// Assignment API
export const assignmentApi = {
    getById: (id: string) => api.get(`/assignments/${id}`),
    getBySection: (sectionId: string) => api.get(`/assignments/sections/${sectionId}`),
    getPublishedBySection: (sectionId: string) => api.get(`/assignments/sections/${sectionId}/published`),
    getUpcoming: (sectionId: string) => api.get(`/assignments/sections/${sectionId}/upcoming`),
    getOverdue: (sectionId: string) => api.get(`/assignments/sections/${sectionId}/overdue`),
    create: (data: any) => api.post('/assignments', data),
    update: (id: string, data: any) => api.put(`/assignments/${id}`, data),
    publish: (id: string) => api.post(`/assignments/${id}/publish`),
    unpublish: (id: string) => api.post(`/assignments/${id}/unpublish`),
    delete: (id: string) => api.delete(`/assignments/${id}`)
};

// Submission API
export const submissionApi = {
    getById: (id: string) => api.get(`/submissions/${id}`),
    getByStudent: (studentId: string) => api.get(`/submissions/students/${studentId}`),
    getByAssignment: (assignmentId: string) => api.get(`/submissions/assignments/${assignmentId}`),
    getUngraded: (assignmentId: string) => api.get(`/submissions/assignments/${assignmentId}/ungraded`),
    getGraded: (assignmentId: string) => api.get(`/submissions/assignments/${assignmentId}/graded`),
    getStudentLatest: (assignmentId: string, studentId: string) => api.get(`/submissions/assignments/${assignmentId}/students/${studentId}`),
    getHistory: (assignmentId: string, studentId: string) => api.get(`/submissions/assignments/${assignmentId}/students/${studentId}/history`),
    create: (data: any) => api.post('/submissions', data),
    grade: (data: any) => api.post('/submissions/grade', data),
};

export default api;