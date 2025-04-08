package scanlin.model;

import scanlin.model.parserLin.DataStorageLin;
import scanlin.model.parserWin.DataStorageWin;

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
        if (dbManager.vulnerabilitySearchWin(id) != null) {
            return dbManager.vulnerabilitySearchWin(id);
        } else if (dbManager.vulnerabilitySearchLin(id) != null) {
            return dbManager.vulnerabilitySearchLin(id);
        }
        return null;
    }

    @Override
    public DataStorageWin getDataStorageWin(){
        DataBaseManager dbManager = new DataBaseManager();
        return dbManager.getDataStorageWin();
    }

    @Override
    public DataStorageLin getDataStorageLin(){
        DataBaseManager dbManager = new DataBaseManager();
        return dbManager.getDataStorageLin();
    }

    @Override
    public String getOS() {
        OSAnalyzer osAnalyzer = new OSAnalyzer();
        //osAnalyzer.printOSInfo();
        return osAnalyzer.getOSName();
    }
}
