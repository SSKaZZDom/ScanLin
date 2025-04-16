package scanlin.model;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DebianVersionComparator implements Comparator<String> {

    @Override
    public int compare(String version1, String version2) {
        String[] parts1 = parseVersion(version1);
        String[] parts2 = parseVersion(version2);

        // Сравнение epoch
        int epochComparison = Integer.compare(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]));
        if (epochComparison != 0) {
            return epochComparison;
        }

        // Сравнение upstream version
        int upstreamComparison = compareUpstreamVersion(parts1[1], parts2[1]);
        if (upstreamComparison != 0) {
            return upstreamComparison;
        }

        // Сравнение debian revision
        return compareDebianRevision(parts1[2], parts2[2]);
    }

    private String[] parseVersion(String version) {
        String epoch = "0";
        String upstreamVersion = version;
        String debianRevision = "";

        int epochIndex = version.indexOf(':');
        if (epochIndex != -1) {
            epoch = version.substring(0, epochIndex);
            upstreamVersion = version.substring(epochIndex + 1);
        }

        int revisionIndex = upstreamVersion.lastIndexOf('-');
        if (revisionIndex != -1) {
            debianRevision = upstreamVersion.substring(revisionIndex + 1);
            upstreamVersion = upstreamVersion.substring(0, revisionIndex);
        }

        return new String[]{epoch, upstreamVersion, debianRevision};
    }

    private int compareUpstreamVersion(String v1, String v2) {
        return compareVersionSegments(v1, v2);
    }

    private int compareDebianRevision(String v1, String v2) {
        // Пустая строка считается более ранней версией
        if (v1.isEmpty() && v2.isEmpty()) {
            return 0;
        } else if (v1.isEmpty()) {
            return -1;
        } else if (v2.isEmpty()) {
            return 1;
        }
        return compareVersionSegments(v1, v2);
    }

    private int compareVersionSegments(String v1, String v2) {
        Pattern pattern = Pattern.compile("(\\D*)(\\d*)");
        Matcher m1 = pattern.matcher(v1);
        Matcher m2 = pattern.matcher(v2);

        while (m1.find() && m2.find()) {
            int partCompare = compareNonNumeric(m1.group(1), m2.group(1));
            if (partCompare != 0) {
                return partCompare;
            }

            partCompare = compareNumeric(m1.group(2), m2.group(2));
            if (partCompare != 0) {
                return partCompare;
            }
        }

        // Если одна строка длиннее другой
        if (m1.hitEnd() && !m2.hitEnd()) {
            return -1;
        } else if (!m1.hitEnd() && m2.hitEnd()) {
            return 1;
        }

        return 0;
    }

    private int compareNonNumeric(String s1, String s2) {
        // '~' считается меньше пустой строки
        if (s1.equals(s2)) {
            return 0;
        } else if (s1.equals("~")) {
            return -1;
        } else if (s2.equals("~")) {
            return 1;
        } else {
            return s1.compareTo(s2);
        }
    }

    private int compareNumeric(String n1, String n2) {
        if (n1.isEmpty() && n2.isEmpty()) {
            return 0;
        } else if (n1.isEmpty()) {
            return -1;
        } else if (n2.isEmpty()) {
            return 1;
        } else {
            return Long.compare(Long.parseLong(n1), Long.parseLong(n2));
        }
    }
}
