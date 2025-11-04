package bg.sofia.uni.fmi.mjt.fittrack.workout;

import bg.sofia.uni.fmi.mjt.fittrack.exception.InvalidWorkoutException;
import java.lang.String;
import java.util.Objects;

public final record CardioWorkout(String name, int duration, int caloriesBurned, int difficulty) implements Workout{

    private final static WorkoutType workoutType;

    static {
        workoutType = WorkoutType.CARDIO;
    }

    public CardioWorkout {
        if(name == null || name.isBlank()) {
            throw new InvalidWorkoutException("Invalid argument for name of CardioWorkout");
        }

        if(duration > 0) {
            throw new InvalidWorkoutException("Invalid argument for duration of CardioWorkout");
        }

        if(caloriesBurned > 0) {
            throw new InvalidWorkoutException("Invalid argument for calories burnt of CardioWorkout");
        }

        if(!(difficulty >= 1 && difficulty <= 5)) {
            throw new InvalidWorkoutException("Invalid argument for difficulty of CardioWorkout");
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CardioWorkout that = (CardioWorkout) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
