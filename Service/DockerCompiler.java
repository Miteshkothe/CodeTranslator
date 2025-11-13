package com.MK.Code_Translator.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;

@Slf4j
public class DockerCompiler {

    private final DockerClient dockerClient;
    private final String hostPath; // folder to store temporary code files

    public DockerCompiler() {
        // Connect to Docker daemon
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375") // Docker Desktop TCP
                .build();

        dockerClient = DockerClientImpl.getInstance(
                config,
                new OkDockerHttpClient.Builder()
                        .dockerHost(config.getDockerHost())
                        .build()
        );

        // Folder to store temporary code
        hostPath = "C:/Users/kothe/code_temp"; // make sure this folder exists
        File tempDir = new File(hostPath);
        if (!tempDir.exists()) tempDir.mkdirs();
    }

    public String executeCode(String code, String lang) throws Exception {
        String extension = getExtension(lang);
        File codeFile;

        // For Java, always use Main.java
        if(lang.equalsIgnoreCase("java")){
            codeFile = new File(hostPath, "Main.java");
        } else {
            codeFile = File.createTempFile("Main", "." + extension);
            codeFile.deleteOnExit(); // temp files deleted after JVM exits
        }

        try (FileWriter writer = new FileWriter(codeFile)) {
            writer.write(code);
        }

        // Bind parent directory of file to Docker
        String hostPathBind = codeFile.getParent();
        Volume containerVolume = new Volume("/code");
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withBinds(new Bind(hostPathBind, containerVolume));

        String image = getImageName(lang);
        String[] cmd = getRunCommand(lang, codeFile.getName()); // pass actual filename

        // Create and start container
        CreateContainerResponse container = dockerClient.createContainerCmd(image)
                .withHostConfig(hostConfig)
                .withWorkingDir("/code")
                .withCmd(cmd)
                .exec();

        String containerId = container.getId();
        dockerClient.startContainerCmd(containerId).exec();

        String logs = getContainerLogs(containerId);

        // Cleanup container
        try {
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        } catch (Exception ignored) {}

        // Delete file only if not Java (keep temp for C++/Python)
        if(!lang.equalsIgnoreCase("java")){
            codeFile.delete();
        }

        return logs.trim();
    }


    // Update run commands to accept filename dynamically
    private String[] getRunCommand(String lang, String filename) {
        switch (lang.toLowerCase()) {
            case "cpp":
                return new String[]{"/bin/sh", "-c", "g++ " + filename + " -o Main && ./Main"};
            case "java":
                return new String[]{"/bin/sh", "-c", "javac " + filename + " && java Main"};
            case "python":
                return new String[]{"/bin/sh", "-c", "python3 " + filename};
            default:
                throw new IllegalArgumentException("Unsupported language: " + lang);
        }
    }

    private String getContainerLogs(String containerId) throws Exception {
        StringBuilder logBuilder = new StringBuilder();
        dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        logBuilder.append(new String(frame.getPayload()));
                    }
                }).awaitCompletion();
        return logBuilder.toString();
    }

    private String getExtension(String lang) {
        switch (lang.toLowerCase()) {
            case "cpp": return "cpp";
            case "java": return "java";
            case "python": return "py";
            default: throw new IllegalArgumentException("Unsupported language: " + lang);
        }
    }

    private String getImageName(String lang) {
        switch (lang.toLowerCase()) {
            case "cpp": return "cpp-runner";
            case "java": return "java-runner";
            case "python": return "python-runner";
            default: throw new IllegalArgumentException("Unsupported language: " + lang);
        }
    }
}
