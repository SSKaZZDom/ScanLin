package scanlin.model;

import scanlin.model.parserLin.CriteriaLin;
import scanlin.model.parserLin.DataStorageLin;
import scanlin.model.parserLin.VulnerabilityLin;
import scanlin.model.parserWin.DataStorageWin;
import scanlin.viewmodel.ViewModelInterface;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Model implements ModelInterface{
    OSAnalyzer osAnalyzer;
    DataBaseManager dbManager;
    CriteriaRunnerLin runner;
    DataStorageLin storageLin;
    DataStorageWin storageWin;
    ViewModelInterface viewModel;
    TestLoading test;
    public Model() {
        this.osAnalyzer = new OSAnalyzer();
        this.dbManager = new DataBaseManager(osAnalyzer.isLinux());
        if (osAnalyzer.isLinux()) {
            storageLin = dbManager.getDataStorageLin();
        } else {
            storageWin = dbManager.getDataStorageWin();
        }
    }
    @Override
    public String getPath() throws IOException {
        PathManager pathManager = new PathManager();
        return pathManager.readFile();
    }


    @Override
    public void updateDataBase() {
        try {
            dbManager.updateDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getVulnerabilityURL(String id){
        if (dbManager.vulnerabilitySearchWin(id) != null) {
            return dbManager.vulnerabilitySearchWin(id);
        } else if (dbManager.vulnerabilitySearchLin(id) != null) {
            return dbManager.vulnerabilitySearchLin(id);
        }
        return null;
    }

    @Override
    public DataStorageWin getDataStorageWin(){
        return dbManager.getDataStorageWin();
    }

    @Override
    public DataStorageLin getDataStorageLin(){
        return dbManager.getDataStorageLin();
    }

    @Override
    public String getOS() {
        osAnalyzer.printOSInfo();
        return osAnalyzer.getOSName();
    }

    @Override
    public void saveReport(List<VulnerabilityLin> vuls) {
        String ip = LocalNetworkIP.getLocalNetworkIp();
        System.out.println(ip);
        assert ip != null;
        ReportSaverLin.exportToCSV(vuls, "/report_" + ip.replace(".","_") + ".csv");
    }

    @Override
    public void setViewModel(ViewModelInterface viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void runTestThread(Consumer<Double> onProgress, Consumer<String> onStatus) {
        this.test = new TestLoading();
        test.runLongOperation(onProgress, onStatus);
    }

    @Override
    public void pauseTest() {
        test.pause();
    }

    @Override
    public void resumeTest(){
        test.resume();
    }

    @Override
    public void stopTest(){
        test.stop();
    }

    /*@Override
    public List<VulnerabilityLin> findVulnerabilitiesLin() {
        this.runner = new CriteriaRunnerLin();
        return runner.VulnerabilityCheck(storageLin);
    }
*/
    @Override
    public void runScan(Consumer<Double> onProgress, Consumer<String> onStatus) {
        this.runner = new CriteriaRunnerLin();
        runner.VulnerabilityCheck(storageLin,  onProgress, onStatus);
        System.out.println(runner.getTrueVuls());
    }

    @Override
    public void pauseScan() {
        runner.pause();
    }

    @Override
    public void resumeScan(){
        runner.resume();
    }

    @Override
    public void stopScan(){
        runner.stop();
    }

    @Override
    public List<VulnerabilityLin> getTrueVuls() {
        return runner.getTrueVuls();
    }

}
