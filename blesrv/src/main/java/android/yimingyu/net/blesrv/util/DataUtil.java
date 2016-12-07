package android.yimingyu.net.blesrv.util;

/**
 * Author：Mingyu Yi on 2016/9/20 19:17
 * Email：461072496@qq.com
 */
public class DataUtil {
    private static final char SPACE=' ';
    private static final String HEX_LOW = "0123456789abcdef";
    private static final String HEX_UPPER = "0123456789ABCDEF";
    private static final char[] DIGITS_LOWER = HEX_LOW.toCharArray();
    private static final char[] DIGITS_UPPER = HEX_UPPER.toCharArray();

    public static String byteArrToStr(byte[] data){
        return new String(byteArrTo4BitsCharArr(data,true,true));
    }

    public static char[] byteArrTo4BitsCharArr(byte[] data, boolean toUpperCase, boolean useSpace){
        return byteArrTo4BitsCharArr(data,toUpperCase ? DIGITS_UPPER : DIGITS_LOWER, useSpace);
    }


    /***
     * 1 char = 2 byte =16 bit;  但是byte和bit都不能直接打印
     * 本法为了直观地打印出16进制字符串，将每个byte的高位和低位都用一个char表示
     * 因此结果char[]的长度是输入byte[]长度的一倍而不是它的一半
     * @param data       例: data=new byte[] {112,111,113,114,115}
     * @param toDigits   大写或小写字母
     * @param useSpace   每两个char是否填补空格
     * @return           结果  70 6F 71 72 73
     */
    private static char[] byteArrTo4BitsCharArr(byte[] data, char[] toDigits, boolean useSpace) {
        int t=data.length*2;
        if(useSpace) t=data.length*3-1; //每两个char插入一个空格
        char[] out = new char[t];
        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
            if(useSpace&&j<t) out[j++] =SPACE;
        }
        return out;
    }


    /***
     * 将字符串每两个char转换成一个byte，除空格外字符串长度必须是偶数
     * @param hexString   十六进制字符串，可以每两个char插入空格
     * @return
     */
    public static byte[] hexStrToByteArr(String hexString){
        String[] hexStrArr = hexString.trim().toUpperCase().split(" ");
        if(hexStrArr.length==1){
            return nonSpaceStrToByteArr(hexStrArr[0]);
        }else {
            return twoCharsHexStrArrToByteArr(hexStrArr);
        }
    }


    /***
     * 将字符串数据转化成byte数组，每个字符串只有两个char
     * @param twoCharsHexStrArr    例： String[] {"70","6F","71","72","73"}
     * @return                      结果： byte[] {112,111,113,114,115}
     */
    private static byte[] twoCharsHexStrArrToByteArr(String[] twoCharsHexStrArr)
    {
        byte[] bytes = new byte[twoCharsHexStrArr.length];
        for (int i = 0; i < twoCharsHexStrArr.length; i++)
        {
            char[] hexChars = twoCharsHexStrArr[i].toCharArray();
            if(hexChars.length!=2) throw new RuntimeException("每个字符串长度必须是2");
            bytes[i] =combineCharsToByte(hexChars[0],hexChars[1]);
        }
        return bytes;
    }

    /***
     * 将偶数长度的字符串每两个char转化成一个byte
     * @param nonSpaceHexStr      例：  706F717273
     * @return                    结果： byte[] {112,111,113,114,115}
     */
    private static byte[] nonSpaceStrToByteArr(String nonSpaceHexStr)
    {
        int len=nonSpaceHexStr.length();
        if((len & 0x01)!=0)  throw new RuntimeException("字符串长度不是偶数");
        len = len/2;
        char[] hexChars=nonSpaceHexStr.toCharArray();
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++)
        {
            int pos = i * 2;
            bytes[i] =combineCharsToByte(hexChars[pos],hexChars[pos+1]);
        }
        return bytes;
    }

    public static byte combineCharsToByte(char high,char low){
        return (byte) (charToByte(high) << 4 | charToByte(low));
    }

    public static byte charToByte(char c) {
        return (byte) HEX_UPPER.indexOf(c);
    }

    /***
     * 将byte数组从指定位置开始的两个byte转化成short，高位在前，低位在后
     * @param bytes  byte数组，高位在前，低位在后
     * @param index  转化开始位置
     * @return
     */
    public static short getShort(byte[] bytes, int index) {
        return (short) (((bytes[index] << 8) | bytes[index+1] & 0xff));
    }

    public static boolean isBitSet(byte bt,int pos){
        return (bt & (1<<pos)) != 0;
    }

    public static byte xorAll(byte[] data,int start){
        if(data.length==1) return data[0];
        byte flag=data[start];
        for (int i = start+1; i <data.length ; i++) {
            flag ^=data[i];
        }
        return flag;
    }
}