package motorph.model;

public abstract class Person implements IPerson {

    private String firstName;
    private String lastName;
    private String birthday;

    protected Person(String firstName, String lastName, String birthday) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.birthday  = birthday;
    }

    protected Person() {}

    @Override
    public String getFullName() {
        String f = firstName != null ? firstName : "";
        String l = lastName  != null ? lastName  : "";
        return (f + " " + l).trim();
    }

    @Override
    public String getFirstName()          { return firstName; }
    public void   setFirstName(String v)  { this.firstName = v; }

    @Override
    public String getLastName()           { return lastName; }
    public void   setLastName(String v)   { this.lastName = v; }

    @Override
    public String getBirthday()           { return birthday; }
    public void   setBirthday(String v)   { this.birthday = v; }

    @Override
    public String toString()              { return getFullName(); }
}