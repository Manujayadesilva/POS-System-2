import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateProduct extends JFrame {
    private JLabel labelUpdateProducts, labelProductId, labelProductName, labelPrice;
    private JTextField textBoxProductId, textBoxProductName, textBoxProductPrice;
    private JButton buttonBack, buttonUpdate;

    public UpdateProduct() {
        initComponents();
        loadProductData();
    }

    private void initComponents() {
        labelUpdateProducts = createLabel("Update Products", Font.BOLD, 28, SwingConstants.CENTER);
        labelProductId = createLabel("Product ID", Font.PLAIN, 18, SwingConstants.RIGHT);
        labelProductName = createLabel("Product Name", Font.PLAIN, 18, SwingConstants.RIGHT);
        labelPrice = createLabel("Price", Font.PLAIN, 18, SwingConstants.RIGHT);
        textBoxProductId = createTextField(25);
        textBoxProductName = createTextField(25);
        textBoxProductPrice = createTextField(25);
        buttonBack = createButton("Back", 16, e -> buttonBackActionPerformed());
        buttonUpdate = createButton("Update", 16, e -> buttonUpdateActionPerformed());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel labelPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JPanel inputPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        labelPanel.add(labelProductId);
        labelPanel.add(labelProductName);
        labelPanel.add(labelPrice);

        inputPanel.add(textBoxProductId);
        inputPanel.add(textBoxProductName);
        inputPanel.add(textBoxProductPrice);

        buttonPanel.add(buttonBack);
        buttonPanel.add(buttonUpdate);

        mainPanel.add(labelPanel, BorderLayout.WEST);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(labelUpdateProducts, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        setTitle("Update Products");
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

    private void buttonBackActionPerformed() {
        UnifiedManagement unifiedManagementForm = new UnifiedManagement();
        unifiedManagementForm.setVisible(true);
        this.dispose();
    }

    private void buttonUpdateActionPerformed() {
        try {
            Connection connection = DBConnector.getConnection();
            String sql = "UPDATE products SET product_name = ?, product_price = ? WHERE product_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, textBoxProductName.getText());
                preparedStatement.setDouble(2, Double.parseDouble(textBoxProductPrice.getText()));
                preparedStatement.setInt(3, Integer.parseInt(textBoxProductId.getText()));

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update the product.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating product. Please check your input.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProductData() {
        String productId = JOptionPane.showInputDialog(this, "Enter Product ID to update:");

        try {
            Connection connection = DBConnector.getConnection();
            String sql = "SELECT * FROM products WHERE product_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, Integer.parseInt(productId));
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    textBoxProductId.setText(productId);
                    textBoxProductName.setText(resultSet.getString("product_name"));
                    textBoxProductPrice.setText(Double.toString(resultSet.getDouble("product_price")));
                } else {
                    JOptionPane.showMessageDialog(this, "Product ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    buttonBackActionPerformed();
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading product data.", "Error", JOptionPane.ERROR_MESSAGE);
            buttonBackActionPerformed();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateProduct().setVisible(true));

    }
}