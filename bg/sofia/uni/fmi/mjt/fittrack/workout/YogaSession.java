package bg.sofia.uni.fmi.mjt.fittrack.workout;

import bg.sofia.uni.fmi.mjt.fittrack.exception.InvalidWorkoutException;
import java.lang.String;

public final record YogaSession(String name, int duration, int caloriesBurned, int difficulty) implements Workout{

    private final static WorkoutType workoutType;

    static {
        workoutType = WorkoutType.YOGA;
    }

    public YogaSession {
        if(name == null || name.isBlank()) {
            throw new InvalidWorkoutException("Invalid argument for name of YogaSession");
        }

        if(duration > 0) {
            throw new InvalidWorkoutException("Invalid argument for duration of YogaSession");
        }

        if(caloriesBurned > 0) {
            throw new InvalidWorkoutException("Invalid argument for calories burnt of YogaSession");
        }

        if(!(difficulty >= 1 && difficulty <= 5)) {
            throw new InvalidWorkoutException("Invalid argument for difficulty of YogaSession");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    @Override
    public int getDifficulty() {
        return difficulty;
    }

    @Override
    public WorkoutType getType() {
        return workoutType;
    }
}
