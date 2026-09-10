/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.Objects;

/**
 * Abstract base class for Student and Teacher - basically anything that
 * counts as a "person" in this system. Holds the fields both of them need
 * (id, name, email, phone) and leaves the person-specific stuff for the
 * subclasses to fill in.
 * 
 * Abstract base class structure drafted with assistance from Claude AI;
 * fields changed to private with getters/setters for full encapsulation —
 * see AI Interaction Log.
 *
 * @author Nishal Kinattukarayil Biju
 */
public abstract class Person implements Reportable {
    private String id;
    private String name;
    private String  email;
    private String phone;

    protected Person(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Every subclass has to say what role it is (e.g. "Student" or "Teacher")
    // and build its own summary line, since they don't show the same info.
    public abstract String getRole();
    public abstract String getSummary();

    public String getId() {
        return id;
    }

    // protected, not public - once an id is set it shouldn't really change
    // from outside code, only from within the class hierarchy
    protected void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Two people count as equal if they have the same id, even if their
    // name/email/phone have since changed. The id is what actually
    // identifies a record.
    @Override
    public boolean equals(Object o) {
        if(this == o) {
           return true;
        }
        if(!(o instanceof Person)) {
            return false;
        }
        Person other = (Person) o;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // so System.out.println(somePerson) just prints the same summary line
    @Override
    public String toString() {
        return getSummary();
    }

}