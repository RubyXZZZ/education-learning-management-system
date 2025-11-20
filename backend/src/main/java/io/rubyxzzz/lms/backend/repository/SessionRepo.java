package io.rubyxzzz.lms.backend.repository;

import io.rubyxzzz.lms.backend.model.Session;
import io.rubyxzzz.lms.backend.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface SessionRepo extends JpaRepository<Session, String> {

    Optional<Session> findBySessionCode(String sessionCode);

    boolean existsBySessionCode(String sessionCode);


    List<Session> findByStartDateBetween(LocalDate start, LocalDate end);

//    @Query("SELECT s FROM Session s WHERE CURRENT_DATE BETWEEN s.registrationOpenDate AND s.registrationDeadline")
//    List<Session> findSessionsWithOpenRegistration();

    @Query("SELECT s FROM Session s WHERE CURRENT_DATE BETWEEN s.startDate AND s.endDate")
    Optional<Session> findCurrentSession();


}
