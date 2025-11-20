package io.rubyxzzz.lms.backend.service;

import io.rubyxzzz.lms.backend.dto.request.CreateSessionReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateSessionReq;
import io.rubyxzzz.lms.backend.dto.response.SessionRes;
import io.rubyxzzz.lms.backend.exception.ResourceNotFoundException;
import io.rubyxzzz.lms.backend.mapper.SessionMapper;
import io.rubyxzzz.lms.backend.model.Session;
import io.rubyxzzz.lms.backend.model.SessionStatus;
import io.rubyxzzz.lms.backend.repository.SessionRepo;
import io.rubyxzzz.lms.backend.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepo sessionRepo;
    private final SessionMapper sessionMapper;

    /**
     * Create a new session
     */
    @Transactional
    public SessionRes createSession(CreateSessionReq request) {
        // Validate uniqueness
        if (sessionRepo.findBySessionCode(request.getSessionCode()).isPresent()) {
            throw new IllegalArgumentException(
                    "Session already exists: " + request.getSessionCode()
            );
        }

        // Build session entity using BeanUtils
        Session session = new Session();

        BeanUtils.copyProperties(request, session);


        // Save and return
        Session savedSession = sessionRepo.save(session);
        return sessionMapper.toResponse(savedSession);
    }


    @Transactional
    public SessionRes updateSession(String sessionId, UpdateSessionReq request) {
        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));

        SessionStatus currentStatus = session.getStatus();
        if (currentStatus == SessionStatus.COMPLETED || currentStatus == SessionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot modify active or completed session");
        }

        if (currentStatus == SessionStatus.UPCOMING && request.getStartDate() != null) {
            session.setStartDate(request.getStartDate());
        }
        if (currentStatus == SessionStatus.UPCOMING && request.getEndDate() != null) {
            session.setEndDate(request.getEndDate());
        }

        Session updated = sessionRepo.save(session);

        return sessionMapper.toResponse(updated);
    }

    /**
     * Get session by UUID
     */
    public SessionRes getSession(String sessionId) {
        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));
        return sessionMapper.toResponse(session);
    }

    /**
     * Get session by session code
     */
    public SessionRes getSessionByCode(String sessionCode) {
        Session session = sessionRepo.findBySessionCode(sessionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "sessionCode", sessionCode));
        return sessionMapper.toResponse(session);
    }

    /**
     * Get current active session
     */
    public SessionRes getCurrentSession() {
        Session session = sessionRepo.findCurrentSession()
                .orElseThrow(() -> new ResourceNotFoundException("No active session found"));
        return sessionMapper.toResponse(session);
    }

    /**
     * Get all sessions
     */
    public List<SessionRes> getAllSessions() {
        return sessionRepo.findAll().stream()
                .sorted(Comparator.comparing(Session::getStartDate).reversed())
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get sessions by date range
     */
    public List<SessionRes> getSessionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return sessionMapper.toResponseList(
                sessionRepo.findByStartDateBetween(startDate, endDate)
        );
    }


//    /**
//     * Get sessions by status
//     */
//    public List<SessionRes> getSessionsByStatus(SessionStatus status) {
//        return sessionMapper.toResponseList(
//                sessionRepo.findByStatus(status)
//        );
//    }

//    /**
//     * Get sessions with open registration
//     */
//    public List<SessionRes> getSessionsWithOpenRegistration() {
//        return sessionMapper.toResponseList(sessionRepo.findSessionsWithOpenRegistration());
//    }
}

