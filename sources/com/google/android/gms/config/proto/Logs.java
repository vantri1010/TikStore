package com.google.android.gms.config.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public final class Logs {

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public interface AndroidConfigFetchProtoOrBuilder extends MessageLiteOrBuilder {
        ConfigFetchReason getReason();

        boolean hasReason();
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public interface ConfigFetchReasonOrBuilder extends MessageLiteOrBuilder {
        ConfigFetchReason.AndroidConfigFetchType getType();

        boolean hasType();
    }

    private Logs() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public static final class ConfigFetchReason extends GeneratedMessageLite<ConfigFetchReason, Builder> implements ConfigFetchReasonOrBuilder {
        /* access modifiers changed from: private */
        public static final ConfigFetchReason DEFAULT_INSTANCE;
        private static volatile Parser<ConfigFetchReason> PARSER = null;
        public static final int TYPE_FIELD_NUMBER = 1;
        private int bitField0_;
        private int type_;

        private ConfigFetchReason() {
        }

        /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
        public enum AndroidConfigFetchType implements Internal.EnumLite {
            UNKNOWN(0),
            SCHEDULED(1),
            BOOT_COMPLETED(2),
            PACKAGE_ADDED(3),
            PACKAGE_REMOVED(4),
            GMS_CORE_UPDATED(5),
            SECRET_CODE(6);
            
            public static final int BOOT_COMPLETED_VALUE = 2;
            public static final int GMS_CORE_UPDATED_VALUE = 5;
            public static final int PACKAGE_ADDED_VALUE = 3;
            public static final int PACKAGE_REMOVED_VALUE = 4;
            public static final int SCHEDULED_VALUE = 1;
            public static final int SECRET_CODE_VALUE = 6;
            public static final int UNKNOWN_VALUE = 0;
            private static final Internal.EnumLiteMap<AndroidConfigFetchType> internalValueMap = null;
            private final int value;

            static {
                internalValueMap = new Internal.EnumLiteMap<AndroidConfigFetchType>() {
                    public AndroidConfigFetchType findValueByNumber(int number) {
                        return AndroidConfigFetchType.forNumber(number);
                    }
                };
            }

            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static AndroidConfigFetchType valueOf(int value2) {
                return forNumber(value2);
            }

            public static AndroidConfigFetchType forNumber(int value2) {
                switch (value2) {
                    case 0:
                        return UNKNOWN;
                    case 1:
                        return SCHEDULED;
                    case 2:
                        return BOOT_COMPLETED;
                    case 3:
                        return PACKAGE_ADDED;
                    case 4:
                        return PACKAGE_REMOVED;
                    case 5:
                        return GMS_CORE_UPDATED;
                    case 6:
                        return SECRET_CODE;
                    default:
                        return null;
                }
            }

            public static Internal.EnumLiteMap<AndroidConfigFetchType> internalGetValueMap() {
                return internalValueMap;
            }

            private AndroidConfigFetchType(int value2) {
                this.value = value2;
            }
        }

        public boolean hasType() {
            return (this.bitField0_ & 1) == 1;
        }

        public AndroidConfigFetchType getType() {
            AndroidConfigFetchType result = AndroidConfigFetchType.forNumber(this.type_);
            return result == null ? AndroidConfigFetchType.UNKNOWN : result;
        }

        /* access modifiers changed from: private */
        public void setType(AndroidConfigFetchType value) {
            if (value != null) {
                this.bitField0_ |= 1;
                this.type_ = value.getNumber();
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void clearType() {
            this.bitField0_ &= -2;
            this.type_ = 0;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                output.writeEnum(1, this.type_);
            }
            this.unknownFields.writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeEnumSize(1, this.type_);
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        public static ConfigFetchReason parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (ConfigFetchReason) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static ConfigFetchReason parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (ConfigFetchReason) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static ConfigFetchReason parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (ConfigFetchReason) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static ConfigFetchReason parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (ConfigFetchReason) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static ConfigFetchReason parseFrom(InputStream input) throws IOException {
            return (ConfigFetchReason) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static ConfigFetchReason parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ConfigFetchReason) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static ConfigFetchReason parseDelimitedFrom(InputStream input) throws IOException {
            return (ConfigFetchReason) parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static ConfigFetchReason parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ConfigFetchReason) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static ConfigFetchReason parseFrom(CodedInputStream input) throws IOException {
            return (ConfigFetchReason) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static ConfigFetchReason parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ConfigFetchReason) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(ConfigFetchReason prototype) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(prototype);
        }

        /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
        public static final class Builder extends GeneratedMessageLite.Builder<ConfigFetchReason, Builder> implements ConfigFetchReasonOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 x0) {
                this();
            }

            private Builder() {
                super(ConfigFetchReason.DEFAULT_INSTANCE);
            }

            public boolean hasType() {
                return ((ConfigFetchReason) this.instance).hasType();
            }

            public AndroidConfigFetchType getType() {
                return ((ConfigFetchReason) this.instance).getType();
            }

            public Builder setType(AndroidConfigFetchType value) {
                copyOnWrite();
                ((ConfigFetchReason) this.instance).setType(value);
                return this;
            }

            public Builder clearType() {
                copyOnWrite();
                ((ConfigFetchReason) this.instance).clearType();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[method.ordinal()]) {
                case 1:
                    return new ConfigFetchReason();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) arg0;
                    ConfigFetchReason other = (ConfigFetchReason) arg1;
                    this.type_ = visitor.visitInt(hasType(), this.type_, other.hasType(), other.type_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= other.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream input = (CodedInputStream) arg0;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) arg1;
                    boolean done = false;
                    while (!done) {
                        try {
                            int tag = input.readTag();
                            if (tag == 0) {
                                done = true;
                            } else if (tag == 8) {
                                int rawValue = input.readEnum();
                                if (AndroidConfigFetchType.forNumber(rawValue) == null) {
                                    super.mergeVarintField(1, rawValue);
                                } else {
                                    this.bitField0_ = 1 | this.bitField0_;
                                    this.type_ = rawValue;
                                }
                            } else if (!parseUnknownField(tag, input)) {
                                done = true;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (ConfigFetchReason.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            ConfigFetchReason configFetchReason = new ConfigFetchReason();
            DEFAULT_INSTANCE = configFetchReason;
            configFetchReason.makeImmutable();
        }

        public static ConfigFetchReason getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ConfigFetchReason> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* renamed from: com.google.android.gms.config.proto.Logs$1  reason: invalid class name */
    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke;

        static {
            int[] iArr = new int[GeneratedMessageLite.MethodToInvoke.values().length];
            $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke = iArr;
            try {
                iArr[GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.IS_INITIALIZED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.MAKE_IMMUTABLE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.NEW_BUILDER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.VISIT.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.MERGE_FROM_STREAM.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_PARSER.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public static final class AndroidConfigFetchProto extends GeneratedMessageLite<AndroidConfigFetchProto, Builder> implements AndroidConfigFetchProtoOrBuilder {
        /* access modifiers changed from: private */
        public static final AndroidConfigFetchProto DEFAULT_INSTANCE;
        private static volatile Parser<AndroidConfigFetchProto> PARSER = null;
        public static final int REASON_FIELD_NUMBER = 1;
        private int bitField0_;
        private ConfigFetchReason reason_;

        private AndroidConfigFetchProto() {
        }

        public boolean hasReason() {
            return (this.bitField0_ & 1) == 1;
        }

        public ConfigFetchReason getReason() {
            ConfigFetchReason configFetchReason = this.reason_;
            return configFetchReason == null ? ConfigFetchReason.getDefaultInstance() : configFetchReason;
        }

        /* access modifiers changed from: private */
        public void setReason(ConfigFetchReason value) {
            if (value != null) {
                this.reason_ = value;
                this.bitField0_ |= 1;
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void setReason(ConfigFetchReason.Builder builderForValue) {
            this.reason_ = (ConfigFetchReason) builderForValue.build();
            this.bitField0_ |= 1;
        }

        /* access modifiers changed from: private */
        public void mergeReason(ConfigFetchReason value) {
            ConfigFetchReason configFetchReason = this.reason_;
            if (configFetchReason == null || configFetchReason == ConfigFetchReason.getDefaultInstance()) {
                this.reason_ = value;
            } else {
                this.reason_ = (ConfigFetchReason) ((ConfigFetchReason.Builder) ConfigFetchReason.newBuilder(this.reason_).mergeFrom(value)).buildPartial();
            }
            this.bitField0_ |= 1;
        }

        /* access modifiers changed from: private */
        public void clearReason() {
            this.reason_ = null;
            this.bitField0_ &= -2;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                output.writeMessage(1, getReason());
            }
            this.unknownFields.writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                size2 = 0 + CodedOutputStream.computeMessageSize(1, getReason());
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        public static AndroidConfigFetchProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (AndroidConfigFetchProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static AndroidConfigFetchProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (AndroidConfigFetchProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static AndroidConfigFetchProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (AndroidConfigFetchProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static AndroidConfigFetchProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (AndroidConfigFetchProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static AndroidConfigFetchProto parseFrom(InputStream input) throws IOException {
            return (AndroidConfigFetchProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static AndroidConfigFetchProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (AndroidConfigFetchProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static AndroidConfigFetchProto parseDelimitedFrom(InputStream input) throws IOException {
            return (AndroidConfigFetchProto) parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static AndroidConfigFetchProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (AndroidConfigFetchProto) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static AndroidConfigFetchProto parseFrom(CodedInputStream input) throws IOException {
            return (AndroidConfigFetchProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static AndroidConfigFetchProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (AndroidConfigFetchProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(AndroidConfigFetchProto prototype) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(prototype);
        }

        /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
        public static final class Builder extends GeneratedMessageLite.Builder<AndroidConfigFetchProto, Builder> implements AndroidConfigFetchProtoOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 x0) {
                this();
            }

            private Builder() {
                super(AndroidConfigFetchProto.DEFAULT_INSTANCE);
            }

            public boolean hasReason() {
                return ((AndroidConfigFetchProto) this.instance).hasReason();
            }

            public ConfigFetchReason getReason() {
                return ((AndroidConfigFetchProto) this.instance).getReason();
            }

            public Builder setReason(ConfigFetchReason value) {
                copyOnWrite();
                ((AndroidConfigFetchProto) this.instance).setReason(value);
                return this;
            }

            public Builder setReason(ConfigFetchReason.Builder builderForValue) {
                copyOnWrite();
                ((AndroidConfigFetchProto) this.instance).setReason(builderForValue);
                return this;
            }

            public Builder mergeReason(ConfigFetchReason value) {
                copyOnWrite();
                ((AndroidConfigFetchProto) this.instance).mergeReason(value);
                return this;
            }

            public Builder clearReason() {
                copyOnWrite();
                ((AndroidConfigFetchProto) this.instance).clearReason();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[method.ordinal()]) {
                case 1:
                    return new AndroidConfigFetchProto();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) arg0;
                    AndroidConfigFetchProto other = (AndroidConfigFetchProto) arg1;
                    this.reason_ = (ConfigFetchReason) visitor.visitMessage(this.reason_, other.reason_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= other.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream input = (CodedInputStream) arg0;
                    ExtensionRegistryLite extensionRegistry = (ExtensionRegistryLite) arg1;
                    boolean done = false;
                    while (!done) {
                        try {
                            int tag = input.readTag();
                            if (tag == 0) {
                                done = true;
                            } else if (tag == 10) {
                                ConfigFetchReason.Builder subBuilder = null;
                                if ((this.bitField0_ & 1) == 1) {
                                    subBuilder = (ConfigFetchReason.Builder) this.reason_.toBuilder();
                                }
                                ConfigFetchReason configFetchReason = (ConfigFetchReason) input.readMessage(ConfigFetchReason.parser(), extensionRegistry);
                                this.reason_ = configFetchReason;
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom(configFetchReason);
                                    this.reason_ = (ConfigFetchReason) subBuilder.buildPartial();
                                }
                                this.bitField0_ |= 1;
                            } else if (!parseUnknownField(tag, input)) {
                                done = true;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (AndroidConfigFetchProto.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            AndroidConfigFetchProto androidConfigFetchProto = new AndroidConfigFetchProto();
            DEFAULT_INSTANCE = androidConfigFetchProto;
            androidConfigFetchProto.makeImmutable();
        }

        public static AndroidConfigFetchProto getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<AndroidConfigFetchProto> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }
}
