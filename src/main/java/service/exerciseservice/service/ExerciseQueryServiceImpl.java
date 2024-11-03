package service.exerciseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.exerciseservice.dto.RequestExerciseDto;
import service.exerciseservice.dto.ResponseExerciseDto;
import service.exerciseservice.dto.RoutineTrackerDto;
import service.exerciseservice.entity.ExerciseRoutine;
import service.exerciseservice.entity.ExerciseRoutineRecord;
import service.exerciseservice.repository.ExerciseRoutineRecordRepository;
import service.exerciseservice.repository.ExerciseRoutineRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseQueryServiceImpl implements ExerciseQueryService {

    private final ExerciseRoutineRepository exerciseRoutineRepository;
    private final ExerciseRoutineRecordRepository exerciseRoutineRecordRepository;

    //Todo: 마음 채우기 루틴 리스트 조회
    @Override
    public List<RequestExerciseDto.ExerciseRoutineDto> getExerciseRoutineList(Long userId) {
        return exerciseRoutineRepository.findByUserId(userId)
                .stream()
                .map(exerciseRoutine -> RequestExerciseDto.ExerciseRoutineDto.builder()
                        .routineName(exerciseRoutine.getExerciseName())
                        .routineId(exerciseRoutine.getId())
                        .imageUrl(exerciseRoutine.getImageUrl())
                        .status(exerciseRoutine.getStatus())
                        .monday(exerciseRoutine.getMonday())
                        .tuesday(exerciseRoutine.getTuesday())
                        .wednesday(exerciseRoutine.getWednesday())
                        .thursday(exerciseRoutine.getThursday())
                        .friday(exerciseRoutine.getFriday())
                        .saturday(exerciseRoutine.getSaturday())
                        .sunday(exerciseRoutine.getSunday())
                        .startTime(exerciseRoutine.getStartTime())
                        .endTime(exerciseRoutine.getEndTime())
                        .explanation(exerciseRoutine.getExplanation())
                        .build())
                .collect(Collectors.toList());

    }

    //Todo: 일별 루틴 일정 조회
    @Override
    public List<ResponseExerciseDto.DayRoutineDto> getDayRoutine(LocalDate date, Long userId) {

        List<ResponseExerciseDto.DayRoutineDto> dayRoutineDtoList = exerciseRoutineRecordRepository.getDayRoutine(date, userId, true);
        System.out.println(dayRoutineDtoList);
        return dayRoutineDtoList;

    }

    //Todo: 마음 루틴 트래커 조회
    @Override
    public List<RoutineTrackerDto.ExerciseRoutineTrackerDto> getHobbyRoutineTrackers(Long userId, int year, int month) {
        Map<String, RoutineTrackerDto.ExerciseRoutineTrackerDto> routineMap = new LinkedHashMap<>();
        List<ExerciseRoutine> routines = exerciseRoutineRepository.findAllWithRecordsByUserId(userId, year, month);
        System.out.println(routines);

        for (ExerciseRoutine routine : routines) {
            if (!routine.getExerciseRoutineRecordList().isEmpty()) {  // 레코드가 있는 경우만 처리
                System.out.println("test");
                RoutineTrackerDto.ExerciseRoutineTrackerDto trackerDto =
                        routineMap.computeIfAbsent(routine.getExerciseName(),
                                k -> new RoutineTrackerDto.ExerciseRoutineTrackerDto(routine.getExerciseName()));

                for (ExerciseRoutineRecord record : routine.getExerciseRoutineRecordList()) {
                    trackerDto.addRecord(new RoutineTrackerDto.ExerciseRecordDto(
                            record.getId(),
                            record.getRoutineDate()
                    ));
                }
            }
        }

        return new ArrayList<>(routineMap.values());
    }


    //Todo: 운동 루틴의 기록 조회하기
    @Override
    public List<ResponseExerciseDto.ExerciseRoutineGroupDto> getRoutinesGroupedByUser() {
        List<ExerciseRoutine> allRoutines = exerciseRoutineRepository.findAllWithRecords();

        // userId로 그룹화
        Map<Long, List<ExerciseRoutine>> routinesByUser = allRoutines.stream()
                .collect(Collectors.groupingBy(ExerciseRoutine::getUserId));

        // 그룹화된 데이터를 DTO로 변환
        return routinesByUser.entrySet().stream()
                .map(entry -> {
                    List<ResponseExerciseDto.ExerciseRoutineGroupDto.RoutineWithRecordsDto> routineDtos =
                            entry.getValue().stream()
                                    .map(routine -> ResponseExerciseDto.ExerciseRoutineGroupDto.RoutineWithRecordsDto.builder()
                                            .routineId(routine.getId())
                                            .exerciseName(routine.getExerciseName())
                                            .records(routine.getExerciseRoutineRecordList().stream()
                                                    .map(record -> ResponseExerciseDto.ExerciseRoutineGroupDto.RecordDto.builder()
                                                            .recordId(record.getId())
                                                            .completeDay(record.getCompleteDay())
                                                            .exerciseDurationTime(record.getExerciseDurationTime())
                                                            .complete(record.isComplete())
                                                            .build())
                                                    .collect(Collectors.toList()))
                                            .build())
                                    .collect(Collectors.toList());

                    return ResponseExerciseDto.ExerciseRoutineGroupDto.builder()
                            .userId(entry.getKey())
                            .routines(routineDtos)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
