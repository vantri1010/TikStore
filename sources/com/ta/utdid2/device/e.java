package com.ta.utdid2.device;

import com.ta.utdid2.a.a.a;
import com.ta.utdid2.a.a.b;
import com.ta.utdid2.a.a.g;

public class e {
    public String d(String str) {
        return a.b(str);
    }

    public String e(String str) {
        String b = a.b(str);
        if (g.a(b)) {
            return null;
        }
        try {
            return new String(b.decode(b, 0));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
