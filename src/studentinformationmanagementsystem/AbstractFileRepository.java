/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
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

/**
 * This is the base for every repository (StudentRepository,
 * TeacherRepository, CourseRepository, EnrollmentRepository). All four of
 * them need the exact same CRUD logic and the exact same load/save-to-file
 * logic, so instead of copy-pasting it four times, it lives here once. Each
 * subclass only has to say how to turn ONE line of text into an object and
 * back again (extractId / fromLine / toLine) - everything else is handled
 * for it.
 * 
 * Generic file-repository structure drafted with assistance from Claude AI;
 * rejected java.nio approach, implemented with BufferedReader/PrintWriter and
 * FileStorable contract instead — see AI Interaction Log.
 */
public abstract class AbstractFileRepository<T> implements CrudOperations<T>, FileStorable
{
    // in-memory store, keyed by id - LinkedHashMap so records stay in the
    // order they were added/loaded instead of getting shuffled around
    protected final Map<String, T> recordMap = new LinkedHashMap<>();

    private final String filePath;

    protected AbstractFileRepository(String filePath)
    {
        this.filePath = filePath;
        ensureDataFolderExists();
    }

    // these three are what actually differ between a Student repo and a
    // Course repo etc. - everything below this point is generic
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
        // returning a new list rather than the map's own values - so
        // whoever calls this can't accidentally mess with our internal state
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

        // first run ever - there's no file yet, which is fine, just start empty
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

                // if one line is corrupted/unparseable, log it and keep
                // going instead of letting the whole file load fail
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
        // just rewrites the whole file from what's currently in memory -
        // simple, and fine for the amount of data this project deals with
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

    // makes sure the data/ folder actually exists before we try to read or
    // write a file inside it - saves every subclass from worrying about this
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
