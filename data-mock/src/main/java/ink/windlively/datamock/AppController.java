package ink.windlively.datamock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    private final MockService mockService;

    public AppController(MockService mockService) {
        this.mockService = mockService;
    }

    @GetMapping("init")
    public String initData(){
        mockService.initData();
        return "initialing...";
    }

    @GetMapping("start-auto-running")
    public String scheduleTransaction(){
        mockService.startAutoRunning();
        return "success";
    }

    @GetMapping("stop-auto-running")
    public String stopScheduleTransaction(){
        mockService.stopAutoRunning();
        return "success";
    }

}
