public class User {

    private int userId;
    private String name;
    private String email;
    private String password;

    public User(int userId, String name, String email, String password) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;

    }

    public void registerUser() {
        System.out.println("User registered: " + name);
    }

    public void loginUser() {
        System.out.println("User logged in: " + email);
    }

}