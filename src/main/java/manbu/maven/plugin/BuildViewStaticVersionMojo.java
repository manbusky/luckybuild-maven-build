package manbu.maven.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Wang Jiacheng.
 * Date: 9/22/17
 * Time: 17:10
 * @goal sequence
 * @phase prepare-package
 */
public class BuildViewStaticVersionMojo extends AbstractMojo {

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * file encoding
     *
     * @parameter property="encoding"
     */
    private String encoding;

    /**
     * skip file name extensions，多个使用逗号(,)隔开；
     *
     * @parameter property="skips"
     */
    private String[] skips;

    /**
     * 处理的文件模式，例如：
     *
     * @parameter property="includes"
     */
    private String[] includes;

    /**
     * Single directory for extra files to include in the WAR.
     *
     * @parameter expression="${basedir}/src/main/webapp"
     */
    private File warSourceDirectoryRoot;

    /**
     * The directory where the webapp is built.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}"
     */
    private File warBuildDirectoryRoot;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("---> includes: " + StringUtils.join(includes, ","));
        getLog().info("---> skips: " + StringUtils.join(skips, ","));

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setFollowSymlinks(false);
        scanner.setCaseSensitive(true);
        scanner.setBasedir(warSourceDirectoryRoot);
        scanner.setIncludes(includes);

        scanner.scan();

        String files[] = scanner.getIncludedFiles();

        FilterProcessor processor = new FilterProcessor(warSourceDirectoryRoot, warBuildDirectoryRoot);
        processor.setEncoding(encoding);
        processor.setLog(getLog());
        processor.setSkips(Arrays.asList(skips));

        int fileCount = 0;
        int lineCount = 0;

        String sequence = ReplaceUtils.sequence();

        for (String file: files) {

            int replaceCount = processor.process(file, sequence);

            if(replaceCount > 0) {

                fileCount ++;
                lineCount += replaceCount;
            }
        }

        getLog().info("---> " + "total: " + fileCount + " file(s), " + lineCount + " line(s).");
    }
}
