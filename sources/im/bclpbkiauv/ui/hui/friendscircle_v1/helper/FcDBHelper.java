package im.bclpbkiauv.ui.hui.friendscircle_v1.helper;

import com.bjz.comm.net.bean.FcReplyBean;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.socks.library.KLog;
import im.bclpbkiauv.javaBean.fc.FollowedFcListBean;
import im.bclpbkiauv.javaBean.fc.HomeFcListBean;
import im.bclpbkiauv.javaBean.fc.RecommendFcListBean;
import im.bclpbkiauv.messenger.ApplicationLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FcDBHelper {
    private static FcDBHelper Instance = new FcDBHelper();
    private static final String TAG = FcDBHelper.class.getSimpleName();
    private LiteOrm fcLiteOrm;

    private FcDBHelper() {
        if (this.fcLiteOrm == null) {
            synchronized (FcDBHelper.class) {
                if (this.fcLiteOrm == null) {
                    this.fcLiteOrm = LiteOrm.newCascadeInstance(ApplicationLoader.applicationContext, "User_Fc.db");
                }
            }
            this.fcLiteOrm.setDebugged(true);
        }
    }

    public static FcDBHelper getInstance() {
        return Instance;
    }

    public void init(ApplicationLoader applicationLoader) {
        if (this.fcLiteOrm == null) {
            synchronized (FcDBHelper.class) {
                if (this.fcLiteOrm == null) {
                    this.fcLiteOrm = LiteOrm.newCascadeInstance(applicationLoader, "User_Fc.db");
                }
            }
            this.fcLiteOrm.setDebugged(true);
        }
    }

    public <T> long insert(T t) {
        return this.fcLiteOrm.save((Object) t);
    }

    public <T> void insertAll(List<T> list) {
        if (list != null && list.size() > 0) {
            this.fcLiteOrm.save(list);
        }
    }

    public <T> List<T> getQueryAll(Class<T> cla) {
        return this.fcLiteOrm.query(cla);
    }

    public <T> ArrayList getQueryByOrder(Class<T> cla) {
        return this.fcLiteOrm.query(new QueryBuilder(cla).appendOrderDescBy("ForumID"));
    }

    public <T> List<T> getQueryByWhere(Class<T> cla, String field, String[] value) {
        LiteOrm liteOrm = this.fcLiteOrm;
        QueryBuilder queryBuilder = new QueryBuilder(cla);
        return liteOrm.query(queryBuilder.where(field + "=?", value));
    }

    public <T> ArrayList<T> queryFcListByUserId(Class<T> cla, long userId) {
        QueryBuilder queryBuilder = new QueryBuilder(cla);
        return this.fcLiteOrm.query(queryBuilder.where("CreateBy = " + userId, new Object[0]).appendOrderDescBy("ForumID"));
    }

    public <T> T queryItemById(Class<T> cla, long forumId) {
        return this.fcLiteOrm.queryById(forumId, cla);
    }

    public <T> List<T> getQueryByWhereLength(Class<T> cla, String field, String[] value, int start, int length) {
        LiteOrm liteOrm = this.fcLiteOrm;
        QueryBuilder queryBuilder = new QueryBuilder(cla);
        return liteOrm.query(queryBuilder.where(field + "=?", value).limit(start, length));
    }

    public <T> void updateItemPermissionStatus(Class<T> cla, long forumId, int permission) {
        WhereBuilder mQueryBuilder = new WhereBuilder(cla).where("ForumID=?", Long.valueOf(forumId));
        HashMap<String, Object> replaceValue = new HashMap<>();
        replaceValue.put("Permission", Integer.valueOf(permission));
        int update = this.fcLiteOrm.update(mQueryBuilder, new ColumnsValue((Map<String, Object>) replaceValue), ConflictAlgorithm.Fail);
    }

    public <T> void updateItemFollowStatus(Class<T> cla, long userId, boolean isFollow) {
        WhereBuilder mQueryBuilder = new WhereBuilder(cla).where("CreateBy=?", Long.valueOf(userId));
        HashMap<String, Object> replaceValue = new HashMap<>();
        replaceValue.put("HasFollow", Boolean.valueOf(isFollow));
        int update = this.fcLiteOrm.update(mQueryBuilder, new ColumnsValue((Map<String, Object>) replaceValue), ConflictAlgorithm.Fail);
    }

    public <T> void updateItemLikeStatus(Class<T> cla, long forumId, boolean isLike, int ThumbUp) {
        WhereBuilder mQueryBuilder = new WhereBuilder(cla).where("ForumID=?", Long.valueOf(forumId));
        HashMap<String, Object> replaceValue = new HashMap<>();
        replaceValue.put("HasThumb", Boolean.valueOf(isLike));
        replaceValue.put("ThumbUp", Integer.valueOf(ThumbUp));
        int update = this.fcLiteOrm.update(mQueryBuilder, new ColumnsValue((Map<String, Object>) replaceValue), ConflictAlgorithm.Fail);
    }

    public <T> void updateItemReply(Class<T> cla, long forumId, FcReplyBean mFcReplyBean) {
        T queryData = queryItemById(cla, forumId);
        if (queryData != null) {
            if (queryData instanceof HomeFcListBean) {
                HomeFcListBean updateData = (HomeFcListBean) queryData;
                ArrayList<FcReplyBean> comments = updateData.getComments();
                if (comments != null) {
                    comments.add(mFcReplyBean);
                }
                updateData.setCommentCount(updateData.getCommentCount() + 1);
                this.fcLiteOrm.save((Object) updateData);
            } else if (queryData instanceof RecommendFcListBean) {
                RecommendFcListBean updateData2 = (RecommendFcListBean) queryData;
                ArrayList<FcReplyBean> comments2 = updateData2.getComments();
                if (comments2 != null) {
                    comments2.add(mFcReplyBean);
                }
                updateData2.setCommentCount(updateData2.getCommentCount() + 1);
                this.fcLiteOrm.save((Object) updateData2);
            } else if (queryData instanceof FollowedFcListBean) {
                FollowedFcListBean updateData3 = (FollowedFcListBean) queryData;
                ArrayList<FcReplyBean> comments3 = updateData3.getComments();
                if (comments3 != null) {
                    comments3.add(mFcReplyBean);
                }
                updateData3.setCommentCount(updateData3.getCommentCount() + 1);
                this.fcLiteOrm.save((Object) updateData3);
            }
        }
    }

    public <T> void delete(T t) {
        this.fcLiteOrm.delete((Object) t);
    }

    public <T> void deleteItemById(Class<T> cla, long id) {
        int back = this.fcLiteOrm.delete(new WhereBuilder(cla).where("ForumID=?", Long.valueOf(id)));
        String str = TAG;
        KLog.d(str, "deleteItemById()-------删除结果" + back);
    }

    public <T> void deleteItemByUserId(Class<T> cla, long userId) {
        int back = this.fcLiteOrm.delete(new WhereBuilder(cla).where("CreateBy=?", Long.valueOf(userId)));
        String str = TAG;
        KLog.d(str, "deleteItemById()-------删除结果" + back);
    }

    public <T> void deleteReply(Class<T> cla, long forumId, long commentID, int commentCount) {
        long j = commentID;
        WhereBuilder mQueryBuilder = new WhereBuilder(cla).where("ForumID=?", Long.valueOf(forumId));
        HashMap<String, Object> replaceValue = new HashMap<>();
        replaceValue.put("CommentCount", Integer.valueOf(commentCount));
        int update = this.fcLiteOrm.update(mQueryBuilder, new ColumnsValue((Map<String, Object>) replaceValue), ConflictAlgorithm.Fail);
        LiteOrm liteOrm = this.fcLiteOrm;
        WhereBuilder whereBuilder = new WhereBuilder(FcReplyBean.class);
        int back1 = liteOrm.delete(whereBuilder.where("CommentID = " + j, new Object[0]));
        String str = TAG;
        KLog.d(str, "deleteReply()------------删除用户评论" + back1);
        LiteOrm liteOrm2 = this.fcLiteOrm;
        WhereBuilder whereBuilder2 = new WhereBuilder(FcReplyBean.class);
        int back2 = liteOrm2.delete(whereBuilder2.where("SupID = " + j, new Object[0]));
        String str2 = TAG;
        KLog.d(str2, "deleteReply()------------删除用户评论" + back2);
    }

    public <T> void delete(Class<T> cla) {
        this.fcLiteOrm.delete(cla);
    }

    public <T> void deleteList(List<T> list) {
        this.fcLiteOrm.delete(list);
    }

    public <T> void deleteAll(Class<T> var1) {
        this.fcLiteOrm.deleteAll(var1);
    }

    public void deleteDatabase() {
        this.fcLiteOrm.deleteDatabase();
    }
}
