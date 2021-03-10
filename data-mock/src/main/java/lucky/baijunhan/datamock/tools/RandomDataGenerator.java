package lucky.baijunhan.datamock.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDataGenerator {

    public static ThreadLocalRandom getRandom(){
        return ThreadLocalRandom.current();
    }

    public static String randomBankAddress(){
        Address build = AddressBuilder.build();
        return build.province + "-" + build.city;
    }

    // 随机地址
    public static String randomAddress(){
        return AddressBuilder.build().getAddress();
    }

    private static final List<String> bankAddressList = Arrays.asList("西安市", "上海市", "北京市", "成都市", "深圳市", "青岛市");

    // 随机身份证号
    public static String randomIdCard(){
        // 地区编码，6位
        int areaCode = getRandom().nextInt(100000, 999999);
        // 出生日期，8位
        String birthday = randomDate(1978, 2000);
        // 序列号，2位
        int serialCode = getRandom().nextInt(100);
        // 性别，1位
        int sex = getRandom().nextInt(1, 10);

        StringBuilder id = new StringBuilder();
        id.append(areaCode);
        id.append(birthday.replace("-", ""));
        id.append(serialCode > 9 ? serialCode : ("0" + serialCode));
        id.append(sex);

        // 校验码码，1位
        int checkNum = 0;
        for (int i = 0; i < 17; i++) {
            checkNum += ((1 << (17 - i)) % 11) * (id.charAt(i) - '0');
        }
        checkNum = (12 - (checkNum % 11)) % 11;
        id.append(checkNum < 10 ? checkNum : "X");
        return id.toString();
    }

    // 随机日期
    public static String randomDate(int startYear, int endYear){
        int year = getRandom().nextInt(startYear, endYear + 1);
        int month = getRandom().nextInt(1, 13);
        int day = getRandom().nextInt(1, 29);
        return "" + year + "-" + (month > 9 ? month : ("0" + month)) + "-" + (day > 9 ? day : ("0" + day));
    }

    // 随机银行和部门
    public static int[] randomDepartment(Map<Integer, int[]> bankAndLeafDepartmentRef){
        int bank =  bankAndLeafDepartmentRef.keySet().toArray(new Integer[0])[getRandom().nextInt(bankAndLeafDepartmentRef.size())];
        int[] ints = bankAndLeafDepartmentRef.get(bank);
        int department = ints[getRandom().nextInt(ints.length)];
        return new int[]{bank, department};
    }

    public static String randomLetterSeq(int minLen, int maxLen){
        int length = getRandom().nextInt(minLen, maxLen + 1);
        char[] cs = new char[length];
        for (int i = 0; i < length; i++) {
            cs[i] = (char) getRandom().nextInt('a', 'z' + 1);
        }
        return new String(cs);
    }

    private final static List<String> emailDomain = Arrays.asList("qq.com", "gmail.com", "foxmail.com", "163.com", "hotmail.com", "outlook.com", "sina.com", "souhu.com", "msn.com");

    public static String randomEmail(){
        String name = randomLetterSeq(6, 12);
        return name + "@" + emailDomain.get(getRandom().nextInt(emailDomain.size()));
    };

    private final static int[] mobilePrefix = new int[]{133,132,188,183,155,156,131,196,172,139,138,135,187,182,130,177,176,166,153,157,185,170};

    public static String randomMobilePhone(){
        int prefix = mobilePrefix[getRandom().nextInt(mobilePrefix.length)];
        int left = getRandom().nextInt(10000000, 100000000);
        return prefix + "" + left;
    }


    private final static char[] firstName="赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴谈宋茅庞熊纪舒屈项祝董梁杜阮蓝闵席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田樊胡凌霍虞万支柯昝管卢莫经房裘缪干解应宗丁宣贲邓郁单杭洪包诸左石崔吉钮龚程嵇邢滑裴陆荣翁荀羊於惠甄曲家封芮羿储靳汲邴糜松井段富巫乌焦巴弓牧隗山谷车侯宓蓬全郗班仰秋仲伊宫宁仇栾暴甘钭厉戎祖武符刘景詹束龙叶幸司韶郜黎蓟薄印宿白怀蒲邰从鄂索咸籍赖卓蔺屠蒙池乔阴胥能苍双闻莘党翟谭贡劳逄姬申扶堵冉宰郦雍郤璩桑桂濮牛寿通边扈燕冀郏浦尚农温别庄晏柴瞿阎充慕连茹习宦艾鱼容向古易慎戈廖庾终暨居衡步都耿满弘匡国文寇广禄阙东欧殳沃利蔚越夔隆师巩厍聂晁勾敖融冷訾辛阚那简饶空曾毋沙乜养鞠须丰巢关蒯相查後荆红游竺权逯盖益桓公".toCharArray();
    private final static char[] girl="秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽".toCharArray();
    private final static char[] boy="伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘".toCharArray();

    public static String randomName(boolean isGirl){
        String first = firstName[getRandom().nextInt(firstName.length)] + "";
        boolean doubleName = getRandom().nextBoolean();
        if(isGirl){
            if(doubleName)
                return first + girl[getRandom().nextInt(girl.length)] + girl[getRandom().nextInt(girl.length)];
            else
                return first + girl[getRandom().nextInt(girl.length)];
        }else {
            if(doubleName)
                return first + boy[getRandom().nextInt(boy.length)] + boy[getRandom().nextInt(boy.length)];
            else
                return first + boy[getRandom().nextInt(boy.length)];
        }
    }

    public static String randomBankCard(){
        char[] cs = new char[18];
        cs[0] = '6';
        cs[1] = (char) ('0' + getRandom().nextInt(1, 7));
        for (int i = 2; i < 18; i++) {
            cs[i] = (char) ('0' + getRandom().nextInt(1, 10));
        }
        return new String(cs);
    }


    private static String[] productTerms = new String[]{"30天", "180天", "90天", "1年"};

    public static String randomTerm(){
        return productTerms[getRandom().nextInt(productTerms.length)];
    }

    private static List<Integer> interestMethods = Arrays.asList(1, 2, 3, 4);
    public static String randomInterestMethods(){
        int len = getRandom().nextInt(interestMethods.size());
        ArrayList<Integer> integers = new ArrayList<>(interestMethods);
        int[] res = new int[len];
        for (int i = 0; i < len; i++) {
            int k = getRandom().nextInt(len - i);
            res[i] = integers.get(k);
            integers.remove(k);
        }
        return Arrays.toString(res);
    }

    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            System.out.println(randomBankCard());
        }
        System.out.println(System.currentTimeMillis() - t0);
        /*
         * 本地测试：
         * randomId: 862ms/100000条 (平均约1000ms)
         * randomAddress: 1893ms/100000条 (平均约2000ms)
         * randomEmail: 659ms/100000条 (平均600ms)
         */
    }



}
