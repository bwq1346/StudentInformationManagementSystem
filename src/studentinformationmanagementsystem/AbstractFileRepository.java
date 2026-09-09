/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 *
 * @author Vikra
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



public class AbstractFileRepository<T> implements CrudOperations<T>, FileStorable
{
    protected final Map<String, T> recordMap = new LinkedHashMap<>();
    
    private final String filePath;
    
    protected AbstractFileRepository(String filePath)
    {
        this.filePath = filePath;
        ensureDataFolderExists();
    }
    
    protected abstract String extractId(T item);
    
    protected abstract T fromLine(String line);
    
    protected abstract String toLine(T item);
    
    @Override
    public void add(T item) throws DuplicateRecordException
    {
        String id = extractId(item);
        if(recordMap.containsKey(id))
        {
            throw new DuplicateRecordException("A record with id '" + id + "' already exists.");
        }
        recordMap.put(id, item);
    }
    
    @Override
    public void update(T item) throws RecordNotFoundException
    {
        String id = extractId(item);
        if(!recordMap.containsKey(id))
        {
            throw new RecordNotFoundException("No record found with id '" + id + "' to update.");
        }
        recordMap.put(id, item);
    }
    
    @Override
    public void delete(String id) throws RecordNotFoundException
    {
        if(!recordMap.containsKey(id))
        {
            throw new RecordNotFoundException("No record found with id '" + id + "' to delete.");
        }
        recordMap.remove(id);
    }
    
    @Override
    public T findById(String id) throws RecordNotFoundException
    {
        T item = recordMap.get(id);
        if(item == null)
        {
            throw new RecordNotFoundException("No record found with id '" + id + "'.");
        }
        return item;
    }
    
    @Override
    public List<T> findAll()
    {
        return new ArrayList<>(recordMap.values());
    }

    @Override
    public boolean existsById(String id)
    {
        return recordMap.containsKey(id);
    }

    @Override
    public int count()
    {
        return recordMap.size();
    }
    
    @Override
    public void loadFromFile()
    {
        File file = new File(filePath);
        
        if (!file.exists())
        {
            return;
        }

        int loadedCount = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            
            while((line = reader.readLine()) != null)
            {
                if (line.isBlank())
                {
                    continue;
                }
                
                try
                {
                    T item = fromLine(line);
                    recordMap.put(extractId(item), item);
                    loadedCount++;
                }
                catch(RuntimeException parseError)
                {
                    ActivityLogger.log("WARNING: could not parse line in " + filePath + " -> " + line);
                }
            }
            
            ActivityLogger.log("Loaded " + loadedCount + " record(s) from " + filePath);
            
        }
        catch(FileNotFoundException e)
        {
            ActivityLogger.log("File not found (skipped): " + filePath);
        }
        catch(IOException e)
        {
            System.out.println("Warning: could not read data file '" + filePath + "' (" + e.getMessage() + ").");
        }
    }
    
    @Override
    public void saveToFile()
    {
        try(PrintWriter writer = new PrintWriter(new FileWriter(filePath)))
        {
            Iterator<Map.Entry<String, T>> it = recordMap.entrySet().iterator();
            while(it.hasNext())
            {
                Map.Entry<String, T> entry = it.next();
                writer.println(toLine(entry.getValue()));
            }
            ActivityLogger.log("Saved " + recordMap.size() + " record(s) to " + filePath);
        }
        catch(IOException e)
        {
            System.out.println("Warning: could not write data file '" + filePath + "' (" + e.getMessage() + ").");
        }
    }
    
    private void ensureDataFolderExists()
    {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists())
        {
            parent.mkdirs();
        }
    }
}
