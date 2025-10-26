package bg.sofia.uni.fmi.mjt.burnout;

import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.semester.ComputerScienceSemester;
import bg.sofia.uni.fmi.mjt.burnout.semester.SoftwareEngineeringSemesterPlanner;
import bg.sofia.uni.fmi.mjt.burnout.subject.Category;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

public class Main {

    public static void main(String[] args) {

        UniversitySubject[] subjects = {
                new UniversitySubject("Calculus", 6, 5, Category.MATH, 40),
                new UniversitySubject("Java Programming", 4, 4, Category.PROGRAMMING, 30),
                new UniversitySubject("Linear Algebra", 5, 3, Category.MATH, 35),
                new UniversitySubject("Data Structures", 3, 5, Category.PROGRAMMING, 25)
        };

        SubjectRequirement[] requirements = {
                new SubjectRequirement(Category.MATH, 1),
                new SubjectRequirement(Category.PROGRAMMING, 1)
        };

        SemesterPlan plan1 = new SemesterPlan(subjects, requirements, 8);

        ComputerScienceSemester computerSemester = new ComputerScienceSemester();
        SoftwareEngineeringSemesterPlanner softwareEngineeringSemesterPlanner = new SoftwareEngineeringSemesterPlanner();

        UniversitySubject[] attendingSubjects = computerSemester.calculateSubjectList(plan1);

        for(UniversitySubject uniSubject : attendingSubjects) {
            if(uniSubject != null) {
                System.out.println(uniSubject);
            }
        }

        attendingSubjects = softwareEngineeringSemesterPlanner.calculateSubjectList(plan1);
        for(UniversitySubject uniSubject : attendingSubjects) {
            if(uniSubject != null) {
                System.out.println(uniSubject);
            }
        }
    }
}
