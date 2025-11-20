package io.rubyxzzz.lms.backend.mapper;

import io.rubyxzzz.lms.backend.dto.response.SessionRes;
import io.rubyxzzz.lms.backend.model.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class SessionMapper {

    private final CourseMapper courseMapper;
    public SessionRes toResponse(Session session) {
        if (session == null) return null;

        SessionRes response = new SessionRes();
        BeanUtils.copyProperties(session, response);

        // Set calculated fields
        response.setActive(session.isActive());

        return response;
    }

    public SessionRes toResponseWithCourses(Session session) {
        if (session == null) return null;

        SessionRes response = new SessionRes();
        BeanUtils.copyProperties(session, response);

        response.setActive(session.isActive());

        // Map courses
        if (session.getCourses() != null) {
            response.setCourses(
                    courseMapper.toResponseList(session.getCourses())
            );
        }

        return response;
    }

    public List<SessionRes> toResponseList(List<Session> sessions) {
        if (sessions == null) return List.of();
        return sessions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
