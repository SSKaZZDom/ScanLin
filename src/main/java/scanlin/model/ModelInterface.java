package scanlin.model;

import scanlin.model.parserWin.DataStorageWin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ModelInterface {
    public String getPath() throws IOException;
    public List<Map<String,String>> getProgramList();
    public void updateDataBase() throws IOException;
    public List<String> getVulnerabilityURL(String id);
    public DataStorageWin getDataStorage();

}
