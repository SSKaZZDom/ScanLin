package scanlin.model.parserWin;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OvalParserWin {

    public DataStorageWin ovalParsing() {
        List<VulnerabilityWin> vulnerabilities = new ArrayList<>();
        List<InventoryWin> inventories = new ArrayList<>();
        List<TestWin> tests = new ArrayList<>();
        List<ObjectWin> objects = new ArrayList<>();
        List<VariableWin> vars = new ArrayList<>();
        String filePath = "data/scanoval.xml";
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Файл не найден");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            VulnerabilityWin vul = new VulnerabilityWin();
            InventoryWin inventory = new InventoryWin();
            List<StateWin> states = new ArrayList<>();
            TestWin test = new TestWin();
            int cnt;
            boolean inventoryFlag = false;

            // Парсер для инвенторис и уязвимостей

            while (!(line = reader.readLine()).contains("</definitions>")) {
                if (line.contains("<definition")) {
                    if (line.contains("class=\"inventory\"")) {
                        inventoryFlag = true;
                        inventory = new InventoryWin();
                        String regex = "id=\"(.*?)\"";
                        inventory.setId(extractValue(line, regex));
                    } else if (line.contains("class=\"vulnerability\"")) {
                        vul = new VulnerabilityWin();
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
                    } else if (line.contains("<criteria")){
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
                    } else if (line.contains("<criteria")){
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

            // Парсер для тестов
            while (!(line = reader.readLine()).contains("</tests>")) {
                if (line.contains("<file_test") || line.contains("<registry_test") || line.contains("<variable_test") ||
                    line.contains("<textfilecontent54_test") || line.contains("<xmlfilecontent_test") ||
                    line.contains("<wmi57_test") || line.contains("<cmdlet_test") || line.contains("<family_test") ||
                    line.contains("<environmentvariable58_test") || line.contains("<service_test")) {
                    test = new TestWin();
                    test.setType(line.substring(line.indexOf("<") + 1, line.indexOf("_test")));
                    test.setXmlns(extractValue(line, "xmlns=\"([^\"]+)\""));
                    test.setId(extractValue(line, "id=\"(.*?)\""));
                    test.setCheckExistence(extractValue(line, "check_existence=\"([^\"]+)\""));
                    test.setCheck(extractValue(line, "check=\"([^\"]+)\""));
                } else if (line.contains("<object")) {
                    test.setObject(extractValue(line, "object_ref=\"(.*?)\""));
                } else if (line.contains("<state")) {
                    test.addState(extractValue(line, "state_ref=\"(.*?)\""));
                } else if (line.contains("</file_test") || line.contains("</registry_test") || line.contains("</variable_test") ||
                        line.contains("</textfilecontent54_test") || line.contains("</xmlfilecontent_test") ||
                        line.contains("</wmi57_test") || line.contains("</cmdlet_test") || line.contains("</family_test") ||
                        line.contains("</environmentvariable58_test") || line.contains("</service_test")) {
                    tests.add(test);
                }
            }
            ObjectWin obj = new ObjectWin();
            while (!(line = reader.readLine()).contains("</objects>")) {
                line = line.trim();
                FilterWin filter;
                if (line.contains("<environmentvariable58_object") ||
                        line.contains("<xmlfilecontent_object") ||
                        line.contains("<cmdlet_object") ||
                        line.contains("<family_object") ||
                        line.contains("<textfilecontent54_object") ||
                        line.contains("<wmi57_object") ||
                        line.contains("<file_object") ||
                        line.contains("<registry_object") ||
                        line.contains("<variable_object") ||
                        line.contains("<service_object")) {
                    obj = new ObjectWin();
                    obj.setType(line.substring(line.indexOf("<") + 1, line.indexOf("_object"))); // Определяем тип (file, registry и т. д.)
                    obj.setId(extractValue(line, "id=\"(.*?)\""));
                    obj.setXmlns(extractValue(line, "xmlns=\"([^\"]+)\""));
                    if (line.contains("/>")) {
                       objects.add(obj);
                    }
                } else if (line.contains("<name") || line.contains("<filename") || line.contains("<service_name")) {
                    obj.setName(extractValue(line, "name>(.*?)</"));
                    if (line.contains("operation")) {
                        obj.setOperation(extractValue(line,"\"operation=\\\"([^\\\"]+)\\\"\""));
                    }
                } else if (line.startsWith("<path ") || line.contains("<filepath ") || line.contains("<key ")) {
                    obj.setVarRef(extractValue(line, "var_ref=\"(.*?)\""));
                    obj.setVarCheck(extractValue(line, "var_check=\"([^\"]+)\""));
                } else if (line.contains("<hive>")) {
                    obj.setHive(extractValue(line, "<hive>(.*?)</hive>"));
                } else if (line.contains("<key>")) {
                    obj.setKey(extractValue(line, "<key>(.*?)</key>"));
                } else if (line.contains("<path>") || line.contains("<filepath>")) {
                    obj.setPath(extractValue(line, "path>(.*?)</"));
                } else if (line.contains("<behaviors")) {
                    if (line.contains("windows_view=\"")) {
                        obj.setWindowsView(extractValue(line, "windows_view=\"(.*?)\""));
                    }
                    if (line.contains("max_depth=\"")) {
                        obj.setMaxDepth(Integer.parseInt(extractValue(line, "max_depth=\"(.*?)\"")));
                    }
                } else if (line.contains("<wql>")) {
                    obj.setWql(extractValue(line, "<wql>(.*?)</wql>"));
                } else if (line.contains("<namespace>")) {
                    obj.setNamespace(extractValue(line, "<namespace>(.*?)</namespace>"));
                } else if (line.contains("<filter") || line.contains("<ns1:filter")) {
                    filter = new FilterWin();
                    filter.setAction(extractValue(line, "action=\"(.*?)\""));
                    filter.setStateId(extractValue(line, "(.*?)"));
                    obj.addFilter(filter);
                } else if (line.contains("<var_ref>")) {
                    obj.setVarRef(extractValue(line, "(.*?)\""));
                } else if (line.contains("<pattern")) {
                    obj.setPattern(extractValue(line, ">(.*?)</pattern>"));
                } else if (line.contains("<instance") && line.contains("operation=\"")) {
                    obj.setOperation(extractValue(line, "operation=\"([^\"]+)\""));
                } else if (line.contains("<xpath>")) {
                    obj.setXpath(extractValue(line, "<xpath>(.*?)</xpath>"));
                } else if (line.contains("<verb>")) {
                    obj.setCmdCommand(extractValue(line, "<verb>(.*?)</verb>"));
                } else if (line.contains("<noun>")) {
                    obj.addCmdCommand((extractValue(line, "<noun>(.*?)</noun>")));
                } else if (line.contains("<oval-def:field name=\"property\">")) {
                    Pattern pattern = Pattern.compile("<oval-def:field name=\"property\">(.*?)</oval-def:field>");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        obj.setSelect(Arrays.asList(matcher.group(1).split(",")));
                    }
                } else if (line.contains("<oval-def:field")) {
                    obj.setParameter(extractValue(line, "name=\"(.*?)\""));
                } else if (line.contains("<set") || line.contains("<ns1:set") || line.contains("oval-def:set")) {
                    List<String> lines = new ArrayList<>();
                    lines.add(line);
                    cnt = 1;
                    while (cnt > 0) {
                        line = reader.readLine();
                        if (line.contains("set>") && !line.contains("</")) {
                            cnt++;
                        } else if (line.contains("</") && line.contains("set>")) {
                            cnt--;
                        }
                        lines.add(line);
                    }
                    obj.setSet(setParser(lines));
                } else if (line.contains("</environmentvariable58_object") ||
                        line.contains("</xmlfilecontent_object") ||
                        line.contains("</cmdlet_object") ||
                        line.contains("</family_object") ||
                        line.contains("</textfilecontent54_object") ||
                        line.contains("</wmi57_object") ||
                        line.contains("</file_object") ||
                        line.contains("</registry_object") ||
                        line.contains("</variable_object") ||
                        line.contains("</service_object")) {
                    //System.out.println(obj);
                    objects.add(obj);
                }
            }
            StateWin state = new StateWin();
            HashMap<String, String> value;
            while (!(line = reader.readLine()).contains("</states")){
                if (line.contains("<xmlfilecontent_state") ||
                        line.contains("<cmdlet_state") ||
                        line.contains("<family_state") ||
                        line.contains("<textfilecontent54_state") ||
                        line.contains("<wmi57_state") ||
                        line.contains("<file_state") ||
                        line.contains("<registry_state") ||
                        line.contains("<variable_state") ||
                        line.contains("<service_state")) {
                    state = new StateWin();
                    state.setType(line.substring(line.indexOf("<") + 1, line.indexOf("_state")));
                    state.setId(extractValue(line, "id=\"(.*?)\""));
                    state.setXmlns(extractValue(line, "xmlns=\"([^\"]+)\""));
                } else if (line.contains("<value") || line.contains("<value_of")) {
                    value = new HashMap<>();
                    value.put("tag", "value");
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    value.put("entity_check", extractValue(line, "entity_check=\"(.*?)\""));
                    if (line.contains("</value>")) {
                        value.put("value", extractValue(line, ">(.*?)</"));
                    }
                    state.addValue(value);
                } else if (line.contains("<text")) {
                    value = new HashMap<>();
                    value.put("tag", "text");
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    value.put("value", extractValue(line, ">(.*?)</"));
                    state.addValue(value);
                } else if (line.contains("<subexpression")) {
                    value = new HashMap<>();
                    value.put("tag", "subexpression");
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    value.put("value", extractValue(line, ">(.*?)</"));
                    state.addValue(value);
                } else if (line.contains("<path")) {
                    value = new HashMap<>();
                    value.put("tag", "path");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    state.addValue(value);
                } else if (line.contains("<product_version") || line.contains("<version")) {
                    value = new HashMap<>();
                    value.put("tag", "version");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    value.put("entity_check", extractValue(line, "entity_check=\"(.*?)\""));
                    state.addValue(value);
                } else if (line.contains("<key")) {
                    value = new HashMap<>();
                    value.put("tag", "key");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    state.addValue(value);
                } else if (line.contains("<result")) {
                    value = new HashMap<>();
                    value.put("tag", "result");
                    value.put("entity_check", extractValue(line, "entity_check=\"(.*?)\""));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    state.addValue(value);
                } else if (line.contains("<field") || line.contains("<oval-def:field")) {
                    value = new HashMap<>();
                    value.put("tag", "field");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    value.put("name", extractValue(line, "name=\"(.*?)\""));
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    value.put("datatype", extractValue(line, "datatype=\"(.*?)\""));
                    state.addValue(value);
                } else if (line.contains("<product_name")) {
                    value = new HashMap<>();
                    value.put("tag", "product_name");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    value.put("operation", extractValue(line, "operation=\"(.*?)\""));
                    state.addValue(value);
                } else if (line.contains("<windows_view")) {
                    value = new HashMap<>();
                    value.put("tag", "windows_view");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    state.addValue(value);
                } else if (line.contains("<start_type")) {
                    value = new HashMap<>();
                    value.put("tag", "start_type");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    state.addValue(value);
                } else if (line.contains("<family")) {
                    value = new HashMap<>();
                    value.put("tag", "family");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    state.addValue(value);
                } else if (line.contains("current_state")) {
                    value = new HashMap<>();
                    value.put("tag", "current_state");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    state.addValue(value);
                } else if (line.contains("<description>")) {
                    value = new HashMap<>();
                    value.put("tag", "description");
                    value.put("value", extractValue(line, ">(.*?)</"));
                    state.addValue(value);
                } else if (line.contains("<company")) {
                    value = new HashMap<>();
                    if (line.contains("var_check")) {
                        value.put("tag", "company");
                        value.put("var_check", extractValue(line, "var_check=\"(.*?)\""));
                        value.put("var_ref", extractValue(line, "var_ref=\"(.*?)\""));
                    } else {
                        value.put("tag", "company");
                        value.put("value", extractValue(line, "<company>(.*?)</company>"));
                    }
                    state.addValue(value);
                } else if (line.contains("<select")) {
                    reader.readLine();
                    reader.readLine();
                } else if (line.contains("<name")) {
                    value = new HashMap<>();
                    value.put("tag", "name");
                    value.put("operation", "not equal");
                    value.put("value", null);
                    state.addValue(value);
                } else if (line.contains("</xmlfilecontent_state") ||
                        line.contains("</cmdlet_state") ||
                        line.contains("</family_state") ||
                        line.contains("</textfilecontent54_state") ||
                        line.contains("</wmi57_state") ||
                        line.contains("</file_state") ||
                        line.contains("</registry_state") ||
                        line.contains("</variable_state") ||
                        line.contains("</service_state")) {
                    states.add(state);
                }
            }

            VariableWin var = new VariableWin();
            int concatCnt = 0;
            int concatFlag = 0;
            boolean unique = false;
            HashMap<String,String> previsiousLine = new HashMap<>();
            while (!(line = reader.readLine()).contains("</variables")) {
                value = new HashMap<>();
                if (line.contains("<local_variable") || line.contains("oval-def:local_variable") || line.contains("<constant_variable")) {
                    var = new VariableWin();
                    var.setId(extractValue(line, "id=\"(.*?)\""));
                    var.setDatatype(extractValue(line, "datatype=\"(.*?)\""));
                    var.setType(line.substring(line.indexOf("<") + 1, line.indexOf("_variable")));
                    concatCnt = 0;
                    concatFlag = 0;
                } else if (line.contains("</local_variable") || line.contains("/oval-def:local_variable") || line.contains("/constant_variable")) {
                    vars.add(var);
                } else if (line.contains("<object_component") || line.contains("<oval-def:object_component")) {
                    value.put("tag", "object_component");
                    value.put("item_field", extractValue(line, "item_field=\"(.*?)\""));
                    value.put("object_ref", extractValue(line, "object_ref=\"(.*?)\""));
                    value.put("record_field", extractValue(line, "record_field=\"(.*?)\""));
                    if (!previsiousLine.isEmpty()) {
                        value.putAll(previsiousLine);
                        previsiousLine = new HashMap<>();
                    }
                    if (unique) {
                        value.put("unique", "yes");
                    }
                    value.put("concat", Integer.toString(concatFlag));
                    var.addValue(value);
                } else if (line.contains("<literal_component") || line.contains("<oval-def:literal_component")) {
                    value.put("tag", "literal_component");
                    value.put("value", extractValue(line, ">(.*?)<"));
                    if (!previsiousLine.isEmpty()) {
                        value.putAll(previsiousLine);
                        previsiousLine = new HashMap<>();
                    }
                    if (unique) {
                        value.put("unique", "yes");
                    }
                    value.put("concat", Integer.toString(concatFlag));
                    var.addValue(value);
                } else if (line.contains("<variable_component")) {
                    value.put("tag", "variable_component");
                    value.put("ver_ref", extractValue(line, "var_ref=\"(.*?)\""));
                    if (!previsiousLine.isEmpty()) {
                        value.putAll(previsiousLine);
                        previsiousLine = new HashMap<>();
                    }
                    if (unique) {
                        value.put("unique", "yes");
                    }
                    value.put("concat", Integer.toString(concatFlag));
                    var.addValue(value);
                } else if (line.contains("<concat") || line.contains("oval-def:concat")) {
                    concatCnt++;
                    concatFlag = concatCnt;
                } else if (line.contains("</concat") || line.contains("/oval-def:concat")) {
                    concatFlag--;
                } else if (line.contains("<regex_capture")) {
                    previsiousLine.put("regex_capture", "yes");
                    previsiousLine.put("pattern_regex_capture", extractValue(line, "pattern=\"(.*?)\""));
                } else if (line.contains("<value")) {
                    var.addConstantValues(extractValue(line, "<value>(.*?)</value>"));
                } else if (line.contains("<unique")) {
                    unique = true;
                } else if (line.contains("</unique")) {
                    unique = false;
                } else if (line.contains("<escape_regex")) {
                    previsiousLine.put("escape_regex", "yes");
                } else if (line.contains("<split")) {
                    previsiousLine.put("split", "yes");
                    previsiousLine.put("delimiter", extractValue(line, "delimiter=\"(.*?)\""));
                } else if (line.contains("<end")) {
                    previsiousLine.put("end", "name");
                    previsiousLine.put("character", extractValue(line, "character=\"(.*?)\""));
                }
            }
            DataStorageWin result = new DataStorageWin(vulnerabilities, inventories, tests, objects, states, vars);
            if (result != null) {
                return result;
            } else {
                System.out.println("Уязвимостей не найдено");
                return null;
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
            return null;
        }
    }


    private ObjectSetWin setParser(List<String> lines) {
        ObjectSetWin result = new ObjectSetWin();
        FilterWin filter;
        int count = 0;
        List<String> subSet;
        for (int cnt = 1; cnt < lines.size(); cnt++) {
            if (lines.get(cnt).contains("<object_reference>")) {
                result.addObjectRef(extractValue(lines.get(cnt), "oval:ru\\.altx-soft\\.win:obj:(\\d+)"));
            } else if ((lines.get(cnt).contains("<filter"))) {
                filter = new FilterWin();
                filter.setAction(extractValue(lines.get(cnt), "action=\"(.*?)\""));
                filter.setStateId(extractValue(lines.get(cnt), "oval:ru\\.altx-soft\\.win:ste:(\\d+)"));
                result.addFilter(filter);
            } else if (lines.get(cnt).contains("<set") || lines.get(cnt).contains("<ns1:set") || lines.get(cnt).contains("<oval-def:set")) {
                count = 1;
                subSet = new ArrayList<>();
                subSet.add(lines.get(cnt));
                cnt++;
                while (count > 0) {
                    subSet.add(lines.get(cnt));
                    if (lines.get(cnt).contains("<set") || lines.get(cnt).contains("<ns1:set") || lines.get(cnt).contains("<oval-def:set")) {
                        count++;
                    } else if (lines.get(cnt).contains("</") && lines.get(cnt).contains("set>")) {
                        count--;
                    }
                    cnt++;
                }
                cnt--;
                result.addNestedSet(setParser(subSet));
            }
        }
        return result;
    }
    private CriteriaWin criteriaParser(List<String> lines) {
        CriteriaWin result = new CriteriaWin();
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
}
