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

    @Override
    public List<Workout> findWorkoutsByFilters(List<WorkoutFilter> filters) {

        // Osven da obhodim vseki edin element ot nashite available workes i da mu prolojim vsichki filtri

        return null;
    }

    //trqbva da maksimizirame izgorenite kalorii dokato mojem sme v totalMinutes
    @Override
    public List<Workout> generateOptimalWeeklyPlan(int totalMinutes) {

        /*

        30 min

        (23,10) , (30,25), (15,20)



         */


        return null;
    }

    @Override
    public Map<WorkoutType, List<Workout>> getWorkoutsGroupedByType() {
        //osven da grupiram po trenirovkite po tip
        //toest obhojdam vsichkite trenirovki i gi postavqm v suotvetniq tip
        // O(N)

        return null;
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
