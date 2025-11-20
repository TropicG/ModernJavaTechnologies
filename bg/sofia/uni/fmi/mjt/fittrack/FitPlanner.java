package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.exception.OptimalPlanImpossibleException;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;
import bg.sofia.uni.fmi.mjt.fittrack.workout.filter.WorkoutFilter;
import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FitPlanner implements FitPlannerAPI {

    //storing the workouts
    private final Collection<Workout> availableWorkouts;

    public FitPlanner(Collection<Workout> availableWorkouts) {

        if (availableWorkouts == null) {
            throw new IllegalArgumentException("Cannot create FitPlanner with null value passed");
        }

        this.availableWorkouts = new ArrayList<>(availableWorkouts);
    }

    // sorting by accending order by duration
    public List<Workout> getSortedByDuration() {

        Comparator<Workout> comparatorDuration = new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2) {
                return Integer.compare(o1.getDuration(), o2.getDuration()); }
        };

        List<Workout> sortedByDuration = new ArrayList<>(this.availableWorkouts);
        sortedByDuration.sort(comparatorDuration);

        return sortedByDuration;
    }

    @Override
    public List<Workout> findWorkoutsByFilters(List<WorkoutFilter> filters) {
        if (filters == null) {
            throw new IllegalArgumentException("findWorkoutsByFilters cannot be called with null values");
        }

        // if we dont have workouts, we return empty list
        if (this.availableWorkouts.isEmpty()) {
            return new ArrayList<>();
        }

        List<Workout> passedWorkouts = new ArrayList<>();

        // checks all the workout and add to the list only those who pass all the filters
        for (Workout workout : availableWorkouts) {
            boolean hasPassedAllFilters = true;
            for (WorkoutFilter filter : filters) {
                if (!filter.matches(workout)) {
                    hasPassedAllFilters = false;
                    break;
                }
            }

            if (hasPassedAllFilters) {
                passedWorkouts.add(workout);
            }
        }
        return passedWorkouts;
    }

    @Override
    public List<Workout> generateOptimalWeeklyPlan(int totalMinutes) {

        if (totalMinutes < 0) {
            throw new IllegalArgumentException("cannot generate plan when total minutes is false");
        }

        // impossible to create a generate optimal weekly plan that is why we are sending empty one
        if (totalMinutes == 0 || this.availableWorkouts.isEmpty()) {
            return new ArrayList<>();
        }

        List<Workout> sortedByDuration = new ArrayList<>(this.availableWorkouts);
        List<Workout> chosenWorkout = new ArrayList<>();

        int sizeSortedDuration = sortedByDuration.size();

        int[][] calculatedWorkouts = new int[sortedByDuration.size() + 1][totalMinutes + 1];

        for (int col = 0; col <= totalMinutes; col++) {
            calculatedWorkouts[0][col] = 0; }

        for (int row = 0; row <= sizeSortedDuration; row++) {
            calculatedWorkouts[row][0] = 0; }

        for (int row = 1; row <= sortedByDuration.size(); row++) {
            for (int col = 1; col <= totalMinutes; col++) {

                //One row represents the current looked workout
                Workout currentWorkout = sortedByDuration.get(row - 1);

                //we do not take the workout, instead get the workout above
                if (currentWorkout.getDuration() > col) {
                    calculatedWorkouts[row][col] = calculatedWorkouts[row - 1][col];}
                // if we decide to take it
                else {
                    int newCalories = currentWorkout.getCaloriesBurned() + calculatedWorkouts[row - 1 ][col - currentWorkout.getDuration()];
                    int oldCalories = calculatedWorkouts[row - 1][col];

                    calculatedWorkouts[row][col] = Integer.max(newCalories, oldCalories);
                }

            }
        }

        int optimalWeeklyPlan = calculatedWorkouts[sortedByDuration.size()][totalMinutes];
        if (optimalWeeklyPlan == 0) {
            throw new OptimalPlanImpossibleException("Plan impossible to create");
        }

        int currentLookedIndex = totalMinutes;
        for (int row = sortedByDuration.size(); row >= 1; row--) {

            if (calculatedWorkouts[row][currentLookedIndex] != calculatedWorkouts[row - 1][currentLookedIndex]) {
                chosenWorkout.add(sortedByDuration.get(row - 1));
                currentLookedIndex -= sortedByDuration.get(row - 1).getDuration();
            }
        }

        return chosenWorkout;
    }

    @Override
    public Map<WorkoutType, List<Workout>> getWorkoutsGroupedByType() {

        // here we will store all the workout types, and each key will have a list of workouts
        Map<WorkoutType, List<Workout>> workoutsByType = new HashMap<>();

        for (Workout workout : availableWorkouts) {

            // if the workout type is present in the map, we add the workout directly to the list
            if (workoutsByType.containsKey(workout.getType())) {
                workoutsByType.get(workout.getType()).add(workout);}

            // if the workout type is not present, we add the workout type as a key and then add the workout to the list
            else {
                workoutsByType.put(workout.getType(), new ArrayList<>());
                workoutsByType.get(workout.getType()).add(workout);
            }
        }

        // returning a view
        return Collections.unmodifiableMap(workoutsByType);
    }

    @Override
    public List<Workout> getWorkoutsSortedByCalories() {

        // comparing by calories in desc
        Comparator<Workout> comparatorSortedByCalories = new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2) {
                return Integer.compare(o2.getCaloriesBurned(), o1.getCaloriesBurned());
            }
        };

        List<Workout> sortedByCalories = new ArrayList<>(this.availableWorkouts);
        sortedByCalories.sort(comparatorSortedByCalories);

        return Collections.unmodifiableList(sortedByCalories);
    }

    @Override
    public List<Workout> getWorkoutsSortedByDifficulty() {

        // comparing by difficulty in asc
        Comparator<Workout> comparatorSortedByDifficulty = new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2) {
                return Integer.compare(o1.getDifficulty(), o2.getDifficulty());
            }
        };

        // moving all the elements to availableWorkout
        List<Workout> sortedByDifficulties = new ArrayList<>(this.availableWorkouts);

        // sorting by the algorithm
        sortedByDifficulties.sort(comparatorSortedByDifficulty);

        // using this approach to prevent NullPointerException throw when using List.of()
        return Collections.unmodifiableList(sortedByDifficulties);
    }

    @Override
    public Set<Workout> getUnmodifiableWorkoutSet() {

        if (this.availableWorkouts.isEmpty()) {
            return new HashSet<>();
        }

        // using this approach to prevent NullPointerException throw when using Set.of()
        Set<Workout> unmodifiableSet = new HashSet<>(this.availableWorkouts);
        return Collections.unmodifiableSet(unmodifiableSet);
    }
}
