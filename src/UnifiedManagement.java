import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSetMetaData;


public class UnifiedManagement extends JFrame {
    private JLabel labelManagement, labelProducts;
    private JButton buttonProducts, buttonSales, buttonSaleItems, buttonUpdate, buttonDelete, buttonAdd, buttonStart;
    private JTable tableProducts;

    public UnifiedManagement() {
        initComponents();
    }

    private void initComponents() {
        labelManagement = new JLabel("Management System");
        labelProducts = new JLabel("Products:");

        buttonProducts = createButton("Products", this::handleCommonButtonActionPerformed);
        buttonSales = createButton("Sales", this::handleCommonButtonActionPerformed);
        buttonSaleItems = createButton("Sale Items", this::handleSaleItemsActionPerformed);
        buttonDelete = createButton("Delete", this::buttonDeleteActionPerformed);
        buttonAdd = createButton("Add", this::buttonAddActionPerformed);
        buttonUpdate = createButton("Update", this::buttonUpdateActionPerformed);
        buttonStart = createButton("Home", this::buttonStartActionPerformed);

        tableProducts = new JTable();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        labelManagement.setFont(new Font("Mongolian Baiti", Font.BOLD, 36));
        labelManagement.setForeground(Color.RED);
        labelManagement.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(buttonProducts);
        panelButtons.add(buttonSales);
        panelButtons.add(buttonSaleItems);
        panelButtons.add(buttonStart);

        JPanel panelLabels = new JPanel(new FlowLayout());
        panelLabels.add(labelProducts);
        panelLabels.add(buttonAdd);
        panelLabels.add(buttonUpdate);
        panelLabels.add(buttonDelete);

        JPanel panelTable = new JPanel(new BorderLayout());
        panelTable.add(new JScrollPane(tableProducts), BorderLayout.CENTER);

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(labelManagement, BorderLayout.CENTER);
        panelTop.add(panelButtons, BorderLayout.SOUTH);

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.add(panelLabels, BorderLayout.NORTH);
        panelCenter.add(panelTable, BorderLayout.CENTER);

        add(panelTop, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);

        setTitle("Management");
        setPreferredSize(new Dimension(1010, 631));
        setLocationRelativeTo(null);
        pack();
    }

    private JButton createButton(String text, ActionPerformedListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        button.addActionListener(e -> listener.handleActionPerformed(e));
        return button;
    }

    private void handleCommonButtonActionPerformed(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        switch (sourceButton.getText()) {
            case "Products":
                fetchProductData();
                break;
            case "Sales":
                Sales salesForm = new Sales();
                salesForm.setVisible(true);
                this.dispose();
                break;
        }
    }

    private void handleSaleItemsActionPerformed(ActionEvent e) {
        SalesItems saleItemsForm = new SalesItems();
        saleItemsForm.setVisible(true);
        this.dispose();
    }

    private void buttonDeleteActionPerformed(ActionEvent e) {
        int selectedRow = tableProducts.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tableProducts.getValueAt(selectedRow, 0);

        String deleteQuery = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_system", "root", "manu@123sql");
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setInt(1, productId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Row deleted successfully.", "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
                fetchProductData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete the row.", "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(this, "Error deleting row from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void buttonAddActionPerformed(ActionEvent e) {
        AddProducts addProductsForm = new AddProducts();
        addProductsForm.setVisible(true);
        this.dispose();
    }

    private void buttonUpdateActionPerformed(ActionEvent e) {
        UpdateProduct updateProduct = new UpdateProduct();
        updateProduct.setVisible(true);
        this.dispose();
    }

    private void buttonStartActionPerformed(ActionEvent e) {
        Start start = new Start();
        start.setVisible(true);
        this.dispose();
    }

    private void fetchProductData() {
        String url = "jdbc:mysql://localhost:3306/pos_system";
        String username = "root";
        String password = "manu@123sql";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT * FROM products";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                model.addRow(rowData);
            }

            tableProducts.setModel(model);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching product data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UnifiedManagement().setVisible(true));
    }

    interface ActionPerformedListener {
        void handleActionPerformed(ActionEvent e);
    }
}
