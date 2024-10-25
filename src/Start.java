import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Start extends JFrame {
    private JButton startButton;
    private JButton managementButton;

    public Start() {
        initUI();
    }

    private void initUI() {
        setTitle("CAROT POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("CAROT POS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        startButton = createButton("Start");
        startButton.addActionListener(e -> handleButtonAction("Start"));
        buttonPanel.add(startButton);

        managementButton = createButton("Management");
        managementButton.addActionListener(e -> handleButtonAction("Management"));
        buttonPanel.add(managementButton);

        add(buttonPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Magneto", Font.BOLD, 24));
        button.setForeground(Color.DARK_GRAY);
        button.setPreferredSize(new Dimension(224, 73));
        return button;
    }

    private void handleButtonAction(String buttonName) {
        switch (buttonName) {
            case "Start":
                OrderAndMenu orderAndMenu = new OrderAndMenu();
                orderAndMenu.setVisible(true);
                this.dispose();
                break;
            case "Management":
                UnifiedLogin unifiedLogin = new UnifiedLogin();
                unifiedLogin.setVisible(true);
                this.dispose();
                break;

        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Start::new);
    }
}
