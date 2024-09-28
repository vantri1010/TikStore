package com.library.util;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ViewUtil {
    public static List<View> getChildViewWithId(View view) {
        List<View> list = new ArrayList<>();
        if (view != null && (view instanceof ViewGroup)) {
            LinkedList<ViewGroup> queue = new LinkedList<>();
            queue.add((ViewGroup) view);
            while (!queue.isEmpty()) {
                ViewGroup current = queue.removeFirst();
                for (int i = 0; i < current.getChildCount(); i++) {
                    View childView = current.getChildAt(i);
                    if (childView instanceof ViewGroup) {
                        queue.addLast((ViewGroup) current.getChildAt(i));
                    }
                    if (childView.getId() != -1) {
                        list.add(childView);
                    }
                }
            }
        }
        return list;
    }

    public static List<View> getChildViewWithTag(View view) {
        List<View> list = new ArrayList<>();
        if (view != null && (view instanceof ViewGroup)) {
            LinkedList<ViewGroup> queue = new LinkedList<>();
            queue.add((ViewGroup) view);
            while (!queue.isEmpty()) {
                ViewGroup current = queue.removeFirst();
                for (int i = 0; i < current.getChildCount(); i++) {
                    View childView = current.getChildAt(i);
                    if (childView instanceof ViewGroup) {
                        queue.addLast((ViewGroup) current.getChildAt(i));
                    }
                    if (childView.getTag() != null) {
                        list.add(childView);
                    }
                }
            }
        }
        return list;
    }
}
