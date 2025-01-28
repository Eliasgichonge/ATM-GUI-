import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class ATMGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea transactionArea;
    private JLabel balanceLabel;
    private Map<String, Customer> customers = new HashMap<>();
    private Customer currentCustomer;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Preferences prefs;

    public ATMGUI() {
        super("Gsu ATM Simulator");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        prefs = Preferences.userNodeForPackage(ATMGUI.class);
        initializeGUI();
    }

    private void initializeGUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(240, 240, 240));

        JPanel welcomePanel = createWelcomePanel();
        mainPanel.add(welcomePanel, "Welcome");

        JPanel atmPanel = createATMPanel();
        mainPanel.add(atmPanel, "ATM");

        JPanel historyPanel = createHistoryPanel();
        mainPanel.add(historyPanel, "History");

        add(mainPanel);
        cardLayout.show(mainPanel, "Welcome");
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome to the Dynamic ATM!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(50, 50, 150));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        inputPanel.add(loginButton);
        inputPanel.add(registerButton);

        panel.add(inputPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("© 2024 GSU CEO ATM", JLabel.CENTER);
        footer.setFont(new Font("Arial", Font.ITALIC, 12));
        panel.add(footer, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());

        // Load last entered username
        String lastUsername = prefs.get("lastUsername", "");
        usernameField.setText(lastUsername);

        return panel;
    }

    private JPanel createATMPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Dynamic ATM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        balanceLabel = new JLabel("Balance: TSh. 0.0", JLabel.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        balanceLabel.setForeground(new Color(0, 128, 0)); // Green color for balance
        panel.add(balanceLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton withdrawButton = new JButton("Withdraw");
        JButton depositButton = new JButton("Deposit");
        JButton balanceButton = new JButton("Check Balance");
        JButton historyButton = new JButton("Transaction History");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(withdrawButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(balanceButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.CENTER);

        transactionArea = new JTextArea(6, 15);
        transactionArea.setEditable(false);
        transactionArea.setLineWrap(true);
        transactionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(transactionArea);
        panel.add(scrollPane, BorderLayout.EAST);

        withdrawButton.addActionListener(e -> handleTransaction("Withdraw"));
        depositButton.addActionListener(e -> handleTransaction("Deposit"));
        balanceButton.addActionListener(e -> displayDynamicBalance());
        historyButton.addActionListener(e -> showTransactionHistory());
        logoutButton.addActionListener(e -> logout());

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextArea historyArea = new JTextArea(15, 30);
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to ATM");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ATM"));
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (customers.containsKey(username) && customers.get(username).getPassword().equals(hashPassword(password))) {
            currentCustomer = customers.get(username);
            updateBalance();
            transactionArea.setText("Welcome back, " + username + "!\n\n");
            cardLayout.show(mainPanel, "ATM");

            // Save the last entered username
            prefs.put("lastUsername", username);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
        } else if (customers.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists.");
        } else {
            customers.put(username, new Customer(username, hashPassword(password), 60.0));
            JOptionPane.showMessageDialog(this, "Registration successful!");
        }
    }

    private void handleTransaction(String transactionType) {
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please log in first.");
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter amount:");
        if (amountStr != null && !amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    throw new NumberFormatException();
                }
                if ("Deposit".equals(transactionType)) {
                    currentCustomer.deposit(amount);
                    currentCustomer.addTransaction("Deposited TShs." + amount);
                } else if ("Withdraw".equals(transactionType)) {
                    if (!currentCustomer.withdraw(amount)) {
                        JOptionPane.showMessageDialog(this, "Insufficient funds.");
                        return;
                    }
                    currentCustomer.addTransaction("Withdrew TShs." + amount);
                }
                updateBalance();
                transactionArea.append(transactionType + ": TShs." + amount + "\n");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid or negative amount.");
            }
        }
    }

    private void displayDynamicBalance() {
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please log in first.");
            return;
        }

        double currentBalance = currentCustomer.getBalance();
        balanceLabel.setText(String.format("Balance: TShs. %.2f", currentBalance));
        transactionArea.append("Checked Balance: TShs." + currentBalance + "\n");
    }

    private void showTransactionHistory() {
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please log in first.");
            return;
        }

        JPanel historyPanel = (JPanel) mainPanel.getComponent(2);
        JTextArea historyArea = (JTextArea) ((JScrollPane) historyPanel.getComponent(0)).getViewport().getView();
        historyArea.setText(currentCustomer.getTransactionHistory());
        cardLayout.show(mainPanel, "History");
    }

    private void logout() {
        currentCustomer = null;
        cardLayout.show(mainPanel, "Welcome");
    }

    private void updateBalance() {
        balanceLabel.setText(String.format("Balance: TShs. %.2f", currentCustomer.getBalance()));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ATMGUI().setVisible(true));
    }
}

class Customer {
    private String username;
    private String password;
    private double balance;
    private List<String> transactionHistory;

    public Customer(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    public String getTransactionHistory() {
        StringBuilder history = new StringBuilder("Transaction History:\n");
        for (String transaction : transactionHistory) {
            history.append(transaction).append("\n");
        }
        return history.toString();
    }
}