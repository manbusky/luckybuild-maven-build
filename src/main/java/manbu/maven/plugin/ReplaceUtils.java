package manbu.maven.plugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wang Jiacheng.
 * Date: 10/5/17
 * Time: 14:55
 */
public class ReplaceUtils {

    private static final String patternString = "(<script type=\"text/javascript\" src=\")([0-9,a-z,A-Z,/,_,\\-,$,{,},.]+\\.js)(\"></script>)$";

    private static final Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);

    public static MatcherWrap match(CharSequence input) {

        Matcher matcher = pattern.matcher(input);

        return new MatcherWrap(input, matcher);
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyMMddHHmm");

    public static String sequence() {

        return DATE_FORMAT.format(new Date(System.currentTimeMillis()));
    }

    static class MatcherWrap {

        private CharSequence source;
        private Matcher matcher;

        public MatcherWrap(CharSequence source, Matcher matcher) {
            this.source = source;
            this.matcher = matcher;
        }

        public boolean match(Collection<String> skips) {

            if(matcher.reset().find()) {

                String filename = matcher.group(2);

                for (String skip : skips) {

                    if(filename.endsWith(skip)) {

                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        public String replace(String version) {

            StringBuilder sb = new StringBuilder();

            sb.append(source);

            if(matcher.reset().find()) {

                int end = matcher.end(2);

                sb.insert(end, "?v=" + version);
            }

            return sb.toString();
        }

    }

}
