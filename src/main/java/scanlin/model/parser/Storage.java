package scanlin.model.parser;

abstract class Storage {
    protected String id;

    public Storage() {
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}