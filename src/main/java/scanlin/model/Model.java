package scanlin.model;

import scanlin.model.parser.DataStorage;
import scanlin.model.parser.Vulnerability;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Model implements ModelInterface{
    @Override
    public String getPath() throws IOException {
        PathManager pathManager = new PathManager();
        return pathManager.readFile();
    }

    @Override
    public List<Map<String,String>> getProgramList() {
        ProgramSearcher programSearcher = new ProgramSearcher();
        return programSearcher.getProgramList();
    }

    @Override
    public void updateDataBase() {
        DataBaseManager dbManager = new DataBaseManager();
        try {
            dbManager.updateDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getVulnerabilityURL(String id){
        DataBaseManager dbManager = new DataBaseManager();
        return dbManager.vulnerabilitySearch(id);
    }

    @Override
    public DataStorage getDataStorage(){
        DataBaseManager dbManager = new DataBaseManager();
        return dbManager.getDataStorage();
    }
}
