public class Exercise {

    private int exerciseId;
    private int workoutId;
    private String name;
    private int sets;
    private int reps;
    private double weightKg;
    private int durationMin;

    public Exercise(int exerciseId, int workoutId, String name, int sets, int reps,
                    double weightKg, int durationMin) {

        this.exerciseId = exerciseId;
        this.workoutId = workoutId;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
        this.durationMin = durationMin;

    }

    public void calculateVolume() {

        if (sets > 0 && reps > 0) {
            double volume = sets * reps * weightKg;
            System.out.println("Training volume: " + volume);
        }

    }

}