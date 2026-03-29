public class Goal {

    private int goalId;
    private int userId;
    private String goalType;
    private double targetValue;
    private String startDate;
    private String endDate;
    private boolean isActive;

    public Goal(int goalId, int userId, String goalType, double targetValue,
                String startDate, String endDate, boolean isActive) {

        this.goalId = goalId;
        this.userId = userId;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;

    }
    /* Set a goal */
    public void setGoal() {
        System.out.println("Goal set: " + goalType);
    }

}