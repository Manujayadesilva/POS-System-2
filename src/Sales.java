import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Sales extends JFrame {
    private JLabel labelManagement;
    private JButton buttonProducts, buttonSales, buttonSaleItems, buttonStart;
    private JTable tableSales;

    public Sales() {
        initComponents();
    }

    private void initComponents() {
        labelManagement = new JLabel("Management System");

        buttonProducts = createButton("Products", this::handleCommonButtonActionPerformed);
        buttonSales = createButton("Sales", this::handleCommonButtonActionPerformed);
        buttonSaleItems = createButton("Sale Items", this::handleSaleItemsButtonActionPerformed);
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

        // Add new panel for Sales label and Delete button
        JPanel panelBottom = new JPanel(new BorderLayout());

        JLabel labelSales = new JLabel("Sales:");
        labelSales.setFont(new Font("Arial", Font.BOLD, 14));

        JButton buttonDelete = new JButton("Delete");
        buttonDelete.setFont(new Font("Arial", Font.BOLD, 12));
        buttonDelete.addActionListener(this::handleDeleteButtonActionPerformed);

        JPanel panelSales = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSales.add(labelSales);
        panelSales.add(buttonDelete);

        panelBottom.add(panelSales, BorderLayout.NORTH);

        add(panelBottom, BorderLayout.SOUTH);

        setTitle("Sales");
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
                fetchSalesData();
                break;
        }
    }

    private void handleSaleItemsButtonActionPerformed(ActionEvent e) {
        SalesItems saleItemsForm = new SalesItems();
        saleItemsForm.setVisible(true);
        this.dispose();
    }

    private void buttonStartActionPerformed(ActionEvent e) {
        Start start = new Start();
        start.setVisible(true);
        this.dispose();
    }

    private void fetchSalesData() {
        String url = "jdbc:mysql://localhost:3306/pos_system";
        String username = "root";
        String password = "manu@123sql";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT * FROM sales";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();

            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                model.addColumn(resultSet.getMetaData().getColumnName(i));
            }

            while (resultSet.next()) {
                Object[] rowData = new Object[model.getColumnCount()];
                for (int i = 1; i <= model.getColumnCount(); i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                model.addRow(rowData);
            }

            tableSales.setModel(model);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching sales data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteButtonActionPerformed(ActionEvent e) {
        int[] selectedRows = tableSales.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected sales?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) tableSales.getModel();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int selectedRow = tableSales.convertRowIndexToModel(selectedRows[i]);
                int saleId = (int) model.getValueAt(selectedRow, 0);
                deleteSale(saleId);
                model.removeRow(selectedRow);
            }
            JOptionPane.showMessageDialog(this, "Selected sales have been deleted.", "Deletion Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSale(int saleId) {
        String url = "jdbc:mysql://localhost:3306/pos_system";
        String username = "root";
        String password = "manu@123sql";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "DELETE FROM sales WHERE sale_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, saleId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting sale.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Sales().setVisible(true));
    }
}
