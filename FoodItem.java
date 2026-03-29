public class FoodItem {

    private int foodItemId;
    private int mealId;
    private String name;
    private double quantity;
    private String unit;
    private int calories;
    private double proteinG;
    private double carbsG;
    private double fatG;
    /* Adding food item */
    public FoodItem(int foodItemId, int mealId, String name, double quantity, String unit,
                    int calories, double proteinG, double carbsG, double fatG) {

        this.foodItemId = foodItemId;
        this.mealId = mealId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;

    }
    
    public int calculateCalories() {
        return calories;
    }

}