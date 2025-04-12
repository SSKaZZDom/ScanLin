package scanlin.model.parserLin;

import scanlin.model.parserLin.*;
import scanlin.model.parserWin.StateWin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OvalParserLin {
    public DataStorageLin ovalParsing() {
        List<VulnerabilityLin> vulnerabilities = new ArrayList<>();
        List<InventoryLin> inventories = new ArrayList<>();
        List<TestLin> tests = new ArrayList<>();
        List<StateLin> states = new ArrayList<>();
        List<ObjectLin> objects = new ArrayList<>();
        String filePath = "data/AstraSE17VulnsOVAL.xml";
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Файл не найден");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            VulnerabilityLin vul = new VulnerabilityLin();
            InventoryLin inventory = new InventoryLin();
            TestLin test = new TestLin();
            ObjectLin obj = new ObjectLin();
            StateLin state = new StateLin();
            int cnt;
            boolean inventoryFlag = false;
            HashMap<String, String> value;

            // Парсер для инвенторис и уязвимостей

            while (!(line = reader.readLine()).contains("</definitions>")) {
                if (line.contains("<definition")) {
                    if (line.contains("class=\"inventory\"")) {
                        inventoryFlag = true;
                        inventory = new InventoryLin();
                        String regex = "id=\"(.*?)\"";
                        inventory.setId(extractValue(line, regex));
                    } else if (line.contains("class=\"vulnerability\"")) {
                        vul = new VulnerabilityLin();
                        String regex = "id=\"(.*?)\"";
                        vul.setId(extractValue(line, regex));
                        inventoryFlag = false;
                    }
                } else if (line.contains("</definition>")) {
                    if (!inventoryFlag) {
                        vulnerabilities.add(vul);
                    } else {
                        inventories.add(inventory);
                    }
                } else if (!inventoryFlag) {
                    if (line.contains("<title>")) {
                        String regex = "<title>(.+?)</title>";
                        vul.setTitle(extractValue(line, regex));
                    } else if (line.contains("<platform>")) {
                        String regex = "<platform>(.+?)</platform>";
                        vul.addPlatform(extractValue(line, regex));
                    } else if (line.contains("<product>")) {
                        String regex = "<product>(.+?)</product>";
                        vul.setProduct(extractValue(line, regex));
                    } else if (line.contains("<reference") && line.contains("FSTEC")) {
                        String regex = "ref_url=\"(https?://[^\"]+)\"";
                        vul.setFstec_url(extractValue(line, regex));
                        regex = "ref_id=\"(.+?)\"";
                        vul.setFstec_id(extractValue(line, regex));
                    } else if (line.contains("<description>")) {
                        String regex = "<description>(.+?)</description>";
                        vul.setDescription(extractValue(line, regex));
                    } else if (line.contains("<severity>")) {
                        String regex = "<severity>(.+?)</severity>";
                        vul.setSeverity(extractValue(line, regex));
                    } else if (line.contains("<cwe>")) {
                        String regex = "<cwe>(.+?)</cwe>";
                        vul.setCWE(extractValue(line, regex));
                    } else if (line.contains("<cvssv20>")) {
                        String regex = "<cvssv20>(.+?)</cvssv20>";
                        vul.setCvss2(extractValue(line, regex));
                    } else if (line.contains("<remediation>")) {
                        StringBuilder remediation = new StringBuilder();
                        line = line.replace("<remediation>", "").trim();
                        while (!line.contains("</remediation>")) {
                            remediation.append(line).append("\n");
                            line = reader.readLine().trim();
                        }
                        line = line.replace("</remediation>", "").trim();
                        remediation.append(line);

                        vul.setRemediation(remediation.toString().trim());
                    } else if (line.contains("<criteria")) {
                        List<String> lines = new ArrayList<>();
                        lines.add(line);
                        cnt = 1;
                        while (cnt > 0) {
                            line = reader.readLine();
                            if (line.contains("<criteria")) {
                                cnt++;
                            } else if (line.contains("</criteria")) {
                                cnt--;
                            }
                            lines.add(line);
                        }
                        vul.setCriteria(criteriaParser(lines));
                    }
                } else {
                    if (line.contains("<title>")) {
                        String regex = "<title>(.+?)</title>";
                        inventory.setTitle(extractValue(line, regex));
                    } else if (line.contains("<platform>")) {
                        String regex = "<platform>(.+?)</platform>";
                        inventory.addPlatform(extractValue(line, regex));
                    } else if (line.contains("<product>")) {
                        String regex = "<product>(.+?)</product>";
                        inventory.setProduct(extractValue(line, regex));
                    } else if (line.contains("<description>")) {
                        String regex = "<description>(.+?)</description>";
                        inventory.setDescription(extractValue(line, regex));
                    } else if (line.contains("<criteria")) {
                        List<String> lines = new ArrayList<>();
                        lines.add(line);
                        cnt = 1;
                        while (cnt > 0) {
                            line = reader.readLine();
                            if (line.contains("<criteria")) {
                                cnt++;
                            } else if (line.contains("</criteria")) {
                                cnt--;
                            }
                            lines.add(line);
                        }
                        inventory.setCriteria(criteriaParser(lines));
                    }
                }
            }

            while (!(line = reader.readLine()).contains("</tests>")) {
                if (line.contains("<dpkginfo_test") || line.contains("<rpminfo_test") ||
                    line.contains("<textfilecontent54_test") || line.contains("<family_test")) {
                    test = new TestLin();
                    test.setType(line.substring(line.indexOf("<") + 1, line.indexOf("_test")));
                    test.setXmlns(extractValue(line, "xmlns=\"([^\"]+)\""));
                    test.setId(extractValue(line, "id=\"(.*?)\""));
                    test.setCheckExistence(extractValue(line, "check_existence=\"([^\"]+)\""));
                    test.setCheck(extractValue(line, "check=\"([^\"]+)\""));
                } else if (line.contains("<object")) {
                    test.setObject(extractValue(line, "object_ref=\"(.*?)\""));
                } else if (line.contains("<state")) {
                    test.setState(extractValue(line, "state_ref=\"(.*?)\""));
                } else if (line.contains("</dpkginfo_test") || line.contains("</rpminfo_test") ||
                        line.contains("</textfilecontent54_test") || line.contains("</family_test")) {
                    tests.add(test);
                }
            }
            while (!(line = reader.readLine()).contains("</objects>")) {
                if (line.contains("<dpkginfo_object") ||
                        line.contains("<rpminfo_object") ||
                        line.contains("<family_object") ||
                        line.contains("<textfilecontent54_object")) {
                    obj = new ObjectLin();
                    obj.setType(line.substring(line.indexOf("<") + 1, line.indexOf("_object")));
                    obj.setId(extractValue(line, "id=\"(.*?)\""));
                    obj.setXmlns(extractValue(line, "xmlns=\"([^\"]+)\""));
                    if (line.contains("/>")) {
                        objects.add(obj);
                    }
                } else if (line.contains("<name") || line.contains("<filename")) {
                    value = new HashMap<>();
                    value.put("tag", "name");
                    value.put("value", extractValue(line, ">(.*?)<"));
                    if (line.contains("operation")) {
                        value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    }
                    obj.addValue(value);
                } else if (line.contains("<path") || line.contains("<filepath")) {
                    value = new HashMap<>();
                    value.put("tag", "path");
                    value.put("value", extractValue(line, ">(.*?)<"));
                    obj.addValue(value);
                } else if (line.contains("<filter")) {
                    value = new HashMap<>();
                    value.put("tag", "filter");
                    value.put("action", extractValue(line, "action=\"(.*?)\""));
                    value.put("value", extractValue(line, ">(.*?)<"));
                    obj.addValue(value);
                } else if (line.contains("<pattern")) {
                    value = new HashMap<>();
                    value.put("tag", "pattern");
                    value.put("value", extractValue(line, ">(.*?)<"));
                    obj.addValue(value);
                } else if (line.contains("<behaviors")) {
                    value = new HashMap<>();
                    value.put("tag", "behaviors");
                    value.put("max_depth", extractValue(line, "max_depth=\"(.*?)\""));
                    obj.addValue(value);
                } else if (line.contains("<instance")) {
                    value = new HashMap<>();
                    value.put("tag", "instance");
                    value.put("value", extractValue(line, ">(.*?)<"));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    obj.addValue(value);
                }  else if (line.contains("</dpkginfo_object") ||
                        line.contains("</rpminfo_object") ||
                        line.contains("</family_object") ||
                        line.contains("</textfilecontent54_object")) {
                    objects.add(obj);
                }
            }
            while (!(line = reader.readLine()).contains("</states")){
                if (line.contains("<dpkginfo_state") ||
                        line.contains("<rpminfo_state") ||
                        line.contains("<family_state") ||
                        line.contains("<textfilecontent54_state")) {
                    state = new StateLin();
                    state.setType(line.substring(line.indexOf("<") + 1, line.indexOf("_state")));
                    state.setId(extractValue(line, "id=\"(.*?)\""));
                    state.setXmlns(extractValue(line, "xmlns=\"([^\"]+)\""));
                } else if (line.contains("<family")) {
                    value = new HashMap<>();
                    value.put("tag", "family");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    state.setValue(value);
                } else if (line.contains("<release")) {
                    value = new HashMap<>();
                    value.put("tag", "release");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    state.setValue(value);
                } else if (line.contains("<subexpression")) {
                    value = new HashMap<>();
                    value.put("tag", "subexpression");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    state.setValue(value);
                } else if (line.contains("<version")) {
                    value = new HashMap<>();
                    value.put("tag", "version");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    state.setValue(value);
                } else if (line.contains("<evr")) {
                    value = new HashMap<>();
                    value.put("tag", "evr");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    state.setValue(value);
                } else if (line.contains("</dpkginfo_state") ||
                        line.contains("</rpminfo_state") ||
                        line.contains("</family_state") ||
                        line.contains("</textfilecontent54_state")) {
                    states.add(state);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DataStorageLin result = new DataStorageLin(vulnerabilities, inventories, tests, objects, states);
        if (result != null) {
            return result;
        } else {
            System.out.println("Уязвимостей не найдено");
            return null;
        }
    }
    private CriteriaLin criteriaParser(List<String> lines) {
        CriteriaLin result = new CriteriaLin();
        int count;
        List<String> subCriteria;
        if (lines.get(0).contains("operator=\"")) {
            String regex = "operator=\"(.*?)\"";
            result.setOperator(extractValue(lines.get(0), regex));
        }
        for (int cnt = 1; cnt < lines.size(); cnt++) {
            if (lines.get(cnt).contains("<extend_definition")) {
                String regex = "def:(.*?)\"";
                result.addDefinition(extractValue(lines.get(cnt), regex));
            } else if (lines.get(cnt).contains("<criterion")) {
                String regex = "tst:(.*?)\"";
                result.addTest(extractValue(lines.get(cnt), regex));
            } else if (lines.get(cnt).contains("<criteria")) {
                count = 1;
                subCriteria = new ArrayList<>();
                subCriteria.add(lines.get(cnt));
                cnt++;
                while (count > 0) {
                    subCriteria.add(lines.get(cnt));
                    if (lines.get(cnt).contains("<criteria")) {
                        count++;
                    } else if (lines.get(cnt).contains("</criteria")) {
                        count--;
                    }
                    cnt++;
                }
                cnt--;
                result.addCriteria(criteriaParser(subCriteria));
            }
        }
        return result;
    }
    private static String extractValue(String line, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public static int countDoubleQuotes(String input) {
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == '"') {
                count++;
            }
        }
        return count;
    }
}
