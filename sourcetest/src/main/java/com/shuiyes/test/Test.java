package com.shuiyes.test;

public class Test {

    public static void main(String[] args) {
        int[] arrayOfInt = new int[18];
        arrayOfInt[0] = 99;
        arrayOfInt[1] = 111;
        arrayOfInt[2] = 109;
        arrayOfInt[3] = 46;
        arrayOfInt[4] = 104;
        arrayOfInt[5] = 100;
        arrayOfInt[6] = 46;
        arrayOfInt[7] = 122;
        arrayOfInt[8] = 104;
        arrayOfInt[9] = 105;
        arrayOfInt[10] = 98;
        arrayOfInt[11] = 111;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<arrayOfInt.length; i++) {
            stringBuilder.append((char)arrayOfInt[i]);
        }
        System.out.println(stringBuilder.toString());
    }

}
