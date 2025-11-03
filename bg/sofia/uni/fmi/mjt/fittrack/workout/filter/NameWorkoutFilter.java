package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public record NameWorkoutFilter(String keyword, boolean caseSensitive) implements WorkoutFilter {

    public NameWorkoutFilter {
        if(keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Invalid keyword parameter for NameWorkoutFilter");
        }
    }

    @Override
    public boolean matches(Workout workout) {
        String nameOfWorkOut = workout.getName();

        if(caseSensitive) {
            return nameOfWorkOut.contains(keyword);
        }
        else {
            return nameOfWorkOut.toLowerCase().contains(keyword.toLowerCase());
        }
    }
}
