package im.bclpbkiauv.messenger.utils;

public class TaskQueue<T> {
    private int miCount = 0;
    private TaskQueue<T>.DataNode pHeader = null;
    private TaskQueue<T>.DataNode pTail = null;

    public synchronized boolean inputQueue(T tMember) {
        if (tMember == null) {
            return false;
        }
        TaskQueue<T>.DataNode dataNode = new DataNode(tMember);
        if (this.miCount == 0) {
            this.pHeader = dataNode;
            this.pTail = dataNode;
        } else {
            DataNode unused = this.pTail.mNextNode = dataNode;
        }
        this.pTail = dataNode;
        this.miCount++;
        return true;
    }

    public synchronized T getQueueTask() {
        TaskQueue<T>.DataNode dataNode = null;
        if (this.pHeader != null) {
            dataNode = this.pHeader;
            this.pHeader = dataNode.mNextNode;
            DataNode unused = dataNode.mNextNode = null;
            this.miCount--;
        }
        if (dataNode == null) {
            return null;
        }
        return dataNode.mData;
    }

    public synchronized void cleanQueue() {
        while (this.pHeader != null) {
            TaskQueue<T>.DataNode dataNode = this.pHeader;
            this.pHeader = dataNode.mNextNode;
            DataNode unused = dataNode.mNextNode = null;
        }
        this.miCount = 0;
    }

    public int getQueueSize() {
        return this.miCount;
    }

    private class DataNode {
        /* access modifiers changed from: private */
        public T mData;
        /* access modifiers changed from: private */
        public TaskQueue<T>.DataNode mNextNode = null;

        public DataNode(T t) {
            this.mData = t;
        }
    }
}
