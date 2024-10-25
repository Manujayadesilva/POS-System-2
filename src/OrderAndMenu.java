import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class OrderAndMenu extends JFrame {


    private JButton buttonDone, buttonHome;
    private JList<String> listBoxPrice;
    private JList<String> selectedProductsList;
    private JList<String> totalPriceList;
    private DefaultListModel<String> selectedListModel;
    private DefaultListModel<String> totalPriceListModel;

    private ArrayList<JButton> productButtons;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public OrderAndMenu() {
        super("Order and Menu");
        initMenu();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        selectedListModel = new DefaultListModel<>();
        selectedProductsList = new JList<>(selectedListModel);
        selectedProductsList.setFont(new Font("Arial", Font.PLAIN, 14));

        totalPriceListModel = new DefaultListModel<>();
        totalPriceList = new JList<>(totalPriceListModel);
        totalPriceList.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane selectedProductsScrollPane = new JScrollPane(selectedProductsList);
        JScrollPane totalPriceScrollPane = new JScrollPane(totalPriceList);

        getContentPane().add(selectedProductsScrollPane, BorderLayout.EAST);
        getContentPane().add(totalPriceScrollPane, BorderLayout.WEST);
    }

    private void initMenu() {
        buttonDone = createButton("Done");
        buttonHome = createButton("Home");
        listBoxPrice = new JList<>();

        buttonHome.addActionListener(this::handleButtonAction);
        buttonDone.addActionListener(this::handleButtonAction);

        fetchProductData();

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout());

        JPanel productButtonPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        for (JButton productButton : productButtons) {
            productButtonPanel.add(productButton);
        }
        JScrollPane scrollPane = new JScrollPane(productButtonPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        menuPanel.add(new JScrollPane(productButtonPanel), BorderLayout.CENTER);
        menuPanel.add(buttonHome, BorderLayout.WEST);
        menuPanel.add(listBoxPrice, BorderLayout.SOUTH);
        menuPanel.add(buttonDone, BorderLayout.SOUTH);

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.add(menuPanel, "MenuPanel");

        getContentPane().add(cardPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.addActionListener(this::handleButtonAction);
        return button;
    }

    private void handleButtonAction(ActionEvent e) {

        JButton sourceButton = (JButton) e.getSource();
        switch (sourceButton.getText()) {
            case "Home":
                showStartForm();
                break;
            case "Done":
                handleDone();
                break;
        }
    }

    private void showStartForm() {

        Start startForm = new Start();
        startForm.setVisible(true);
        this.dispose();
    }

    private void fetchProductData() {
        String url = "jdbc:mysql://localhost:3306/pos_system";
        String username = "root";
        String password = "manu@123sql";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT product_name, product_price FROM products";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                productButtons = new ArrayList<>();

                while (resultSet.next()) {
                    String productName = resultSet.getString("product_name");
                    double productPrice = resultSet.getDouble("product_price");

                    JButton productButton = new JButton(productName + " - $" + productPrice);
                    productButton.setFont(new Font("Arial", Font.PLAIN, 14));
                    productButton.addActionListener(this::handleProductButtonAction);

                    productButtons.add(productButton);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching product data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleProductButtonAction(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String buttonText = sourceButton.getText();

        String productName = buttonText.split(" - ")[0];
        double productPrice = Double.parseDouble(buttonText.split(" - ")[1].replace("$", ""));

        selectedListModel.addElement(productName + " - $" + productPrice);
        updateTotalPrice();
    }




    private void handleDone() {
        double totalPrice = calculateTotalPrice();
        JOptionPane.showMessageDialog(this, "Order is complete!\nTotal Price: $" + totalPrice, "Order Complete", JOptionPane.INFORMATION_MESSAGE);

        updateSalesTable();
        updateOrdersTable();

        selectedListModel.clear();
        totalPriceListModel.clear();

        cardLayout.show(cardPanel, "MenuPanel");
    }

    private double calculateTotalPrice() {
        double totalPrice = 0;
        for (int i = 0; i < selectedListModel.getSize(); i++) {
            String item = selectedListModel.getElementAt(i);
            double price = Double.parseDouble(item.split(" - ")[1].replace("$", ""));
            totalPrice += price;
        }
        return totalPrice;
    }

    private void updateSalesTable() {
        String url = "jdbc:mysql://localhost:3306/pos_system";
        String username = "root";
        String password = "manu@123sql";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertSql = "INSERT INTO sales (total_amount,sale_date) VALUES (?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                double totalPrice = calculateTotalPrice();
                insertStatement.setDouble(1, totalPrice);

                java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                insertStatement.setDate(2, currentDate);

                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating sales table.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOrdersTable() {
        String url = "jdbc:mysql://localhost:3306/pos_system";
        String username = "root";
        String password = "manu@123sql";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {

            int saleId = -1;
            String getSaleIdQuery = "SELECT MAX(sale_id) AS max_sale_id FROM sales";
            try (PreparedStatement statement = connection.prepareStatement(getSaleIdQuery)) {
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    saleId = resultSet.getInt("max_sale_id");
                }
            }

            String insertSql = "INSERT INTO orders (sale_id, product_id, quantity) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                for (int i = 0; i < selectedListModel.getSize(); i++) {
                    String[] itemSplit = selectedListModel.getElementAt(i).split(" - ");
                    String productName = itemSplit[0];
                    int productId = getProductIdByName(connection, productName);
                    int quantity = 1;

                    insertStatement.setInt(1, saleId);
                    insertStatement.setInt(2, productId);
                    insertStatement.setInt(3, quantity);
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating orders table.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private int getProductIdByName(Connection connection, String productName) throws SQLException {
        int productId = -1;

        String sql = "SELECT product_id FROM products WHERE product_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, productName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    productId = resultSet.getInt("product_id");
                }
            }
        }

        return productId;
    }


    private void updateTotalPrice() {
        double totalPrice = calculateTotalPrice();

        totalPriceListModel.clear();
        totalPriceListModel.addElement("Total Price: $" + totalPrice);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OrderAndMenu::new);
    }
}
