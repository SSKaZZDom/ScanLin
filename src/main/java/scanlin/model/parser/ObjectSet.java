package scanlin.model.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class ObjectSet {
    private List<String> objectRefs = new ArrayList<>();
    private List<Filter> filters = new ArrayList<>();
    private List<ObjectSet> nestedSets = new ArrayList<>(); // Для вложенных set

    // Геттеры и Сеттеры
    public List<String> getObjectRefs() {
        return objectRefs;
    }

    public void setObjectRefs(List<String> objectRefs) {
        this.objectRefs = objectRefs;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<ObjectSet> getNestedSets() {
        return nestedSets;
    }

    public void setNestedSets(List<ObjectSet> nestedSets) {
        this.nestedSets = nestedSets;
    }

    // Метод для добавления objectRef
    public void addObjectRef(String objectRef) {
        this.objectRefs.add(objectRef);
    }

    // Метод для добавления фильтра
    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    // Метод для добавления вложенного Set
    public void addNestedSet(ObjectSet nestedSet) {
        this.nestedSets.add(nestedSet);
    }
}
