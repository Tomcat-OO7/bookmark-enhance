package life.cookedfox.bookmarkenhance.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class SystemCommandUtils {

    private static final String DOCKER_RUN_SINGLEFILE = "docker run singlefile %s > %s";

    public static void saveSingleFile(String url, String filePath) {
        exec(String.format(DOCKER_RUN_SINGLEFILE, url, filePath));
    }

    public static void exec(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", command);

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }

            int exitCode = process.waitFor();
            log.info("Exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            log.error("系统命令执行异常", e);
        }
    }
}
