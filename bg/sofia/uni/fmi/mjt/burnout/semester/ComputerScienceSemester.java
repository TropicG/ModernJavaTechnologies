package bg.sofia.uni.fmi.mjt.burnout.semester;


import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

public final class ComputerScienceSemester extends AbstractSemesterPlanner {

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan) {

        UniversitySubject[] subjectsForSemester = semesterPlan.subjects();
        int minAmountCredits = semesterPlan.minimalAmountOfCredits();

        //sorting
        UniversitySubject temp = null;
        for(int i = 0; i < subjectsForSemester.length; i++){
            for(int j = i; j < subjectsForSemester.length; j++) {
                if(subjectsForSemester[i].rating() < subjectsForSemester[j].rating()) {
                    temp = subjectsForSemester[i];
                    subjectsForSemester[i] = subjectsForSemester[j];
                    subjectsForSemester[j] = temp;
                }
            }
        }

        UniversitySubject[] attendingSubjects = new UniversitySubject[subjectsForSemester.length];
        int currentCredits = 0;

        for(int i = 0; currentCredits < minAmountCredits; i++) {
            attendingSubjects[i] = subjectsForSemester[i];
            currentCredits += subjectsForSemester[i].rating();
        }

        return attendingSubjects;
    }

    @Override
    public int calculateJarCount(UniversitySubject[] subjects, int maximumSlackTime, int semesterDuration) {


        return 0;
    }




}
