package scanlin.model;


import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexMatcher {

    /**
     * Проверяет, соответствует ли строка регулярному выражению.
     *
     * @param input     строка, которую проверяем
     * @param regex     регулярное выражение
     * @return true, если совпадает; false в противном случае
     */
    public static boolean matches(String input, String regex) {
        if (input == null || regex == null) {
            return false;
        }

        try {
            Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(input).matches();
        } catch (PatternSyntaxException e) {
            System.err.println("Некорректное регулярное выражение: " + regex);
            return false;
        }
    }
}