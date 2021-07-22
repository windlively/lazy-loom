package ink.windlively.datamock;

import org.springframework.stereotype.Service;

@Service
public class MockService {

    private final StockDataInsertService stockDataInsertService;

    private final MockAction action;

    public MockService(StockDataInsertService stockDataInsertService, MockAction action) {
        this.stockDataInsertService = stockDataInsertService;
        this.action = action;
    }

    public void initData(){
        if(stockDataInsertService.inInit) throw new IllegalStateException("data is already in init");
        stopAutoRunning();
        new Thread(stockDataInsertService::initData).start();
    }

    public void startAutoRunning(){
        if(stockDataInsertService.inInit) throw new IllegalStateException("data is in init");
        action.setAutoRunning(true);
    }

    public void stopAutoRunning(){
        action.setAutoRunning(false);
    }

}
