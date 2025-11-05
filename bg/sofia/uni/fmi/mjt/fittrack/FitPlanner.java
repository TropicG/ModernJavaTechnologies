package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;
import bg.sofia.uni.fmi.mjt.fittrack.workout.filter.WorkoutFilter;
import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

import java.util.*;

public class FitPlanner implements FitPlannerAPI {

    //storing the workouts
    private final Collection<Workout> availableWorkouts;

    public FitPlanner(Collection<Workout> availableWorkouts) {
        this.availableWorkouts = new ArrayList<>(availableWorkouts);
    }

    // sorting by accending order by duration
    public List<Workout> getSortedByDuration() {

        Comparator<Workout> comparatorDuration = new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2){
                return Integer.compare(o1.getDuration(), o2.getDuration());
            }
        };

        List<Workout> sortedByDuration = new ArrayList<>(this.availableWorkouts);
        sortedByDuration.sort(comparatorDuration);

        return sortedByDuration;
    }

    @Override
    public List<Workout> findWorkoutsByFilters(List<WorkoutFilter> filters) {

        List<Workout> passedWorkouts = new ArrayList<>();

        // checks all the workout and add only those who pass all the filters
        for(Workout workout : availableWorkouts) {

            boolean hasPassedAllFilters = true;
            for(WorkoutFilter filter : filters) {
                if(!filter.matches(workout)) {
                    hasPassedAllFilters = false;
                    break;
                }
            }

            if(hasPassedAllFilters) {
                passedWorkouts.add(workout);
            }
        }

        return passedWorkouts;
    }

    @Override
    public List<Workout> generateOptimalWeeklyPlan(int totalMinutes) {

        List<Workout> sortedByDuration = new ArrayList<>(this.availableWorkouts);
        List<Workout> chosenWorkout = new ArrayList<>();

        int sizeSortedDuration = sortedByDuration.size();

        int[][] calculatedWorkouts = new int[sortedByDuration.size() + 1][totalMinutes + 1];

        for(int col = 0; col <= totalMinutes; col++) {
            calculatedWorkouts[0][col] = 0;
        }

        for(int row = 0; row <= sizeSortedDuration; row++){
            calculatedWorkouts[row][0] = 0;
        }

        for(int row = 1; row <= sortedByDuration.size(); row++){
            for(int col = 1; col <= totalMinutes; col++) {

                //One row represents the current looked workout
                Workout currentWorkout = sortedByDuration.get(row - 1);

                //we do not take the workout, instead get the workout above
                if(currentWorkout.getDuration() > col) {
                    calculatedWorkouts[row][col] = calculatedWorkouts[row-1][col];
                } // if we decide to take it
                else {
                    int newCalories = currentWorkout.getCaloriesBurned() + calculatedWorkouts[row -1 ][col - currentWorkout.getDuration()];
                    int oldCalories = calculatedWorkouts[row - 1][col];

                    calculatedWorkouts[row][col] = Integer.max(newCalories, oldCalories);
                }

            }
        }

        int currentLookedIndex = totalMinutes;
        for(int row = sortedByDuration.size(); row >= 1; row--) {

            if(calculatedWorkouts[row][currentLookedIndex] != calculatedWorkouts[row-1][currentLookedIndex]) {
                chosenWorkout.add(sortedByDuration.get(row -1));
                currentLookedIndex -= sortedByDuration.get(row - 1).getDuration();
            }
        }

        return chosenWorkout;
    }

    @Override
    public Map<WorkoutType, List<Workout>> getWorkoutsGroupedByType() {

        Map<WorkoutType, List<Workout>> workoutsByType = new HashMap<>();

        for(Workout workout : availableWorkouts) {
            if(workoutsByType.containsKey(workout.getType())) {
                workoutsByType.get(workout.getType()).add(workout);
            }
            else {
                workoutsByType.put(workout.getType(), new ArrayList<>());
                workoutsByType.get(workout.getType()).add(workout);
            }
        }

        return workoutsByType;
    }

    @Override
    public List<Workout> getWorkoutsSortedByCalories() {

        Comparator<Workout> comparatorSortedByCalories = new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2) {
                return Integer.compare(o1.getCaloriesBurned(), o2.getCaloriesBurned());
            }
        };

        List<Workout> sortedByCalories = new ArrayList<>(this.availableWorkouts);
        sortedByCalories.sort(comparatorSortedByCalories);

        return sortedByCalories;
    }

    @Override
    public List<Workout> getWorkoutsSortedByDifficulty() {

        Comparator<Workout> comparatorSortedByDifficulty = new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2) {
                return Integer.compare(o1.getDifficulty(), o2.getDifficulty());
            }
        };

        List<Workout> sortedByDifficulties = new ArrayList<>(this.availableWorkouts);
        sortedByDifficulties.sort(comparatorSortedByDifficulty);

        return sortedByDifficulties;
    }

    @Override
    public Set<Workout> getUnmodifiableWorkoutSet() {
        return Set.copyOf(this.availableWorkouts);
    }
}
