import javax.swing.*; //GUI components
import java.awt.*; //graphical layout 
import java.awt.event.*; //event handling of GUI components
import java.io.*; //IO operations
import java.util.ArrayList; 
import java.util.Arrays;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.net.URI;

//imports for deadline handling
import java.util.Date; //gets current dates
import java.text.SimpleDateFormat; //formatting and parsing dates

public class MainMenu {
    private JFrame mainMenuFrame; //Main frame of GUI
    private Todo toDoList1; //task list
    
    private ArrayList<JButton> memberButtons; //list of members buttons
    private ArrayList<Todo> memberTodos; //list of members task
    
    private JProgressBar progressBar; //member progress bar
    private JLabel label; //label for progress in text format
    private int totalProgress; //overall progress bar
    
    private int numMembers; //# of members in project
    private JLabel countdownLabel; //label for days til deadline
    private String projectDeadline; //deadline of the project
    
    private int x = 0; 
    private int y = 100;
    
    public MainMenu()
    {
        //initialize lists
        memberButtons = new ArrayList<>(); 
        memberTodos = new ArrayList<>();
        
        //Configure the main frame settings
        mainMenuFrame = new JFrame("Main Menu");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //close app on exit
        mainMenuFrame.setSize(1000, 600); //size
        mainMenuFrame.setLayout(new BorderLayout()); //default layout
        
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        //create a button to start the project
        JButton startProjectButton = new JButton("Start Project");
        startProjectButton.setBackground(Color.WHITE); // Set button background to white
        startProjectButton.setForeground(new Color(212, 175, 55)); // Gold text color
        startProjectButton.setPreferredSize(new Dimension(200, 50));
        panel.add(startProjectButton);
        
        JButton loadProjectButton = new JButton("Load Project");
        loadProjectButton.setPreferredSize(new Dimension(200, 50));
        loadProjectButton.setBackground(Color.WHITE); // Set button background to white
        loadProjectButton.setForeground(new Color(212, 175, 55)); // Gold text color
        panel.add(loadProjectButton);
        
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(-100, 0, 0, 0);
        wrapperPanel.add(panel, gbc);
        
        mainMenuFrame.add(wrapperPanel, BorderLayout.CENTER);
        
        //on button click, hide & remove button
        startProjectButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                startProjectButton.setVisible(false);
                mainMenuFrame.remove(startProjectButton);
                
                loadProjectButton.setVisible(false);
                mainMenuFrame.remove(loadProjectButton);
                
                mainMenuFrame.remove(wrapperPanel);
                
                deleteTextFiles();
                
                Initialization initializeMenu = new Initialization(MainMenu.this); //next page for initializing prokject specific features
                
                mainMenuFrame.setLayout(new FlowLayout());
                countdownLabel = new JLabel("-----------------------------"); //placeholder text setting length
                mainMenuFrame.add(countdownLabel, BorderLayout.NORTH); //adds label to frame
           }
        });
        
        loadProjectButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                startProjectButton.setVisible(false);
                mainMenuFrame.remove(startProjectButton);
                
                loadProjectButton.setVisible(false);
                mainMenuFrame.remove(loadProjectButton);
                
                mainMenuFrame.remove(wrapperPanel);
                
                mainMenuFrame.setLayout(new FlowLayout());
                countdownLabel = new JLabel("-----------------------------");
                mainMenuFrame.add(countdownLabel, BorderLayout.NORTH);
                
                startProjectOverview(numOfTextFiles(), "2025/01/24", true);
                updateButtonNames();
            }
        });
        
        mainMenuFrame.setVisible(true); //make main frame visible
    }
    
    //method to display frame and update progress
    public void show()
    {
        updateProgress(); //updates progress bar and associated label
        mainMenuFrame.setVisible(true); //shows main menu frame
    }
    
    //method for the project overview
    public void startProjectOverview(int theNumMembers, String theDeadline, boolean isLoaded)
    {
        
        
        //get number of team members and deadline
        this.numMembers = theNumMembers;
        this.projectDeadline = theDeadline;
        //System.out.println(this.projectDeadline); //debuggin purposes
        
        updateCountdown(); //update countdown
        
        Timer timer = new Timer(86400000, e -> updateCountdown());
        timer.start(); //Update countdown when project overview starts
        
        //pop up menu for member options
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addMemberButton = new JMenuItem("Add Member");
        JMenuItem editMemberButton = new JMenuItem("Edit Member");
        JMenuItem removeMemberButton = new JMenuItem("Remove Member");
        popupMenu.add(addMemberButton);
        popupMenu.add(editMemberButton);
        popupMenu.add(removeMemberButton);
        
        //change layout for mroe specific positioning
        mainMenuFrame.setLayout(new BorderLayout());
        
        //Initializing the progress bar
        progressBar = new JProgressBar();
        progressBar.setForeground(new Color(212, 175, 55)); // Gold color for progress bar fill
        progressBar.setBackground(Color.BLACK); // Black background for the progress bar
        progressBar.setValue(0); //begin at 0
        progressBar.setStringPainted(true); //show percentage

        //panel to hold the progress bar
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //add surroinding empty space
        progressPanel.add(progressBar, BorderLayout.CENTER); //center progress bar
        
        //label displaying progress
        label = new JLabel("Progress: " + totalProgress + "/" + y, JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        progressPanel.add(label, BorderLayout.NORTH);
        
        mainMenuFrame.add(progressPanel, BorderLayout.NORTH); //add progress panel to main menu frame
        
        //panels for team members
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); //horizontal/row arrangement
        
        //for each member, create a file to hold their data, buttons etc. 
        for(int i = 0; i < numMembers; i++){
            if(isLoaded){
                String theNewestFileName = getFileName(i);
                memberTodos.add(new Todo(theNewestFileName, "Member " + (i+1), MainMenu.this, true)); //create Todo class instances
            }
            else{
                memberTodos.add(new Todo("todoList" + (i+1) + ".txt", "Member " + (i+1), MainMenu.this, false));
            }
            
            JButton memberButton = new JButton(memberTodos.get(i).getName()); //buttons for each member
            
            memberButton.setBackground(Color.WHITE); // Set button background to white
            memberButton.setForeground(new Color(212, 175, 55)); // Gold text color
            memberButton.setPreferredSize(new Dimension(175, 100)); //set the buttons size
            memberButtons.add(memberButton); //adds buttons to the arraylist of buttons
            
            buttonPanel.add(memberButton); //adds button to the panel
            
            //for button click (R & L click)...
            memberButton.addMouseListener(new MouseAdapter(){ 
                public void mousePressed(MouseEvent e){
                    if(e.getButton() == MouseEvent.BUTTON1){ //left click
                        mainMenuFrame.setVisible(false); //hide main menu
                        memberTodos.get(memberButtons.indexOf(memberButton)).show(); //show members to do list
                    }
                    else if(e.getButton() == MouseEvent.BUTTON3){ //right click
                        if(buttonPanel.isAncestorOf(memberButton)){
                            popupMenu.putClientProperty("button", null);
                            popupMenu.putClientProperty("button", memberButton);
                            popupMenu.show(memberButton, e.getX(), e.getY());
                        }
                    }
                }
            });
        }
        
        //Remove a member
        removeMemberButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JButton removedButton = (JButton) popupMenu.getClientProperty("button"); //get selected button
                if(removedButton != null){
                    int indexToRemove = memberButtons.indexOf(removedButton); //get buttons index
                    
                    File removedFile = new File(memberTodos.get(indexToRemove).getTheFileName());
                    removedFile.delete(); //delete members file
                    
                    memberTodos.remove(indexToRemove); //remove members to do list
                    memberButtons.remove(indexToRemove); //remove button from arraylist
                    
                    buttonPanel.remove(removedButton); //remove button from the panel
                    
                    updateProgress(); //calculate new progress
                    
                    buttonPanel.revalidate(); //refresh panel
                    buttonPanel.repaint();
                }
            }
        });
        
        //edit members button name
        editMemberButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JButton edittedButton = (JButton) popupMenu.getClientProperty("button"); //retrieve/select jbutton
                if(edittedButton != null){
                    String newName = JOptionPane.showInputDialog(mainMenuFrame, "Enter a new name for member:", "Edit Member", JOptionPane.PLAIN_MESSAGE);
                    if (newName != null && !newName.trim().isEmpty()) {
                        memberTodos.get(memberButtons.indexOf(edittedButton)).setName(newName); //update name
                        memberTodos.get(memberButtons.indexOf(edittedButton)).saveList(); //update list
                        edittedButton.setText(memberTodos.get(memberButtons.indexOf(edittedButton)).getName()); //update new button text
                    }    
                }
            }
        });
        
        //add members
        addMemberButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String newMemberName = JOptionPane.showInputDialog(mainMenuFrame, "Enter new member's name:", "Add Member", JOptionPane.PLAIN_MESSAGE);
                if (newMemberName != null && !newMemberName.trim().isEmpty()) {
                    
                    memberTodos.add(new Todo("todoList" + getNextFileNum() + ".txt", newMemberName, MainMenu.this, false));
            
                    JButton memberButton = new JButton(memberTodos.get(memberTodos.size()-1).getName());
                    memberButton.setPreferredSize(new Dimension(175, 100));
                    memberButtons.add(memberButton);
                    
                    memberButton.setBackground(Color.WHITE); // Set button background to white
                    memberButton.setForeground(new Color(212, 175, 55)); // Gold text color
                    
                    buttonPanel.add(memberButton);
                    
                    memberButton.addMouseListener(new MouseAdapter(){
                        public void mousePressed(MouseEvent e){
                            if(e.getButton() == MouseEvent.BUTTON1){
                                mainMenuFrame.setVisible(false);
                                memberTodos.get(memberButtons.indexOf(memberButton)).show();
                            }
                            else if(e.getButton() == MouseEvent.BUTTON3){
                                if(buttonPanel.isAncestorOf(memberButton)){
                                    popupMenu.putClientProperty("button", null);
                                    popupMenu.putClientProperty("button", memberButton);
                                    popupMenu.show(memberButton, e.getX(), e.getY());
                                }
                            }
                        }
                    });
                    
                    updateProgress();
                    
                    buttonPanel.revalidate();
                    buttonPanel.repaint();
                }
            }
        });
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        
        JButton linkedWhiteboard = new JButton("Open Team Whiteboard");
        linkedWhiteboard.setPreferredSize(new Dimension(225, 100));
        linkedWhiteboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    /* Code for the button to act as a hyperlink
                    URI uri = new URI("https://progresspointnotes.glitch.me/");
                    Desktop.getDesktop().browse(uri);
                    */
                    //Forced to use the following code as a work around
                    JOptionPane.showMessageDialog(mainMenuFrame, "This would open: https://progresspointnotes.glitch.me/", "Whiteboard Hyperlink", JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception ex){
                    System.out.println("CodeHS does not support BROWSE action");
                }
            }
        });
        
        JButton linkedTimer = new JButton("Open Pomodoro Timer");
        linkedTimer.setPreferredSize(new Dimension(225, 100));
        linkedTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    /* Code for the button to act as a hyperlink
                    URI uri = new URI("https://progresspointpomodoro.glitch.me/");
                    Desktop.getDesktop().browse(uri);
                    */
                    //Forced to use the following code as a work around
                    JOptionPane.showMessageDialog(mainMenuFrame, "This would open: https://progresspointpomodoro.glitch.me/", "Pomodoro Timer Hyperlink", JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception ex){
                    System.out.println("CodeHS does not support BROWSE action");
                }
            }
        });
        
        linkedWhiteboard.setBackground(Color.WHITE); // Set button background to white
        linkedWhiteboard.setForeground(new Color(0, 0, 0)); // Black text color
        
        linkedTimer.setBackground(Color.WHITE); // Set button background to white
        linkedTimer.setForeground(new Color(0, 0, 0)); // Black text color
        
        bottomPanel.add(linkedWhiteboard);
        bottomPanel.add(linkedTimer);
        
        mainMenuFrame.add(bottomPanel, BorderLayout.SOUTH);
        
        mainMenuFrame.add(buttonPanel, BorderLayout.CENTER);
        this.show();
    }
    
    //deletes text
    public void deleteTextFiles()
    {
        File directory = new File(".");
        File[] files = directory.listFiles();
        
        if(files != null){
            for (File file : files){
                if (file.isFile() && file.getName().endsWith(".txt")){
                    file.delete();
                }
            }
        }
    }

    //gets the name of the file
    public String getFileName(int index)
    {
        File directory = new File(".");
        File[] files = directory.listFiles((dir, name) -> name.startsWith("todoList") && name.endsWith(".txt")); //filter through giles
        
        if(files != null){
            Arrays.sort(files, (f1, f2) -> { //sorts files by creation time in ascending order
                try {
                    //convert files to paths for attribute reading
                    Path path1= f1.toPath();
                    Path path2 = f2.toPath();
                    
                    //
                    BasicFileAttributes attr1 = Files.readAttributes(path1, BasicFileAttributes.class);
                    BasicFileAttributes attr2 = Files.readAttributes(path2, BasicFileAttributes.class);
                    
                    return attr1.creationTime().compareTo(attr2.creationTime());
                }
                catch (IOException e) {
                    return 0;
                }
            });
        }
        
        return files[index].getName();
        
    }
    
    //get the file number, determine which member is gonna be edited
    public int getNextFileNum()
    {
        File directory = new File(".");
        File[] files = directory.listFiles();
        int maxNumber = 0;
        
        for (File file : files){
            String fileName = file.getName();
            if (fileName.startsWith("todoList") && fileName.endsWith(".txt")){
                try{
                    int number = Integer.parseInt(fileName.substring(8, fileName.length() - 4));
                    maxNumber = Math.max(maxNumber, number);
                } 
                catch (NumberFormatException e){
                    continue;
                }
            }
        }
        return maxNumber + 1;
    }
    
   public int numOfTextFiles() //count the number of files, eg. for all members
   {
       File directory = new File(".");
       File[] files = directory.listFiles();
       int fileCount = 0;
       for(File file: files){
           if(file.isFile() && file.getName().endsWith(".txt")){
               fileCount++;
           }
       }
       return fileCount;
   }
   
   //updates the button names 
   public void updateButtonNames()
   {
       File directory = new File("."); //create new file object
       File[] files = directory.listFiles(); 
       
       ArrayList<String> newNames = new ArrayList<>(); //list to store the names
       
       for(File file : files){
           try(BufferedReader reader = new BufferedReader(new FileReader(file))){
               if(file.isFile() && file.getName().endsWith(".txt")){ //ensure its the text file
                   newNames.add(reader.readLine()); //and the first line(name) to list
               }
           }
           catch (IOException e){
               continue;
           }
       }
       
       for(int i = 0; i < newNames.size(); i++){ //update membertTodos objects with new name
           memberTodos.get(i).setName(newNames.get(i));
           memberButtons.get(i).setText(newNames.get(i));
       }
   }
    
    //calculates and re-set the total, project progress
    public void updateProgress()
    {
        int totalProgress = 0;
        for(Todo member : memberTodos){
            totalProgress += member.getProgress();
        }
        totalProgress = (int)((totalProgress) / memberTodos.size());
        label.setText("Progress: " + totalProgress + "/" + y);
        progressBar.setValue(totalProgress);
    }
    
    //method to update the countdown label
    private void updateCountdown()
    {   
        try 
        {   
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); //define date format
            Date deadlineDate = dateFormat.parse(projectDeadline); //extract structure from formatted date
            long diff = deadlineDate.getTime() - System.currentTimeMillis(); //calculate days left
            long daysUntilDeadline = diff / (1000 * 60 * 60 * 24); //calculate days left
            countdownLabel.setText("Days left: " + daysUntilDeadline); //set the text for the countdown label
        } 
        catch (Exception e)
        {
            //System.out.println("Bugged Deadline: " + projectDeadline); //debugging
            countdownLabel.setText("Invalid Deadline");
        }
    }
}
