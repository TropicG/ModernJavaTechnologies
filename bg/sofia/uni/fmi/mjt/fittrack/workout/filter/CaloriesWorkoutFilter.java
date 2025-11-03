package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public record CaloriesWorkoutFilter(int min, int max) implements WorkoutFilter{

    public CaloriesWorkoutFilter {
        if(max < 0) {
            throw new IllegalArgumentException("Invalid keyword parameter for NameWorkoutFilter");
        }

        if(min > max) {
            throw new IllegalArgumentException("Invalid keyword parameter for NameWorkoutFilter");
        }

        if(min < 0) {
            throw new IllegalArgumentException("Invalid keyword parameter for NameWorkoutFilter");
        }
    }

    @Override
    public boolean matches(Workout workout) {
        return (workout.getCaloriesBurned() >= min) && (workout.getCaloriesBurned() <= max);
    }
}
