package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public class NameWorkoutFilter implements WorkoutFilter {

    private final String keyword;
    private final boolean caseSensitive;

    public NameWorkoutFilter(String keyword, boolean caseSensitive) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Invalid keyword parameter for NameWorkoutFilter");
        }

        this.keyword = keyword;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean matches(Workout workout) {
        // if null is passed, no match found
        if (workout == null) {
            return false;
        }

        String nameOfWorkOut = workout.getName();

        // substring matching with case sensitivity
        if (caseSensitive) {
            return nameOfWorkOut.contains(keyword);
        } else {
            // substring matching without case sensitivity
            return nameOfWorkOut.toLowerCase().contains(keyword.toLowerCase());
        }

    }
}
