public class WorkoutRecord {

    private int workoutId;
    private int userId;
    private String date;
    private String workoutType;
    private String notes;

    public WorkoutRecord(int workoutId, int userId, String date, String workoutType, String notes) {

        this.workoutId = workoutId;
        this.userId = userId;
        this.date = date;
        this.workoutType = workoutType;
        this.notes = notes;

    }
    /* Adding workout */
    public void addWorkout() {
        System.out.println("Workout added: " + workoutType);
    }
    /* Deleting workout */
    public void deleteWorkout() {
        System.out.println("Workout deleted");
    }

}