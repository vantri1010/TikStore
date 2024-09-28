package org.aspectj.runtime.reflect;

import com.litesuits.orm.db.assit.SQLBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.LockSignature;

class LockSignatureImpl extends SignatureImpl implements LockSignature {
    private Class parameterType;

    LockSignatureImpl(Class c) {
        super(8, JoinPoint.SYNCHRONIZATION_LOCK, c);
        this.parameterType = c;
    }

    LockSignatureImpl(String stringRep) {
        super(stringRep);
    }

    /* access modifiers changed from: protected */
    public String createToString(StringMaker sm) {
        if (this.parameterType == null) {
            this.parameterType = extractType(3);
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("lock(");
        stringBuffer.append(sm.makeTypeName(this.parameterType));
        stringBuffer.append(SQLBuilder.PARENTHESES_RIGHT);
        return stringBuffer.toString();
    }

    public Class getParameterType() {
        if (this.parameterType == null) {
            this.parameterType = extractType(3);
        }
        return this.parameterType;
    }
}
