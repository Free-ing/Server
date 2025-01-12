package service.exerciseservice.entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.exerciseservice.base.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "exercise_routine_record")
public class ExerciseRoutineRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDate completeDay;
    private String exerciseName;

    private long exerciseDurationTime;

    private LocalDate routineDate;
    private boolean complete;
    private boolean status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_routine_id")
    private ExerciseRoutine exerciseRoutine;

    @Builder
    public ExerciseRoutineRecord(LocalDate routineDate,boolean complete, Long userId, LocalDate completeDay,
                                 String exerciseName, ExerciseRoutine exerciseRoutine, boolean status) {
        this.routineDate = routineDate;
        this.userId = userId;
        this.completeDay = completeDay;
        this.exerciseName = exerciseName;
        this.exerciseRoutine = exerciseRoutine;
        this.complete = complete;
        this.status = status;
    }

    public void updateCompleteAndCompleteDate(Boolean complete, LocalDate date, long duration){
        this.complete = complete;
        this.completeDay = date;
        this.exerciseDurationTime = duration;
    }

    public void updateStatus(boolean status){
        this.status = status;
    }

    public void offRoutineRecord(){
        this.status = false;
    }
}