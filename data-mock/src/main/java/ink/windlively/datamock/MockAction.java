package ink.windlively.datamock;

import lombok.extern.slf4j.Slf4j;
import ink.windlively.datamock.tools.DaoTools;
import ink.windlively.datamock.tools.RandomDataGenerator;
import ink.windlively.datamock.tools.SQLGenerator;
import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ink.windlively.datamock.StockDataInsertService.SCHEMA_NAME;

@Service
@Slf4j
public class MockAction {

    private final DataSource dataSource;

    public final AtomicBoolean autoRunning = new AtomicBoolean(false);

    public MockAction(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional
    public void customerTransaction(long customerId) {
        List<Map<String, Object>> maps = DaoTools.execSelect(dataSource, "SELECT *  FROM customer_transaction WHERE customer_id=" + customerId);
        if (maps.isEmpty()) return;

        Map<String, Object> map = maps.stream().max(Comparator.comparing((a) -> ((Date) (a.get("transaction_time"))))).get();
        BigDecimal balance = (BigDecimal) map.get("balance");

        double amount = Double.parseDouble(balance.toString());
        boolean out = RandomDataGenerator.getRandom().nextBoolean();
        if (amount < 100) return;

        double dealAmount;
        if (out) {
            dealAmount = RandomDataGenerator.getRandom().nextDouble(10, 100);
            amount -= dealAmount;
        } else {
            dealAmount = RandomDataGenerator.getRandom().nextDouble(100, 1000);
            amount += dealAmount;
        }
        map.remove("id");
        map.put("balance", amount);
        map.put("transaction_amount", dealAmount);
        map.put("borrow_loan", out ? 1 : 2);
        map.put("transaction_time", new Date());
        map.put("comment", String.format((out ? "出账" : "入账") + "%.2f元", dealAmount));
        String sql = SQLGenerator.genInsertSQL(map, null, SCHEMA_NAME, "customer_transaction");
        log.info(sql);
        DaoTools.execUpdate(dataSource, sql);
        String transSQL = "UPDATE customer_statistics SET balance=" + amount + " WHERE customer_id=" + customerId;
        log.info(transSQL);
        DaoTools.execUpdate(dataSource, transSQL);
        log.info("finish one transaction, customer id: {}", customerId);
    }

    ExecutorService executors = Executors.newFixedThreadPool(10);

    public void batchUserTransaction() {
        long[] range = findCustomerIdRange();
        long[] longs = RandomDataGenerator.getRandom().longs(1000, range[0], range[1] + 1).distinct().toArray();
        log.info("batch length: {}", longs.length);
        Arrays.stream(longs).forEach(this::customerTransaction);
    }

    @Scheduled(fixedDelay = 2000)
    @Async
    public void scheduleTransaction() {
        try {
            if(!autoRunning.get()) return;

            CountDownLatch countDownLatch = new CountDownLatch(5);
            for (int i = 0; i < 5; i++) {
                executors.submit(() -> {
                    try {
                         batchUserTransaction();
                    }catch (Throwable ex){
                        log.error(ex.getMessage(), ex);
                    }finally {
                        countDownLatch.countDown();
                    }

                });
            }
            countDownLatch.await();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Scheduled(fixedDelay = 1000)
    @Async
    public void scheduleProductionDeal(){
        try {
            if(!autoRunning.get()) return;

            CountDownLatch countDownLatch = new CountDownLatch(5);
            for (int i = 0; i < 5; i++) {
                executors.submit(() -> {
                    try {
                        batchProductionDeal();
                    }catch (Throwable ex){
                        log.error(ex.getMessage(), ex);
                    }finally {
                        countDownLatch.countDown();
                    }

                });
            }
            countDownLatch.await();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void setAutoRunning(boolean bool) {
        autoRunning.set(bool);
    }

    @Transactional
    public void productionDeal(long customerId, List<Map<String, Object>> allProduction) {
        Map<String, Object> customerAccount = DaoTools.selectOne(dataSource, "SELECT * FROM customer_account_info WHERE customer_id=" + customerId);
        if(customerAccount == null) return;

        int riskLevel = (int) customerAccount.get("risk_level");
        allProduction = allProduction.stream().filter(m -> ((int)m.get("risk_level")) == riskLevel).collect(Collectors.toList());
        if(allProduction.isEmpty()) {
            log.info("not fount suitable production for customer {}", customerId);
            return;
        }
        Map<String, Object> production = allProduction.get(RandomDataGenerator.getRandom().nextInt(allProduction.size()));

        long sellerId = (long) customerAccount.get("loc_seller");
        Date dealTime = new Date();
        long productionId = (int) production.get("id");
        int minAmount = ((BigDecimal)production.get("min_amount")).intValue();
        int maxAmount = ((BigDecimal)production.get("max_amount")).intValue();
        int amount = RandomDataGenerator.getRandom().nextInt(minAmount, maxAmount);
        int branchBank = (int) customerAccount.get("account_bank");

        String term = (String) production.get("term");
        term = term.trim();

        Date productionRealArriveDate;
        int value = Integer.parseInt(term.substring(0, term.length() - 1));
        if(term.endsWith("年")){
            productionRealArriveDate = new DateTime(dealTime).plusYears(value).toDate();
        } else {
            productionRealArriveDate = new DateTime(dealTime).plusDays(value).toDate();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("seller_id", sellerId);
        map.put("customer_id", customerId);
        map.put("deal_time", dealTime);
        map.put("product_id", productionId);
        map.put("production_real_arrive_date", productionRealArriveDate);
        map.put("amount", amount);
        map.put("branch_bank", branchBank);
        map.put("is_auto_transfer_save", true);

        String sql = SQLGenerator.genInsertSQL(map, null, SCHEMA_NAME, "product_deal_data");
        DaoTools.execUpdate(dataSource, sql);
        log.info(sql);
        sql = "UPDATE production SET history_income = history_income+" + amount + " WHERE id=" + productionId;
        DaoTools.execUpdate(dataSource, sql);
        log.info(sql);
        List<Map<String, Object>> processList = DaoTools.execSelect(dataSource, "SELECT * FROM achievement_process WHERE seller_id=" + sellerId);
        if(!processList.isEmpty()){
            Map<String, Object> update = new HashMap<>();
            Map<String, Object> process = processList.get(0);
            BigDecimal finishAmount = ((BigDecimal) process.get("finish_amount")).add(new BigDecimal(amount));
            BigDecimal targetAmount = ((BigDecimal) process.get("target_amount")).multiply(new BigDecimal(10000));
            update.put("id", process.get("id"));
            update.put("finish_amount", finishAmount);
            Date finishTime;;
            if(finishAmount.compareTo(targetAmount) > 0 && (finishTime = (Date) process.get("finish_time")) == null){
                update.put("finish_time", new Date());
            }
            sql = SQLGenerator.genUpdateSQL(update, null, Collections.singletonList("id"), SCHEMA_NAME, "achievement_process");
            DaoTools.execUpdate(dataSource, sql);
            log.info(sql);
        }
    }

    public void batchProductionDeal(){
        List<Map<String, Object>> allProduction = Collections.unmodifiableList(DaoTools.execSelect(dataSource, "SELECT * FROM production"));
        long[] range = findCustomerIdRange();
        long[] longs = RandomDataGenerator.getRandom().longs(500, range[0], range[1] + 1).distinct().toArray();
        log.info("batch length: {}", longs.length);
        Arrays.stream(longs).forEach(i -> productionDeal(i, allProduction));

    }

    private long[] findCustomerIdRange(){
        Map<String, Object> map = DaoTools.selectOne(dataSource, "SELECT MAX(id) as max, MIN(id) as min FROM customer");
        long max = (long) map.get("max");
        long min = (long) map.get("min");
        return new long[]{min, max};
    }

}
