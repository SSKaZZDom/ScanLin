package scanlin.model.parserWin;

import java.util.ArrayList;
import java.util.List;

public class CriteriaWin {
    private String operator;
    private List<String> definitions;
    private List<String> tests;
    private List<CriteriaWin> criteria;
    public CriteriaWin() {
        this.tests = new ArrayList<>();
        this.definitions = new ArrayList<>();
        this.criteria = new ArrayList<>();
    }

    public CriteriaWin(String operator) {
        this.tests = new ArrayList<>();
        this.definitions = new ArrayList<>();
        this.operator = operator;
        this.criteria = new ArrayList<>();
    }

    public void addCriteria(CriteriaWin criteria) {
        this.criteria.add(criteria);
    }
    public void addTest(String test) {
        this.tests.add(test);
    }

    public void addDefinition(String definition) {
        this.definitions.add(definition);
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<CriteriaWin> getCriteria() {
        return criteria;
    }

    public List<String> getTests() {
        return tests;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "Criteria:{\n" +
                "operator='" + operator + '\'' + '\n' +
                "tests=" + tests + '\n' +
                "inventories=" + definitions + '\n' +
                "Criteria=" + criteria + "}" + '\n';
    }
}
