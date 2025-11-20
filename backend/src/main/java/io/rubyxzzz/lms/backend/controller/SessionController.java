package io.rubyxzzz.lms.backend.controller;


import io.rubyxzzz.lms.backend.dto.request.CreateSessionReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateSessionReq;
import io.rubyxzzz.lms.backend.dto.response.SessionRes;
import io.rubyxzzz.lms.backend.model.SessionStatus;
import io.rubyxzzz.lms.backend.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Session REST Controller
 * Handles session (term) management
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;


    /**
     * Create new session
     * POST /api/sessions
     */
    @PreAuthorize("hasAuthority('SESSIONS_CREATE')")
    @PostMapping
    public ResponseEntity<SessionRes> createSession(
            @Valid @RequestBody CreateSessionReq request) {

        SessionRes session = sessionService.createSession(request);
        return new ResponseEntity<>(session, HttpStatus.CREATED);
    }

    /**
     * Update session
     * PUT /api/sessions/{id}
     */
    @PreAuthorize("hasAuthority('SESSIONS_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<SessionRes> updateSession(
            @PathVariable String id,
            @Valid @RequestBody UpdateSessionReq request) {

        SessionRes session = sessionService.updateSession(id, request);
        return ResponseEntity.ok(session);
    }

    /**
     * Get current active session
     * GET /api/sessions/current
     */
    @PreAuthorize("hasAuthority('SESSIONS_VIEW')")
    @GetMapping("/current")
    public ResponseEntity<SessionRes> getCurrentSession() {
        SessionRes session = sessionService.getCurrentSession();
        return ResponseEntity.ok(session);
    }

    /**
     * Get all sessions
     * GET /api/sessions
     */
    @PreAuthorize("hasAuthority('SESSIONS_VIEW')")
    @GetMapping
    public ResponseEntity<List<SessionRes>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

//    /**
//     * Get sessions by status
//     * GET /api/sessions/status/{status}
//     */
//    @GetMapping("/status/{status}")
//    public ResponseEntity<List<SessionRes>> getSessionsByStatus(
//            @PathVariable SessionStatus status
//    ) {
//        return ResponseEntity.ok(sessionService.getSessionsByStatus(status));
//    }

//    /**
//     * Get sessions by date range
//     * GET /api/sessions/dateRange
//     */
//
//    @GetMapping("/dateRange")
//    public ResponseEntity<List<SessionRes>> getSessionsByDateRange(
//            @RequestParam LocalDate startDate,
//            @RequestParam LocalDate endDate
//    ) {
//        return ResponseEntity.ok(
//                sessionService.getSessionsByDateRange(startDate, endDate)
//        );
//    }

    /**
     * Get session by UUID
     * GET /api/sessions/{id}
     */
    @PreAuthorize("hasAuthority('SESSIONS_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<SessionRes> getSession(@PathVariable String id) {
        SessionRes session = sessionService.getSession(id);
        return ResponseEntity.ok(session);
    }

    /**
     * Get session by session code
     * GET /api/sessions/by-code/{sessionCode}
     */
    @PreAuthorize("hasAuthority('SESSIONS_VIEW')")
    @GetMapping("/by-code/{sessionCode}")
    public ResponseEntity<SessionRes> getSessionByCode(
            @PathVariable String sessionCode) {
        SessionRes session = sessionService.getSessionByCode(sessionCode);
        return ResponseEntity.ok(session);
    }

//    /**
//     * Get sessions with open registration
//     * GET /api/sessions/open-registration
//     */
//    @GetMapping("/open-registration")
//    public ResponseEntity<List<SessionRes>> getSessionsWithOpenRegistration() {
//        List<SessionRes> sessions = sessionService.getSessionsWithOpenRegistration();
//        return ResponseEntity.ok(sessions);
//    }
}
