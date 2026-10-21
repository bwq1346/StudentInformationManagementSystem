/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 *Single-method interface used for MVC notification: when a Manager's
 * underlying data changes (add/edit/delete), it's the job of whatever
 * component made the change to call onDataChanged() on the View so it can
 * refresh itself. This plays the same role java.util.Observer/Observable
 * used to, without relying on that deprecated (since Java 9) API.
 * 
 * @author Nishal
 */
public class ModelListener {
    void onDataChanged();
}
