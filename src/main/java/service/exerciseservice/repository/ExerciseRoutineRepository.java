package service.exerciseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.exerciseservice.entity.ExerciseRoutine;
import service.exerciseservice.entity.ExerciseRoutineRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRoutineRepository extends JpaRepository<ExerciseRoutine, Long> {
    List<ExerciseRoutine> findByUserId(Long userId);

    Optional<ExerciseRoutine> findByIdAndUserId(Long routineId, Long userId);

    @Query("select er from ExerciseRoutine er where er.userId =:userId")
    List<ExerciseRoutine> findExerciseRoutineLIstByUserId(Long userId);

    @Query("SELECT e FROM ExerciseRoutine e " +
            "LEFT JOIN FETCH e.exerciseRoutineRecordList er " +
            "WHERE er.userId = :userId " +
            "AND (er IS NULL OR (YEAR(er.routineDate) = :year AND MONTH(er.routineDate) = :month)) AND er.complete = true")
    List<ExerciseRoutine> findAllWithRecordsByUserId(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );


    @Query("SELECT er FROM ExerciseRoutine er WHERE er.status = true")
    List<ExerciseRoutine> findActiveRoutines();
}
