package bg.sofia.uni.fmi.mjt.burnout.semester;

public sealed abstract class AbstractSemesterPlanner implements SemesterPlannerAPI permits ComputerScienceSemesterPlanner, SoftwareEngineeringSemesterPlanner{
}
