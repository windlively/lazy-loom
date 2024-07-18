package ink.windlively.example.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class M1 {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {
        var example = """
                {
                    "orderId": "3002198456321429603",
                    "orderStatus": 270,
                    "orderTime": "2024-07-13 09:00:00",
                    "couponPriceFen": 5000,
                    "payType": 2,
                    "payStatus": 2,
                    "payTime": "2024-07-13T18:54:38+8:00",
                    "totalPriceFen": 562000,
                    "completeTime": "2024-07-13T18:53:26+8:00",
                    "orderCompleteTime": "2024-07-14T18:54:44+8:00",
                    "confirmBillTime": "2024-07-13T18:54:24+8:00",
                    "billFeeFen": 562000,
                    "orderDisplayStatus": "已支付",
                    "orderStatusGroup": 3,
                    "sendBillTime": "2024-07-13T18:53:29+8:00"
                }
                """;

        Map<String, Object> obj = objectMapper.readValue(example, new TypeReference<>() {
        });

        Set<String> keys = obj.keySet();

        System.out.println("EP_MULTIPART_FACTOR_PRICE_ORDER".toLowerCase());

    }

}
