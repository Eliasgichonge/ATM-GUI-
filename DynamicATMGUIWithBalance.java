import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class DynamicATMGUIWithBalance extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea transactionArea;
    private JLabel balanceLabel;
    private Map<String, Customer> customers = new HashMap<>();
    private Customer currentCustomer;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public DynamicATMGUIWithBalance() {
        super("Gsu ATM Simulator");
        setSize(500, 500); // Reduced size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeGUI();
    }

    private void initializeGUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Welcome Screen
        JPanel welcomePanel = createWelcomePanel();
        mainPanel.add(welcomePanel, "Welcome");

        // Main ATM Screen
        JPanel atmPanel = createATMPanel();
        mainPanel.add(atmPanel, "ATM");

        add(mainPanel);
        cardLayout.show(mainPanel, "Welcome");
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Reduced border

        JLabel welcomeLabel = new JLabel("Welcome to the Dynamic ATM!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Reduced font size
        panel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // Reduced padding
        inputPanel.setBorder(new EmptyBorder(5, 10, 5, 10)); // Reduced border

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
        footer.setFont(new Font("Arial", Font.ITALIC, 10)); // Reduced font size
        panel.add(footer, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());

        return panel;
    }

    private JPanel createATMPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Dynamic ATM", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Reduced font size
        topPanel.add(titleLabel, BorderLayout.NORTH);

        balanceLabel = new JLabel("Balance: TSh. 0.0", JLabel.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Reduced font size
        balanceLabel.setForeground(Color.BLUE);
        topPanel.add(balanceLabel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // Reduced padding
        buttonPanel.setBorder(new EmptyBorder(5, 10, 5, 10)); // Reduced border

        JButton withdrawButton = new JButton("Withdraw");
        JButton depositButton = new JButton("Deposit");
        JButton balanceButton = new JButton("Check Balance");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(withdrawButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(balanceButton);
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.CENTER);

        transactionArea = new JTextArea(6, 15); // Adjusted size
        transactionArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(transactionArea);
        panel.add(scrollPane, BorderLayout.SOUTH);

        withdrawButton.addActionListener(e -> handleTransaction("Withdraw"));
        depositButton.addActionListener(e -> handleTransaction("Deposit"));
        balanceButton.addActionListener(e -> displayDynamicBalance());
        logoutButton.addActionListener(e -> logout());

        return panel;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (customers.containsKey(username) && customers.get(username).getPassword().equals(password)) {
            currentCustomer = customers.get(username);
            updateBalance();
            transactionArea.setText("Welcome back, " + username + "!\n\n");
            cardLayout.show(mainPanel, "ATM");
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
            customers.put(username, new Customer(username, password, 60.0));
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
                if ("Deposit".equals(transactionType)) {
                    currentCustomer.deposit(amount);
                } else if ("Withdraw".equals(transactionType)) {
                    if (!currentCustomer.withdraw(amount)) {
                        JOptionPane.showMessageDialog(this, "Insufficient funds.");
                        return;
                    }
                }
                updateBalance();
                transactionArea.append(transactionType + ": TShs." + amount + "\n");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        }
    }

    private void displayDynamicBalance() {
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please log in first.");
            return;
        }

        // Create a smooth animation for balance update
        new Thread(() -> {
            double currentBalance = currentCustomer.getBalance();
            double displayedBalance = 0;
            balanceLabel.setForeground(Color.GREEN); // Change color to green during animation

            while (displayedBalance < currentBalance) {
                displayedBalance += Math.min(10, currentBalance - displayedBalance); // Increment by 10
                balanceLabel.setText(String.format("Balance: TShs. %.2f", displayedBalance));
                try {
                    Thread.sleep(50); // Pause for animation effect
                } catch (InterruptedException ignored) {
                }
            }

            balanceLabel.setForeground(Color.BLUE); // Revert color after animation
            transactionArea.append("Checked Balance: TShs." + currentBalance + "\n");
        }).start();
    }

    private void logout() {
        currentCustomer = null;
        cardLayout.show(mainPanel, "Welcome");
    }

    private void updateBalance() {
        balanceLabel.setText(String.format("Balance: TShs. %.2f", currentCustomer.getBalance()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DynamicATMGUIWithBalance().setVisible(true));
    }
}

class Customer {
    private String username;
    private String password;
    private double balance;

    public Customer(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
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
}
