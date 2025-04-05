package scanlin.model.parser;

class Filter {
    private String action;
    private String stateId;
    public Filter() {}
    public Filter(String action, String stateId) {
        this.action = action;
        this.stateId = stateId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "action='" + action + '\'' +
                ", stateId='" + stateId + '\'' +
                '}';
    }
}
