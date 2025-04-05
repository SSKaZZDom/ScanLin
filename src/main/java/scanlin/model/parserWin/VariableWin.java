package scanlin.model.parserWin;

import scanlin.model.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VariableWin extends Storage {
    private String id;
    private String datatype;
    private String type;
    private List<HashMap<String, String>> values;
    private List<String> constantValues;
    public VariableWin() {
        values = new ArrayList<>();
        constantValues = new ArrayList<>();
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }
    public String getDatatype() {
        return this.datatype;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<String> getConstantValues() {
        return this.constantValues;
    }
    public void setConstantValues(List<String> constantValues) {
        this.constantValues = constantValues;
    }
    public void addConstantValues(String constantValue) {
        this.constantValues.add(constantValue);
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
        sb.append("Variable {");
        sb.append("id='").append(id).append("', ");
        sb.append("datatype='").append(datatype).append("', ");
        sb.append("type='").append(type).append("'");
        sb.append("\n");
        if (type.equals("constant")) {
            sb.append("values=");
            sb.append(constantValues);
        } else {
            sb.append("values=");
            sb.append(values);
        }
        sb.append("\n");
        return sb.toString();
    }
}
