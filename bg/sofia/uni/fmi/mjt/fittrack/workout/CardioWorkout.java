package bg.sofia.uni.fmi.mjt.fittrack.workout;

import bg.sofia.uni.fmi.mjt.fittrack.exception.InvalidWorkoutException;
import java.lang.String;
import java.util.Objects;

public final class CardioWorkout implements Workout{

    private final static WorkoutType workoutType;

    private final String name;
    private final int duration;
    private final int caloriesBurned;
    private final int difficulty;

    static {
        workoutType = WorkoutType.CARDIO;
    }

    private static void validateParams(String name, int duration, int caloriesBurned, int difficulty) {
        if(name == null || name.isBlank()) {
            throw new InvalidWorkoutException("Invalid argument for name of CardioWorkout");
        }

        if(duration <= 0) {
            throw new InvalidWorkoutException("Invalid argument for duration of CardioWorkout");
        }

        if(caloriesBurned <= 0) {
            throw new InvalidWorkoutException("Invalid argument for calories burnt of CardioWorkout");
        }

        if(!(difficulty >= 1 && difficulty <= 5)) {
            throw new InvalidWorkoutException("Invalid argument for difficulty of CardioWorkout");
        }
    }

    public CardioWorkout(String name, int duration, int caloriesBurned, int difficulty) {
        validateParams(name, duration, caloriesBurned, difficulty);
        this.name = name;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.difficulty = difficulty;
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
        if(this == o) {
            return true;
        }

        if(o == null || this.getClass() != o.getClass()){
            return false;
        }

        Workout otherWorkout = (Workout)o;
        return Objects.equals(this.name, otherWorkout.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "CardioWorkout{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", caloriesBurned=" + caloriesBurned +
                ", difficulty=" + difficulty +
                '}';
    }
}
