package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.exception.OptimalPlanImpossibleException;
import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;
import bg.sofia.uni.fmi.mjt.fittrack.workout.filter.WorkoutFilter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for the core functionality of the FitTrack Planner.
 * Enables searching, rating, and optimal planning of workouts.
 */
public interface FitPlannerAPI {

    /**
     * Returns a list of workouts that match all provided filters.
     *
     * @param filters a list of filters to be applied.
     * @return a list of workouts that satisfy all filters.
     * @throws IllegalArgumentException if filters is null.
     */
    List<Workout> findWorkoutsByFilters(List<WorkoutFilter> filters);

    /**
     * Generates an optimal weekly workout plan that maximizes burned calories
     * while fitting within the specified total time limit.
     *
     * @param totalMinutes total available time (in minutes) for workouts during the week
     * @return a list of optimally selected workouts, sorted by calories, then by difficulty, in descending order.
     *         Returns an empty list if totalMinutes is 0.
     * @throws OptimalPlanImpossibleException if a valid plan cannot be generated (e.g., all workouts exceed the time limit)
     * @throws IllegalArgumentException       if totalMinutes is negative
     */
    List<Workout> generateOptimalWeeklyPlan(int totalMinutes) throws OptimalPlanImpossibleException;

    /**
     * Groups all available workouts by type.
     *
     * @return an unmodifiable Map where the key is WorkoutType and the value is a list of workouts of that type.
     */
    Map<WorkoutType, List<Workout>> getWorkoutsGroupedByType();

    /**
     * Returns a list of all workouts, sorted by burned calories in descending order.
     *
     * @return an unmodifiable list of workouts sorted by calories in descending order.
     */
    List<Workout> getWorkoutsSortedByCalories();

    /**
     * Returns a list of all workouts, sorted by difficulty in ascending order.
     *
     * @return an unmodifiable list of workouts sorted by difficulty in ascending order.
     */
    List<Workout> getWorkoutsSortedByDifficulty();

    /**
     * Returns an unmodifiable set of all available workouts.
     *
     * @return an unmodifiable Set containing all workouts.
     */
    Set<Workout> getUnmodifiableWorkoutSet();

}