package com.googlecode.mp4parser.util;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    static Pattern component = Pattern.compile("(....|\\.\\.)(\\[(.*)\\])?");

    private Path() {
    }

    public static String createPath(Box box) {
        return createPath(box, "");
    }

    private static String createPath(Box box, String path) {
        Container parent = box.getParent();
        int index = 0;
        for (Box sibling : parent.getBoxes()) {
            if (sibling.getType().equals(box.getType())) {
                if (sibling == box) {
                    break;
                }
                index++;
            }
        }
        String path2 = String.valueOf(String.format("/%s[%d]", new Object[]{box.getType(), Integer.valueOf(index)})) + path;
        if (parent instanceof Box) {
            return createPath((Box) parent, path2);
        }
        return path2;
    }

    public static <T extends Box> T getPath(Box box, String path) {
        List<T> all = getPaths(box, path, true);
        if (all.isEmpty()) {
            return null;
        }
        return (Box) all.get(0);
    }

    public static <T extends Box> T getPath(Container container, String path) {
        List<T> all = getPaths(container, path, true);
        if (all.isEmpty()) {
            return null;
        }
        return (Box) all.get(0);
    }

    public static <T extends Box> T getPath(AbstractContainerBox containerBox, String path) {
        List<T> all = getPaths(containerBox, path, true);
        if (all.isEmpty()) {
            return null;
        }
        return (Box) all.get(0);
    }

    public static <T extends Box> List<T> getPaths(Box box, String path) {
        return getPaths(box, path, false);
    }

    public static <T extends Box> List<T> getPaths(Container container, String path) {
        return getPaths(container, path, false);
    }

    private static <T extends Box> List<T> getPaths(AbstractContainerBox container, String path, boolean singleResult) {
        return getPaths((Object) container, path, singleResult);
    }

    private static <T extends Box> List<T> getPaths(Container container, String path, boolean singleResult) {
        return getPaths((Object) container, path, singleResult);
    }

    private static <T extends Box> List<T> getPaths(Box box, String path, boolean singleResult) {
        return getPaths((Object) box, path, singleResult);
    }

    private static <T extends Box> List<T> getPaths(Object thing, String path, boolean singleResult) {
        String later;
        String now;
        if (path.startsWith("/")) {
            String path2 = path.substring(1);
            while (thing instanceof Box) {
                thing = ((Box) thing).getParent();
            }
            path = path2;
        }
        if (path.length() != 0) {
            if (path.contains("/")) {
                later = path.substring(path.indexOf(47) + 1);
                now = path.substring(0, path.indexOf(47));
            } else {
                now = path;
                later = "";
            }
            Matcher m = component.matcher(now);
            if (m.matches()) {
                String type = m.group(1);
                if ("..".equals(type)) {
                    if (thing instanceof Box) {
                        return getPaths(((Box) thing).getParent(), later, singleResult);
                    }
                    return Collections.emptyList();
                } else if (!(thing instanceof Container)) {
                    return Collections.emptyList();
                } else {
                    int index = -1;
                    if (m.group(2) != null) {
                        index = Integer.parseInt(m.group(3));
                    }
                    List<T> children = new LinkedList<>();
                    int currentIndex = 0;
                    for (Box box1 : ((Container) thing).getBoxes()) {
                        if (box1.getType().matches(type)) {
                            if (index == -1 || index == currentIndex) {
                                children.addAll(getPaths(box1, later, singleResult));
                            }
                            currentIndex++;
                        }
                        if ((singleResult || index >= 0) && !children.isEmpty()) {
                            return children;
                        }
                    }
                    return children;
                }
            } else {
                throw new RuntimeException(String.valueOf(now) + " is invalid path.");
            }
        } else if (thing instanceof Box) {
            return Collections.singletonList((Box) thing);
        } else {
            throw new RuntimeException("Result of path expression seems to be the root container. This is not allowed!");
        }
    }

    public static boolean isContained(Box box, String path) {
        return getPaths(box, path).contains(box);
    }
}
