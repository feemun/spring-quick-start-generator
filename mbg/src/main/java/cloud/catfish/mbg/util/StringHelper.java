package cloud.catfish.mbg.util;

public class StringHelper {

    /**
     * 将给定字符串的首字母转换为小写。
     *
     * @param str 给定的字符串
     * @return 首字母小写后的字符串。如果字符串为空或null，则返回原值。
     */
    public static String firstCharToLower(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        char firstChar = str.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            return Character.toLowerCase(firstChar) + str.substring(1);
        } else {
            // 如果首字符已经为小写，则直接返回原字符串
            return str;
        }
    }

    public static void main(String[] args) {
        String input = "UmsAdmin";
        String output = firstCharToLower(input);
        System.out.println(output); // 输出：umsAdmin
    }
}