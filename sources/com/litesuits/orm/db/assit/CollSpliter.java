package com.litesuits.orm.db.assit;

import java.util.ArrayList;
import java.util.Collection;

public class CollSpliter {

    public interface Spliter<T> {
        int oneSplit(ArrayList<T> arrayList) throws Exception;
    }

    public static <T> int split(Collection<T> collection, int perSize, Spliter<T> spliter) throws Exception {
        ArrayList<T> list = new ArrayList<>();
        int count = 0;
        if (collection.size() <= perSize) {
            list.addAll(collection);
            return 0 + spliter.oneSplit(list);
        }
        int i = 0;
        int j = 1;
        for (T data : collection) {
            if (i < j * perSize) {
                list.add(data);
            } else {
                count += spliter.oneSplit(list);
                j++;
                list.clear();
                list.add(data);
            }
            i++;
        }
        if (list.size() > 0) {
            return count + spliter.oneSplit(list);
        }
        return count;
    }
}
