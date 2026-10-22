package studentinformationmanagementsystem;

/**
 * Factory that creates the right form panel for each entity type.
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