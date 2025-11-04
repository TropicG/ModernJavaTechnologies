package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;
import bg.sofia.uni.fmi.mjt.fittrack.workout.filter.WorkoutFilter;
import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

import java.util.*;

public class FitPlanner implements FitPlannerAPI {

    private final Collection<Workout> availableWorkouts;

    public FitPlanner(Collection<Workout> availableWorkouts) {
        this.availableWorkouts = new ArrayList<>(availableWorkouts);
    }

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

        for(Workout workout : availableWorkouts) {
            for(WorkoutFilter filter : filters) {
                if(!filter.matches(workout)) {
                    continue;
                }
            }
            passedWorkouts.add(workout);
        }

        return passedWorkouts;
    }

    //trqbva da maksimizirame izgorenite kalorii dokato mojem sme v totalMinutes
    @Override
    public List<Workout> generateOptimalWeeklyPlan(int totalMinutes) {

        List<Workout> sortedByDuration = getSortedByDuration();

        int[][] calculatedWorkouts = new int[sortedByDuration.size() + 1][totalMinutes + 1];

        for(int i = 0; i <= totalMinutes; i++) {
            calculatedWorkouts[0][i] = 0;
        }

        for(int i = 0; i <= sortedByDuration.size(); i++){
            
        }




        return null;
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
        return new HashSet<>(Collections.unmodifiableCollection(this.availableWorkouts));
    }





}
