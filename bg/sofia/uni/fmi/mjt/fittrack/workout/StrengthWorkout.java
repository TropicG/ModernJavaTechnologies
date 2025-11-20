package bg.sofia.uni.fmi.mjt.fittrack.workout;

import bg.sofia.uni.fmi.mjt.fittrack.exception.InvalidWorkoutException;
import java.util.Objects;

public final class StrengthWorkout implements Workout {

    private static final WorkoutType WORKOUT_TYPE;

    private static final int MIN_DIFFICULTY = 1;
    private static final int MAX_DIFFICULTY = 5;

    private final String name;
    private final int duration;
    private final int caloriesBurned;
    private final int difficulty;

    static {
        WORKOUT_TYPE = WorkoutType.STRENGTH;
    }

    public StrengthWorkout(String name, int duration, int caloriesBurned, int difficulty) {
        validateParams(name, duration, caloriesBurned, difficulty);
        this.name = name;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.difficulty = difficulty;
    }

    private static void validateParams(String name, int duration, int caloriesBurned, int difficulty) {
        if (name == null || name.isBlank()) {
            throw new InvalidWorkoutException("Invalid argument for name of StrengthWorkout");
        }

        if (duration <= 0) {
            throw new InvalidWorkoutException("Invalid argument for duration of StrengthWorkout");
        }

        if (caloriesBurned <= 0) {
            throw new InvalidWorkoutException("Invalid argument for calories burnt of StrengthWorkout");
        }

        if (!(difficulty >= MIN_DIFFICULTY && difficulty <= MAX_DIFFICULTY)) {
            throw new InvalidWorkoutException("Invalid argument for difficulty of StrengthWorkout");
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
        return WORKOUT_TYPE;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        Workout otherWorkout = (Workout) o;
        return Objects.equals(this.name, otherWorkout.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }

    @Override
    public String toString() {
        return "StrengthWorkout{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", caloriesBurned=" + caloriesBurned +
                ", difficulty=" + difficulty +
                '}';
    }
}
