package bg.sofia.uni.fmi.mjt.burnout.semester;


import bg.sofia.uni.fmi.mjt.burnout.exception.DisappointmentException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.Category;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

public final class ComputerScienceSemesterPlanner extends AbstractSemesterPlanner {

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan) {

        // all the subjects that are in the semester plan
        UniversitySubject[] subjectsForSemester = semesterPlan.subjects();

        // minimal amounts of credits that we will need to pass
        int minAmountCredits = semesterPlan.minimalAmountOfCredits();

        //sorting by rating
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

        // counting how many subjects we are going to apply
        int currentCredits = 0;
        int numberOfAttendenceSubjects = 0;
        for (int i = 0; currentCredits < minAmountCredits; i++) {
            currentCredits += subjectsForSemester[i].rating();
            numberOfAttendenceSubjects++;
        }

        // these are the subjects that the computer scince student will attend this semester
        UniversitySubject[] attendingSubjects = new UniversitySubject[numberOfAttendenceSubjects];

        for(int i = 0; i < numberOfAttendenceSubjects; i++) {
            attendingSubjects[i] = subjectsForSemester[i];
        }

        return attendingSubjects;
    }

    @Override
    public int calculateJarCount(UniversitySubject[] subjects, int maximumSlackTime, int semesterDuration) {
        int jarCount = 0;

        int totalStudyDays = 0;
        int totalSlackDays = 0;

        for(UniversitySubject subject : subjects) {

            double neededTimeForRest = 0.0;
            switch(subject.category()){
                case Category.MATH -> neededTimeForRest = 0.2;
                case Category.PROGRAMMING -> neededTimeForRest = 0.1;
                case Category.THEORY ->  neededTimeForRest = 0.15;
                case Category.PRACTICAL -> neededTimeForRest = 0.05;
            }

            totalStudyDays += subject.neededStudyTime();
            totalSlackDays += (int) Math.ceil((double)subject.neededStudyTime() * neededTimeForRest);

            // in case you are slacking too much
            if(totalSlackDays > maximumSlackTime) {
                throw new DisappointmentException("You dissapointed baba");
            }
        }

        jarCount += (int) totalStudyDays / 5;
        if(totalStudyDays + totalSlackDays > semesterDuration) {
            jarCount *= 2;
        }

        return jarCount;
    }




}
