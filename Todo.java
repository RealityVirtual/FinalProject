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

    private int x = 0;
    private int y = 100;

    private final String fileName;
    private final JFrame frame;
    private final DefaultListModel<Task> listModel;
    private final JList<Task> list;
    private final MainMenu main;
    
    private String name;

    // Constructor
    public Todo(String fileName, String theName, MainMenu mainMenu, boolean isLoaded) {
        this.fileName = fileName;
        this.name = theName;
        this.main = mainMenu;
        frame = new JFrame("To-Do List");
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        loadList();
        setupGUI();
        
        if(!isLoaded)
            saveList();
        frame.setVisible(false);
    }
    
    //set up the to do GUI components, panel at the bottom
    private void setupGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

                GradientPaint gp = new GradientPaint(0, 0, Color.BLACK, width, height, Color.WHITE);
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
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 215, 0), width, height, new Color(255, 215, 0));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        buttonPanel.setLayout(new FlowLayout());

        // Create custom rounded buttons
        JButton addButton = createCustomButton("Add Item");
        JButton deleteButton = createCustomButton("Delete Item");
        JButton editButton = createCustomButton("Edit Item");
        JButton checkButton = createCustomButton("Check/Uncheck");
        JButton backButton = createCustomButton("Main Menu");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(checkButton);
        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(panel);

        // Progress bar section
        progressBar = new JProgressBar(0, y);
        progressBar.setValue((int)(((double)checkProgress() / listModel.size()) * 100));
        progressBar.setStringPainted(true);
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        progressPanel.add(progressBar, BorderLayout.CENTER);

        label = new JLabel("Progress: " + x + "/" + y, JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        progressPanel.add(label, BorderLayout.NORTH);

        //JButton updateButton = createCustomButton("Update Progress");
        //updateButton.addActionListener(e -> updateProgress());
        //progressPanel.add(updateButton, BorderLayout.SOUTH);

        panel.add(progressPanel, BorderLayout.NORTH);

        // Button Actions
        addButton.addActionListener(e -> addItem());
        deleteButton.addActionListener(e -> deleteItem());
        editButton.addActionListener(e -> editItem());
        checkButton.addActionListener(e -> toggleCheckItem());

        backButton.addActionListener(e -> {
           frame.setVisible(false);
           main.show();
        });

        frame.setVisible(true);
    }
    
    //retrieve the name of the member
    public String getName()
    {
        return this.name;
    }
    
    //set the name
    public void setName(String newName)
    {
        this.name = newName;
    }
    
    //get progress of the tasks
    public int getProgress()
    {
        return (int)(((double)checkProgress() / listModel.size()) * 100);
    }
    
    //check number of completed task
    private int checkProgress()
    {
        int tasksComplete = 0;
        for (int i = 0; i < listModel.size(); i++){
            if (listModel.get(i).isCompleted()){
                tasksComplete++;
            }
        }
        return tasksComplete;
    }
    
    public void updateProgress() {
        x = (int)(((double)checkProgress() / listModel.size()) * 100);
        progressBar.setValue(x);
        label.setText("Progress: " + x + "/" + y);
    }

    //
    private JButton createCustomButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();

                // Draw gradient background
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, width, height, Color.WHITE);
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
            Task newTask = new Task(newItem);
            listModel.addElement(newTask);
            updateProgress();
            saveList();
        }
    }

    private void deleteItem() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
            updateProgress();
            saveList();
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an item to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editItem() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            Task currentTask = listModel.getElementAt(selectedIndex);
            String newDescription = JOptionPane.showInputDialog(frame, "Edit the item:", currentTask.getDescription());
            if (newDescription != null && !newDescription.trim().isEmpty()){
                currentTask.setDescription(newDescription);
                listModel.set(selectedIndex, currentTask);
                saveList();
            }
        }
        else {
                JOptionPane.showMessageDialog(frame, "Please select an item to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            }
    }

    private void toggleCheckItem() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            Task selectTask = listModel.getElementAt(selectedIndex);
            selectTask.setCompleted(!selectTask.isCompleted());
            listModel.set(selectedIndex, selectTask);
            updateProgress();
            saveList();
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an item to check/uncheck.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getTheFileName()
    {
        return this.fileName;
    }

    
    private void loadList() {
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                reader.readLine();
                reader.readLine();
                while((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()){
                        boolean isTaskComplete = line.startsWith("[X]");
                        String taskDescription = line.replaceAll("\\[X\\] ", "").replaceAll("\\[ \\] ", "").trim();
                        Task task = new Task(taskDescription);
                        task.setCompleted(isTaskComplete);
                        listModel.addElement(task);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error loading list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //save the list
    public void saveList() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write(this.name);
                writer.newLine();
                writer.newLine();
            for (int i = 0; i < listModel.size(); i++) {
                Task task = listModel.getElementAt(i);
                if(task.isCompleted())
                    writer.write("[X] ");
                else
                    writer.write("[ ] ");
                writer.write(task.getDescription());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void show() {
        frame.setVisible(true);
    }
}
