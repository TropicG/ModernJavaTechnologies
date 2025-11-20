package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

/**
 * Interface for filtering workouts according to various criteria.
 */
public interface WorkoutFilter {

    /**
     * Checks whether a given workout matches the filter's conditions.
     *
     * @param workout the workout to check.
     * @return true if the workout matches the filter, false otherwise.
     */
    boolean matches(Workout workout);

}