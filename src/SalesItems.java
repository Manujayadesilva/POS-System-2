import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;

public class SalesItems extends JFrame {
    private JLabel labelManagement;
    private JButton buttonProducts, buttonSales, buttonSaleItems, buttonStart;
    private JTable tableSales;

    public SalesItems() {
        initComponents();
    }

    private void initComponents() {
        labelManagement = new JLabel("Management System");

        buttonProducts = createButton("Products", this::handleCommonButtonActionPerformed);
        buttonSales = createButton("Sales", this::handleCommonButtonActionPerformed);
        buttonSaleItems = createButton("Sale Items", this::buttonSaleItemsActionPerformed);
        buttonStart = createButton("Home", this::buttonStartActionPerformed);

        tableSales = new JTable();

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

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(labelManagement, BorderLayout.CENTER);
        panelTop.add(panelButtons, BorderLayout.SOUTH);

        add(panelTop, BorderLayout.NORTH);
        add(new JScrollPane(tableSales), BorderLayout.CENTER);

        setTitle("Sale Items");
        setPreferredSize(new Dimension(1010, 631));
        setLocationRelativeTo(null);
        pack();
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        button.addActionListener(listener);
        return button;
    }

    private void handleCommonButtonActionPerformed(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        switch (sourceButton.getText()) {
            case "Products":
                UnifiedManagement unifiedManagementForm = new UnifiedManagement();
                unifiedManagementForm.setVisible(true);
                this.dispose();
                break;
            case "Sales":
                Sales sales = new Sales();
                sales.setVisible(true);
                this.dispose();
                break;
        }
    }

    private void buttonStartActionPerformed(ActionEvent e) {
        Start start = new Start();
        start.setVisible(true);
        this.dispose();
    }


    private void buttonSaleItemsActionPerformed(ActionEvent e) {
        String url = "jdbc:mysql://localhost:3306/pos_system";
        String username = "root";
        String password = "manu@123sql";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT * FROM orders";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();


            model.addColumn("Order ID");
            model.addColumn("Sale ID");
            model.addColumn("Product ID");
            model.addColumn("Quantity");


            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("order_id"),
                        resultSet.getInt("sale_id"),
                        resultSet.getInt("product_id"),
                        resultSet.getInt("quantity")
                };
                model.addRow(rowData);
            }


            tableSales.setModel(model);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching sales item data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalesItems().setVisible(true));
    }
}
