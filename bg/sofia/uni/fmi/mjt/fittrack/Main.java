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



        List<Workout> workouts = Arrays.asList(
                new CardioWorkout("HIIT", 3, 400, 4),
                new StrengthWorkout("Upper Body", 4, 350, 3),
                new YogaSession("Morning Flow", 2, 150, 2),
                new CardioWorkout("Cycling", 6, 600, 5),
                new StrengthWorkout("Leg Day", 3, 250, 2)
        );

        FitPlanner planner = new FitPlanner(workouts);
        List<Workout> plan = planner.generateOptimalWeeklyPlan(12);

        for(Workout workout : plan) {
            System.out.println(workout);
        }

    }
}
