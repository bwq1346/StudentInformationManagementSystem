/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 * Factory pattern: hands back the right concrete EntityFormPanel for a
 * given entity type, so StudentView/TeacherView/CourseView don't need to
 * know about each other's concrete form classes - same shape as the
 * FruitFactory example from the Design Patterns lecture (Client asks the
 * Factory for a Product by type; Factory decides which ConcreteProduct to
 * build and return).
 *
 * @author Nishal Kinattukarayil Biju
 */
public class FormFactory {

    public enum EntityType {
        STUDENT, TEACHER, COURSE
    }

    public static EntityFormPanel createForm(EntityType type) {
        switch (type) {
            case STUDENT:
                return new StudentFormPanel();
            case TEACHER:
                return new TeacherFormPanel();
            case COURSE:
                return new CourseFormPanel();
            default:
                throw new IllegalArgumentException("Unknown entity type: " + type);
        }
    }
}