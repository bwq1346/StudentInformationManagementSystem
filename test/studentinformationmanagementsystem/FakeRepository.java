package studentinformationmanagementsystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * In-memory CrudOperations used so the manager tests don't need a database.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class FakeRepository<T> implements CrudOperations<T> {

    private final Map<String, T> store = new LinkedHashMap<>();
    private final Function<T, String> idExtractor;

    public FakeRepository(Function<T, String> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public void add(T item) throws DuplicateRecordException {
        String id = idExtractor.apply(item);
        if (store.containsKey(id)) {
            throw new DuplicateRecordException("Record with id " + id + " already exists.");
        }
        store.put(id, item);
    }

    @Override
    public void update(T item) throws RecordNotFoundException {
        String id = idExtractor.apply(item);
        if (!store.containsKey(id)) {
            throw new RecordNotFoundException("No record found with id " + id);
        }
        store.put(id, item);
    }

    @Override
    public void delete(String id) throws RecordNotFoundException {
        if (store.remove(id) == null) {
            throw new RecordNotFoundException("No record found with id " + id);
        }
    }

    @Override
    public T findById(String id) throws RecordNotFoundException {
        T item = store.get(id);
        if (item == null) {
            throw new RecordNotFoundException("No record found with id " + id);
        }
        return item;
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    @Override
    public int count() {
        return store.size();
    }
}