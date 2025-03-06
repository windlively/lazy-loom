package ink.windlively.example.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class M1 {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {
        var input = """
                {"sessionId":"2848279310804860928|FREIGHT_SUB_ORDER_GOODS_MODIFY","windowScene":"FREIGHT_SUB_ORDER_GOODS_MODIFY","openTime":1733173366642,"eventStartTime":1733173366488,"eventNodeList":[{"schemaName":"trade_order_sharding","tableName":"order_goods_0","opType":"UPDATE","eventTime":1733173366488,"before":[{"updated_at":"2024-12-03 05:02:33","deleted":"N"}],"after":[{"id":"252283","order_no":"2848279310804860928","sub_order_no":"49204595713","order_goods_no":"1481221121","group_no":"49204595713","goods_type":"FREIGHT","top_classify_id":"10001","top_classify_name":"装修建材","secondary_classify_id":"100010074","secondary_classify_name":"窗帘杆","load_and_unload_id":"3","load_and_unload_name":"传送带","package_id":"7","package_type":"捆扎","exist_resource":"N","remark":"窗帘杆，捆扎，传送带，1吨，1立方，长3.3米/宽5.4米/高6.8米","max_length":"3.300","max_width":"5.400","max_height":"6.800","max_volume":"1.0","max_weight":"1.0","updated_at":"2024-12-03 05:02:46","created_at":"2024-12-03 05:02:33","deleted":"Y","is_archived":"N"}],"scene":"FREIGHT_SUB_ORDER_GOODS_MODIFY","nodeStep":"softDelete","index":0,"windowKey":"2848279310804860928|FREIGHT_SUB_ORDER_GOODS_MODIFY"},{"schemaName":"trade_order_sharding","tableName":"order_goods_0","opType":"INSERT","eventTime":1733173366488,"after":[{"id":"252284","order_no":"2848279310804860928","sub_order_no":"49204595713","order_goods_no":"1481223169","group_no":"49204595713","goods_type":"FREIGHT","top_classify_id":"10004","top_classify_name":"门窗","secondary_classify_id":"100040047","secondary_classify_name":"钢木门","tertiary_classify_id":"123","tertiary_classify_name":"长形","name":"920","load_and_unload_id":"2","load_and_unload_name":"吊装","unknown_weight_reason":"散的","package_id":"6","package_type":"散装","max_quantity":"2","exist_resource":"Y","shape":"尖","shape_type":"OBLONG","remark":"钢木门，散装，吊装，3吨，长5.8米/宽2米/高6.9米","max_length":"5.800","max_width":"2.0","max_height":"6.900","max_weight":"3.0","sequence_no":"1","updated_at":"2024-12-03 05:02:46","created_at":"2024-12-03 05:02:46","deleted":"N","is_archived":"N"}],"scene":"FREIGHT_SUB_ORDER_GOODS_MODIFY","nodeStep":"insertNew","index":0,"windowKey":"2848279310804860928|FREIGHT_SUB_ORDER_GOODS_MODIFY"},{"schemaName":"trade_order_sharding","tableName":"order_event_0","opType":"INSERT","eventTime":1733173366605,"after":[{"id":"4347815","order_no":"2848279310804860928","event_no":"2848279422935441408","sub_order_no":"49204595713","parent_order_no":"","scene":"FREIGHT_SUB_ORDER_UP","event_type":"CORE_INFORMATION_CHANGE","event_user_role":"ENTITY_USER","event_user_id":"","context":"{\\"data\\":[{\\"modifyType\\":\\"MODIFY_GOODS\\",\\"goodsList\\":[{\\"orderNo\\":\\"2848279310804860928\\",\\"subOrderNo\\":\\"49204595713\\",\\"orderGoodsNo\\":\\"1481223169\\",\\"groupNo\\":\\"49204595713\\",\\"topClassifyId\\":\\"10004\\",\\"topClassifyName\\":\\"门窗\\",\\"secondaryClassifyId\\":\\"100040047\\",\\"secondaryClassifyName\\":\\"钢木门\\",\\"tertiaryClassifyId\\":\\"123\\",\\"tertiaryClassifyName\\":\\"长形\\",\\"packageId\\":\\"6\\",\\"packageType\\":\\"TIE_UP\\",\\"packageName\\":\\"散装\\",\\"loadAndUnloadId\\":\\"2\\",\\"loadAndUnloadName\\":\\"吊装\\",\\"unknownWeightReason\\":\\"散的\\",\\"images\\":[{\\"orderNo\\":\\"2848279310804860928\\",\\"subBusinessNo\\":\\"49204595713\\",\\"subBusinessType\\":\\"FREIGHT_SUB_ORDER_REQUEST\\",\\"userId\\":\\"1101040685\\",\\"userRole\\":\\"ENTITY_USER\\",\\"scene\\":\\"FREIGHT_SUB_ORDER_UP\\",\\"mediaType\\":\\"IMAGE\\",\\"bizTags\\":[\\"GOODS_FREIGHT\\"],\\"url\\":\\"/ops2/rs/dist/images/small_pieces1.png\\",\\"associatedNo\\":\\"1481223169\\"}],\\"remark\\":\\"钢木门，散装，吊装，3吨，长5.8米/宽2米/高6.9米\\",\\"type\\":\\"FREIGHT\\",\\"specification\\":{\\"shape\\":\\"尖\\",\\"shapeType\\":\\"OBLONG\\",\\"length\\":{\\"max\\":5.8},\\"width\\":{\\"max\\":2.0},\\"height\\":{\\"max\\":6.9},\\"volume\\":{},\\"weight\\":{\\"max\\":3.0},\\"quantity\\":{\\"max\\":2.0}},\\"labels\\":[\\"注释备注\\"],\\"name\\":\\"920\\",\\"segmentNo\\":1}]}]}","deleted":"N","is_archived":"N","updated_at":"2024-12-03 05:02:46","created_at":"2024-12-03 05:02:46","published":"N"}],"scene":"FREIGHT_SUB_ORDER_GOODS_MODIFY","nodeStep":"insertEvent","index":0,"windowKey":"2848279310804860928|FREIGHT_SUB_ORDER_GOODS_MODIFY"}],"closeType":"EVENT_END","nodeNames":["softDelete","insertNew","insertEvent"]}                
                """;

        Map map = objectMapper.readValue(input, Map.class);

        List<Map<String, Object>> eventNodeList = (List<Map<String, Object>>) map.get("eventNodeList");
        List<Map<String, Object>> list = eventNodeList.stream().map(e -> {
            Map<String, Object> canalEntity = new HashMap<>();
            canalEntity.put("old", e.get("before"));
            canalEntity.put("data", e.get("after"));
            canalEntity.put("table", e.get("tableName"));
            canalEntity.put("type", e.get("opType"));
            canalEntity.put("ts", e.get("eventTime"));
            canalEntity.put("database", e.get("schemaName"));
            return canalEntity;
        }).toList();

        System.out.println(objectMapper.writeValueAsString(list));

    }

}
