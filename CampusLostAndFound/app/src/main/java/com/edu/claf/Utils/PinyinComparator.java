package com.edu.claf.Utils;

import com.edu.claf.bean.SortBean;

import java.util.Comparator;

/**
 * 拼音比较器
 */
public class PinyinComparator  implements Comparator<SortBean> {

        public int compare(SortBean o1, SortBean o2) {
            if (o1.getSortLetters().equals("@")
                    || o2.getSortLetters().equals("#")) {
                return -1;
            } else if (o1.getSortLetters().equals("#")
                    || o2.getSortLetters().equals("@")) {
                return 1;
            } else {
                return o1.getSortLetters().compareTo(o2.getSortLetters());
            }
        }

    }
