package ai.openfabric.api.controller;

import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.dockerjava.api.command.InspectContainerResponse;

import ai.openfabric.api.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ai.openfabric.api.model.Worker;
import java.util.*;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @PostMapping(path = "/hello")
    public @ResponseBody String hello(@RequestBody String name) {
        return "Hello!" + name;
    }

    @GetMapping(path="/list")
    public List<Worker> getWorkers(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "20") Integer size, @RequestParam(defaultValue = "createdAt") String sort_by){
        return workerService.listAll(page,size,sort_by);
    }

    @PostMapping(path = "/add")
    public void add(@RequestBody Worker worker){
        workerService.save(worker);
    }

    @GetMapping(path = "/{id}/information")
    public @ResponseBody Worker get(@PathVariable String id){
            Worker worker = workerService.get(id);
            return worker;
    }

    @PostMapping(path="/stop")
    public @ResponseBody Worker stopWorker(@RequestBody String id){
        return workerService.stopWorker(id);
    }

    @PostMapping(path="/start")
    public @ResponseBody Worker startWorker(@RequestBody String id){
        return workerService.startWorker(id);
    }


    @GetMapping(path = "/{id}/statistics")
    public ResponseEntity<String> statistics(@PathVariable String id){
        ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String js_string="{}";
        try {
            InspectContainerResponse resp = workerService.getWorkerStatistics(id);
            js_string = mapper.writeValueAsString(resp.getHostConfig());
            // String config = mapper.writeValueAsString(resp.getHostConfig());
            // HashMap<String, JSO> stats = new HashMap<>();
            // stats.put("host", new);
            // stats.put("config", config);
             
            // js_string = mapper.writeValueAsString(stats);

        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ResponseEntity<String>(js_string,HttpStatus.OK);
    }


}
