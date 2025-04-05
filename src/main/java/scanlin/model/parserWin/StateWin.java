package scanlin.model.parserWin;

import scanlin.model.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StateWin extends Storage {
    private String type;
    private String xmlns;
    private String id;
    private List<HashMap<String, String>> values;

    public StateWin() {
        values = new ArrayList<>();
    }
    public String getType() {
        return type;
    }

    public String getXmlns() {
        return xmlns;
    }

    public String getId() {
        return id;
    }
    public void setType(String type) {
        this.type = type;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public void setId(String id) {
        this.id = id;
    }
    public List<HashMap<String, String>> getValues() {
        return values;
    }

    public HashMap<String, String> getValue(int index) {
        return values.get(index);
    }
    public void setValues(List<HashMap<String, String>> values) {
        this.values = values;
    }
    public void addValue(HashMap<String, String> value) {
        this.values.add(value);
    }
    public void updateValue(int index, HashMap<String, String> newValue) {
        if (index >= 0 && index < values.size()) {
            values.set(index, newValue);
        } else {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public void updateKey(int index, String key, String value) {
        if (index >= 0 && index < values.size()) {
            HashMap<String, String> oldValue = values.get(index);
            if (oldValue != null) {
                oldValue.put(key, value);
                updateValue(index, oldValue);
            } else {
                throw new NullPointerException("HashMap at index " + index + " is null");
            }
        } else {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public void removeValue(int index) {
        if (index >= 0 && index < values.size()) {
            values.remove(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public void removeKey(int index, String key) {
        if (index >= 0 && index < values.size()) {
            HashMap<String, String> map = values.get(index);
            map.remove(key);
        } else {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }

    public HashMap<String,String> getValue(String tag) {
        for (HashMap<String, String> value : values) {
            if (value.get("tag").equals(tag)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State {");
        sb.append("type='").append(type).append("', ");
        sb.append("xmlns='").append(xmlns).append("', ");
        sb.append("id='").append(id).append("', ");
        sb.append("values=");
        sb.append(values);
        sb.append(" }");

        return sb.toString();
    }
}
