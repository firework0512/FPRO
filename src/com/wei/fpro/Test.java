package com.wei.fpro;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Test {
    public static void main(String[] args) {
        int a[] = {2, 6, 7, 8};
        StringBuffer sb = new StringBuffer();

        Test t = new Test();
        t.parse(a);

    }

    void parse(int[] a) {
        List<Pair<Integer,Integer>> dataList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        int max = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > max)
                max = a[i];
        }
        int[] t = new int[max + 1];
        for (int i = 0; i < a.length; i++) {
            t[a[i]] = 1;
        }
        int start = 0;
        int len = 0;
        for (int i = 0; i < t.length; i++) {
            if (t[i] != 0) {
                if (len == 0)
                    start = i;
                len++;
            } else {
                if (len == 1) {
                    sb.append(start).append(',');
                    dataList.add(new Pair<>(start, start));
                }else if (len > 1){
                    sb.append(start).append('-').append(start + len - 1).append(',');
                    dataList.add(new Pair<>(start, start + len - 1));
                }
                len = 0;
            }
        }
        if (len == 1) {
            sb.append(start).append(',');
            dataList.add(new Pair<>(start, start));
        }else if (len > 1) {
            sb.append(start).append('-').append(start + len - 1).append(',');
            dataList.add(new Pair<>(start, start + len - 1));
        }
        sb.deleteCharAt(sb.length() - 1);
        System.out.print(sb);
        System.out.print(Arrays.toString(dataList.toArray()));
    }
}
