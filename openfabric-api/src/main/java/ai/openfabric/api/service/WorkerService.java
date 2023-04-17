package ai.openfabric.api.service;

import java.util.List;
import java.util.ArrayList;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;

import ai.openfabric.api.repository.WorkerRepository;
import ai.openfabric.api.model.Worker;

@Service
@Transactional
public class WorkerService {

    @Autowired
    WorkerRepository workerRepo;

    DockerService dockerService = DockerService.getInstance();

    public List<Worker> listAll(Integer page, Integer size, String sort_by){
        List<Worker> list = new ArrayList<>();
        Pageable paging = PageRequest.of(page,size,Sort.by(sort_by));
        Page<Worker> pageResult = workerRepo.findAll(paging);
        if(pageResult.hasContent()){
            list =  pageResult.getContent();
        }
        return list;
    }

    public void save(Worker worker){
        workerRepo.save(worker);
        if (worker.getContainer_id()==null) {
            CreateContainerResponse container = null;
            container = dockerService.createContainer(worker);
            if (container!=null) {
                worker.setContainer_id(container.getId());
                workerRepo.save(worker);
            }
        }
    }

    public Worker get(String id){
        Worker worker = workerRepo.findById(id).get();
        return worker;
    }

    public Worker startWorker(String worker){
        dockerService.startContainer(get(worker).getContainer_id());
        return updateWorkerStatus(worker, 1);
    }

    public Worker stopWorker(String worker){
        dockerService.stopContainer(get(worker).getContainer_id());
        return updateWorkerStatus(worker, 0);
    }

    public Worker updateWorkerStatus(String worker, int status){
        Worker existingWorker = get(worker);
        existingWorker.setStatus(status);
        workerRepo.save(existingWorker);
        return existingWorker;
    }

    public InspectContainerResponse getWorkerStatistics(String id){
        Worker worker = get(id);
        InspectContainerResponse resp = null;
        if (worker.getContainer_id()!=null) {
            resp= dockerService.getContainerStatistics(worker.getContainer_id());
        }
        return resp;
    }
    
}
