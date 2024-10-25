import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddProducts extends JFrame {
    private JLabel labelAddProducts,  labelProductName, labelPrice;
    private JTextField textBoxProductName, textBoxProductPrice;
    private JButton buttonBack, buttonSave;


    public AddProducts() {
        initComponents();
    }

    private void initComponents() {
        labelAddProducts = createLabel("Add Products", Font.BOLD, 28, SwingConstants.CENTER);
        labelProductName = createLabel("Product Name", Font.PLAIN, 18, SwingConstants.RIGHT);
        labelPrice = createLabel("Price", Font.PLAIN, 18, SwingConstants.RIGHT);
        textBoxProductName = createTextField(25);
        textBoxProductPrice = createTextField(25);
        buttonBack = createButton("Back", 16, e -> buttonBackActionPerformed());
        buttonSave = createButton("Save", 16, e -> buttonSaveActionPerformed());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel labelPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JPanel inputPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        labelPanel.add(labelProductName);
        labelPanel.add(labelPrice);

        inputPanel.add(textBoxProductName);
        inputPanel.add(textBoxProductPrice);

        buttonPanel.add(buttonBack);
        buttonPanel.add(buttonSave);

        mainPanel.add(labelPanel, BorderLayout.WEST);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(labelAddProducts, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        setTitle("Add Products");
        setPreferredSize(new Dimension(700, 300));
        setLocationRelativeTo(null);
        pack();
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

    private JButton createButton(String text, int fontSize, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.PLAIN, fontSize));
        button.addActionListener(actionListener);
        return button;
    }

    private JPanel createPanelWithButton(JButton button) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(button, BorderLayout.CENTER);
        return panel;
    }

    private void buttonBackActionPerformed() {
        UnifiedManagement unifiedManagementForm = new UnifiedManagement();
        this.dispose();
        unifiedManagementForm.setVisible(true);
    }

    private void buttonSaveActionPerformed() {
        try {
            Connection connection = DBConnector.getConnection();
            String sql = "INSERT INTO products (product_name, product_price) VALUES (?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, textBoxProductName.getText());
                preparedStatement.setDouble(2, Double.parseDouble(textBoxProductPrice.getText()));


                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving product. Please check your input.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        textBoxProductName.setText("");
        textBoxProductPrice.setText("");
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddProducts().setVisible(true));
    }
}
