package bg.sofia.uni.fmi.mjt.burnout.semester;


import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

public final class ComputerScienceSemesterPlanner extends AbstractSemesterPlanner {

    private int calcSubjectsToAttend(int minAmountCredits, UniversitySubject[] subjectsForSemester) {
        // counting how many subjects we are going to apply
        int currentCredits = 0;
        int numberOfAttendenceSubjects = 0;
        for (int i = 0; (currentCredits < minAmountCredits) && (i < subjectsForSemester.length); i++) {

            // skipping null values to prevent NullPointerException
            if(subjectsForSemester[i] == null) {
                continue;
            }

            currentCredits += subjectsForSemester[i].rating();
            numberOfAttendenceSubjects++;
        }

        // if the CS student still cannot cover his semester credists
        if(currentCredits < minAmountCredits) {
            throw new CryToStudentsDepartmentException("The CS student cannot cover his semester credits");
        }

        return numberOfAttendenceSubjects;
    }

    private void sortingSubjectsByRating(UniversitySubject[] subjectsForSemester) {
        //sorting by rating
        UniversitySubject temp = null;
        for(int i = 0; i < subjectsForSemester.length; i++){
            for(int j = i + 1; j < subjectsForSemester.length; j++) {

                // there is a possibility that the array can have somewhere a null element
                // NOTE: This swapping idea with null values were given to me by Gemini
                boolean shouldSwap = false;

                // if we find a null elements, it is treated as smaller always and pushed at the end of the array
                if(subjectsForSemester[i] == null && subjectsForSemester[j] != null) {
                    shouldSwap = true;
                }
                // proper swapping if both of the elements are not null
                else if(subjectsForSemester[i] != null && subjectsForSemester[j] != null
                && (subjectsForSemester[i].rating() < subjectsForSemester[j].rating())) {
                    shouldSwap = true;
                }

                if(shouldSwap) {
                    temp = subjectsForSemester[i];
                    subjectsForSemester[i] = subjectsForSemester[j];
                    subjectsForSemester[j] = temp;
                }
            }
        }
    }

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan) {

        validatePassesSemesterPlan(semesterPlan);

        // all the subjects that are in the semester plan
        UniversitySubject[] subjectsForSemester = semesterPlan.subjects();
        SubjectRequirement[] subjectRequirements = semesterPlan.subjectRequirements();

        if(checkDuplicateSubjectReq(subjectRequirements)) {
            throw new InvalidSubjectRequirementsException("For the Software Engineering student not proper subject requirements were passed");
        }

        // minimal amounts of credits that we will need to pass
        int minAmountCredits = semesterPlan.minimalAmountOfCredits();

        // for CS students it is important to attend the highest rated subjects, sorting in desc order
        sortingSubjectsByRating(subjectsForSemester);

        // counting how many subjects we are going to apply
        int numberOfAttendingSubjects = calcSubjectsToAttend(minAmountCredits, subjectsForSemester);

        // these are the subjects that the computer since student will attend this semester
        UniversitySubject[] attendingSubjects = new UniversitySubject[numberOfAttendingSubjects];
        for(int i = 0; i < numberOfAttendingSubjects; i++) {
            attendingSubjects[i] = subjectsForSemester[i];
        }

        return attendingSubjects;
    }
}
