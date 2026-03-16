import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL =
        "jdbc:sqlserver://localhost:1433;instanceName=SQLEXPRESS;databaseName=fitness_log_app;encrypt=false;trustServerCertificate=true";

    private static final String USER     = "sa";
    private static final String PASSWORD = "Okcomputer"; // Change to your sa password

    public Connection connect() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC driver not found.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public boolean testConnection() {
        try (Connection conn = connect()) {
            return conn != null;
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    // ── ADD MEAL + FOOD ───────────────────────────────────────────────
    public boolean addMealWithFood(int userId, String date, String mealType, String notes,
                                   String foodName, double quantity, String unit,
                                   int calories, double proteinG, double carbsG, double fatG) {

        String insertMeal = "INSERT INTO MealRecord (userId, Date, mealType, Notes) VALUES (?, ?, ?, ?)";
        String insertFood = "INSERT INTO FoodItem (mealId, Name, Quantity, Unit, Calories, proteinG, carbsG, fatG) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement mealStmt = null;
        PreparedStatement foodStmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = connect();
            conn.setAutoCommit(false);

            mealStmt = conn.prepareStatement(insertMeal, Statement.RETURN_GENERATED_KEYS);
            mealStmt.setInt(1, userId);
            mealStmt.setString(2, date);
            mealStmt.setString(3, mealType);
            mealStmt.setString(4, notes);
            mealStmt.executeUpdate();

            generatedKeys = mealStmt.getGeneratedKeys();
            int mealId = -1;
            if (generatedKeys.next()) {
                mealId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to get mealId.");
            }

            foodStmt = conn.prepareStatement(insertFood);
            foodStmt.setInt(1, mealId);
            foodStmt.setString(2, foodName);
            foodStmt.setDouble(3, quantity);
            foodStmt.setString(4, unit);
            foodStmt.setInt(5, calories);
            foodStmt.setDouble(6, proteinG);
            foodStmt.setDouble(7, carbsG);
            foodStmt.setDouble(8, fatG);
            foodStmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            System.out.println("Error adding meal and food: " + e.getMessage());
            return false;
        } finally {
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) {}
            try { if (mealStmt != null) mealStmt.close(); } catch (SQLException e) {}
            try { if (foodStmt != null) foodStmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    // ── ADD WORKOUT + EXERCISE ────────────────────────────────────────
    public boolean addWorkoutWithExercise(int userId, String date, String workoutType, String notes,
                                          String exerciseName, int sets, int reps,
                                          double weightKg, int durationMin) {

        String insertWorkout  = "INSERT INTO WorkoutRecord (userId, Date, workoutType, Notes) VALUES (?, ?, ?, ?)";
        String insertExercise = "INSERT INTO Exercise (workoutId, Name, Sets, Reps, weightKg, durationMin) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement workoutStmt  = null;
        PreparedStatement exerciseStmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = connect();
            conn.setAutoCommit(false);

            workoutStmt = conn.prepareStatement(insertWorkout, Statement.RETURN_GENERATED_KEYS);
            workoutStmt.setInt(1, userId);
            workoutStmt.setString(2, date);
            workoutStmt.setString(3, workoutType);
            workoutStmt.setString(4, notes);
            workoutStmt.executeUpdate();

            generatedKeys = workoutStmt.getGeneratedKeys();
            int workoutId = -1;
            if (generatedKeys.next()) {
                workoutId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to get workoutId.");
            }

            exerciseStmt = conn.prepareStatement(insertExercise);
            exerciseStmt.setInt(1, workoutId);
            exerciseStmt.setString(2, exerciseName);
            exerciseStmt.setInt(3, sets);
            exerciseStmt.setInt(4, reps);
            exerciseStmt.setDouble(5, weightKg);
            exerciseStmt.setInt(6, durationMin);
            exerciseStmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            System.out.println("Error adding workout and exercise: " + e.getMessage());
            return false;
        } finally {
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) {}
            try { if (workoutStmt != null) workoutStmt.close(); } catch (SQLException e) {}
            try { if (exerciseStmt != null) exerciseStmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    // ── GET HISTORY (meals + workouts combined) ───────────────────────
    // Returns rows of: { Date, Type, Details, Calories }
    public List<String[]> getHistory(int userId) {
        List<String[]> rows = new ArrayList<>();

        // Meals
        String mealSql =
            "SELECT m.Date, m.mealType, f.Name, f.Calories " +
            "FROM MealRecord m " +
            "JOIN FoodItem f ON m.mealId = f.mealId " +
            "WHERE m.userId = ? " +
            "ORDER BY m.Date DESC";

        // Workouts
        String workoutSql =
            "SELECT w.Date, w.workoutType, e.Name, 0 AS Calories " +
            "FROM WorkoutRecord w " +
            "JOIN Exercise e ON w.workoutId = e.workoutId " +
            "WHERE w.userId = ? " +
            "ORDER BY w.Date DESC";

        try (Connection conn = connect()) {
            try (PreparedStatement stmt = conn.prepareStatement(mealSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    rows.add(new String[]{
                        rs.getString("Date"),
                        "Meal — " + rs.getString("mealType"),
                        rs.getString("Name"),
                        rs.getInt("Calories") + " kcal"
                    });
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(workoutSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    rows.add(new String[]{
                        rs.getString("Date"),
                        "Workout — " + rs.getString("workoutType"),
                        rs.getString("Name"),
                        "—"
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching history: " + e.getMessage());
        }

        // Sort all rows by date descending
        rows.sort((a, b) -> b[0].compareTo(a[0]));
        return rows;
    }

    // ── GET GOALS ─────────────────────────────────────────────────────
    public List<String[]> getGoals(int userId) {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT goalId, goalType, targetValue, startDate, endDate, isActive FROM Goal WHERE userId = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rows.add(new String[]{
                    String.valueOf(rs.getInt("goalId")),
                    rs.getString("goalType"),
                    String.valueOf(rs.getDouble("targetValue")),
                    rs.getString("startDate"),
                    rs.getString("endDate"),
                    rs.getBoolean("isActive") ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            System.out.println("Error fetching goals: " + e.getMessage());
        }
        return rows;
    }

    // ── ADD GOAL ──────────────────────────────────────────────────────
    public boolean addGoal(int userId, String goalType, double targetValue, String startDate, String endDate) {
        String sql = "INSERT INTO Goal (userId, goalType, targetValue, startDate, endDate, isActive) VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, goalType);
            stmt.setDouble(3, targetValue);
            stmt.setString(4, startDate);
            stmt.setString(5, endDate);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding goal: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE GOAL ───────────────────────────────────────────────────
    public boolean deleteGoal(int goalId) {
        String sql = "DELETE FROM Goal WHERE goalId = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goalId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting goal: " + e.getMessage());
            return false;
        }
    }
}