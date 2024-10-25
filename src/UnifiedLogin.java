import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UnifiedLogin extends JFrame {
    private JLabel labelLogin, labelUsername, labelPassword;
    private JTextField textFieldUsername;
    private JPasswordField passwordField;
    private JButton buttonLogin, buttonManagement;

    public UnifiedLogin() {
        initComponents();
    }

    private void initComponents() {
        labelLogin = createLabel("Log In", Font.BOLD, 36, SwingConstants.CENTER);
        labelUsername = createLabel("Username:", Font.PLAIN, 18, SwingConstants.RIGHT);
        labelPassword = createLabel("Password:", Font.PLAIN, 18, SwingConstants.RIGHT);

        textFieldUsername = createTextField(25);
        passwordField = createPasswordField(25);

        buttonLogin = createButton("Log In", 16, e -> handleLoginActionPerformed());
        buttonManagement = createButton("Home", 16, e -> handleManagementActionPerformed());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel labelPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        labelPanel.add(labelLogin, BorderLayout.CENTER);

        inputPanel.add(labelUsername);
        inputPanel.add(textFieldUsername);
        inputPanel.add(labelPassword);
        inputPanel.add(passwordField);

        buttonPanel.add(buttonLogin);
        buttonPanel.add(buttonManagement);

        mainPanel.add(labelPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setTitle("Login");
        setPreferredSize(new Dimension(400, 250));
        centerOnScreen();
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    private JLabel createLabel(String text, int fontStyle, int fontSize, int alignment) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Verdana", fontStyle, fontSize));
        label.setHorizontalAlignment(alignment);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JTextField createTextField(int height) {
        JTextField textField = new JTextField();
        Dimension preferredSize = new Dimension(150, height);
        textField.setPreferredSize(preferredSize);
        return textField;
    }

    private JPasswordField createPasswordField(int height) {
        JPasswordField passwordField = new JPasswordField();
        Dimension preferredSize = new Dimension(150, height);
        passwordField.setPreferredSize(preferredSize);
        return passwordField;
    }

    private JButton createButton(String text, int fontSize, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.PLAIN, fontSize));
        button.addActionListener(actionListener);
        return button;
    }

    private void handleLoginActionPerformed() {
        String enteredUsername = textFieldUsername.getText();
        char[] enteredPassword = passwordField.getPassword();
        String password = new String(enteredPassword);

        String validUsername = "dse";
        String validPassword = "dse123";

        if (enteredUsername.equals(validUsername) && password.equals(validPassword)) {
            JOptionPane.showMessageDialog(this, "Login successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            UnifiedManagement unifiedManagement = new UnifiedManagement();
            unifiedManagement.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }
        textFieldUsername.setText("");
        passwordField.setText("");
    }

    private void handleManagementActionPerformed() {
        Start start = new Start();
        start.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UnifiedLogin().setVisible(true));
    }
}
