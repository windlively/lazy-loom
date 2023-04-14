package ink.windlively.example.runner;

import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TempScript {


    public static void main(String[] args) {
        String l = RandomGenerator.of("wqe").toString();
        String k = switch (l) {
            case "0" -> l + "0";
            default -> l;
        };

        String str = """
                order_id
                order_display_id
                order_uuid

                order_type
                business_type
                city_id
                country_id
                consignee_id
                total_distance

                client_type\s
                order_service_type
                invoice_type

                remark
                is_immediate
                order_fleet_status
                order_status

                create_time
                modify_time
                order_datetime
                end_datetime
                cancel_time\s
                order_status_2_time
                address_list\s
                user_id、ep_id、dept_id、tel、name
                "order_vehicle_id、 order_vehicle_name、
                standard_order_vehicle_id、
                vehicle_attr"
                pay_type、to_pay_type

                no_offer_order、is_multiple_price
                price_plan、bargain_type
                amount_fen 、paid_amount_fen
                "platform_coupon_price_fen、
                subsidy_price、
                coupon_price_fen"
                driver_amount_fen\s

                transport_platform_type
                driver_id
                reject_type
                driver_id_history
                pickup_ts
                send_bill_time
                complete_time\s
                reject_ts""";
        List<String> source = Stream.of(str.replace("\"", "").split("[\n、]")).filter(e -> !e.isBlank()).map(String::trim).collect(Collectors.toList());
        System.out.println(source);
        str = """
                 order_id\s
                 order_display_id\s
                 order_uuid\s
                 order_type\s
                 order_status\s
                 city_id\s
                 country_id\s
                 driver_id\s
                 driver_id_history\s
                 user_id\s
                 ep_id\s
                 name\s
                 tel\s
                 latlong\s
                 client_type\s
                 business_type\s
                 order_vehicle_id\s
                 order_vehicle_name\s
                 freight_order_vehicle_id\s
                 freight_order_vehicle_name\s
                 freight_type\s
                 freight_uuid\s
                 remark\s
                 address_list\s
                 cancelled_by\s
                 driver_ontime\s
                 order_ontime\s
                 service_ontime\s
                 has_insurance\s
                 is_immediate\s
                 order_service_type\s
                 order_subset\s
                 rank_id\s
                 reject_type\s
                 virtual_bind_status\s
                 request_freight_type\s
                total_price_fen
                 amount_fen\s
                 driver_amount_fen\s
                 paid_amount_fen\s
                 pay_type\s
                 pay_status\s
                 invoice_fen\s
                 invoice_status\s
                 rear_pay_status
                 coupon_id\s
                coupon_price_fen
                is_user_favorite
                order_group
                is_delete
                invoice_type
                order_fleet_status
                is_incidentally
                hit_one_price
                vehicle_attr
                is_multiple_price
                price_plan
                pre_payer_type
                receipt_status
                total_distance
                dept_id
                consignee_id
                to_pay_type
                subsidy_price
                bargain_type
                platform_coupon_id
                platform_coupon_price_fen
                transport_platform_type
                 order_datetime\s
                end_datetime
                 create_time\s
                 modify_time\s
                 cancel_time\s
                 complete_time\s
                 confirm_bill_time\s
                 order_status_2_time\s
                 pay_time\s
                 pickup_ts\s
                 reject_ts\s
                 send_bill_time\s""";
        List<String> compare = Stream.of(str.replace("\"", "").split("[\n、]")).filter(e -> !e.isBlank()).map(String::trim).collect(Collectors.toList());
        System.out.println(compare);

        compare.removeAll(source);

        System.out.println(compare);

    }

}
