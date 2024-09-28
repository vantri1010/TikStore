package im.bclpbkiauv.ui.adapters;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.adapters.BaseLocationAdapter;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseLocationAdapter extends RecyclerListView.SelectionAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentRequestNum;
    private BaseLocationAdapterDelegate delegate;
    private long dialogId;
    protected ArrayList<String> iconUrls = new ArrayList<>();
    /* access modifiers changed from: private */
    public BDLocation lastSearchLocation;
    private String lastSearchQuery;
    protected ArrayList<TLRPC.TL_messageMediaVenue> places = new ArrayList<>();
    /* access modifiers changed from: private */
    public Timer searchTimer;
    protected boolean searching;
    private boolean searchingUser;

    public interface BaseLocationAdapterDelegate {
        void didLoadedSearchResult(ArrayList<TLRPC.TL_messageMediaVenue> arrayList);
    }

    public void destroy() {
        if (this.currentRequestNum != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
            this.currentRequestNum = 0;
        }
    }

    public void setDelegate(long did, BaseLocationAdapterDelegate delegate2) {
        this.dialogId = did;
        this.delegate = delegate2;
    }

    public void searchDelayed(final String query, final BDLocation coordinate) {
        if (query == null || query.length() == 0) {
            this.places.clear();
            notifyDataSetChanged();
            return;
        }
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Timer timer = new Timer();
        this.searchTimer = timer;
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    BaseLocationAdapter.this.searchTimer.cancel();
                    Timer unused = BaseLocationAdapter.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                AndroidUtilities.runOnUIThread(new Runnable(query, coordinate) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ BDLocation f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        BaseLocationAdapter.AnonymousClass1.this.lambda$run$0$BaseLocationAdapter$1(this.f$1, this.f$2);
                    }
                });
            }

            public /* synthetic */ void lambda$run$0$BaseLocationAdapter$1(String query, BDLocation coordinate) {
                BDLocation unused = BaseLocationAdapter.this.lastSearchLocation = null;
                BaseLocationAdapter.this.searchPlacesWithQuery(query, coordinate, true);
            }
        }, 200, 500);
    }

    private void searchBotUser() {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
            req.username = MessagesController.getInstance(this.currentAccount).venueSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BaseLocationAdapter.this.lambda$searchBotUser$1$BaseLocationAdapter(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$searchBotUser$1$BaseLocationAdapter(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable(response) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BaseLocationAdapter.this.lambda$null$0$BaseLocationAdapter(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$0$BaseLocationAdapter(TLObject response) {
        TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        BDLocation coord = this.lastSearchLocation;
        this.lastSearchLocation = null;
        searchPlacesWithQuery(this.lastSearchQuery, coord, false);
    }

    public void searchPlacesWithQuery(String query, BDLocation coordinate, boolean searchUser) {
        LatLng coordLatLng = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
        LatLng lastLatLng = new LatLng(this.lastSearchLocation.getLatitude(), this.lastSearchLocation.getLongitude());
        if (coordinate == null) {
            return;
        }
        if (this.lastSearchLocation == null || DistanceUtil.getDistance(lastLatLng, coordLatLng) >= 200.0d) {
            this.lastSearchLocation = coordinate;
            this.lastSearchQuery = query;
            if (this.searching) {
                this.searching = false;
                if (this.currentRequestNum != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
                    this.currentRequestNum = 0;
                }
            }
            this.searching = true;
            TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat(MessagesController.getInstance(this.currentAccount).venueSearchBot);
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
                req.query = query == null ? "" : query;
                req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                req.offset = "";
                req.geo_point = new TLRPC.TL_inputGeoPoint();
                req.geo_point.lat = AndroidUtilities.fixLocationCoord(coordinate.getLatitude());
                req.geo_point._long = AndroidUtilities.fixLocationCoord(coordinate.getLongitude());
                req.flags = 1 | req.flags;
                long j = this.dialogId;
                int lower_id = (int) j;
                int i = (int) (j >> 32);
                if (lower_id != 0) {
                    req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id);
                } else {
                    req.peer = new TLRPC.TL_inputPeerEmpty();
                }
                this.currentRequestNum = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        BaseLocationAdapter.this.lambda$searchPlacesWithQuery$3$BaseLocationAdapter(tLObject, tL_error);
                    }
                });
                notifyDataSetChanged();
            } else if (searchUser) {
                searchBotUser();
            }
        }
    }

    public /* synthetic */ void lambda$searchPlacesWithQuery$3$BaseLocationAdapter(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                BaseLocationAdapter.this.lambda$null$2$BaseLocationAdapter(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$BaseLocationAdapter(TLRPC.TL_error error, TLObject response) {
        this.currentRequestNum = 0;
        this.searching = false;
        this.places.clear();
        this.iconUrls.clear();
        if (error != null) {
            BaseLocationAdapterDelegate baseLocationAdapterDelegate = this.delegate;
            if (baseLocationAdapterDelegate != null) {
                baseLocationAdapterDelegate.didLoadedSearchResult(this.places);
            }
        } else {
            TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
            int size = res.results.size();
            for (int a = 0; a < size; a++) {
                TLRPC.BotInlineResult result = res.results.get(a);
                if ("venue".equals(result.type) && (result.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue)) {
                    TLRPC.TL_botInlineMessageMediaVenue mediaVenue = (TLRPC.TL_botInlineMessageMediaVenue) result.send_message;
                    ArrayList<String> arrayList = this.iconUrls;
                    arrayList.add("https://ss3.4sqi.net/img/categories_v2/" + mediaVenue.venue_type + "_64.png");
                    TLRPC.TL_messageMediaVenue venue = new TLRPC.TL_messageMediaVenue();
                    venue.geo = mediaVenue.geo;
                    venue.address = mediaVenue.address;
                    venue.title = mediaVenue.title;
                    venue.venue_type = mediaVenue.venue_type;
                    venue.venue_id = mediaVenue.venue_id;
                    venue.provider = mediaVenue.provider;
                    this.places.add(venue);
                }
            }
        }
        notifyDataSetChanged();
    }
}
