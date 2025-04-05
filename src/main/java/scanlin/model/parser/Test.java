package scanlin.model.parser;

import java.util.ArrayList;
import java.util.List;

public class Test extends Storage{
    String id;
    String type;
    String check;
    String checkExistence;
    String xmlns;
    String object;
    List<String> states;
    public Test() {
        states = new ArrayList<>();
    }
    public Test(String type, String id, String check, String checkExistence,
                String xmlns, String object, List<String> states) {
        this.check = check;
        this.id = id;
        this.checkExistence = checkExistence;
        this.xmlns = xmlns;
        this.object = object;
        this.states = states;
        this.type = type;
    }
    @Override
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCheck() {
        return check;
    }

    public String getCheckExistence() {
        return checkExistence;
    }

    public String getXmlns() {
        return xmlns;
    }

    public String getObject() {
        return object;
    }

    public List<String> getStates() {
        return states;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public void setCheckExistence(String checkExistence) {
        this.checkExistence = checkExistence;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public void addState(String state) {
        this.states.add(state);
    }

    @Override
    public String toString() {
        return "Test{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", check='" + check + '\'' +
                ", checkExistence='" + checkExistence + '\'' +
                ", object='" + object + '\'' +
                ", states=" + states.toString() +
                '}';
    }
}
