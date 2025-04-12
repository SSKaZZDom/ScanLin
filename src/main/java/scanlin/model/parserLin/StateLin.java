package scanlin.model.parserLin;

import scanlin.model.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StateLin extends Storage {
    private String type;
    private String xmlns;
    private String id;
    private HashMap<String, String> value;

    public StateLin() {
        value = new HashMap<>();
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
    public HashMap<String, String> getValue() {
        return value;
    }

    public void setValue(HashMap<String, String> value) {
        this.value = value;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State {");
        sb.append("type='").append(type).append("', ");
        sb.append("xmlns='").append(xmlns).append("', ");
        sb.append("id='").append(id).append("', ");
        sb.append("value=");
        sb.append(value);
        sb.append(" }");

        return sb.toString();
    }
}
