public class MealRecord {

    private int mealId;
    private int userId;
    private String date;
    private String mealType;
    private String notes;

    public MealRecord(int mealId, int userId, String date, String mealType, String notes) {

        this.mealId = mealId;
        this.userId = userId;
        this.date = date;
        this.mealType = mealType;
        this.notes = notes;

    }

    public void addMeal() {
        System.out.println("Meal added: " + mealType);
    }

    public void deleteMeal() {
        System.out.println("Meal deleted");
    }

}