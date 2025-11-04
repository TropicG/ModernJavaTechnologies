package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.workout.CardioWorkout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.StrengthWorkout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.YogaSession;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Workout cardioWorkout = new CardioWorkout("CardioWorkout",20,200,3);
        Workout strengthWorkout = new StrengthWorkout("StrengthWorkout", 30, 300, 5);
        Workout yogaWorkout = new YogaSession("YogaSession", 50, 100, 1);

        Workout cardioWorkout1 = new CardioWorkout("CCCC", 20, 20, 2);


        FitPlanner fitPlanner = new FitPlanner(List.of(cardioWorkout, strengthWorkout, yogaWorkout));



        List<Workout> workouts = Arrays.asList(
                new CardioWorkout("HIIT", 30, 400, 4),
                new StrengthWorkout("Upper Body", 45, 350, 3),
                new YogaSession("Morning Flow", 20, 150, 2),
                new CardioWorkout("Cycling", 60, 600, 5),
                new StrengthWorkout("Leg Day", 30, 250, 2),
                new YogaSession("Evening Relax", 15, 100, 1)
        );

        FitPlanner planner = new FitPlanner(workouts);
       // List<Workout> plan = planner.generateOptimalWeeklyPlan(120);

    }
}
