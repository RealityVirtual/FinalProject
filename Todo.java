//imports

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class Todo {  
    // Define constants and variables
    private JProgressBar progressBar;
    private JLabel label;

    private int x = 4;
    private int y = 10;

    private final String fileName;
    private final JFrame frame;
    private final DefaultListModel<String> listModel;
    private final JList<String> list;

    // Constructor
    public Todo(String fileName) {
        this.fileName = fileName;
        frame = new JFrame("To-Do List");
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        loadList();
        setupGUI();
    }

    private void setupGUI() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 600);

        // Set a custom font for the entire app
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("List.font", new Font("SansSerif", Font.PLAIN, 14));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Custom background gradient for the main panel
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();

                GradientPaint gp = new GradientPaint(0, 0, Color.LIGHT_GRAY, width, height, Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        panel.setLayout(new BorderLayout());

        // Panel for the to-do list
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 128, 128), width, height, Color.PINK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        buttonPanel.setLayout(new FlowLayout());

        // Create custom rounded buttons
        JButton addButton = createCustomButton("Add Item");
        JButton deleteButton = createCustomButton("Delete Item");
        JButton checkButton = createCustomButton("Check/Uncheck");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(checkButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(panel);

        // Progress bar section
        progressBar = new JProgressBar(0, y);
        progressBar.setValue(x);
        progressBar.setStringPainted(true);
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        progressPanel.add(progressBar, BorderLayout.CENTER);

        label = new JLabel("Progress: " + x + "/" + y, JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        progressPanel.add(label, BorderLayout.NORTH);

        JButton updateButton = createCustomButton("Update Progress");
        updateButton.addActionListener(e -> updateProgress(x + 1));
        progressPanel.add(updateButton, BorderLayout.SOUTH);

        panel.add(progressPanel, BorderLayout.NORTH);

        // Button Actions
        addButton.addActionListener(e -> addItem());
        deleteButton.addActionListener(e -> deleteItem());
        checkButton.addActionListener(e -> toggleCheckItem());

        frame.setVisible(true);
    }

    public void updateProgress(int newX) {
        
        if (newX <= y) {
            x = newX;
            progressBar.setValue(x);
            label.setText("Progress: " + x + "/" + y);
        } else {
            JOptionPane.showMessageDialog(null, "Value cannot exceed " + y, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createCustomButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();

                // Draw gradient background
                GradientPaint gp = new GradientPaint(0, 0, Color.ORANGE, width, height, Color.YELLOW);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, width, height, 20, 20);

                // Draw button text
                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                int stringWidth = fm.stringWidth(getText());
                int stringHeight = fm.getAscent();
                g2d.drawString(getText(), (width - stringWidth) / 2, (height + stringHeight) / 2 - 2);
                g2d.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.dispose();
            }
        };
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private void addItem() {
        String newItem = JOptionPane.showInputDialog(frame, "Enter a new to-do item:", "Add Item", JOptionPane.PLAIN_MESSAGE);
        if (newItem != null && !newItem.trim().isEmpty()) {
            listModel.addElement(newItem);
            saveList();
        }
    }

    private void deleteItem() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
            saveList();
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an item to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleCheckItem() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            String item = listModel.getElementAt(selectedIndex);
            if (item.startsWith("[X] ")) {
                item = item.substring(4); // Uncheck
            } else {
                item = "[X] " + item; // Check
            }
            listModel.set(selectedIndex, item);
            saveList();
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an item to check/uncheck.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadList() {
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    listModel.addElement(line);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error loading list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveList() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < listModel.size(); i++) {
                writer.write(listModel.getElementAt(i));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
