package scanlin.model.parserWin;

import java.util.ArrayList;
import java.util.List;

class ObjectSetWin {
    private List<String> objectRefs = new ArrayList<>();
    private List<FilterWin> filters = new ArrayList<>();
    private List<ObjectSetWin> nestedSets = new ArrayList<>(); // Для вложенных set

    // Геттеры и Сеттеры
    public List<String> getObjectRefs() {
        return objectRefs;
    }

    public void setObjectRefs(List<String> objectRefs) {
        this.objectRefs = objectRefs;
    }

    public List<FilterWin> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterWin> filters) {
        this.filters = filters;
    }

    public List<ObjectSetWin> getNestedSets() {
        return nestedSets;
    }

    public void setNestedSets(List<ObjectSetWin> nestedSets) {
        this.nestedSets = nestedSets;
    }

    // Метод для добавления objectRef
    public void addObjectRef(String objectRef) {
        this.objectRefs.add(objectRef);
    }

    // Метод для добавления фильтра
    public void addFilter(FilterWin filter) {
        this.filters.add(filter);
    }

    // Метод для добавления вложенного Set
    public void addNestedSet(ObjectSetWin nestedSet) {
        this.nestedSets.add(nestedSet);
    }
}
