/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 * Just the load/save behaviour, kept as its own interface instead of being
 * bundled into CrudOperations. Not every CrudOperations implementation
 * would necessarily need file storage, so splitting it out keeps the two
 * ideas separate (this is basically Interface Segregation from SOLID).
 *
 * @author Vikra
 */

public interface FileStorable
{
    void loadFromFile();
    void saveToFile();
}
