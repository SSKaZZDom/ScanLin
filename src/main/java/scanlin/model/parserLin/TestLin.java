package scanlin.model.parserLin;

import scanlin.model.Storage;

import scanlin.model.Storage;

import java.util.ArrayList;
import java.util.List;

public class TestLin extends Storage {
    String id;
    String type;
    String check;
    String checkExistence;
    String xmlns;
    String object;
    String state;
    public TestLin() {
    }
    public TestLin(String type, String id, String check, String checkExistence,
                   String xmlns, String object, String state) {
        this.check = check;
        this.id = id;
        this.checkExistence = checkExistence;
        this.xmlns = xmlns;
        this.object = object;
        this.state = state;
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

    public String getState() {
        return state;
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

    public void setState(String state) {
        this.state = state;
    }


    @Override
    public String toString() {
        return "Test{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", check='" + check + '\'' +
                ", checkExistence='" + checkExistence + '\'' +
                ", object='" + object + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}

