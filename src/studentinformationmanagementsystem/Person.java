/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.Objects;

/**
 *
 * @author Nishal
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
    
    public abstract String getRole();
    public abstract String getSummary();
    
    public String getId() {
        return id;
    }
    
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
    
    @Override
    public String toString() {
        return getSummary();
    }
    
}
