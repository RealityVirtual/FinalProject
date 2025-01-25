import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Handles the initialization of a project, including user input for members and deadline.
public class Initialization {
    private JFrame frame; // Main frame for the initialization window.
    private JTextField membersField; // Input field for the number of members.
    private JTextField deadlineField; // Input field for the project deadline.
    
    private int numberOfMembers; // Stores the number of members entered by the user.
    private String projectDeadline; // Stores the project deadline entered by the user.
    
    // Constructor: Sets up the initialization window and handles user input.
    public Initialization(MainMenu mainMenu) {
        frame = new JFrame("Project Initialization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create and configure input fields and labels.
        JLabel membersLabel = new JLabel("Number of Members:");
        membersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        membersField = new JTextField();
        membersField.setPreferredSize(new Dimension(200, 25));
        membersField.setMaximumSize(new Dimension(250, 25));
        
        JLabel deadlineLabel = new JLabel("Deadline (YYYY/MM/DD):");
        deadlineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        deadlineField = new JTextField();
        deadlineField.setPreferredSize(new Dimension(200, 25));
        deadlineField.setMaximumSize(new Dimension(250, 25));
        
        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(250, 35));
        submitButton.setMaximumSize(new Dimension(250, 35));
        submitButton.setBackground(new Color(212, 175, 55)); // Set button background to white
        submitButton.setForeground(Color.BLACK); // Gold text color
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to the frame.
        panel.add(Box.createVerticalGlue());
        panel.add(membersLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(membersField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(deadlineLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(deadlineField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(submitButton);
        panel.add(Box.createVerticalGlue());
        
        frame.add(panel, BorderLayout.CENTER);
        
        // Add functionality to the submit button.
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String membersInput = membersField.getText();
                String deadlineInput = deadlineField.getText();
                
                String formatRegex = "\\d{4}/\\d{2}/\\d{2}";
                
                // Validate input fields.
                if (membersInput.isEmpty() || deadlineInput.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all information.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                else if (!Pattern.matches(formatRegex, deadlineInput)){
                    JOptionPane.showMessageDialog(frame, "Please input project deadline with valid format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    LocalDate deadline = LocalDate.parse(deadlineInput, formatter);
                    if (deadline.isBefore(LocalDate.now())){
                        JOptionPane.showMessageDialog(frame, "Please input project deadline with future date.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(frame, "Please input project deadline with valid format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    numberOfMembers = Integer.parseInt(membersInput);
                    
                    // Ensure the number of members does not exceed the limit.
                    if (numberOfMembers > 5) {
                        JOptionPane.showMessageDialog(frame, "Project can only have up to 5 members.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    projectDeadline = deadlineInput;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for Members.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Close the initialization window and proceed to the project overview.
                frame.dispose();
                mainMenu.startProjectOverview(numberOfMembers, projectDeadline, false);
            }
        });
        
        frame.setVisible(true); // Display the frame.
    }
    
    // Returns the number of members entered by the user.
    public int getMemberCount() {
        return numberOfMembers;
    }
    
    // Returns the project deadline entered by the user.
    public String getDeadline() {
        return projectDeadline;
    }
}
