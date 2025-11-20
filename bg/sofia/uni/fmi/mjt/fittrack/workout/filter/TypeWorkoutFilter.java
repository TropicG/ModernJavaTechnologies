package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;

import java.util.Objects;

public class TypeWorkoutFilter implements WorkoutFilter {

    private final WorkoutType type;

    public TypeWorkoutFilter(WorkoutType type) {
        if(type == null) {
            throw new IllegalArgumentException("Cannot initialise TypeWorkoutFilter with null value");
        }

        this.type = type;
    }

    @Override
    public boolean matches(Workout workout) {
        if(workout == null) {
            throw new IllegalArgumentException("Passed null value in matches function on TypeWorkoutFilter");
        }

        return Objects.equals(this.type, workout.getType());
    }
}
