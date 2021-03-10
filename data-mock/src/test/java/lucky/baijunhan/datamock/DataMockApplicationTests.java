package lucky.baijunhan.datamock;

import lucky.baijunhan.datamock.tools.DaoTools;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static lucky.baijunhan.datamock.tools.RandomDataGenerator.*;
import static lucky.baijunhan.datamock.tools.SQLGenerator.genInsertSQL;
import static lucky.baijunhan.datamock.tools.SQLGenerator.genUpdateSQL;

@SpringBootTest
class DataMockApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	DataSource dataSource;

	@Test
	void createCustomer(){
		for (int i = 0; i < 100000; i++) {
			String idCard = randomIdCard();
			String name = randomName(((idCard.charAt(16)-'0') & 1) == 0);
			String address = randomAddress();
			Map<String, Object> customer = new HashMap<>();
			customer.put("id", i + 1);
			customer.put("id_card", idCard);
			customer.put("name", name);
			customer.put("address", address);
			DaoTools.execUpdate(dataSource, genInsertSQL(customer, null, "data_flow_mock_src", "customer"));
		}
	}

	@Test
	void mockTransaction(){
		while (true) {
			int customerId = getRandom().nextInt(1, 100001);
			double amount = getRandom().nextDouble(100, 1000);
			Map<String, Object> map = new HashMap<>();
			map.put("customer_id", customerId);
			map.put("amount", amount);
			DaoTools.execUpdate(dataSource, genInsertSQL(map, null, "data_flow_mock_src", "customer_transaction_log"));
		}
	}

}
