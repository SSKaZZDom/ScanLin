package scanlin.model;

import scanlin.model.parser.DataStorage;
import scanlin.model.parser.Vulnerability;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ModelInterface {
    public String getPath() throws IOException;
    public List<Map<String,String>> getProgramList();
    public void updateDataBase();
    public List<String> getVulnerabilityURL(String id);
    public DataStorage getDataStorage();

}
