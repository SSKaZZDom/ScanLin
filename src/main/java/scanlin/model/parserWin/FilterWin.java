package scanlin.model.parserWin;

class FilterWin {
    private String action;
    private String stateId;
    public FilterWin() {}
    public FilterWin(String action, String stateId) {
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
