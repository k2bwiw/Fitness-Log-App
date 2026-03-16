import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FitnessLogGUI {

    // ── Colour palette ──────────────────────────────────────────────
    private static final Color BG         = new Color(240, 250, 244);
    private static final Color WHITE      = Color.WHITE;
    private static final Color TOPBAR     = new Color(93, 202, 165);
    private static final Color ACCENT     = new Color(29, 158, 117);
    private static final Color BORDER     = new Color(192, 221, 151);
    private static final Color TEXT_DARK  = new Color(8, 80, 65);
    private static final Color TEXT_MID   = new Color(59, 109, 17);
    private static final Color TEXT_LIGHT = new Color(99, 153, 34);
    private static final Color PILL_BG    = new Color(234, 243, 222);
    private static final Color ICON_MINT  = new Color(225, 245, 238);
    private static final Color ERROR_RED  = new Color(180, 40, 40);
    private static final Color LINK_COLOR = new Color(29, 158, 117);

    private JFrame frame;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private int    loggedUserId   = -1;
    private String loggedUserName = "";

    public FitnessLogGUI() {
        buildFrame();
    }

    // ── Main frame ───────────────────────────────────────────────────
    private void buildFrame() {
        frame = new JFrame("Fitness Log App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 650);
        frame.setMinimumSize(new Dimension(600, 500));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(buildLoginScreen(), BorderLayout.CENTER);
        frame.getContentPane().setBackground(BG);
        frame.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════
    // LOGIN SCREEN
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildLoginScreen() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(36, 40, 36, 40)
        ));
        card.setPreferredSize(new Dimension(360, 430));

        // Logo
        JLabel logo = new JLabel("💪");
        logo.setFont(new Font("SansSerif", Font.PLAIN, 48));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Fitness Log");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        JTextField emailField = authField("Email");
        JPasswordField passField = new JPasswordField();
        stylePasswordField(passField, "Password");

        // Error label
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR_RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button
        JButton loginBtn = saveButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        ActionListener doLogin = e -> {
            String email    = emailField.getText().trim();
            String password = new String(passField.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter your email and password.");
                return;
            }
            int userId = checkLogin(email, password);
            if (userId > 0) {
                loggedUserId   = userId;
                loggedUserName = getUserName(userId);
                switchToMainApp();
            } else {
                errorLabel.setText("Incorrect email or password.");
                passField.setText("");
            }
        };
        loginBtn.addActionListener(doLogin);
        passField.addActionListener(doLogin);

        // "Don't have an account? Sign up" link
        JPanel switchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        switchRow.setOpaque(false);
        JLabel switchText = new JLabel("Don't have an account?");
        switchText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        switchText.setForeground(TEXT_LIGHT);
        JButton signupLink = linkButton("Sign up");
        signupLink.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(buildSignupScreen(), BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        });
        switchRow.add(switchText);
        switchRow.add(signupLink);

        card.add(logo);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(26));
        card.add(authFieldLabel("Email"));
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));
        card.add(authFieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(switchRow);

        outer.add(card);
        return outer;
    }

    // ══════════════════════════════════════════════════════════════════
    // SIGN UP SCREEN
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildSignupScreen() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));
        card.setPreferredSize(new Dimension(400, 580));

        JLabel logo = new JLabel("💪");
        logo.setFont(new Font("SansSerif", Font.PLAIN, 40));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Fill in your details to get started");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        JTextField nameField  = authField("Full Name");
        JTextField emailField = authField("Email address");
        JPasswordField passField    = new JPasswordField(); stylePasswordField(passField, "Password");
        JPasswordField confirmField = new JPasswordField(); stylePasswordField(confirmField, "Confirm Password");

        // Error / success label
        JLabel msgLabel = new JLabel(" ");
        msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        msgLabel.setForeground(ERROR_RED);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sign up button
        JButton signupBtn = saveButton("Create Account");
        signupBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        signupBtn.addActionListener(e -> {
            String name     = nameField.getText().trim();
            String email    = emailField.getText().trim();
            String pass     = new String(passField.getPassword());
            String confirm  = new String(confirmField.getPassword());

            // Validation
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                msgLabel.setForeground(ERROR_RED);
                msgLabel.setText("All fields are required.");
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                msgLabel.setForeground(ERROR_RED);
                msgLabel.setText("Please enter a valid email address.");
                return;
            }
            if (pass.length() < 6) {
                msgLabel.setForeground(ERROR_RED);
                msgLabel.setText("Password must be at least 6 characters.");
                return;
            }
            if (!pass.equals(confirm)) {
                msgLabel.setForeground(ERROR_RED);
                msgLabel.setText("Passwords do not match.");
                confirmField.setText("");
                return;
            }
            if (emailExists(email)) {
                msgLabel.setForeground(ERROR_RED);
                msgLabel.setText("An account with this email already exists.");
                return;
            }

            // Create account
            boolean created = registerUser(name, email, pass);
            if (created) {
                JOptionPane.showMessageDialog(frame,
                    "Account created! You can now sign in.",
                    "Welcome, " + name + "!",
                    JOptionPane.INFORMATION_MESSAGE);
                frame.getContentPane().removeAll();
                frame.add(buildLoginScreen(), BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            } else {
                msgLabel.setForeground(ERROR_RED);
                msgLabel.setText("Something went wrong. Please try again.");
            }
        });

        // Back to login link
        JPanel switchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        switchRow.setOpaque(false);
        JLabel switchText = new JLabel("Already have an account?");
        switchText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        switchText.setForeground(TEXT_LIGHT);
        JButton loginLink = linkButton("Sign in");
        loginLink.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(buildLoginScreen(), BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        });
        switchRow.add(switchText);
        switchRow.add(loginLink);

        card.add(logo);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(22));
        card.add(authFieldLabel("Full Name"));
        card.add(Box.createVerticalStrut(4));
        card.add(nameField);
        card.add(Box.createVerticalStrut(12));
        card.add(authFieldLabel("Email address"));
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));
        card.add(authFieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passField);
        card.add(Box.createVerticalStrut(12));
        card.add(authFieldLabel("Confirm Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(confirmField);
        card.add(Box.createVerticalStrut(8));
        card.add(msgLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(signupBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(switchRow);

        outer.add(card);
        return outer;
    }

    // ── DB: check login ───────────────────────────────────────────────
    private int checkLogin(String email, String password) {
        String sql = "SELECT userId FROM Users WHERE Email = ? AND Password = ?";
        try (Connection conn = new DatabaseManager().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("userId");
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return -1;
    }

    // ── DB: get user name ─────────────────────────────────────────────
    private String getUserName(int userId) {
        String sql = "SELECT Name FROM Users WHERE userId = ?";
        try (Connection conn = new DatabaseManager().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("Name");
        } catch (SQLException e) {
            System.out.println("Get name error: " + e.getMessage());
        }
        return "User";
    }

    // ── DB: check if email already exists ─────────────────────────────
    private boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        try (Connection conn = new DatabaseManager().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Email check error: " + e.getMessage());
        }
        return false;
    }

    // ── DB: register new user ─────────────────────────────────────────
    private boolean registerUser(String name, String email, String password) {
        String sql = "INSERT INTO Users (Name, Email, Password, createAt) VALUES (?, ?, ?, GETDATE())";
        try (Connection conn = new DatabaseManager().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Register error: " + e.getMessage());
        }
        return false;
    }

    // ── Switch to main app ────────────────────────────────────────────
    private void switchToMainApp() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        frame.add(buildTopBar(),  BorderLayout.NORTH);
        frame.add(buildContent(), BorderLayout.CENTER);
        frame.add(buildNavBar(),  BorderLayout.SOUTH);
        frame.revalidate();
        frame.repaint();
    }

    // ── Top bar ──────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(TOPBAR);
        bar.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel logo = new JLabel("💪  Fitness Log");
        logo.setFont(new Font("SansSerif", Font.BOLD, 17));
        logo.setForeground(WHITE);

        JButton logout = new JButton("Logout");
        logout.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logout.setForeground(WHITE);
        logout.setBackground(new Color(15, 110, 86));
        logout.setBorderPainted(false);
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> {
            loggedUserId   = -1;
            loggedUserName = "";
            frame.getContentPane().removeAll();
            frame.setLayout(new BorderLayout());
            frame.add(buildLoginScreen(), BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        });

        JLabel user = pill("  " + loggedUserName + "  ", WHITE, ACCENT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(user);
        right.add(logout);

        bar.add(logo,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Card layout ───────────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);
        contentPanel.add(buildHomePage(),    "Home");
        contentPanel.add(buildWorkoutPage(), "Workout");
        contentPanel.add(buildFoodPage(),    "Food");
        contentPanel.add(buildHistoryPage(), "History");
        contentPanel.add(buildGoalsPage(),   "Goals");
        return contentPanel;
    }

    // ── Nav bar ───────────────────────────────────────────────────────
    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new GridLayout(1, 5));
        nav.setBackground(WHITE);
        nav.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));
        String[][] items = {{"🏠","Home"},{"🏋️","Workout"},{"🥗","Food"},{"📋","History"},{"🎯","Goals"}};
        for (String[] item : items) nav.add(navButton(item[0], item[1]));
        return nav;
    }

    private JButton navButton(String icon, String label) {
        JButton btn = new JButton("<html><center>" + icon + "<br><span style='font-size:9px'>" + label + "</span></center></html>");
        btn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 60));
        btn.addActionListener(e -> { cardLayout.show(contentPanel, label); btn.setForeground(ACCENT); });
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(PILL_BG); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(WHITE); }
        });
        return btn;
    }

    // ── HOME PAGE ────────────────────────────────────────────────────
    private JScrollPane buildHomePage() {
        JPanel page = scrollPage();

        JLabel greeting = new JLabel("Welcome, " + loggedUserName + "!");
        greeting.setFont(new Font("SansSerif", Font.BOLD, 22));
        greeting.setForeground(TEXT_DARK);
        greeting.setBorder(new EmptyBorder(0, 0, 6, 0));

        JLabel statusPill = pill("● DB connected", TEXT_MID, PILL_BG);

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setOpaque(false);
        stats.add(statCard("🔥", "Calories", "0", "kcal"));
        stats.add(statCard("🏋️", "Workouts", "0", "done"));
        stats.add(statCard("🥗", "Meals",    "0", "logged"));

        JPanel recent = new JPanel();
        recent.setLayout(new BoxLayout(recent, BoxLayout.Y_AXIS));
        recent.setBackground(WHITE);
        recent.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(20, 0, 20, 0)));
        JLabel empty = new JLabel("No activity yet — start logging!");
        empty.setFont(new Font("SansSerif", Font.PLAIN, 13));
        empty.setForeground(TEXT_LIGHT);
        empty.setAlignmentX(Component.CENTER_ALIGNMENT);
        recent.add(empty);

        page.add(greeting);
        page.add(statusPill);
        page.add(Box.createVerticalStrut(16));
        page.add(sectionTitle("Today's summary"));
        page.add(stats);
        page.add(Box.createVerticalStrut(16));
        page.add(sectionTitle("Recent activity"));
        page.add(recent);

        return wrap(page);
    }

    // ── WORKOUT PAGE ─────────────────────────────────────────────────
    private JScrollPane buildWorkoutPage() {
        JPanel page = scrollPage();
        page.add(pageHeader("🏋️", "Add Workout"));
        page.add(Box.createVerticalStrut(14));

        String[] labels = {"Date (YYYY-MM-DD)", "Workout Type", "Notes", "Exercise Name",
                           "Sets", "Reps", "Weight (kg)", "Duration (min)"};
        JTextField[] fields = new JTextField[labels.length];
        JPanel form = formPanel();
        for (int i = 0; i < labels.length; i++) {
            fields[i] = styledField();
            form.add(formRow(labels[i], fields[i]));
        }
        fields[0].setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        page.add(form);
        page.add(Box.createVerticalStrut(14));

        JButton save = saveButton("Save Workout");
        save.addActionListener(e -> {
            try {
                boolean ok = new DatabaseManager().addWorkoutWithExercise(
                    loggedUserId,
                    fields[0].getText(), fields[1].getText(), fields[2].getText(), fields[3].getText(),
                    Integer.parseInt(fields[4].getText().isEmpty()  ? "0" : fields[4].getText()),
                    Integer.parseInt(fields[5].getText().isEmpty()  ? "0" : fields[5].getText()),
                    Double.parseDouble(fields[6].getText().isEmpty()? "0" : fields[6].getText()),
                    Integer.parseInt(fields[7].getText().isEmpty()  ? "0" : fields[7].getText())
                );
                if (ok) {
                    JOptionPane.showMessageDialog(frame, "Workout saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    for (int i = 1; i < fields.length; i++) fields[i].setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to save workout.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers for Sets, Reps, Weight and Duration.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        page.add(save);
        return wrap(page);
    }

    // ── FOOD PAGE ────────────────────────────────────────────────────
    private JScrollPane buildFoodPage() {
        JPanel page = scrollPage();
        page.add(pageHeader("🥗", "Add Food"));
        page.add(Box.createVerticalStrut(14));

        String[] labels = {"Date (YYYY-MM-DD)", "Meal Type", "Notes", "Food Name",
                           "Quantity", "Unit", "Calories", "Protein (g)", "Carbs (g)", "Fat (g)"};
        JTextField[] fields = new JTextField[labels.length];
        JPanel form = formPanel();
        for (int i = 0; i < labels.length; i++) {
            fields[i] = styledField();
            form.add(formRow(labels[i], fields[i]));
        }
        fields[0].setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        page.add(form);
        page.add(Box.createVerticalStrut(14));

        JButton save = saveButton("Save Meal");
        save.addActionListener(e -> {
            try {
                boolean ok = new DatabaseManager().addMealWithFood(
                    loggedUserId,
                    fields[0].getText(), fields[1].getText(), fields[2].getText(), fields[3].getText(),
                    Double.parseDouble(fields[4].getText().isEmpty() ? "0" : fields[4].getText()),
                    fields[5].getText(),
                    Integer.parseInt(fields[6].getText().isEmpty()   ? "0" : fields[6].getText()),
                    Double.parseDouble(fields[7].getText().isEmpty() ? "0" : fields[7].getText()),
                    Double.parseDouble(fields[8].getText().isEmpty() ? "0" : fields[8].getText()),
                    Double.parseDouble(fields[9].getText().isEmpty() ? "0" : fields[9].getText())
                );
                if (ok) {
                    JOptionPane.showMessageDialog(frame, "Meal saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    for (int i = 1; i < fields.length; i++) fields[i].setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to save meal.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers for Quantity, Calories, Protein, Carbs and Fat.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        page.add(save);
        return wrap(page);
    }

    // ── HISTORY PAGE ─────────────────────────────────────────────────
    private JScrollPane buildHistoryPage() {
        JPanel page = scrollPage();
        page.add(pageHeader("📋", "History"));
        page.add(Box.createVerticalStrut(14));

        String[] cols = {"Date", "Type", "Details", "Calories"};
        java.util.List<String[]> historyRows = new DatabaseManager().getHistory(loggedUserId);

        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        for (String[] row : historyRows) model.addRow(row);

        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setBackground(WHITE);
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER);
        table.getTableHeader().setBackground(PILL_BG);
        table.getTableHeader().setForeground(TEXT_MID);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionBackground(ICON_MINT);

        JPanel tableCard = card();
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        tableCard.setPreferredSize(new Dimension(0, 300));

        JButton refresh = saveButton("↻  Refresh");
        refresh.addActionListener(e -> {
            model.setRowCount(0);
            for (String[] row : new DatabaseManager().getHistory(loggedUserId)) model.addRow(row);
        });

        if (historyRows.isEmpty()) {
            JLabel none = new JLabel("No records yet — start logging workouts and meals!");
            none.setFont(new Font("SansSerif", Font.PLAIN, 13));
            none.setForeground(TEXT_LIGHT);
            none.setAlignmentX(Component.LEFT_ALIGNMENT);
            page.add(none);
            page.add(Box.createVerticalStrut(10));
        }

        page.add(tableCard);
        page.add(Box.createVerticalStrut(10));
        page.add(refresh);
        return wrap(page);
    }

    // ── GOALS PAGE ───────────────────────────────────────────────────
    private JScrollPane buildGoalsPage() {
        JPanel page = scrollPage();
        page.add(pageHeader("🎯", "Goals"));
        page.add(Box.createVerticalStrut(16));

        // ── Add goal form ──
        page.add(sectionTitle("Add a new goal"));
        page.add(Box.createVerticalStrut(8));

        JPanel form = formPanel();

        JTextField typeField  = styledField();
        JTextField targetField = styledField();
        JTextField startField = styledField();
        JTextField endField   = styledField();

        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        startField.setText(today);

        form.add(formRow("Goal Type (e.g. calories)", typeField));
        form.add(formRow("Target Value",              targetField));
        form.add(formRow("Start Date (YYYY-MM-DD)",   startField));
        form.add(formRow("End Date (YYYY-MM-DD)",     endField));

        page.add(form);
        page.add(Box.createVerticalStrut(12));

        // ── Goals table ──
        String[] cols = {"Goal Type", "Target", "Start Date", "End Date", "Active", "ID"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        // Load existing goals
        for (String[] row : new DatabaseManager().getGoals(loggedUserId)) model.addRow(row);

        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setBackground(WHITE);
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER);
        table.getTableHeader().setBackground(PILL_BG);
        table.getTableHeader().setForeground(TEXT_MID);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionBackground(ICON_MINT);
        // Hide the ID column (used internally for delete)
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        JPanel tableCard = card();
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        tableCard.setPreferredSize(new Dimension(0, 200));

        // ── Buttons row ──
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);

        JButton addBtn = saveButton("Save Goal");
        addBtn.addActionListener(e -> {
            String goalType = typeField.getText().trim();
            String targetStr = targetField.getText().trim();
            String start = startField.getText().trim();
            String end   = endField.getText().trim();

            if (goalType.isEmpty() || targetStr.isEmpty() || start.isEmpty() || end.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double target = Double.parseDouble(targetStr);
                boolean ok = new DatabaseManager().addGoal(loggedUserId, goalType, target, start, end);
                if (ok) {
                    JOptionPane.showMessageDialog(frame, "Goal saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    typeField.setText(""); targetField.setText(""); endField.setText("");
                    model.setRowCount(0);
                    for (String[] row : new DatabaseManager().getGoals(loggedUserId)) model.addRow(row);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to save goal.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Target value must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton deleteBtn = saveButton("Delete Selected");
        deleteBtn.setBackground(new Color(160, 40, 40));
        deleteBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { deleteBtn.setBackground(new Color(200, 60, 60)); }
            public void mouseExited(MouseEvent e)  { deleteBtn.setBackground(new Color(160, 40, 40)); }
        });
        deleteBtn.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a goal to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int goalId = Integer.parseInt(model.getValueAt(selected, 5).toString());
            boolean ok = new DatabaseManager().deleteGoal(goalId);
            if (ok) {
                model.removeRow(selected);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to delete goal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(addBtn);
        btnRow.add(deleteBtn);

        page.add(btnRow);
        page.add(Box.createVerticalStrut(20));
        page.add(sectionTitle("Your goals"));
        page.add(Box.createVerticalStrut(8));
        page.add(tableCard);

        return wrap(page);
    }

    // ══════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════

    private JPanel scrollPage() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
        return p;
    }

    private JScrollPane wrap(JPanel page) {
        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setBackground(BG);
        return scroll;
    }

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(TEXT_MID);
        l.setBorder(new EmptyBorder(0, 0, 6, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel pageHeader(String icon, String title) {
        JLabel l = new JLabel(icon + "  " + title);
        l.setFont(new Font("SansSerif", Font.BOLD, 22));
        l.setForeground(TEXT_DARK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel authFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setForeground(TEXT_MID);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField authField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setForeground(TEXT_DARK);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(6, 10, 6, 10)));
        f.setBackground(new Color(248, 254, 250));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return f;
    }

    private void stylePasswordField(JPasswordField f, String placeholder) {
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setForeground(TEXT_DARK);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(6, 10, 6, 10)));
        f.setBackground(new Color(248, 254, 250));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
    }

    private JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(LINK_COLOR);
        b.setBackground(null);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel pill(String text, Color fg, Color bg) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(fg);
        l.setBackground(bg);
        l.setOpaque(true);
        l.setBorder(new EmptyBorder(3, 10, 3, 10));
        return l;
    }

    private JPanel statCard(String icon, String label, String value, String unit) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(12, 14, 12, 14)));
        JLabel ic = new JLabel(icon); ic.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JLabel lb = new JLabel(label); lb.setFont(new Font("SansSerif", Font.PLAIN, 11)); lb.setForeground(TEXT_MID);
        JLabel vl = new JLabel(value); vl.setFont(new Font("SansSerif", Font.BOLD, 22)); vl.setForeground(TEXT_DARK);
        JLabel un = new JLabel(unit);  un.setFont(new Font("SansSerif", Font.PLAIN, 11)); un.setForeground(TEXT_LIGHT);
        p.add(ic); p.add(Box.createVerticalStrut(4));
        p.add(lb); p.add(vl); p.add(un);
        return p;
    }

    private JPanel formPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(14, 16, 14, 16)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JPanel formRow(String label, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setBorder(new EmptyBorder(4, 0, 4, 0));
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setForeground(TEXT_MID);
        l.setPreferredSize(new Dimension(160, 30));
        row.add(l, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setForeground(TEXT_DARK);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(4, 8, 4, 8)));
        f.setBackground(new Color(248, 254, 250));
        return f;
    }

    private JButton saveButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(ACCENT);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(200, 40));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(TOPBAR); }
            public void mouseExited(MouseEvent e)  { b.setBackground(ACCENT); }
        });
        return b;
    }

    private JPanel card() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(new LineBorder(BORDER, 1, true));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        return p;
    }

    // ── Entry point ──────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new FitnessLogGUI();
        });
    }
}