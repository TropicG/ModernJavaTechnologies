package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;

import java.util.Objects;

public record TypeWorkoutFilter(WorkoutType type) implements WorkoutFilter {

    public TypeWorkoutFilter {
        if(type == null) {
            throw new IllegalArgumentException("Invalid argument");
        }
    }

    @Override
    public boolean matches(Workout workout) {
        if(workout == null) {
            return false;
        }

        return Objects.equals(this.type, workout.getType());
    }
}
