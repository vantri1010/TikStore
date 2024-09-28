package com.bumptech.glide.request;

public interface RequestCoordinator {
    boolean canNotifyCleared(Request request);

    boolean canNotifyStatusChanged(Request request);

    boolean canSetImage(Request request);

    boolean isAnyResourceSet();

    void onRequestFailed(Request request);

    void onRequestSuccess(Request request);

    public enum RequestState {
        RUNNING(false),
        PAUSED(false),
        CLEARED(false),
        SUCCESS(true),
        FAILED(true);
        
        private final boolean isComplete;

        private RequestState(boolean isComplete2) {
            this.isComplete = isComplete2;
        }

        /* access modifiers changed from: package-private */
        public boolean isComplete() {
            return this.isComplete;
        }
    }
}
