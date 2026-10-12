package waypoint;

public class Destination
{
    //~ Fields ................................................................
    private String name;
    private LinkedChain<Activity> activities;
    //~ Constructors ..........................................................
    
    public Destination(String name)
    {
        setName(name);
        activities = new LinkedChain<Activity>();
    }
    //~Public  Methods ........................................................
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                "Destination name cannot be null or blank");
            
        }
        this.name = name.trim();
    }
    
    public void addActivity(Activity a)
    {
        if (a == null)
        {
            throw new IllegalArgumentException("Activity cannot be null");
        }
        activities.add(a);
    }
    
    public Activity getActivity(int p)
    {
        return activities.get(p);
    }
    
    public Activity removeActivity(int p)
    {
        return activities.remove(p);
    }
    
    public int getActivityCount()
    {
        return activities.size();
    }
    
    public boolean hasActivities()
    {
        return !activities.isEmpty();
    }
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Destination s = (Destination)o;
        return name.equals(s.name);
    }
    
    public int hashCode()
    {
        return name.hashCode();
    }
    
    public String toString()
    {
        return name + " (" + getActivityCount() + " activities)";
    }
}
