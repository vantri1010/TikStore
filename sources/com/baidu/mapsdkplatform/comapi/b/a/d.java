package com.baidu.mapsdkplatform.comapi.b.a;

import com.baidu.mapsdkplatform.comapi.util.g;
import java.io.File;
import java.util.Arrays;

class d implements Runnable {
    final /* synthetic */ c a;

    d(c cVar) {
        this.a = cVar;
    }

    public void run() {
        File[] listFiles;
        if (g.a().b() != null) {
            File file = new File(c.a);
            if (file.exists() && (listFiles = file.listFiles()) != null && listFiles.length != 0) {
                try {
                    Arrays.sort(listFiles, new e());
                } catch (Exception e) {
                }
                int length = listFiles.length;
                if (length > 10) {
                    length = 10;
                }
                for (int i = 0; i < length; i++) {
                    File file2 = listFiles[i];
                    if (!file2.isDirectory() && file2.exists() && file2.isFile() && file2.getName().contains(c.b) && (file2.getName().endsWith(".txt") || (file2.getName().endsWith(".zip") && file2.exists()))) {
                        boolean unused = this.a.a(file2);
                    }
                }
                if (listFiles.length > 10) {
                    this.a.a(listFiles);
                }
            }
        }
    }
}
