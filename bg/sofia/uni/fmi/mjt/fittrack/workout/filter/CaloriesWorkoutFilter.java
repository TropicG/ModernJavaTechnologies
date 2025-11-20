package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public class CaloriesWorkoutFilter implements WorkoutFilter{

    private final int min;
    private final int max;

    public CaloriesWorkoutFilter(int min, int max) {
        if(max < 0) {
            throw new IllegalArgumentException("Invalid keyword parameter for NameWorkoutFilter");
        }

        if(min > max) {
            throw new IllegalArgumentException("Invalid keyword parameter for NameWorkoutFilter");
        }

        if(min < 0) {
            throw new IllegalArgumentException("Invalid keyword parameter for NameWorkoutFilter");
        }

        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(Workout workout) {

        if(workout == null) {
            throw new IllegalArgumentException("The passes workout to duration workout filter cannot be null");
        }

        return (workout.getCaloriesBurned() >= min) && (workout.getCaloriesBurned() <= max);
    }
}
