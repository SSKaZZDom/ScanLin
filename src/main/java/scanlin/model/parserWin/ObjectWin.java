package scanlin.model.parserWin;

import scanlin.model.Storage;

import java.util.ArrayList;
import java.util.List;

public class ObjectWin extends Storage {
    private String id;
    private String type;
    private String name;
    private String xmlns;
    private String path;
    private String varRef;
    private String varCheck;
    private String hive;
    private String key;
    private String operation;
    private List<FilterWin> filters;
    private String windowsView;
    private ObjectSetWin set;
    private String pattern;
    private String xpath;
    private String cmdCommand;
    private String parameter;
    private List<String> select;
    private String namespace;
    private String wql;
    private int maxDepth;

    // Конструктор по умолчанию
    public ObjectWin() {
        filters = new ArrayList<>();
        select = new ArrayList<>();
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getXmlns() { return xmlns; }
    public void setXmlns(String xmlns) { this.xmlns = xmlns; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getVarRef() { return varRef; }
    public void setVarRef(String varRef) { this.varRef = varRef; }

    public String getVarCheck() { return varCheck; }
    public void setVarCheck(String varCheck) { this.varCheck = varCheck; }

    public String getHive() { return hive; }
    public void setHive(String hive) { this.hive = hive; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getWindowsView() { return windowsView; }
    public void setWindowsView(String windowsView) { this.windowsView = windowsView; }

    public int getMaxDepth() { return maxDepth; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public String getXpath() { return xpath; }
    public void setXpath(String xpath) { this.xpath = xpath; }

    public String getCmdCommand() { return cmdCommand; }
    public void setCmdCommand(String cmdCommand) { this.cmdCommand = cmdCommand; }
    public void addCmdCommand(String cmdCommand1) { this.cmdCommand = this.cmdCommand + "-" + cmdCommand1; }

    public String getParameter() { return parameter; }
    public void setParameter(String parameter) { this.parameter = parameter; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public String getWql() { return wql; }
    public void setWql(String wql) { this.wql = wql; }

    public List<FilterWin> getFilters() { return filters; }
    public void setFilters(List<FilterWin> filters) { this.filters = filters; }
    public void addFilter(FilterWin filter) { this.filters.add(filter); }

    public List<String> getSelect() { return select; }
    public void setSelect(List<String> select) { this.select = select; }
    public void addSelect(String select) { this.select.add(select); }

    public ObjectSetWin getSet() { return set; }
    public void setSet(ObjectSetWin set) { this.set = set; }

    @Override
    public String toString() {
        return String.format(
                "Object { id='%s', type='%s', name='%s', xmlns='%s', path='%s', varRef='%s', varCheck='%s', " +
                        "hive='%s', key='%s', operation='%s', windowsView='%s', maxDepth=%d, pattern='%s', xpath='%s', " +
                        "cmdCommand='%s', parameter='%s', namespace='%s', wql='%s', filters=%s, select=%s }",
                id, type, name, xmlns, path, varRef, varCheck, hive, key, operation,
                windowsView, maxDepth, pattern, xpath, cmdCommand, parameter, namespace, wql,
                filters, select
        );
    }
}