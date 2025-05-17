package scanlin.model;

import scanlin.model.parserLin.DataStorageLin;
import scanlin.model.parserLin.VulnerabilityLin;
import scanlin.model.parserWin.DataStorageWin;
import scanlin.viewmodel.ViewModelInterface;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface ModelInterface {
    public String getPath() throws IOException;
    public void updateDataBase() throws IOException;
    public List<String> getVulnerabilityURL(String id);
    public DataStorageWin getDataStorageWin();
    public DataStorageLin getDataStorageLin();
    public String getOS();
    public List<VulnerabilityLin> findVulnerabilitiesLin();
    public void saveReport(List<VulnerabilityLin> vuls);
    public void setViewModel(ViewModelInterface viewModel);
    public void runTestThread(Consumer<Double> onProgress, Consumer<String> onStatus);
    public void pauseTest();
    public void resumeTest();
    public void stopTest();
}
