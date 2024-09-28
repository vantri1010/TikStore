package im.bclpbkiauv.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.LocationActivity;
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.LocationCell;
import im.bclpbkiauv.ui.cells.LocationLoadingCell;
import im.bclpbkiauv.ui.cells.LocationPoweredCell;
import im.bclpbkiauv.ui.cells.SendLocationCell;
import im.bclpbkiauv.ui.cells.SharingLiveLocationCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Locale;

@Deprecated
public class LocationActivityAdapter extends BaseLocationAdapter implements LocationController.LocationFetchCallback {
    private String addressName;
    private TLRPC.TL_channelLocation chatLocation;
    private int currentAccount = UserConfig.selectedAccount;
    private ArrayList<LocationActivity.LiveLocation> currentLiveLocations = new ArrayList<>();
    private MessageObject currentMessageObject;
    private BDLocation customLocation;
    private long dialogId;
    private boolean fetchingLocation;
    private BDLocation gpsLocation;
    private int locationType;
    private Context mContext;
    private int overScrollHeight;
    private BDLocation previousFetchedLocation;
    private boolean pulledUp;
    private SendLocationCell sendLocationCell;
    private int shareLiveLocationPotistion = -1;

    public LocationActivityAdapter(Context context, int type, long did) {
        this.mContext = context;
        this.locationType = type;
        this.dialogId = did;
    }

    public void setOverScrollHeight(int value) {
        this.overScrollHeight = value;
    }

    public void setGpsLocation(BDLocation location) {
        int i;
        boolean notSet = this.gpsLocation == null;
        this.gpsLocation = location;
        if (this.customLocation == null) {
            fetchLocationAddress();
        }
        if (notSet && (i = this.shareLiveLocationPotistion) > 0) {
            notifyItemChanged(i);
        }
        if (this.currentMessageObject != null) {
            notifyItemChanged(1);
            updateLiveLocations();
        } else if (this.locationType != 2) {
            updateCell();
        } else {
            updateLiveLocations();
        }
    }

    public void updateLiveLocations() {
        if (!this.currentLiveLocations.isEmpty()) {
            notifyItemRangeChanged(2, this.currentLiveLocations.size());
        }
    }

    public void setCustomLocation(BDLocation location) {
        this.customLocation = location;
        fetchLocationAddress();
        updateCell();
    }

    public void setLiveLocations(ArrayList<LocationActivity.LiveLocation> liveLocations) {
        this.currentLiveLocations = new ArrayList<>(liveLocations);
        int uid = UserConfig.getInstance(this.currentAccount).getClientUserId();
        int a = 0;
        while (true) {
            if (a >= this.currentLiveLocations.size()) {
                break;
            } else if (this.currentLiveLocations.get(a).id == uid) {
                this.currentLiveLocations.remove(a);
                break;
            } else {
                a++;
            }
        }
        notifyDataSetChanged();
    }

    public void setMessageObject(MessageObject messageObject) {
        this.currentMessageObject = messageObject;
        notifyDataSetChanged();
    }

    public void setChatLocation(TLRPC.TL_channelLocation location) {
        this.chatLocation = location;
    }

    private void updateCell() {
        String address;
        SendLocationCell sendLocationCell2 = this.sendLocationCell;
        if (sendLocationCell2 == null) {
            return;
        }
        if (this.locationType == 4) {
            if (this.addressName != null) {
                address = this.addressName;
            } else if ((this.customLocation == null && this.gpsLocation == null) || this.fetchingLocation) {
                address = LocaleController.getString("Loading", R.string.Loading);
            } else if (this.customLocation != null) {
                address = String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude())});
            } else if (this.gpsLocation != null) {
                address = String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(this.gpsLocation.getLatitude()), Double.valueOf(this.gpsLocation.getLongitude())});
            } else {
                address = LocaleController.getString("Loading", R.string.Loading);
            }
            this.sendLocationCell.setText(LocaleController.getString("ChatSetThisLocation", R.string.ChatSetThisLocation), address);
        } else if (this.customLocation != null) {
            sendLocationCell2.setText(LocaleController.getString("SendSelectedLocation", R.string.SendSelectedLocation), String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude())}));
        } else if (this.gpsLocation != null) {
            sendLocationCell2.setText(LocaleController.getString("SendLocation", R.string.SendLocation), LocaleController.formatString("AccurateTo", R.string.AccurateTo, LocaleController.formatPluralString("Meters", this.gpsLocation.getGpsAccuracyStatus())));
        } else {
            sendLocationCell2.setText(LocaleController.getString("SendLocation", R.string.SendLocation), LocaleController.getString("Loading", R.string.Loading));
        }
    }

    private String getAddressName() {
        return this.addressName;
    }

    public void onLocationAddressAvailable(String address, String displayAddress, BDLocation location) {
    }

    public void fetchLocationAddress() {
        BDLocation location;
        if (this.locationType == 4) {
            if (this.customLocation != null) {
                location = this.customLocation;
            } else if (this.gpsLocation != null) {
                location = this.gpsLocation;
            } else {
                return;
            }
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng preLatLng = new LatLng(this.previousFetchedLocation.getLatitude(), this.previousFetchedLocation.getLongitude());
            if (this.previousFetchedLocation == null || DistanceUtil.getDistance(newLatLng, preLatLng) > 100.0d) {
                this.addressName = null;
            }
            updateCell();
            this.fetchingLocation = true;
        }
    }

    public int getItemCount() {
        int i = this.locationType;
        if (i == 5 || i == 4) {
            return 2;
        }
        if (this.currentMessageObject != null) {
            return (this.currentLiveLocations.isEmpty() ? 0 : this.currentLiveLocations.size() + 2) + 2;
        } else if (i == 2) {
            return this.currentLiveLocations.size() + 2;
        } else {
            if (this.searching || (!this.searching && this.places.isEmpty())) {
                if (this.locationType != 0) {
                    return 5;
                }
                return 4;
            } else if (this.locationType == 1) {
                return this.places.size() + 4 + (true ^ this.places.isEmpty() ? 1 : 0);
            } else {
                return this.places.size() + 3 + (true ^ this.places.isEmpty() ? 1 : 0);
            }
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new EmptyCell(this.mContext);
                break;
            case 1:
                view = new SendLocationCell(this.mContext, false);
                break;
            case 2:
                view = new GraySectionCell(this.mContext);
                break;
            case 3:
                view = new LocationCell(this.mContext);
                break;
            case 4:
                view = new LocationLoadingCell(this.mContext);
                break;
            case 5:
                view = new LocationPoweredCell(this.mContext);
                break;
            case 6:
                SendLocationCell cell = new SendLocationCell(this.mContext, true);
                cell.setDialogId(this.dialogId);
                view = cell;
                break;
            default:
                Context context = this.mContext;
                int i = this.locationType;
                view = new SharingLiveLocationCell(context, true, (i == 4 || i == 5) ? 16 : 54);
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    public void setPulledUp() {
        if (!this.pulledUp) {
            this.pulledUp = true;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    LocationActivityAdapter.this.lambda$setPulledUp$0$LocationActivityAdapter();
                }
            });
        }
    }

    public /* synthetic */ void lambda$setPulledUp$0$LocationActivityAdapter() {
        notifyItemChanged(this.locationType == 0 ? 2 : 3);
    }

    public boolean isPulledUp() {
        return this.pulledUp;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType != 0) {
            boolean z = true;
            if (itemViewType == 1) {
                this.sendLocationCell = (SendLocationCell) holder.itemView;
                updateCell();
            } else if (itemViewType != 2) {
                if (itemViewType != 3) {
                    if (itemViewType == 4) {
                        ((LocationLoadingCell) holder.itemView).setLoading(this.searching);
                    } else if (itemViewType == 6) {
                        SendLocationCell sendLocationCell2 = (SendLocationCell) holder.itemView;
                        if (this.gpsLocation == null) {
                            z = false;
                        }
                        sendLocationCell2.setHasLocation(z);
                    } else if (itemViewType == 7) {
                        SharingLiveLocationCell locationCell = (SharingLiveLocationCell) holder.itemView;
                        TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
                        if (tL_channelLocation != null) {
                            locationCell.setDialog(this.dialogId, tL_channelLocation);
                        } else {
                            MessageObject messageObject = this.currentMessageObject;
                        }
                    }
                } else if (this.locationType == 0) {
                    ((LocationCell) holder.itemView).setLocation((TLRPC.TL_messageMediaVenue) this.places.get(position - 3), (String) this.iconUrls.get(position - 3), true);
                } else {
                    ((LocationCell) holder.itemView).setLocation((TLRPC.TL_messageMediaVenue) this.places.get(position - 4), (String) this.iconUrls.get(position - 4), true);
                }
            } else if (this.currentMessageObject != null) {
                ((GraySectionCell) holder.itemView).setText(LocaleController.getString("LiveLocations", R.string.LiveLocations));
            } else if (this.pulledUp) {
                ((GraySectionCell) holder.itemView).setText(LocaleController.getString("NearbyPlaces", R.string.NearbyPlaces));
            } else {
                ((GraySectionCell) holder.itemView).setText(LocaleController.getString("ShowNearbyPlaces", R.string.ShowNearbyPlaces));
            }
        } else {
            ((EmptyCell) holder.itemView).setHeight(this.overScrollHeight);
        }
    }

    public Object getItem(int i) {
        int i2 = this.locationType;
        if (i2 != 4) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null) {
                if (i == 1) {
                    return messageObject;
                }
                if (i > 3 && i < this.places.size() + 3) {
                    return this.currentLiveLocations.get(i - 4);
                }
            } else if (i2 == 2) {
                if (i >= 2) {
                    return this.currentLiveLocations.get(i - 2);
                }
                return null;
            } else if (i2 == 1) {
                if (i > 3 && i < this.places.size() + 4) {
                    return this.places.get(i - 4);
                }
            } else if (i > 2 && i < this.places.size() + 3) {
                return this.places.get(i - 3);
            }
            return null;
        } else if (this.addressName == null) {
            return null;
        } else {
            TLRPC.TL_messageMediaVenue venue = new TLRPC.TL_messageMediaVenue();
            venue.address = this.addressName;
            venue.geo = new TLRPC.TL_geoPoint();
            if (this.customLocation != null) {
                venue.geo.lat = this.customLocation.getLatitude();
                venue.geo._long = this.customLocation.getLongitude();
            } else if (this.gpsLocation != null) {
                venue.geo.lat = this.gpsLocation.getLatitude();
                venue.geo._long = this.gpsLocation.getLongitude();
            }
            return venue;
        }
    }

    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        int i = this.locationType;
        if (i == 5) {
            return 7;
        }
        if (i == 4) {
            return 1;
        }
        if (this.currentMessageObject != null) {
            if (position == 2) {
                return 2;
            }
            if (position != 3) {
                return 7;
            }
            this.shareLiveLocationPotistion = position;
            return 6;
        } else if (i != 2) {
            if (i == 1) {
                if (position == 1) {
                    return 1;
                }
                if (position == 2) {
                    this.shareLiveLocationPotistion = position;
                    return 6;
                } else if (position == 3) {
                    return 2;
                } else {
                    if (this.searching || (!this.searching && this.places.isEmpty())) {
                        return 4;
                    }
                    if (position == this.places.size() + 4) {
                        return 5;
                    }
                }
            } else if (position == 1) {
                return 1;
            } else {
                if (position == 2) {
                    return 2;
                }
                if (this.searching || (!this.searching && this.places.isEmpty())) {
                    return 4;
                }
                if (position == this.places.size() + 3) {
                    return 5;
                }
            }
            return 3;
        } else if (position != 1) {
            return 7;
        } else {
            this.shareLiveLocationPotistion = position;
            return 6;
        }
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int viewType = holder.getItemViewType();
        if (viewType == 6) {
            if (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null && this.gpsLocation == null) {
                return false;
            }
            return true;
        } else if (viewType == 1 || viewType == 3 || viewType == 7) {
            return true;
        } else {
            return false;
        }
    }
}
