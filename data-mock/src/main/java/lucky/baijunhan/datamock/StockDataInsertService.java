package lucky.baijunhan.datamock;

import lombok.extern.slf4j.Slf4j;
import lucky.baijunhan.datamock.tools.DaoTools;
import lucky.baijunhan.datamock.tools.RandomDataGenerator;
import lucky.baijunhan.datamock.tools.SQLGenerator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class StockDataInsertService {

    public static final String SCHEMA_NAME = "adp_mock_spr_src";

    private final DataSource dataSource;

    private final ExecutorService executors = Executors.newFixedThreadPool(15);

    public boolean inInit = false;

    public StockDataInsertService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Value("classpath:/init.sql")
    private Resource initTableScript;

    public void initData() {
        log.info("start init all data");
        try {
            Scanner scanner = new Scanner(initTableScript.getInputStream());
            StringBuilder content = new StringBuilder();
            while (scanner.hasNextLine()) content.append(scanner.nextLine()).append("\n");
            DaoTools.execUpdate(dataSource, content.toString());
            log.info("executed script: \n{}", content);

            initProduction(2000);
            initDepartment();
            initSeller();
            initCustomer(5000000);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            inInit = false;
            log.info("finish init all data");
        }
    }


    // 初始化部门的数据
    private void initDepartment() {
        DaoTools.execUpdate(dataSource, "DELETE FROM department");
        AtomicInteger id = new AtomicInteger(1);
        // 总行信息
        Map<String, Object> rootDepartment = buildDepartmentMap(id.getAndIncrement(), -1, 0, "杭州市", "总行", false);
        DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(rootDepartment, null, SCHEMA_NAME, "department"));

        // 生成10个支行
        List<Map<String, Object>> branchBanks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> branchBank = buildDepartmentMap(id.getAndIncrement(), 1, 2, RandomDataGenerator.randomBankAddress(), "支行" + i, false);
            DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(branchBank, null, SCHEMA_NAME, "department"));
            branchBanks.add(branchBank);
        }

        // 生成各个支行的子部门
        branchBanks.stream().flatMap(o -> {
            int bId = (int) o.get("id");
            String address = (String) o.get("address");
            Map<String, Object> subDepartmentA = buildDepartmentMap(id.getAndIncrement(), bId, 4, address, "个人信贷业务部", true);
            Map<String, Object> subDepartmentB = buildDepartmentMap(id.getAndIncrement(), bId, 4, address, "个人储蓄业务部", true);
            Map<String, Object> subDepartmentC = buildDepartmentMap(id.getAndIncrement(), bId, 4, address, "个人理财业务部", true);
            return Stream.of(subDepartmentA, subDepartmentB, subDepartmentC);
        }).forEach(e -> DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(e, null, SCHEMA_NAME, "department")));


    }

    private void initProduction(int count){
        log.info("starting init production data");
        for (int i = 0; i < count; i++) {

            Map<String, Object> product = new HashMap<>();
            product.put("name", "金融产品" + i);
            product.put("product_type", RandomDataGenerator.getRandom().nextInt(3));
            product.put("product_tag", "默认标签");
            product.put("term", RandomDataGenerator.randomTerm());
            product.put("rate", RandomDataGenerator.getRandom().nextDouble(0.1, 7));
            product.put("is_float_rate", RandomDataGenerator.getRandom().nextBoolean());
            product.put("interest_method", RandomDataGenerator.getRandom().nextInt(5));
            product.put("min_amount", RandomDataGenerator.getRandom().nextInt(1, 11) * 1000);
            product.put("max_amount", RandomDataGenerator.getRandom().nextInt(10, 101) * 1000);
            product.put("can_pre_end", RandomDataGenerator.getRandom().nextBoolean());
            product.put("can_transfer", RandomDataGenerator.getRandom().nextBoolean());
            product.put("can_pledge", RandomDataGenerator.getRandom().nextBoolean());
            product.put("risk_level", RandomDataGenerator.getRandom().nextInt(1, 6));
            product.put("start_date", RandomDataGenerator.randomDate(2015, 2019));
            product.put("end_date", RandomDataGenerator.randomDate(2025, 2030));
            product.put("build_date", product.get("start_date"));
            product.put("due_date", product.get("end_date"));
            product.put("year_income_rate", RandomDataGenerator.getRandom().nextDouble(0.12, 0.6));
            product.put("history_income", RandomDataGenerator.getRandom().nextDouble(100000, 1000000));
            DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(product, null, SCHEMA_NAME, "production"));
        }
        log.info("finish init {} production", count);
    }

    private static Map<String, Object> buildDepartmentMap(int id, int parentId, int typeId, String address, String departmentName, boolean isLeaf) {
        Map<String, Object> department = new HashMap<>();
        department.put("id", id);
        department.put("parent_id", parentId);
        department.put("type_id", typeId);
        department.put("address", address);
        department.put("department_name", departmentName);
        department.put("is_leaf", isLeaf);
        return department;
    }

    // 初始化客户经理数据
    private void initSeller() {
        log.info("starting init seller data...");
        DaoTools.execUpdate(dataSource, "DELETE FROM seller_info");
        Map<Integer, int[]> bankAndLeafDepartmentRef =
                DaoTools.execSelect(dataSource, "SELECT id FROM department WHERE type_id = 2").stream().collect(
                        Collectors.toMap(i -> (int) i.get("id"), i -> DaoTools.execSelect(dataSource, "SELECT id FROM department WHERE parent_id=" + i.get("id") + " AND is_leaf").stream().mapToInt(o -> (int) o.get("id")).toArray())
                );
        AtomicInteger id = new AtomicInteger(1);
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            executors.submit(() -> {
                try {
                    Map<String, Object> seller = createOneRandomSeller(bankAndLeafDepartmentRef, -1, 3, id.getAndIncrement());
                    int subSellerCountA = RandomDataGenerator.getRandom().nextInt(10, 21);
                    for (int j = 0; j < subSellerCountA; j++) {
                        Map<String, Object> sellerA = createOneRandomSeller(bankAndLeafDepartmentRef, (int) seller.get("id"), 2, id.getAndIncrement());
                        int subSellerCountB = RandomDataGenerator.getRandom().nextInt(6, 11);
                        for (int s = 0; s < subSellerCountB; s++) {
                            createOneRandomSeller(bankAndLeafDepartmentRef, (int) sellerA.get("id"), 1, id.getAndIncrement());
                        }
                    }
                }catch (Throwable ex){
                    log.error(ex.getMessage(), ex);
                }finally {
                    countDownLatch.countDown();
                }

            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        log.info("finish init {} seller data", id.get() - 1);

    }

    private Map<String, Object> createOneRandomSeller(Map<Integer, int[]> bankAndLeafDepartmentRef, int leader, int position, Integer id) {
        String idCard = RandomDataGenerator.randomIdCard();
        int[] ints = RandomDataGenerator.randomDepartment(bankAndLeafDepartmentRef);
        int bank = ints[0];
        int depart = ints[1];
        String email = RandomDataGenerator.randomEmail();
        String mobile = RandomDataGenerator.randomMobilePhone();
        String entryTime = RandomDataGenerator.randomDate(2009, 2020);
        String name = RandomDataGenerator.randomName(((idCard.charAt(16) - '0') & 1) == 0);
        Map<String, Object> map = new HashMap<>();
        map.put("id_card", idCard);
        map.put("current_bank", bank);
        map.put("entry_time", entryTime);
        map.put("name", name);
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("leader_id", leader);
        map.put("position", position);
        map.put("department_id", depart);
        map.put("id", id);

        DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(map, null, SCHEMA_NAME, "seller_info"));
        Map<String, Object> process = createAchievementProcess(id, map);
        DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(process, null, SCHEMA_NAME, "achievement_process"));
        return map;
    }


    public void initCustomer(int count) {
        if (count < 20) throw new IllegalArgumentException("count is too small(need greater than 20)");
        long l = System.currentTimeMillis();
        log.info("starting init customer data, count is {}", count);
        DaoTools.execUpdate(dataSource, "DELETE FROM customer");
        DaoTools.execUpdate(dataSource, "DELETE FROM customer_account_info");
        Map<Integer, Integer> departmentAndBankRef = DaoTools.execSelect(dataSource, "SELECT id, parent_id FROM department WHERE is_leaf").stream().collect(
                Collectors.toMap(e -> (int) e.get("id"), e -> (int) e.get("parent_id"))
        );

        long maxSeller = (long) Objects.requireNonNull(DaoTools.selectOne(dataSource, "SELECT MAX(id) AS id FROM seller_info")).get("id");
        int groupSize = count / 10;
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            int start = i * groupSize + 1;
            int end = i == 9 ? count : ((i + 1) * groupSize);
            executors.submit(() -> {
                try {
                    for (int j = start; j <= end; j++) {
                        Map<String, Object> customer = createOneCustomer(j);
                        Map<String, Object> customerAccountInfo = createCustomerAccountInfo(j, (int) maxSeller, departmentAndBankRef);
                        Map<String, Object> customerInitializationTransaction = createCustomerInitializationTransaction(j, customerAccountInfo);
                        Map<String, Object> relation = createCustomerSellerInitRelation(j, (int) customerAccountInfo.get("loc_seller"));
                        Map<String, Object> customerStatistics = createCustomerStatistics(j, (double) customerInitializationTransaction.get("balance"));
                        DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(customer, null, SCHEMA_NAME, "customer"));
                        DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(customerAccountInfo, null, SCHEMA_NAME, "customer_account_info"));
                        DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(customerInitializationTransaction, null, SCHEMA_NAME, "customer_transaction"));
                        DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(relation, null, SCHEMA_NAME, "customer_seller_relation"));
                        DaoTools.execUpdate(dataSource, SQLGenerator.genInsertSQL(customerStatistics, null, SCHEMA_NAME, "customer_statistics"));
                    }
                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        log.info("finish init {} customer data, used time: {}", count, System.currentTimeMillis() - l);
    }

    public static void main(String[] args) {
        System.out.println(('3' - '0') & 1);
    }

    private Map<String, Object> createOneCustomer(Integer id) {
        String idCard = RandomDataGenerator.randomIdCard();
        boolean gender = ((idCard.charAt(16) - '0') & 1) == 1;
        String realName = RandomDataGenerator.randomName(!gender);
        String mobile = RandomDataGenerator.randomMobilePhone();

        String address = RandomDataGenerator.randomAddress();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("real_name", realName);
        map.put("id_card", idCard);
        map.put("gender", gender);
        map.put("mobile", mobile);
        map.put("address", address);
        return map;
    }

    private Map<String, Object> createCustomerAccountInfo(int customerId, int sellerMaxCount, Map<Integer, Integer> departmentAndBankRef) {
        String entryTime = RandomDataGenerator.randomDate(2011, 2019);
        int riskLevel = RandomDataGenerator.getRandom().nextInt(1, 6);
        String riskTestDate = entryTime;
        int locSeller = RandomDataGenerator.getRandom().nextInt(1, sellerMaxCount);
        Map<String, Object> seller = DaoTools.selectOne(dataSource, "SELECT * FROM seller_info WHERE id=" + locSeller);
        int accountBank = departmentAndBankRef.get((int) seller.get("department_id"));
        String accountNo = RandomDataGenerator.randomBankCard();
        int accountLevel = RandomDataGenerator.getRandom().nextInt(1, 6);
        Map<String, Object> map = new HashMap<>();
        map.put("customer_id", customerId);
        map.put("risk_level", riskLevel);
        map.put("risk_test_date", riskTestDate);
        map.put("account_bank", accountBank);
        map.put("entry_time", entryTime);
        map.put("loc_seller", locSeller);
        map.put("account_no", accountNo);
        map.put("account_level", accountLevel);
        return map;
    }

    private Map<String, Object> createCustomerInitializationTransaction(int customerId, Map<String, Object> customerAccount) {
        DateTime entryTime = DateTime.parse((String) customerAccount.get("entry_time"));
        String transactionTime = entryTime.plusDays(RandomDataGenerator.getRandom().nextInt(5)).plusHours(RandomDataGenerator.getRandom().nextInt(24)).toString("yyyy-MM-dd");
        boolean isOut = RandomDataGenerator.getRandom().nextBoolean();
        int borrow_loan = isOut ? 1 : 2;
        int level = (int) customerAccount.get("account_level");
        double balance = RandomDataGenerator.getRandom().nextDouble(10000 * level, 100000 * level * level);
        double transactionAmount = RandomDataGenerator.getRandom().nextDouble(100, 10000);
        Map<String, Object> map = new HashMap<>();
        map.put("customer_id", customerId);
        map.put("transaction_time", transactionTime);
        map.put("comment", String.format((isOut ? "出账" : "入账") + "%.2f元", transactionAmount));
        map.put("balance", balance);
        map.put("transaction_amount", transactionAmount);
        map.put("borrow_loan", borrow_loan);
        map.put("customer_account_id", customerAccount.get("account_no"));
        return map;
    }

    private Map<String, Object> createCustomerSellerInitRelation(int customerId, int sellerId) {
        boolean wechatRelation = RandomDataGenerator.getRandom().nextBoolean();
        Map<String, Object> map = new HashMap<>();
        map.put("customer_id", customerId);
        map.put("seller_id", sellerId);
        map.put("wechat_relation", wechatRelation);
        return map;
    }

    private Map<String, Object> createCustomerStatistics(int customerId, double balance){
        Map<String, Object> map = new HashMap<>();
        map.put("customer_id", customerId);
        map.put("balance", balance);
        map.put("l_date", DateTime.now().toString("yyyy-MM-dd"));
        return map;
    }

    private Map<String, Object> createAchievementProcess(int sellerId, Map<String, Object> seller){
        int targetAmount = RandomDataGenerator.getRandom().nextInt(10, 100);
        Map<String, Object> map = new HashMap<>();
        map.put("seller_id", sellerId);
        map.put("target_name", "计划目标");
        map.put("target_amount", targetAmount);
        map.put("start_time", seller.get("entry_time"));
        map.put("end_time", RandomDataGenerator.randomDate(2022, 2025));
        map.put("finish_amount", RandomDataGenerator.getRandom().nextInt(10) * 10000);
        return map;
    }

}
