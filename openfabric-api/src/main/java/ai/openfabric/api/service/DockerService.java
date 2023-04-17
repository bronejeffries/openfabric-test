package ai.openfabric.api.service;

import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import com.github.dockerjava.api.DockerClient;
import ai.openfabric.api.model.Worker;
import io.reactivex.annotations.NonNull;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.PortBinding;

public class DockerService {

    private static DockerService instance;

    private DockerClientConfig dockerClientConfig;
    private DockerClient dockerClient;

    public DockerService() {
        dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        dockerClient = DockerClientBuilder.getInstance(dockerClientConfig).build();
    }

    public static DockerService getInstance() {
        if (instance == null) {
            instance = new DockerService();
        }
        return instance;
    }

    public DockerClient getClient() {
        return this.dockerClient;
    }

    public CreateContainerResponse createContainer(Worker worker) {
        CreateContainerResponse container = null;

        CreateContainerCmd cmd = getClient().createContainerCmd(worker.getImage());

        if (worker.getCmd() != null) {
            cmd.withCmd(worker.getCmd());
        }

        if (worker.getName() != null) {
            cmd.withName(worker.getName());
        }

        if (worker.getHost_name() != null) {
            cmd.withHostName(worker.getHost_name());
        }

        if (worker.getEnv() != null) {
            cmd.withEnv(worker.getEnv());
        }

        if (worker.getPorts() != null) {
            cmd.withPortBindings(PortBinding.parse(worker.getPorts()));
        }

        if (worker.getBinds() != null) {
            cmd.withBinds(Bind.parse(worker.getBinds()));
        }

        try {
            container = cmd.exec();
        } catch (NotFoundException e) {

            System.out.println("Pulling Image .........." + worker.getImage());
            PullImageCmd pullImageCmd = getClient().pullImageCmd(worker.getImage());
            try {
                pullImageCmd.exec(new PullImageResultCallback()).awaitCompletion();
                System.out.println(".......Image Download Complet....");
                container = createContainer(worker);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        return container;
    }

    public void startContainer(@NonNull String containerID) {
        dockerClient.startContainerCmd(containerID).exec();
    }

    public void stopContainer(@NonNull String containerID) {
        dockerClient.stopContainerCmd(containerID).exec();
    }

    public InspectContainerResponse getContainerStatistics(@NonNull String containerID) {
        return dockerClient.inspectContainerCmd(containerID).exec();
    }

}
