/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 *
 * @author Vikra
 */

import java.util.List;


public class CrudOperations
{
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
}
