package com.google.firebase.remoteconfig.proto;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public final class ConfigPersistence {

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public interface ConfigHolderOrBuilder extends MessageLiteOrBuilder {
        ByteString getExperimentPayload(int i);

        int getExperimentPayloadCount();

        List<ByteString> getExperimentPayloadList();

        NamespaceKeyValue getNamespaceKeyValue(int i);

        int getNamespaceKeyValueCount();

        List<NamespaceKeyValue> getNamespaceKeyValueList();

        long getTimestamp();

        boolean hasTimestamp();
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public interface KeyValueOrBuilder extends MessageLiteOrBuilder {
        String getKey();

        ByteString getKeyBytes();

        ByteString getValue();

        boolean hasKey();

        boolean hasValue();
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public interface MetadataOrBuilder extends MessageLiteOrBuilder {
        boolean getDeveloperModeEnabled();

        int getLastFetchStatus();

        long getLastKnownExperimentStartTime();

        boolean hasDeveloperModeEnabled();

        boolean hasLastFetchStatus();

        boolean hasLastKnownExperimentStartTime();
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public interface NamespaceKeyValueOrBuilder extends MessageLiteOrBuilder {
        KeyValue getKeyValue(int i);

        int getKeyValueCount();

        List<KeyValue> getKeyValueList();

        String getNamespace();

        ByteString getNamespaceBytes();

        boolean hasNamespace();
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public interface PersistedConfigOrBuilder extends MessageLiteOrBuilder {
        ConfigHolder getActiveConfigHolder();

        Resource getAppliedResource(int i);

        int getAppliedResourceCount();

        List<Resource> getAppliedResourceList();

        ConfigHolder getDefaultsConfigHolder();

        ConfigHolder getFetchedConfigHolder();

        Metadata getMetadata();

        boolean hasActiveConfigHolder();

        boolean hasDefaultsConfigHolder();

        boolean hasFetchedConfigHolder();

        boolean hasMetadata();
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public interface ResourceOrBuilder extends MessageLiteOrBuilder {
        long getAppUpdateTime();

        String getNamespace();

        ByteString getNamespaceBytes();

        int getResourceId();

        boolean hasAppUpdateTime();

        boolean hasNamespace();

        boolean hasResourceId();
    }

    private ConfigPersistence() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public static final class PersistedConfig extends GeneratedMessageLite<PersistedConfig, Builder> implements PersistedConfigOrBuilder {
        public static final int ACTIVE_CONFIG_HOLDER_FIELD_NUMBER = 2;
        public static final int APPLIED_RESOURCE_FIELD_NUMBER = 5;
        public static final int DEFAULTS_CONFIG_HOLDER_FIELD_NUMBER = 3;
        /* access modifiers changed from: private */
        public static final PersistedConfig DEFAULT_INSTANCE;
        public static final int FETCHED_CONFIG_HOLDER_FIELD_NUMBER = 1;
        public static final int METADATA_FIELD_NUMBER = 4;
        private static volatile Parser<PersistedConfig> PARSER;
        private ConfigHolder activeConfigHolder_;
        private Internal.ProtobufList<Resource> appliedResource_ = emptyProtobufList();
        private int bitField0_;
        private ConfigHolder defaultsConfigHolder_;
        private ConfigHolder fetchedConfigHolder_;
        private Metadata metadata_;

        private PersistedConfig() {
        }

        public boolean hasFetchedConfigHolder() {
            return (this.bitField0_ & 1) == 1;
        }

        public ConfigHolder getFetchedConfigHolder() {
            ConfigHolder configHolder = this.fetchedConfigHolder_;
            return configHolder == null ? ConfigHolder.getDefaultInstance() : configHolder;
        }

        /* access modifiers changed from: private */
        public void setFetchedConfigHolder(ConfigHolder value) {
            if (value != null) {
                this.fetchedConfigHolder_ = value;
                this.bitField0_ |= 1;
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void setFetchedConfigHolder(ConfigHolder.Builder builderForValue) {
            this.fetchedConfigHolder_ = (ConfigHolder) builderForValue.build();
            this.bitField0_ |= 1;
        }

        /* access modifiers changed from: private */
        public void mergeFetchedConfigHolder(ConfigHolder value) {
            ConfigHolder configHolder = this.fetchedConfigHolder_;
            if (configHolder == null || configHolder == ConfigHolder.getDefaultInstance()) {
                this.fetchedConfigHolder_ = value;
            } else {
                this.fetchedConfigHolder_ = (ConfigHolder) ((ConfigHolder.Builder) ConfigHolder.newBuilder(this.fetchedConfigHolder_).mergeFrom(value)).buildPartial();
            }
            this.bitField0_ |= 1;
        }

        /* access modifiers changed from: private */
        public void clearFetchedConfigHolder() {
            this.fetchedConfigHolder_ = null;
            this.bitField0_ &= -2;
        }

        public boolean hasActiveConfigHolder() {
            return (this.bitField0_ & 2) == 2;
        }

        public ConfigHolder getActiveConfigHolder() {
            ConfigHolder configHolder = this.activeConfigHolder_;
            return configHolder == null ? ConfigHolder.getDefaultInstance() : configHolder;
        }

        /* access modifiers changed from: private */
        public void setActiveConfigHolder(ConfigHolder value) {
            if (value != null) {
                this.activeConfigHolder_ = value;
                this.bitField0_ |= 2;
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void setActiveConfigHolder(ConfigHolder.Builder builderForValue) {
            this.activeConfigHolder_ = (ConfigHolder) builderForValue.build();
            this.bitField0_ |= 2;
        }

        /* access modifiers changed from: private */
        public void mergeActiveConfigHolder(ConfigHolder value) {
            ConfigHolder configHolder = this.activeConfigHolder_;
            if (configHolder == null || configHolder == ConfigHolder.getDefaultInstance()) {
                this.activeConfigHolder_ = value;
            } else {
                this.activeConfigHolder_ = (ConfigHolder) ((ConfigHolder.Builder) ConfigHolder.newBuilder(this.activeConfigHolder_).mergeFrom(value)).buildPartial();
            }
            this.bitField0_ |= 2;
        }

        /* access modifiers changed from: private */
        public void clearActiveConfigHolder() {
            this.activeConfigHolder_ = null;
            this.bitField0_ &= -3;
        }

        public boolean hasDefaultsConfigHolder() {
            return (this.bitField0_ & 4) == 4;
        }

        public ConfigHolder getDefaultsConfigHolder() {
            ConfigHolder configHolder = this.defaultsConfigHolder_;
            return configHolder == null ? ConfigHolder.getDefaultInstance() : configHolder;
        }

        /* access modifiers changed from: private */
        public void setDefaultsConfigHolder(ConfigHolder value) {
            if (value != null) {
                this.defaultsConfigHolder_ = value;
                this.bitField0_ |= 4;
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void setDefaultsConfigHolder(ConfigHolder.Builder builderForValue) {
            this.defaultsConfigHolder_ = (ConfigHolder) builderForValue.build();
            this.bitField0_ |= 4;
        }

        /* access modifiers changed from: private */
        public void mergeDefaultsConfigHolder(ConfigHolder value) {
            ConfigHolder configHolder = this.defaultsConfigHolder_;
            if (configHolder == null || configHolder == ConfigHolder.getDefaultInstance()) {
                this.defaultsConfigHolder_ = value;
            } else {
                this.defaultsConfigHolder_ = (ConfigHolder) ((ConfigHolder.Builder) ConfigHolder.newBuilder(this.defaultsConfigHolder_).mergeFrom(value)).buildPartial();
            }
            this.bitField0_ |= 4;
        }

        /* access modifiers changed from: private */
        public void clearDefaultsConfigHolder() {
            this.defaultsConfigHolder_ = null;
            this.bitField0_ &= -5;
        }

        public boolean hasMetadata() {
            return (this.bitField0_ & 8) == 8;
        }

        public Metadata getMetadata() {
            Metadata metadata = this.metadata_;
            return metadata == null ? Metadata.getDefaultInstance() : metadata;
        }

        /* access modifiers changed from: private */
        public void setMetadata(Metadata value) {
            if (value != null) {
                this.metadata_ = value;
                this.bitField0_ |= 8;
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void setMetadata(Metadata.Builder builderForValue) {
            this.metadata_ = (Metadata) builderForValue.build();
            this.bitField0_ |= 8;
        }

        /* access modifiers changed from: private */
        public void mergeMetadata(Metadata value) {
            Metadata metadata = this.metadata_;
            if (metadata == null || metadata == Metadata.getDefaultInstance()) {
                this.metadata_ = value;
            } else {
                this.metadata_ = (Metadata) ((Metadata.Builder) Metadata.newBuilder(this.metadata_).mergeFrom(value)).buildPartial();
            }
            this.bitField0_ |= 8;
        }

        /* access modifiers changed from: private */
        public void clearMetadata() {
            this.metadata_ = null;
            this.bitField0_ &= -9;
        }

        public List<Resource> getAppliedResourceList() {
            return this.appliedResource_;
        }

        public List<? extends ResourceOrBuilder> getAppliedResourceOrBuilderList() {
            return this.appliedResource_;
        }

        public int getAppliedResourceCount() {
            return this.appliedResource_.size();
        }

        public Resource getAppliedResource(int index) {
            return (Resource) this.appliedResource_.get(index);
        }

        public ResourceOrBuilder getAppliedResourceOrBuilder(int index) {
            return (ResourceOrBuilder) this.appliedResource_.get(index);
        }

        private void ensureAppliedResourceIsMutable() {
            if (!this.appliedResource_.isModifiable()) {
                this.appliedResource_ = GeneratedMessageLite.mutableCopy(this.appliedResource_);
            }
        }

        /* access modifiers changed from: private */
        public void setAppliedResource(int index, Resource value) {
            if (value != null) {
                ensureAppliedResourceIsMutable();
                this.appliedResource_.set(index, value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void setAppliedResource(int index, Resource.Builder builderForValue) {
            ensureAppliedResourceIsMutable();
            this.appliedResource_.set(index, (Resource) builderForValue.build());
        }

        /* access modifiers changed from: private */
        public void addAppliedResource(Resource value) {
            if (value != null) {
                ensureAppliedResourceIsMutable();
                this.appliedResource_.add(value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void addAppliedResource(int index, Resource value) {
            if (value != null) {
                ensureAppliedResourceIsMutable();
                this.appliedResource_.add(index, value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void addAppliedResource(Resource.Builder builderForValue) {
            ensureAppliedResourceIsMutable();
            this.appliedResource_.add((Resource) builderForValue.build());
        }

        /* access modifiers changed from: private */
        public void addAppliedResource(int index, Resource.Builder builderForValue) {
            ensureAppliedResourceIsMutable();
            this.appliedResource_.add(index, (Resource) builderForValue.build());
        }

        /* access modifiers changed from: private */
        public void addAllAppliedResource(Iterable<? extends Resource> values) {
            ensureAppliedResourceIsMutable();
            AbstractMessageLite.addAll(values, this.appliedResource_);
        }

        /* access modifiers changed from: private */
        public void clearAppliedResource() {
            this.appliedResource_ = emptyProtobufList();
        }

        /* access modifiers changed from: private */
        public void removeAppliedResource(int index) {
            ensureAppliedResourceIsMutable();
            this.appliedResource_.remove(index);
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                output.writeMessage(1, getFetchedConfigHolder());
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeMessage(2, getActiveConfigHolder());
            }
            if ((this.bitField0_ & 4) == 4) {
                output.writeMessage(3, getDefaultsConfigHolder());
            }
            if ((this.bitField0_ & 8) == 8) {
                output.writeMessage(4, getMetadata());
            }
            for (int i = 0; i < this.appliedResource_.size(); i++) {
                output.writeMessage(5, (MessageLite) this.appliedResource_.get(i));
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
                size2 = 0 + CodedOutputStream.computeMessageSize(1, getFetchedConfigHolder());
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeMessageSize(2, getActiveConfigHolder());
            }
            if ((this.bitField0_ & 4) == 4) {
                size2 += CodedOutputStream.computeMessageSize(3, getDefaultsConfigHolder());
            }
            if ((this.bitField0_ & 8) == 8) {
                size2 += CodedOutputStream.computeMessageSize(4, getMetadata());
            }
            for (int i = 0; i < this.appliedResource_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(5, (MessageLite) this.appliedResource_.get(i));
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        public static PersistedConfig parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (PersistedConfig) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static PersistedConfig parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (PersistedConfig) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static PersistedConfig parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (PersistedConfig) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static PersistedConfig parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (PersistedConfig) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static PersistedConfig parseFrom(InputStream input) throws IOException {
            return (PersistedConfig) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static PersistedConfig parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PersistedConfig) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static PersistedConfig parseDelimitedFrom(InputStream input) throws IOException {
            return (PersistedConfig) parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static PersistedConfig parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PersistedConfig) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static PersistedConfig parseFrom(CodedInputStream input) throws IOException {
            return (PersistedConfig) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static PersistedConfig parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PersistedConfig) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(PersistedConfig prototype) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(prototype);
        }

        /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
        public static final class Builder extends GeneratedMessageLite.Builder<PersistedConfig, Builder> implements PersistedConfigOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 x0) {
                this();
            }

            private Builder() {
                super(PersistedConfig.DEFAULT_INSTANCE);
            }

            public boolean hasFetchedConfigHolder() {
                return ((PersistedConfig) this.instance).hasFetchedConfigHolder();
            }

            public ConfigHolder getFetchedConfigHolder() {
                return ((PersistedConfig) this.instance).getFetchedConfigHolder();
            }

            public Builder setFetchedConfigHolder(ConfigHolder value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).setFetchedConfigHolder(value);
                return this;
            }

            public Builder setFetchedConfigHolder(ConfigHolder.Builder builderForValue) {
                copyOnWrite();
                ((PersistedConfig) this.instance).setFetchedConfigHolder(builderForValue);
                return this;
            }

            public Builder mergeFetchedConfigHolder(ConfigHolder value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).mergeFetchedConfigHolder(value);
                return this;
            }

            public Builder clearFetchedConfigHolder() {
                copyOnWrite();
                ((PersistedConfig) this.instance).clearFetchedConfigHolder();
                return this;
            }

            public boolean hasActiveConfigHolder() {
                return ((PersistedConfig) this.instance).hasActiveConfigHolder();
            }

            public ConfigHolder getActiveConfigHolder() {
                return ((PersistedConfig) this.instance).getActiveConfigHolder();
            }

            public Builder setActiveConfigHolder(ConfigHolder value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).setActiveConfigHolder(value);
                return this;
            }

            public Builder setActiveConfigHolder(ConfigHolder.Builder builderForValue) {
                copyOnWrite();
                ((PersistedConfig) this.instance).setActiveConfigHolder(builderForValue);
                return this;
            }

            public Builder mergeActiveConfigHolder(ConfigHolder value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).mergeActiveConfigHolder(value);
                return this;
            }

            public Builder clearActiveConfigHolder() {
                copyOnWrite();
                ((PersistedConfig) this.instance).clearActiveConfigHolder();
                return this;
            }

            public boolean hasDefaultsConfigHolder() {
                return ((PersistedConfig) this.instance).hasDefaultsConfigHolder();
            }

            public ConfigHolder getDefaultsConfigHolder() {
                return ((PersistedConfig) this.instance).getDefaultsConfigHolder();
            }

            public Builder setDefaultsConfigHolder(ConfigHolder value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).setDefaultsConfigHolder(value);
                return this;
            }

            public Builder setDefaultsConfigHolder(ConfigHolder.Builder builderForValue) {
                copyOnWrite();
                ((PersistedConfig) this.instance).setDefaultsConfigHolder(builderForValue);
                return this;
            }

            public Builder mergeDefaultsConfigHolder(ConfigHolder value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).mergeDefaultsConfigHolder(value);
                return this;
            }

            public Builder clearDefaultsConfigHolder() {
                copyOnWrite();
                ((PersistedConfig) this.instance).clearDefaultsConfigHolder();
                return this;
            }

            public boolean hasMetadata() {
                return ((PersistedConfig) this.instance).hasMetadata();
            }

            public Metadata getMetadata() {
                return ((PersistedConfig) this.instance).getMetadata();
            }

            public Builder setMetadata(Metadata value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).setMetadata(value);
                return this;
            }

            public Builder setMetadata(Metadata.Builder builderForValue) {
                copyOnWrite();
                ((PersistedConfig) this.instance).setMetadata(builderForValue);
                return this;
            }

            public Builder mergeMetadata(Metadata value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).mergeMetadata(value);
                return this;
            }

            public Builder clearMetadata() {
                copyOnWrite();
                ((PersistedConfig) this.instance).clearMetadata();
                return this;
            }

            public List<Resource> getAppliedResourceList() {
                return Collections.unmodifiableList(((PersistedConfig) this.instance).getAppliedResourceList());
            }

            public int getAppliedResourceCount() {
                return ((PersistedConfig) this.instance).getAppliedResourceCount();
            }

            public Resource getAppliedResource(int index) {
                return ((PersistedConfig) this.instance).getAppliedResource(index);
            }

            public Builder setAppliedResource(int index, Resource value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).setAppliedResource(index, value);
                return this;
            }

            public Builder setAppliedResource(int index, Resource.Builder builderForValue) {
                copyOnWrite();
                ((PersistedConfig) this.instance).setAppliedResource(index, builderForValue);
                return this;
            }

            public Builder addAppliedResource(Resource value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).addAppliedResource(value);
                return this;
            }

            public Builder addAppliedResource(int index, Resource value) {
                copyOnWrite();
                ((PersistedConfig) this.instance).addAppliedResource(index, value);
                return this;
            }

            public Builder addAppliedResource(Resource.Builder builderForValue) {
                copyOnWrite();
                ((PersistedConfig) this.instance).addAppliedResource(builderForValue);
                return this;
            }

            public Builder addAppliedResource(int index, Resource.Builder builderForValue) {
                copyOnWrite();
                ((PersistedConfig) this.instance).addAppliedResource(index, builderForValue);
                return this;
            }

            public Builder addAllAppliedResource(Iterable<? extends Resource> values) {
                copyOnWrite();
                ((PersistedConfig) this.instance).addAllAppliedResource(values);
                return this;
            }

            public Builder clearAppliedResource() {
                copyOnWrite();
                ((PersistedConfig) this.instance).clearAppliedResource();
                return this;
            }

            public Builder removeAppliedResource(int index) {
                copyOnWrite();
                ((PersistedConfig) this.instance).removeAppliedResource(index);
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[method.ordinal()]) {
                case 1:
                    return new PersistedConfig();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    this.appliedResource_.makeImmutable();
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) arg0;
                    PersistedConfig other = (PersistedConfig) arg1;
                    this.fetchedConfigHolder_ = (ConfigHolder) visitor.visitMessage(this.fetchedConfigHolder_, other.fetchedConfigHolder_);
                    this.activeConfigHolder_ = (ConfigHolder) visitor.visitMessage(this.activeConfigHolder_, other.activeConfigHolder_);
                    this.defaultsConfigHolder_ = (ConfigHolder) visitor.visitMessage(this.defaultsConfigHolder_, other.defaultsConfigHolder_);
                    this.metadata_ = (Metadata) visitor.visitMessage(this.metadata_, other.metadata_);
                    this.appliedResource_ = visitor.visitList(this.appliedResource_, other.appliedResource_);
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
                                ConfigHolder.Builder subBuilder = null;
                                if ((this.bitField0_ & 1) == 1) {
                                    subBuilder = (ConfigHolder.Builder) this.fetchedConfigHolder_.toBuilder();
                                }
                                ConfigHolder configHolder = (ConfigHolder) input.readMessage(ConfigHolder.parser(), extensionRegistry);
                                this.fetchedConfigHolder_ = configHolder;
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom(configHolder);
                                    this.fetchedConfigHolder_ = (ConfigHolder) subBuilder.buildPartial();
                                }
                                this.bitField0_ |= 1;
                            } else if (tag == 18) {
                                ConfigHolder.Builder subBuilder2 = null;
                                if ((this.bitField0_ & 2) == 2) {
                                    subBuilder2 = (ConfigHolder.Builder) this.activeConfigHolder_.toBuilder();
                                }
                                ConfigHolder configHolder2 = (ConfigHolder) input.readMessage(ConfigHolder.parser(), extensionRegistry);
                                this.activeConfigHolder_ = configHolder2;
                                if (subBuilder2 != null) {
                                    subBuilder2.mergeFrom(configHolder2);
                                    this.activeConfigHolder_ = (ConfigHolder) subBuilder2.buildPartial();
                                }
                                this.bitField0_ |= 2;
                            } else if (tag == 26) {
                                ConfigHolder.Builder subBuilder3 = null;
                                if ((this.bitField0_ & 4) == 4) {
                                    subBuilder3 = (ConfigHolder.Builder) this.defaultsConfigHolder_.toBuilder();
                                }
                                ConfigHolder configHolder3 = (ConfigHolder) input.readMessage(ConfigHolder.parser(), extensionRegistry);
                                this.defaultsConfigHolder_ = configHolder3;
                                if (subBuilder3 != null) {
                                    subBuilder3.mergeFrom(configHolder3);
                                    this.defaultsConfigHolder_ = (ConfigHolder) subBuilder3.buildPartial();
                                }
                                this.bitField0_ |= 4;
                            } else if (tag == 34) {
                                Metadata.Builder subBuilder4 = null;
                                if ((this.bitField0_ & 8) == 8) {
                                    subBuilder4 = (Metadata.Builder) this.metadata_.toBuilder();
                                }
                                Metadata metadata = (Metadata) input.readMessage(Metadata.parser(), extensionRegistry);
                                this.metadata_ = metadata;
                                if (subBuilder4 != null) {
                                    subBuilder4.mergeFrom(metadata);
                                    this.metadata_ = (Metadata) subBuilder4.buildPartial();
                                }
                                this.bitField0_ |= 8;
                            } else if (tag == 42) {
                                if (!this.appliedResource_.isModifiable()) {
                                    this.appliedResource_ = GeneratedMessageLite.mutableCopy(this.appliedResource_);
                                }
                                this.appliedResource_.add((Resource) input.readMessage(Resource.parser(), extensionRegistry));
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
                        synchronized (PersistedConfig.class) {
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
            PersistedConfig persistedConfig = new PersistedConfig();
            DEFAULT_INSTANCE = persistedConfig;
            persistedConfig.makeImmutable();
        }

        public static PersistedConfig getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<PersistedConfig> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* renamed from: com.google.firebase.remoteconfig.proto.ConfigPersistence$1  reason: invalid class name */
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
    public static final class KeyValue extends GeneratedMessageLite<KeyValue, Builder> implements KeyValueOrBuilder {
        /* access modifiers changed from: private */
        public static final KeyValue DEFAULT_INSTANCE;
        public static final int KEY_FIELD_NUMBER = 1;
        private static volatile Parser<KeyValue> PARSER = null;
        public static final int VALUE_FIELD_NUMBER = 2;
        private int bitField0_;
        private String key_ = "";
        private ByteString value_ = ByteString.EMPTY;

        private KeyValue() {
        }

        public boolean hasKey() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getKey() {
            return this.key_;
        }

        public ByteString getKeyBytes() {
            return ByteString.copyFromUtf8(this.key_);
        }

        /* access modifiers changed from: private */
        public void setKey(String value) {
            if (value != null) {
                this.bitField0_ |= 1;
                this.key_ = value;
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void clearKey() {
            this.bitField0_ &= -2;
            this.key_ = getDefaultInstance().getKey();
        }

        /* access modifiers changed from: private */
        public void setKeyBytes(ByteString value) {
            if (value != null) {
                this.bitField0_ |= 1;
                this.key_ = value.toStringUtf8();
                return;
            }
            throw null;
        }

        public boolean hasValue() {
            return (this.bitField0_ & 2) == 2;
        }

        public ByteString getValue() {
            return this.value_;
        }

        /* access modifiers changed from: private */
        public void setValue(ByteString value) {
            if (value != null) {
                this.bitField0_ |= 2;
                this.value_ = value;
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void clearValue() {
            this.bitField0_ &= -3;
            this.value_ = getDefaultInstance().getValue();
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                output.writeString(1, getKey());
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeBytes(2, this.value_);
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
                size2 = 0 + CodedOutputStream.computeStringSize(1, getKey());
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeBytesSize(2, this.value_);
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        public static KeyValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (KeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static KeyValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (KeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static KeyValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (KeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static KeyValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (KeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static KeyValue parseFrom(InputStream input) throws IOException {
            return (KeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static KeyValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (KeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static KeyValue parseDelimitedFrom(InputStream input) throws IOException {
            return (KeyValue) parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static KeyValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (KeyValue) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static KeyValue parseFrom(CodedInputStream input) throws IOException {
            return (KeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static KeyValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (KeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(KeyValue prototype) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(prototype);
        }

        /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
        public static final class Builder extends GeneratedMessageLite.Builder<KeyValue, Builder> implements KeyValueOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 x0) {
                this();
            }

            private Builder() {
                super(KeyValue.DEFAULT_INSTANCE);
            }

            public boolean hasKey() {
                return ((KeyValue) this.instance).hasKey();
            }

            public String getKey() {
                return ((KeyValue) this.instance).getKey();
            }

            public ByteString getKeyBytes() {
                return ((KeyValue) this.instance).getKeyBytes();
            }

            public Builder setKey(String value) {
                copyOnWrite();
                ((KeyValue) this.instance).setKey(value);
                return this;
            }

            public Builder clearKey() {
                copyOnWrite();
                ((KeyValue) this.instance).clearKey();
                return this;
            }

            public Builder setKeyBytes(ByteString value) {
                copyOnWrite();
                ((KeyValue) this.instance).setKeyBytes(value);
                return this;
            }

            public boolean hasValue() {
                return ((KeyValue) this.instance).hasValue();
            }

            public ByteString getValue() {
                return ((KeyValue) this.instance).getValue();
            }

            public Builder setValue(ByteString value) {
                copyOnWrite();
                ((KeyValue) this.instance).setValue(value);
                return this;
            }

            public Builder clearValue() {
                copyOnWrite();
                ((KeyValue) this.instance).clearValue();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[method.ordinal()]) {
                case 1:
                    return new KeyValue();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) arg0;
                    KeyValue other = (KeyValue) arg1;
                    this.key_ = visitor.visitString(hasKey(), this.key_, other.hasKey(), other.key_);
                    this.value_ = visitor.visitByteString(hasValue(), this.value_, other.hasValue(), other.value_);
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
                            } else if (tag == 10) {
                                String s = input.readString();
                                this.bitField0_ |= 1;
                                this.key_ = s;
                            } else if (tag == 18) {
                                this.bitField0_ |= 2;
                                this.value_ = input.readBytes();
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
                        synchronized (KeyValue.class) {
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
            KeyValue keyValue = new KeyValue();
            DEFAULT_INSTANCE = keyValue;
            keyValue.makeImmutable();
        }

        public static KeyValue getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<KeyValue> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public static final class NamespaceKeyValue extends GeneratedMessageLite<NamespaceKeyValue, Builder> implements NamespaceKeyValueOrBuilder {
        /* access modifiers changed from: private */
        public static final NamespaceKeyValue DEFAULT_INSTANCE;
        public static final int KEY_VALUE_FIELD_NUMBER = 2;
        public static final int NAMESPACE_FIELD_NUMBER = 1;
        private static volatile Parser<NamespaceKeyValue> PARSER;
        private int bitField0_;
        private Internal.ProtobufList<KeyValue> keyValue_ = emptyProtobufList();
        private String namespace_ = "";

        private NamespaceKeyValue() {
        }

        public boolean hasNamespace() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getNamespace() {
            return this.namespace_;
        }

        public ByteString getNamespaceBytes() {
            return ByteString.copyFromUtf8(this.namespace_);
        }

        /* access modifiers changed from: private */
        public void setNamespace(String value) {
            if (value != null) {
                this.bitField0_ |= 1;
                this.namespace_ = value;
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void clearNamespace() {
            this.bitField0_ &= -2;
            this.namespace_ = getDefaultInstance().getNamespace();
        }

        /* access modifiers changed from: private */
        public void setNamespaceBytes(ByteString value) {
            if (value != null) {
                this.bitField0_ |= 1;
                this.namespace_ = value.toStringUtf8();
                return;
            }
            throw null;
        }

        public List<KeyValue> getKeyValueList() {
            return this.keyValue_;
        }

        public List<? extends KeyValueOrBuilder> getKeyValueOrBuilderList() {
            return this.keyValue_;
        }

        public int getKeyValueCount() {
            return this.keyValue_.size();
        }

        public KeyValue getKeyValue(int index) {
            return (KeyValue) this.keyValue_.get(index);
        }

        public KeyValueOrBuilder getKeyValueOrBuilder(int index) {
            return (KeyValueOrBuilder) this.keyValue_.get(index);
        }

        private void ensureKeyValueIsMutable() {
            if (!this.keyValue_.isModifiable()) {
                this.keyValue_ = GeneratedMessageLite.mutableCopy(this.keyValue_);
            }
        }

        /* access modifiers changed from: private */
        public void setKeyValue(int index, KeyValue value) {
            if (value != null) {
                ensureKeyValueIsMutable();
                this.keyValue_.set(index, value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void setKeyValue(int index, KeyValue.Builder builderForValue) {
            ensureKeyValueIsMutable();
            this.keyValue_.set(index, (KeyValue) builderForValue.build());
        }

        /* access modifiers changed from: private */
        public void addKeyValue(KeyValue value) {
            if (value != null) {
                ensureKeyValueIsMutable();
                this.keyValue_.add(value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void addKeyValue(int index, KeyValue value) {
            if (value != null) {
                ensureKeyValueIsMutable();
                this.keyValue_.add(index, value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void addKeyValue(KeyValue.Builder builderForValue) {
            ensureKeyValueIsMutable();
            this.keyValue_.add((KeyValue) builderForValue.build());
        }

        /* access modifiers changed from: private */
        public void addKeyValue(int index, KeyValue.Builder builderForValue) {
            ensureKeyValueIsMutable();
            this.keyValue_.add(index, (KeyValue) builderForValue.build());
        }

        /* access modifiers changed from: private */
        public void addAllKeyValue(Iterable<? extends KeyValue> values) {
            ensureKeyValueIsMutable();
            AbstractMessageLite.addAll(values, this.keyValue_);
        }

        /* access modifiers changed from: private */
        public void clearKeyValue() {
            this.keyValue_ = emptyProtobufList();
        }

        /* access modifiers changed from: private */
        public void removeKeyValue(int index) {
            ensureKeyValueIsMutable();
            this.keyValue_.remove(index);
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                output.writeString(1, getNamespace());
            }
            for (int i = 0; i < this.keyValue_.size(); i++) {
                output.writeMessage(2, (MessageLite) this.keyValue_.get(i));
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
                size2 = 0 + CodedOutputStream.computeStringSize(1, getNamespace());
            }
            for (int i = 0; i < this.keyValue_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(2, (MessageLite) this.keyValue_.get(i));
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        public static NamespaceKeyValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (NamespaceKeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static NamespaceKeyValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (NamespaceKeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static NamespaceKeyValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (NamespaceKeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static NamespaceKeyValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (NamespaceKeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static NamespaceKeyValue parseFrom(InputStream input) throws IOException {
            return (NamespaceKeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static NamespaceKeyValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (NamespaceKeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static NamespaceKeyValue parseDelimitedFrom(InputStream input) throws IOException {
            return (NamespaceKeyValue) parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static NamespaceKeyValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (NamespaceKeyValue) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static NamespaceKeyValue parseFrom(CodedInputStream input) throws IOException {
            return (NamespaceKeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static NamespaceKeyValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (NamespaceKeyValue) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(NamespaceKeyValue prototype) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(prototype);
        }

        /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
        public static final class Builder extends GeneratedMessageLite.Builder<NamespaceKeyValue, Builder> implements NamespaceKeyValueOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 x0) {
                this();
            }

            private Builder() {
                super(NamespaceKeyValue.DEFAULT_INSTANCE);
            }

            public boolean hasNamespace() {
                return ((NamespaceKeyValue) this.instance).hasNamespace();
            }

            public String getNamespace() {
                return ((NamespaceKeyValue) this.instance).getNamespace();
            }

            public ByteString getNamespaceBytes() {
                return ((NamespaceKeyValue) this.instance).getNamespaceBytes();
            }

            public Builder setNamespace(String value) {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).setNamespace(value);
                return this;
            }

            public Builder clearNamespace() {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).clearNamespace();
                return this;
            }

            public Builder setNamespaceBytes(ByteString value) {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).setNamespaceBytes(value);
                return this;
            }

            public List<KeyValue> getKeyValueList() {
                return Collections.unmodifiableList(((NamespaceKeyValue) this.instance).getKeyValueList());
            }

            public int getKeyValueCount() {
                return ((NamespaceKeyValue) this.instance).getKeyValueCount();
            }

            public KeyValue getKeyValue(int index) {
                return ((NamespaceKeyValue) this.instance).getKeyValue(index);
            }

            public Builder setKeyValue(int index, KeyValue value) {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).setKeyValue(index, value);
                return this;
            }

            public Builder setKeyValue(int index, KeyValue.Builder builderForValue) {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).setKeyValue(index, builderForValue);
                return this;
            }

            public Builder addKeyValue(KeyValue value) {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).addKeyValue(value);
                return this;
            }

            public Builder addKeyValue(int index, KeyValue value) {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).addKeyValue(index, value);
                return this;
            }

            public Builder addKeyValue(KeyValue.Builder builderForValue) {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).addKeyValue(builderForValue);
                return this;
            }

            public Builder addKeyValue(int index, KeyValue.Builder builderForValue) {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).addKeyValue(index, builderForValue);
                return this;
            }

            public Builder addAllKeyValue(Iterable<? extends KeyValue> values) {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).addAllKeyValue(values);
                return this;
            }

            public Builder clearKeyValue() {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).clearKeyValue();
                return this;
            }

            public Builder removeKeyValue(int index) {
                copyOnWrite();
                ((NamespaceKeyValue) this.instance).removeKeyValue(index);
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[method.ordinal()]) {
                case 1:
                    return new NamespaceKeyValue();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    this.keyValue_.makeImmutable();
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) arg0;
                    NamespaceKeyValue other = (NamespaceKeyValue) arg1;
                    this.namespace_ = visitor.visitString(hasNamespace(), this.namespace_, other.hasNamespace(), other.namespace_);
                    this.keyValue_ = visitor.visitList(this.keyValue_, other.keyValue_);
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
                                String s = input.readString();
                                this.bitField0_ |= 1;
                                this.namespace_ = s;
                            } else if (tag == 18) {
                                if (!this.keyValue_.isModifiable()) {
                                    this.keyValue_ = GeneratedMessageLite.mutableCopy(this.keyValue_);
                                }
                                this.keyValue_.add((KeyValue) input.readMessage(KeyValue.parser(), extensionRegistry));
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
                        synchronized (NamespaceKeyValue.class) {
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
            NamespaceKeyValue namespaceKeyValue = new NamespaceKeyValue();
            DEFAULT_INSTANCE = namespaceKeyValue;
            namespaceKeyValue.makeImmutable();
        }

        public static NamespaceKeyValue getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<NamespaceKeyValue> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public static final class ConfigHolder extends GeneratedMessageLite<ConfigHolder, Builder> implements ConfigHolderOrBuilder {
        /* access modifiers changed from: private */
        public static final ConfigHolder DEFAULT_INSTANCE;
        public static final int EXPERIMENT_PAYLOAD_FIELD_NUMBER = 3;
        public static final int NAMESPACE_KEY_VALUE_FIELD_NUMBER = 1;
        private static volatile Parser<ConfigHolder> PARSER = null;
        public static final int TIMESTAMP_FIELD_NUMBER = 2;
        private int bitField0_;
        private Internal.ProtobufList<ByteString> experimentPayload_ = emptyProtobufList();
        private Internal.ProtobufList<NamespaceKeyValue> namespaceKeyValue_ = emptyProtobufList();
        private long timestamp_;

        private ConfigHolder() {
        }

        public List<NamespaceKeyValue> getNamespaceKeyValueList() {
            return this.namespaceKeyValue_;
        }

        public List<? extends NamespaceKeyValueOrBuilder> getNamespaceKeyValueOrBuilderList() {
            return this.namespaceKeyValue_;
        }

        public int getNamespaceKeyValueCount() {
            return this.namespaceKeyValue_.size();
        }

        public NamespaceKeyValue getNamespaceKeyValue(int index) {
            return (NamespaceKeyValue) this.namespaceKeyValue_.get(index);
        }

        public NamespaceKeyValueOrBuilder getNamespaceKeyValueOrBuilder(int index) {
            return (NamespaceKeyValueOrBuilder) this.namespaceKeyValue_.get(index);
        }

        private void ensureNamespaceKeyValueIsMutable() {
            if (!this.namespaceKeyValue_.isModifiable()) {
                this.namespaceKeyValue_ = GeneratedMessageLite.mutableCopy(this.namespaceKeyValue_);
            }
        }

        /* access modifiers changed from: private */
        public void setNamespaceKeyValue(int index, NamespaceKeyValue value) {
            if (value != null) {
                ensureNamespaceKeyValueIsMutable();
                this.namespaceKeyValue_.set(index, value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void setNamespaceKeyValue(int index, NamespaceKeyValue.Builder builderForValue) {
            ensureNamespaceKeyValueIsMutable();
            this.namespaceKeyValue_.set(index, (NamespaceKeyValue) builderForValue.build());
        }

        /* access modifiers changed from: private */
        public void addNamespaceKeyValue(NamespaceKeyValue value) {
            if (value != null) {
                ensureNamespaceKeyValueIsMutable();
                this.namespaceKeyValue_.add(value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void addNamespaceKeyValue(int index, NamespaceKeyValue value) {
            if (value != null) {
                ensureNamespaceKeyValueIsMutable();
                this.namespaceKeyValue_.add(index, value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void addNamespaceKeyValue(NamespaceKeyValue.Builder builderForValue) {
            ensureNamespaceKeyValueIsMutable();
            this.namespaceKeyValue_.add((NamespaceKeyValue) builderForValue.build());
        }

        /* access modifiers changed from: private */
        public void addNamespaceKeyValue(int index, NamespaceKeyValue.Builder builderForValue) {
            ensureNamespaceKeyValueIsMutable();
            this.namespaceKeyValue_.add(index, (NamespaceKeyValue) builderForValue.build());
        }

        /* access modifiers changed from: private */
        public void addAllNamespaceKeyValue(Iterable<? extends NamespaceKeyValue> values) {
            ensureNamespaceKeyValueIsMutable();
            AbstractMessageLite.addAll(values, this.namespaceKeyValue_);
        }

        /* access modifiers changed from: private */
        public void clearNamespaceKeyValue() {
            this.namespaceKeyValue_ = emptyProtobufList();
        }

        /* access modifiers changed from: private */
        public void removeNamespaceKeyValue(int index) {
            ensureNamespaceKeyValueIsMutable();
            this.namespaceKeyValue_.remove(index);
        }

        public boolean hasTimestamp() {
            return (this.bitField0_ & 1) == 1;
        }

        public long getTimestamp() {
            return this.timestamp_;
        }

        /* access modifiers changed from: private */
        public void setTimestamp(long value) {
            this.bitField0_ |= 1;
            this.timestamp_ = value;
        }

        /* access modifiers changed from: private */
        public void clearTimestamp() {
            this.bitField0_ &= -2;
            this.timestamp_ = 0;
        }

        public List<ByteString> getExperimentPayloadList() {
            return this.experimentPayload_;
        }

        public int getExperimentPayloadCount() {
            return this.experimentPayload_.size();
        }

        public ByteString getExperimentPayload(int index) {
            return (ByteString) this.experimentPayload_.get(index);
        }

        private void ensureExperimentPayloadIsMutable() {
            if (!this.experimentPayload_.isModifiable()) {
                this.experimentPayload_ = GeneratedMessageLite.mutableCopy(this.experimentPayload_);
            }
        }

        /* access modifiers changed from: private */
        public void setExperimentPayload(int index, ByteString value) {
            if (value != null) {
                ensureExperimentPayloadIsMutable();
                this.experimentPayload_.set(index, value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void addExperimentPayload(ByteString value) {
            if (value != null) {
                ensureExperimentPayloadIsMutable();
                this.experimentPayload_.add(value);
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void addAllExperimentPayload(Iterable<? extends ByteString> values) {
            ensureExperimentPayloadIsMutable();
            AbstractMessageLite.addAll(values, this.experimentPayload_);
        }

        /* access modifiers changed from: private */
        public void clearExperimentPayload() {
            this.experimentPayload_ = emptyProtobufList();
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            for (int i = 0; i < this.namespaceKeyValue_.size(); i++) {
                output.writeMessage(1, (MessageLite) this.namespaceKeyValue_.get(i));
            }
            if ((this.bitField0_ & 1) == 1) {
                output.writeFixed64(2, this.timestamp_);
            }
            for (int i2 = 0; i2 < this.experimentPayload_.size(); i2++) {
                output.writeBytes(3, (ByteString) this.experimentPayload_.get(i2));
            }
            this.unknownFields.writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            for (int i = 0; i < this.namespaceKeyValue_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(1, (MessageLite) this.namespaceKeyValue_.get(i));
            }
            if ((this.bitField0_ & 1) == 1) {
                size2 += CodedOutputStream.computeFixed64Size(2, this.timestamp_);
            }
            int dataSize = 0;
            for (int i2 = 0; i2 < this.experimentPayload_.size(); i2++) {
                dataSize += CodedOutputStream.computeBytesSizeNoTag((ByteString) this.experimentPayload_.get(i2));
            }
            int size3 = size2 + dataSize + (getExperimentPayloadList().size() * 1) + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        public static ConfigHolder parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (ConfigHolder) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static ConfigHolder parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (ConfigHolder) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static ConfigHolder parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (ConfigHolder) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static ConfigHolder parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (ConfigHolder) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static ConfigHolder parseFrom(InputStream input) throws IOException {
            return (ConfigHolder) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static ConfigHolder parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ConfigHolder) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static ConfigHolder parseDelimitedFrom(InputStream input) throws IOException {
            return (ConfigHolder) parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static ConfigHolder parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ConfigHolder) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static ConfigHolder parseFrom(CodedInputStream input) throws IOException {
            return (ConfigHolder) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static ConfigHolder parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ConfigHolder) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(ConfigHolder prototype) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(prototype);
        }

        /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
        public static final class Builder extends GeneratedMessageLite.Builder<ConfigHolder, Builder> implements ConfigHolderOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 x0) {
                this();
            }

            private Builder() {
                super(ConfigHolder.DEFAULT_INSTANCE);
            }

            public List<NamespaceKeyValue> getNamespaceKeyValueList() {
                return Collections.unmodifiableList(((ConfigHolder) this.instance).getNamespaceKeyValueList());
            }

            public int getNamespaceKeyValueCount() {
                return ((ConfigHolder) this.instance).getNamespaceKeyValueCount();
            }

            public NamespaceKeyValue getNamespaceKeyValue(int index) {
                return ((ConfigHolder) this.instance).getNamespaceKeyValue(index);
            }

            public Builder setNamespaceKeyValue(int index, NamespaceKeyValue value) {
                copyOnWrite();
                ((ConfigHolder) this.instance).setNamespaceKeyValue(index, value);
                return this;
            }

            public Builder setNamespaceKeyValue(int index, NamespaceKeyValue.Builder builderForValue) {
                copyOnWrite();
                ((ConfigHolder) this.instance).setNamespaceKeyValue(index, builderForValue);
                return this;
            }

            public Builder addNamespaceKeyValue(NamespaceKeyValue value) {
                copyOnWrite();
                ((ConfigHolder) this.instance).addNamespaceKeyValue(value);
                return this;
            }

            public Builder addNamespaceKeyValue(int index, NamespaceKeyValue value) {
                copyOnWrite();
                ((ConfigHolder) this.instance).addNamespaceKeyValue(index, value);
                return this;
            }

            public Builder addNamespaceKeyValue(NamespaceKeyValue.Builder builderForValue) {
                copyOnWrite();
                ((ConfigHolder) this.instance).addNamespaceKeyValue(builderForValue);
                return this;
            }

            public Builder addNamespaceKeyValue(int index, NamespaceKeyValue.Builder builderForValue) {
                copyOnWrite();
                ((ConfigHolder) this.instance).addNamespaceKeyValue(index, builderForValue);
                return this;
            }

            public Builder addAllNamespaceKeyValue(Iterable<? extends NamespaceKeyValue> values) {
                copyOnWrite();
                ((ConfigHolder) this.instance).addAllNamespaceKeyValue(values);
                return this;
            }

            public Builder clearNamespaceKeyValue() {
                copyOnWrite();
                ((ConfigHolder) this.instance).clearNamespaceKeyValue();
                return this;
            }

            public Builder removeNamespaceKeyValue(int index) {
                copyOnWrite();
                ((ConfigHolder) this.instance).removeNamespaceKeyValue(index);
                return this;
            }

            public boolean hasTimestamp() {
                return ((ConfigHolder) this.instance).hasTimestamp();
            }

            public long getTimestamp() {
                return ((ConfigHolder) this.instance).getTimestamp();
            }

            public Builder setTimestamp(long value) {
                copyOnWrite();
                ((ConfigHolder) this.instance).setTimestamp(value);
                return this;
            }

            public Builder clearTimestamp() {
                copyOnWrite();
                ((ConfigHolder) this.instance).clearTimestamp();
                return this;
            }

            public List<ByteString> getExperimentPayloadList() {
                return Collections.unmodifiableList(((ConfigHolder) this.instance).getExperimentPayloadList());
            }

            public int getExperimentPayloadCount() {
                return ((ConfigHolder) this.instance).getExperimentPayloadCount();
            }

            public ByteString getExperimentPayload(int index) {
                return ((ConfigHolder) this.instance).getExperimentPayload(index);
            }

            public Builder setExperimentPayload(int index, ByteString value) {
                copyOnWrite();
                ((ConfigHolder) this.instance).setExperimentPayload(index, value);
                return this;
            }

            public Builder addExperimentPayload(ByteString value) {
                copyOnWrite();
                ((ConfigHolder) this.instance).addExperimentPayload(value);
                return this;
            }

            public Builder addAllExperimentPayload(Iterable<? extends ByteString> values) {
                copyOnWrite();
                ((ConfigHolder) this.instance).addAllExperimentPayload(values);
                return this;
            }

            public Builder clearExperimentPayload() {
                copyOnWrite();
                ((ConfigHolder) this.instance).clearExperimentPayload();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[method.ordinal()]) {
                case 1:
                    return new ConfigHolder();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    this.namespaceKeyValue_.makeImmutable();
                    this.experimentPayload_.makeImmutable();
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) arg0;
                    ConfigHolder other = (ConfigHolder) arg1;
                    this.namespaceKeyValue_ = visitor.visitList(this.namespaceKeyValue_, other.namespaceKeyValue_);
                    this.timestamp_ = visitor.visitLong(hasTimestamp(), this.timestamp_, other.hasTimestamp(), other.timestamp_);
                    this.experimentPayload_ = visitor.visitList(this.experimentPayload_, other.experimentPayload_);
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
                                if (!this.namespaceKeyValue_.isModifiable()) {
                                    this.namespaceKeyValue_ = GeneratedMessageLite.mutableCopy(this.namespaceKeyValue_);
                                }
                                this.namespaceKeyValue_.add((NamespaceKeyValue) input.readMessage(NamespaceKeyValue.parser(), extensionRegistry));
                            } else if (tag == 17) {
                                this.bitField0_ |= 1;
                                this.timestamp_ = input.readFixed64();
                            } else if (tag == 26) {
                                if (!this.experimentPayload_.isModifiable()) {
                                    this.experimentPayload_ = GeneratedMessageLite.mutableCopy(this.experimentPayload_);
                                }
                                this.experimentPayload_.add(input.readBytes());
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
                        synchronized (ConfigHolder.class) {
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
            ConfigHolder configHolder = new ConfigHolder();
            DEFAULT_INSTANCE = configHolder;
            configHolder.makeImmutable();
        }

        public static ConfigHolder getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ConfigHolder> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public static final class Metadata extends GeneratedMessageLite<Metadata, Builder> implements MetadataOrBuilder {
        /* access modifiers changed from: private */
        public static final Metadata DEFAULT_INSTANCE;
        public static final int DEVELOPER_MODE_ENABLED_FIELD_NUMBER = 2;
        public static final int LAST_FETCH_STATUS_FIELD_NUMBER = 1;
        public static final int LAST_KNOWN_EXPERIMENT_START_TIME_FIELD_NUMBER = 3;
        private static volatile Parser<Metadata> PARSER;
        private int bitField0_;
        private boolean developerModeEnabled_;
        private int lastFetchStatus_;
        private long lastKnownExperimentStartTime_;

        private Metadata() {
        }

        public boolean hasLastFetchStatus() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getLastFetchStatus() {
            return this.lastFetchStatus_;
        }

        /* access modifiers changed from: private */
        public void setLastFetchStatus(int value) {
            this.bitField0_ |= 1;
            this.lastFetchStatus_ = value;
        }

        /* access modifiers changed from: private */
        public void clearLastFetchStatus() {
            this.bitField0_ &= -2;
            this.lastFetchStatus_ = 0;
        }

        public boolean hasDeveloperModeEnabled() {
            return (this.bitField0_ & 2) == 2;
        }

        public boolean getDeveloperModeEnabled() {
            return this.developerModeEnabled_;
        }

        /* access modifiers changed from: private */
        public void setDeveloperModeEnabled(boolean value) {
            this.bitField0_ |= 2;
            this.developerModeEnabled_ = value;
        }

        /* access modifiers changed from: private */
        public void clearDeveloperModeEnabled() {
            this.bitField0_ &= -3;
            this.developerModeEnabled_ = false;
        }

        public boolean hasLastKnownExperimentStartTime() {
            return (this.bitField0_ & 4) == 4;
        }

        public long getLastKnownExperimentStartTime() {
            return this.lastKnownExperimentStartTime_;
        }

        /* access modifiers changed from: private */
        public void setLastKnownExperimentStartTime(long value) {
            this.bitField0_ |= 4;
            this.lastKnownExperimentStartTime_ = value;
        }

        /* access modifiers changed from: private */
        public void clearLastKnownExperimentStartTime() {
            this.bitField0_ &= -5;
            this.lastKnownExperimentStartTime_ = 0;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                output.writeInt32(1, this.lastFetchStatus_);
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeBool(2, this.developerModeEnabled_);
            }
            if ((this.bitField0_ & 4) == 4) {
                output.writeFixed64(3, this.lastKnownExperimentStartTime_);
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
                size2 = 0 + CodedOutputStream.computeInt32Size(1, this.lastFetchStatus_);
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeBoolSize(2, this.developerModeEnabled_);
            }
            if ((this.bitField0_ & 4) == 4) {
                size2 += CodedOutputStream.computeFixed64Size(3, this.lastKnownExperimentStartTime_);
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        public static Metadata parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Metadata) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static Metadata parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Metadata) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static Metadata parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Metadata) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static Metadata parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Metadata) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static Metadata parseFrom(InputStream input) throws IOException {
            return (Metadata) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static Metadata parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Metadata) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Metadata parseDelimitedFrom(InputStream input) throws IOException {
            return (Metadata) parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static Metadata parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Metadata) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Metadata parseFrom(CodedInputStream input) throws IOException {
            return (Metadata) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static Metadata parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Metadata) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Metadata prototype) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(prototype);
        }

        /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
        public static final class Builder extends GeneratedMessageLite.Builder<Metadata, Builder> implements MetadataOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 x0) {
                this();
            }

            private Builder() {
                super(Metadata.DEFAULT_INSTANCE);
            }

            public boolean hasLastFetchStatus() {
                return ((Metadata) this.instance).hasLastFetchStatus();
            }

            public int getLastFetchStatus() {
                return ((Metadata) this.instance).getLastFetchStatus();
            }

            public Builder setLastFetchStatus(int value) {
                copyOnWrite();
                ((Metadata) this.instance).setLastFetchStatus(value);
                return this;
            }

            public Builder clearLastFetchStatus() {
                copyOnWrite();
                ((Metadata) this.instance).clearLastFetchStatus();
                return this;
            }

            public boolean hasDeveloperModeEnabled() {
                return ((Metadata) this.instance).hasDeveloperModeEnabled();
            }

            public boolean getDeveloperModeEnabled() {
                return ((Metadata) this.instance).getDeveloperModeEnabled();
            }

            public Builder setDeveloperModeEnabled(boolean value) {
                copyOnWrite();
                ((Metadata) this.instance).setDeveloperModeEnabled(value);
                return this;
            }

            public Builder clearDeveloperModeEnabled() {
                copyOnWrite();
                ((Metadata) this.instance).clearDeveloperModeEnabled();
                return this;
            }

            public boolean hasLastKnownExperimentStartTime() {
                return ((Metadata) this.instance).hasLastKnownExperimentStartTime();
            }

            public long getLastKnownExperimentStartTime() {
                return ((Metadata) this.instance).getLastKnownExperimentStartTime();
            }

            public Builder setLastKnownExperimentStartTime(long value) {
                copyOnWrite();
                ((Metadata) this.instance).setLastKnownExperimentStartTime(value);
                return this;
            }

            public Builder clearLastKnownExperimentStartTime() {
                copyOnWrite();
                ((Metadata) this.instance).clearLastKnownExperimentStartTime();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[method.ordinal()]) {
                case 1:
                    return new Metadata();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) arg0;
                    Metadata other = (Metadata) arg1;
                    this.lastFetchStatus_ = visitor.visitInt(hasLastFetchStatus(), this.lastFetchStatus_, other.hasLastFetchStatus(), other.lastFetchStatus_);
                    this.developerModeEnabled_ = visitor.visitBoolean(hasDeveloperModeEnabled(), this.developerModeEnabled_, other.hasDeveloperModeEnabled(), other.developerModeEnabled_);
                    this.lastKnownExperimentStartTime_ = visitor.visitLong(hasLastKnownExperimentStartTime(), this.lastKnownExperimentStartTime_, other.hasLastKnownExperimentStartTime(), other.lastKnownExperimentStartTime_);
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
                                this.bitField0_ |= 1;
                                this.lastFetchStatus_ = input.readInt32();
                            } else if (tag == 16) {
                                this.bitField0_ |= 2;
                                this.developerModeEnabled_ = input.readBool();
                            } else if (tag == 25) {
                                this.bitField0_ |= 4;
                                this.lastKnownExperimentStartTime_ = input.readFixed64();
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
                        synchronized (Metadata.class) {
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
            Metadata metadata = new Metadata();
            DEFAULT_INSTANCE = metadata;
            metadata.makeImmutable();
        }

        public static Metadata getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Metadata> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public static final class Resource extends GeneratedMessageLite<Resource, Builder> implements ResourceOrBuilder {
        public static final int APP_UPDATE_TIME_FIELD_NUMBER = 2;
        /* access modifiers changed from: private */
        public static final Resource DEFAULT_INSTANCE;
        public static final int NAMESPACE_FIELD_NUMBER = 3;
        private static volatile Parser<Resource> PARSER = null;
        public static final int RESOURCE_ID_FIELD_NUMBER = 1;
        private long appUpdateTime_;
        private int bitField0_;
        private String namespace_ = "";
        private int resourceId_;

        private Resource() {
        }

        public boolean hasResourceId() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getResourceId() {
            return this.resourceId_;
        }

        /* access modifiers changed from: private */
        public void setResourceId(int value) {
            this.bitField0_ |= 1;
            this.resourceId_ = value;
        }

        /* access modifiers changed from: private */
        public void clearResourceId() {
            this.bitField0_ &= -2;
            this.resourceId_ = 0;
        }

        public boolean hasAppUpdateTime() {
            return (this.bitField0_ & 2) == 2;
        }

        public long getAppUpdateTime() {
            return this.appUpdateTime_;
        }

        /* access modifiers changed from: private */
        public void setAppUpdateTime(long value) {
            this.bitField0_ |= 2;
            this.appUpdateTime_ = value;
        }

        /* access modifiers changed from: private */
        public void clearAppUpdateTime() {
            this.bitField0_ &= -3;
            this.appUpdateTime_ = 0;
        }

        public boolean hasNamespace() {
            return (this.bitField0_ & 4) == 4;
        }

        public String getNamespace() {
            return this.namespace_;
        }

        public ByteString getNamespaceBytes() {
            return ByteString.copyFromUtf8(this.namespace_);
        }

        /* access modifiers changed from: private */
        public void setNamespace(String value) {
            if (value != null) {
                this.bitField0_ |= 4;
                this.namespace_ = value;
                return;
            }
            throw null;
        }

        /* access modifiers changed from: private */
        public void clearNamespace() {
            this.bitField0_ &= -5;
            this.namespace_ = getDefaultInstance().getNamespace();
        }

        /* access modifiers changed from: private */
        public void setNamespaceBytes(ByteString value) {
            if (value != null) {
                this.bitField0_ |= 4;
                this.namespace_ = value.toStringUtf8();
                return;
            }
            throw null;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                output.writeInt32(1, this.resourceId_);
            }
            if ((this.bitField0_ & 2) == 2) {
                output.writeFixed64(2, this.appUpdateTime_);
            }
            if ((this.bitField0_ & 4) == 4) {
                output.writeString(3, getNamespace());
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
                size2 = 0 + CodedOutputStream.computeInt32Size(1, this.resourceId_);
            }
            if ((this.bitField0_ & 2) == 2) {
                size2 += CodedOutputStream.computeFixed64Size(2, this.appUpdateTime_);
            }
            if ((this.bitField0_ & 4) == 4) {
                size2 += CodedOutputStream.computeStringSize(3, getNamespace());
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = size3;
            return size3;
        }

        public static Resource parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Resource) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static Resource parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Resource) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static Resource parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Resource) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static Resource parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Resource) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static Resource parseFrom(InputStream input) throws IOException {
            return (Resource) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static Resource parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Resource) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Resource parseDelimitedFrom(InputStream input) throws IOException {
            return (Resource) parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static Resource parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Resource) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Resource parseFrom(CodedInputStream input) throws IOException {
            return (Resource) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static Resource parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Resource) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Resource prototype) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(prototype);
        }

        /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
        public static final class Builder extends GeneratedMessageLite.Builder<Resource, Builder> implements ResourceOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 x0) {
                this();
            }

            private Builder() {
                super(Resource.DEFAULT_INSTANCE);
            }

            public boolean hasResourceId() {
                return ((Resource) this.instance).hasResourceId();
            }

            public int getResourceId() {
                return ((Resource) this.instance).getResourceId();
            }

            public Builder setResourceId(int value) {
                copyOnWrite();
                ((Resource) this.instance).setResourceId(value);
                return this;
            }

            public Builder clearResourceId() {
                copyOnWrite();
                ((Resource) this.instance).clearResourceId();
                return this;
            }

            public boolean hasAppUpdateTime() {
                return ((Resource) this.instance).hasAppUpdateTime();
            }

            public long getAppUpdateTime() {
                return ((Resource) this.instance).getAppUpdateTime();
            }

            public Builder setAppUpdateTime(long value) {
                copyOnWrite();
                ((Resource) this.instance).setAppUpdateTime(value);
                return this;
            }

            public Builder clearAppUpdateTime() {
                copyOnWrite();
                ((Resource) this.instance).clearAppUpdateTime();
                return this;
            }

            public boolean hasNamespace() {
                return ((Resource) this.instance).hasNamespace();
            }

            public String getNamespace() {
                return ((Resource) this.instance).getNamespace();
            }

            public ByteString getNamespaceBytes() {
                return ((Resource) this.instance).getNamespaceBytes();
            }

            public Builder setNamespace(String value) {
                copyOnWrite();
                ((Resource) this.instance).setNamespace(value);
                return this;
            }

            public Builder clearNamespace() {
                copyOnWrite();
                ((Resource) this.instance).clearNamespace();
                return this;
            }

            public Builder setNamespaceBytes(ByteString value) {
                copyOnWrite();
                ((Resource) this.instance).setNamespaceBytes(value);
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[method.ordinal()]) {
                case 1:
                    return new Resource();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) arg0;
                    Resource other = (Resource) arg1;
                    this.resourceId_ = visitor.visitInt(hasResourceId(), this.resourceId_, other.hasResourceId(), other.resourceId_);
                    this.appUpdateTime_ = visitor.visitLong(hasAppUpdateTime(), this.appUpdateTime_, other.hasAppUpdateTime(), other.appUpdateTime_);
                    this.namespace_ = visitor.visitString(hasNamespace(), this.namespace_, other.hasNamespace(), other.namespace_);
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
                                this.bitField0_ |= 1;
                                this.resourceId_ = input.readInt32();
                            } else if (tag == 17) {
                                this.bitField0_ |= 2;
                                this.appUpdateTime_ = input.readFixed64();
                            } else if (tag == 26) {
                                String s = input.readString();
                                this.bitField0_ |= 4;
                                this.namespace_ = s;
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
                        synchronized (Resource.class) {
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
            Resource resource = new Resource();
            DEFAULT_INSTANCE = resource;
            resource.makeImmutable();
        }

        public static Resource getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Resource> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }
}
