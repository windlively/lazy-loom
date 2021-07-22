package ink.windlively.tools;

public class T2 {

    public static void main(String[] args) {

        System.out.println(pattern("abcccedeeff", "deef"));
    }


    public static String pattern(String N, String M){
        char[] sN = N.toCharArray();
        char[] sM = M.toCharArray();

        for (int i = 0; i < sN.length; i++) {
            int k = i;
            int j = 0;
            for (j = 0; j < sM.length; j++) {
                if(sN[k ++] != sM[j]) break;
            }
            if(j == sM.length){
                return M;
            }
        }

        return "";

    }
}
