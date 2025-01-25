import java.io.Serializable;

// Represents a task with a description and completion status.
public class Task implements Serializable 
{
    private String description; // The task description.
    private boolean completed;  // Completion status of the task.
    
    // Initializes the task with a description, defaults to not completed.
    public Task(String theDescription)
    {
        this.description = theDescription;
        this.completed = false;
    }
    
    // Returns the task description.
    public String getDescription()
    {
        return description;
    }
    
    // Updates the task description.
    public void setDescription(String newDescription)
    {
        this.description = newDescription;
    }
    
    // Returns the completion status.
    public boolean isCompleted()
    {
        return completed;
    }
    
    // Updates the completion status.
    public void setCompleted(boolean newCompleted)
    {
        this.completed = newCompleted;
    }
    
    // Returns a string representation of the task with its status.
    public String toString()
    {
        return (completed ? "[X] " : "[ ] ") + description;
    }
}
