package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class UnknownDescriptor extends BaseDescriptor {
    private static Logger log = Logger.getLogger(UnknownDescriptor.class.getName());
    private ByteBuffer data;

    public void parseDetail(ByteBuffer bb) throws IOException {
        this.data = (ByteBuffer) bb.slice().limit(getSizeOfInstance());
    }

    public String toString() {
        return "UnknownDescriptor" + "{tag=" + this.tag + ", sizeOfInstance=" + this.sizeOfInstance + ", data=" + this.data + '}';
    }
}
