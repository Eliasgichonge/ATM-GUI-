import javax.swing.*; // Importing Swing components for GUI
import javax.swing.border.EmptyBorder; // For border management
import java.awt.*; // For layout management and color
import java.awt.event.ActionEvent; // For action events
import java.awt.event.ActionListener; // For action listener interface
import java.security.MessageDigest; // For hashing passwords
import java.security.NoSuchAlgorithmException; // For handling hashing errors
import java.util.ArrayList; // For using dynamic arrays
import java.util.HashMap; // For storing customer data
import java.util.List; // For using lists
import java.util.Map; // For using maps (key-value pairs)
import java.util.prefs.Preferences; // For storing user preferences

public class ATMGUI extends JFrame {
    private JTextField usernameField; // Field for username input
    private JPasswordField passwordField; // Field for password input
    private JTextArea transactionArea; // Area to display transaction history
    private JLabel balanceLabel; // Label to show account balance
    private Map<String, Customer> customers = new HashMap<>(); // Map to store customers
    private Customer currentCustomer; // Reference to the currently logged-in customer
    private CardLayout cardLayout; // Layout manager for switching panels
    private JPanel mainPanel; // Main panel to hold different screens
    private Preferences prefs; // Preferences for saving user data

    // Constructor to set up the ATM GUI
    public ATMGUI() {
        super("Gsu ATM Simulator"); // Set the window title
        setSize(600, 500); // Set the window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit on close
        setLocationRelativeTo(null); // Center the window on the screen
        prefs = Preferences.userNodeForPackage(ATMGUI.class); // Initialize preferences
        initializeGUI(); // Call method to set up the GUI
    }

    // Method to initialize the GUI components
    private void initializeGUI() {
        cardLayout = new CardLayout(); // Create CardLayout for panel switching
        mainPanel = new JPanel(cardLayout); // Initialize main panel with CardLayout
        mainPanel.setBackground(new Color(240, 240, 240)); // Set background color

        // Create and add different panels to the main panel
        JPanel welcomePanel = createWelcomePanel();
        mainPanel.add(welcomePanel, "Welcome"); // Add welcome panel

        JPanel atmPanel = createATMPanel();
        mainPanel.add(atmPanel, "ATM"); // Add ATM panel

        JPanel historyPanel = createHistoryPanel();
        mainPanel.add(historyPanel, "History"); // Add history panel

        add(mainPanel); // Add main panel to the frame
        cardLayout.show(mainPanel, "Welcome"); // Show the welcome panel by default
    }

    // Method to create the welcome panel
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout()); // Create panel with BorderLayout
        panel.setBackground(Color.WHITE); // Set background color
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

        // Create and add welcome label
        JLabel welcomeLabel = new JLabel("Welcome to the Dynamic ATM!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font style and size
        welcomeLabel.setForeground(new Color(50, 50, 150)); // Set text color
        panel.add(welcomeLabel, BorderLayout.NORTH); // Add label to the top

        // Create panel for username and password input
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // 3 rows, 2 columns
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

        // Initialize input fields and add to input panel
        usernameField = new JTextField(); // Text field for username
        passwordField = new JPasswordField(); // Password field for password
        inputPanel.add(new JLabel("Username:")); // Add username label
        inputPanel.add(usernameField); // Add username field
        inputPanel.add(new JLabel("Password:")); // Add password label
        inputPanel.add(passwordField); // Add password field

        // Create and add login and register buttons
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        inputPanel.add(loginButton); // Add login button
        inputPanel.add(registerButton); // Add register button

        panel.add(inputPanel, BorderLayout.CENTER); // Add input panel to the center

        // Create footer label
        JLabel footer = new JLabel("© 2024 GSU CEO ATM", JLabel.CENTER);
        footer.setFont(new Font("Arial", Font.ITALIC, 12)); // Set font style
        panel.add(footer, BorderLayout.SOUTH); // Add footer to the bottom

        // Add action listeners for buttons
        loginButton.addActionListener(e -> handleLogin()); // Handle login
        registerButton.addActionListener(e -> handleRegister()); // Handle registration

        // Load last entered username from preferences
        String lastUsername = prefs.get("lastUsername", ""); // Get last username
        usernameField.setText(lastUsername); // Set last username in the field

        return panel; // Return the welcome panel
    }

    // Method to create the ATM panel
    private JPanel createATMPanel() {
        JPanel panel = new JPanel(new BorderLayout()); // Create panel with BorderLayout
        panel.setBackground(Color.WHITE); // Set background color
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

        // Create title panel with title label
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Dynamic ATM"); // Title label
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font style
        titlePanel.add(titleLabel); // Add title label to title panel
        panel.add(titlePanel, BorderLayout.NORTH); // Add title panel to the top

        // Create and set up balance label
        balanceLabel = new JLabel("Balance: TSh. 0.0", JLabel.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font style
        balanceLabel.setForeground(new Color(0, 128, 0)); // Set text color
        panel.add(balanceLabel, BorderLayout.SOUTH); // Add balance label to the bottom

        // Create panel for action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // 3 rows, 2 columns
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

        // Create action buttons and add to button panel
        JButton withdrawButton = new JButton("Withdraw");
        JButton depositButton = new JButton("Deposit");
        JButton balanceButton = new JButton("Check Balance");
        JButton historyButton = new JButton("Transaction History");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(withdrawButton); // Add withdraw button
        buttonPanel.add(depositButton); // Add deposit button
        buttonPanel.add(balanceButton); // Add check balance button
        buttonPanel.add(historyButton); // Add transaction history button
        buttonPanel.add(logoutButton); // Add logout button

        panel.add(buttonPanel, BorderLayout.CENTER); // Add button panel to the center

        // Initialize transaction area for displaying transaction logs
        transactionArea = new JTextArea(6, 15); // 6 rows, 15 columns
        transactionArea.setEditable(false); // Make it non-editable
        transactionArea.setLineWrap(true); // Enable line wrapping
        transactionArea.setWrapStyleWord(true); // Wrap at word boundaries
        JScrollPane scrollPane = new JScrollPane(transactionArea); // Create scroll pane
        panel.add(scrollPane, BorderLayout.EAST); // Add scroll pane to the east

        // Add action listeners for buttons
        withdrawButton.addActionListener(e -> handleTransaction("Withdraw")); // Handle withdraw
        depositButton.addActionListener(e -> handleTransaction("Deposit")); // Handle deposit
        balanceButton.addActionListener(e -> displayDynamicBalance()); // Display balance
        historyButton.addActionListener(e -> showTransactionHistory()); // Show history
        logoutButton.addActionListener(e -> logout()); // Handle logout

        return panel; // Return the ATM panel
    }

    // Method to create the transaction history panel
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout()); // Create panel with BorderLayout
        panel.setBackground(Color.WHITE); // Set background color
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
        
        // Initialize history area and set it to be non-editable
        JTextArea historyArea = new JTextArea(15, 30); // 15 rows, 30 columns
        historyArea.setEditable(false); // Make it non-editable
        JScrollPane scrollPane = new JScrollPane(historyArea); // Create scroll pane
        panel.add(scrollPane, BorderLayout.CENTER); // Add scroll pane to the center

        // Create back button to return to the ATM panel
        JButton backButton = new JButton("Back to ATM");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ATM")); // Switch to ATM panel
        panel.add(backButton, BorderLayout.SOUTH); // Add button to the bottom

        return panel; // Return the history panel
    }

    // Method to handle login action
    private void handleLogin() {
        String username = usernameField.getText(); // Get username from field
        String password = new String(passwordField.getPassword()); // Get password from field

        // Validate credentials
        if (customers.containsKey(username) && customers.get(username).getPassword().equals(hashPassword(password))) {
            currentCustomer = customers.get(username); // Set current customer
            updateBalance(); // Update balance display
            transactionArea.setText("Welcome back, " + username + "!\n\n"); // Welcome message
            cardLayout.show(mainPanel, "ATM"); // Switch to ATM panel

            // Save the last entered username
            prefs.put("lastUsername", username); // Store username in preferences
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password."); // Show error message
        }
    }

    // Method to handle registration action
    private void handleRegister() {
        String username = usernameField.getText(); // Get username from field
        String password = new String(passwordField.getPassword()); // Get password from field

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password."); // Error for empty fields
        } else if (customers.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists."); // Error for existing username
        } else {
            // Create new customer and add to the map
            customers.put(username, new Customer(username, hashPassword(password), 60.0)); // Add new customer
            JOptionPane.showMessageDialog(this, "Registration successful!"); // Success message
        }
    }

    // Method to handle transactions (withdrawals or deposits)
    private void handleTransaction(String transactionType) {
        if (currentCustomer == null) { // Check if user is logged in
            JOptionPane.showMessageDialog(this, "Please log in first."); // Error for not logged in
            return; // Exit early
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter amount:"); // Prompt for amount
        if (amountStr != null && !amountStr.isEmpty()) { // Check if input is valid
            try {
                double amount = Double.parseDouble(amountStr); // Parse the amount
                if (amount <= 0) { // Check for non-positive amount
                    throw new NumberFormatException(); // Trigger exception for invalid input
                }
                // Handle deposit
                if ("Deposit".equals(transactionType)) {
                    currentCustomer.deposit(amount); // Deposit amount
                    currentCustomer.addTransaction("Deposited TShs." + amount); // Log transaction
                } 
                // Handle withdrawal
                else if ("Withdraw".equals(transactionType)) {
                    if (!currentCustomer.withdraw(amount)) { // Check for sufficient funds
                        JOptionPane.showMessageDialog(this, "Insufficient funds."); // Error for insufficient funds
                        return; // Exit early
                    }
                    currentCustomer.addTransaction("Withdrew TShs." + amount); // Log transaction
                }
                updateBalance(); // Update balance display
                transactionArea.append(transactionType + ": TShs." + amount + "\n"); // Log transaction in area
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid or negative amount."); // Error for invalid input
            }
        }
    }

    // Method to display the current balance
    private void displayDynamicBalance() {
        if (currentCustomer == null) { // Check if user is logged in
            JOptionPane.showMessageDialog(this, "Please log in first."); // Error for not logged in
            return; // Exit early
        }

        double currentBalance = currentCustomer.getBalance(); // Get current balance
        balanceLabel.setText(String.format("Balance: TShs. %.2f", currentBalance)); // Update balance label
        transactionArea.append("Checked Balance: TShs." + currentBalance + "\n"); // Log balance check
    }

    // Method to show transaction history
    private void showTransactionHistory() {
        if (currentCustomer == null) { // Check if user is logged in
            JOptionPane.showMessageDialog(this, "Please log in first."); // Error for not logged in
            return; // Exit early
        }

        // Retrieve and display transaction history
        JPanel historyPanel = (JPanel) mainPanel.getComponent(2); // Get history panel
        JTextArea historyArea = (JTextArea) ((JScrollPane) historyPanel.getComponent(0)).getViewport().getView(); // Get history text area
        historyArea.setText(currentCustomer.getTransactionHistory()); // Set history text
        cardLayout.show(mainPanel, "History"); // Switch to history panel
    }

    // Method to handle logout action
    private void logout() {
        currentCustomer = null; // Reset current customer
        cardLayout.show(mainPanel, "Welcome"); // Switch to welcome panel
    }

    // Method to update the balance label
    private void updateBalance() {
        balanceLabel.setText(String.format("Balance: TShs. %.2f", currentCustomer.getBalance())); // Update balance display
    }

    // Method to hash passwords for security
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // Create SHA-256 hash
            byte[] hashedBytes = md.digest(password.getBytes()); // Hash the password
            StringBuilder sb = new StringBuilder(); // StringBuilder for building hash string
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b)); // Convert to hex format
            }
            return sb.toString(); // Return hashed password
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e); // Handle exception
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ATMGUI().setVisible(true)); // Run GUI in the Event Dispatch Thread
    }
}

// Class representing a customer
class Customer {
    private String username; // Customer's username
    private String password; // Customer's hashed password
    private double balance; // Customer's account balance
    private List<String> transactionHistory; // List to hold transaction history

    // Constructor to initialize customer details
    public Customer(String username, String password, double balance) {
        this.username = username; // Set username
        this.password = password; // Set password
        this.balance = balance; // Set initial balance
        this.transactionHistory = new ArrayList<>(); // Initialize transaction history
    }

    // Getter for password
    public String getPassword() {
        return password; // Return hashed password
    }

    // Getter for balance
    public double getBalance() {
        return balance; // Return current balance
    }

    // Method to deposit money
    public void deposit(double amount) {
        balance += amount; // Increase balance by the deposit amount
    }

    // Method to withdraw money
    public boolean withdraw(double amount) {
        if (balance >= amount) { // Check if sufficient balance
            balance -= amount; // Decrease balance by withdrawal amount
            return true; // Successful withdrawal
        }
        return false; // Insufficient balance
    }

    // Method to add a transaction to history
    public void addTransaction(String transaction) {
        transactionHistory.add(transaction); // Add transaction to history
    }

    // Method to retrieve transaction history
    public String getTransactionHistory() {
        StringBuilder history = new StringBuilder("Transaction History:\n"); // Initialize history string
        for (String transaction : transactionHistory) {
            history.append(transaction).append("\n"); // Append each transaction
        }
        return history.toString(); // Return complete transaction history
    }
}