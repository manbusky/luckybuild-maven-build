package manbu.maven.plugin;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Wang Jiacheng.
 * Date: 10/5/17
 * Time: 17:01
 */
public class FilterProcessor {

    private Set<String> skips = new HashSet<>();

    private String encoding;

    private Log log;

    private File source;

    private File target;

    public FilterProcessor(File source, File target) {

        this.source = source;
        this.target = target;
    }

    public void setSkips(Collection<String> skips) {

        this.skips.addAll(skips);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public int process(String name, String version) throws MojoExecutionException {

        File sourceFile = new File(this.source, name);

        List<String> lines;

        int replaceCount = 0;

        try (FileInputStream inputStream = new FileInputStream(sourceFile)) {

            lines = IOUtils.readLines(inputStream, encoding);

            for (int i = 0, count = lines.size(); i < count; i++) {

                String line = lines.get(i);

                ReplaceUtils.MatcherWrap matcher = ReplaceUtils.match(line);

                if (matcher.match(skips)) {

                    replaceCount ++;

                    lines.set(i, matcher.replace(version));
                }
            }

        } catch (IOException e) {

            throw new MojoExecutionException("Read source file error.",  e);
        }

        File targetFile = new File(target, name);

        if (!targetFile.getParentFile().exists() && !targetFile.getParentFile().mkdirs()) {

            throw new MojoExecutionException( "Cannot create resource output directory: " + targetFile.getParentFile() );
        }

        if(replaceCount > 0) {

            log.info("---> " + "[" + replaceCount + "]\t" + sourceFile.getPath());
        }

        try(FileWriter writer = new FileWriter(targetFile)) {

            IOUtils.writeLines(lines, null, writer);

        } catch (IOException e) {

            throw new MojoExecutionException("Write result file error.", e);
        }

        return replaceCount;
    }

}
