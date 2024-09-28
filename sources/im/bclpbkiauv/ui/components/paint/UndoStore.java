package im.bclpbkiauv.ui.components.paint;

import im.bclpbkiauv.messenger.AndroidUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UndoStore {
    /* access modifiers changed from: private */
    public UndoStoreDelegate delegate;
    private List<UUID> operations = new ArrayList();
    private Map<UUID, Runnable> uuidToOperationMap = new HashMap();

    public interface UndoStoreDelegate {
        void historyChanged();
    }

    public boolean canUndo() {
        return !this.operations.isEmpty();
    }

    public void setDelegate(UndoStoreDelegate undoStoreDelegate) {
        this.delegate = undoStoreDelegate;
    }

    public void registerUndo(UUID uuid, Runnable undoRunnable) {
        this.uuidToOperationMap.put(uuid, undoRunnable);
        this.operations.add(uuid);
        notifyOfHistoryChanges();
    }

    public void unregisterUndo(UUID uuid) {
        this.uuidToOperationMap.remove(uuid);
        this.operations.remove(uuid);
        notifyOfHistoryChanges();
    }

    public void undo() {
        if (this.operations.size() != 0) {
            int lastIndex = this.operations.size() - 1;
            UUID uuid = this.operations.get(lastIndex);
            this.uuidToOperationMap.remove(uuid);
            this.operations.remove(lastIndex);
            this.uuidToOperationMap.get(uuid).run();
            notifyOfHistoryChanges();
        }
    }

    public void reset() {
        this.operations.clear();
        this.uuidToOperationMap.clear();
        notifyOfHistoryChanges();
    }

    private void notifyOfHistoryChanges() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (UndoStore.this.delegate != null) {
                    UndoStore.this.delegate.historyChanged();
                }
            }
        });
    }
}
