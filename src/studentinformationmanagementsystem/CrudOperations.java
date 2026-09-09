package studentinformationmanagementsystem;

import java.util.List;

/**
 * The standard set of create/read/update/delete operations that every
 * repository in this project supports. It's generic (CrudOperations&lt;T&gt;)
 * so the same interface works for Student, Teacher, Course and Enrollment
 * without repeating it four times.
 */
public interface CrudOperations<T>
{
    void add(T item) throws DuplicateRecordException;

    void update(T item) throws RecordNotFoundException;

    void delete(String id) throws RecordNotFoundException;

    T findById(String id) throws RecordNotFoundException;

    List<T> findAll();

    boolean existsById(String id);

    int count();
}
