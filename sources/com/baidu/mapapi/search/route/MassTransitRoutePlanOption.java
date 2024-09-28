package com.baidu.mapapi.search.route;

public class MassTransitRoutePlanOption {
    public String mCoordType = "bd09ll";
    public PlanNode mFrom = null;
    public int mPageIndex = 1;
    public int mPageSize = 10;
    public TacticsIncity mTacticsIncity = TacticsIncity.ETRANS_SUGGEST;
    public TacticsIntercity mTacticsIntercity = TacticsIntercity.ETRANS_LEAST_TIME;
    public PlanNode mTo = null;
    public TransTypeIntercity mTransTypeIntercity = TransTypeIntercity.ETRANS_TRAIN_FIRST;

    public enum TacticsIncity {
        ETRANS_SUGGEST(0),
        ETRANS_LEAST_TRANSFER(1),
        ETRANS_LEAST_WALK(2),
        ETRANS_NO_SUBWAY(3),
        ETRANS_LEAST_TIME(4),
        ETRANS_SUBWAY_FIRST(5);
        
        private int a;

        private TacticsIncity(int i) {
            this.a = 0;
            this.a = i;
        }

        public int getInt() {
            return this.a;
        }
    }

    public enum TacticsIntercity {
        ETRANS_LEAST_TIME(0),
        ETRANS_START_EARLY(1),
        ETRANS_LEAST_PRICE(2);
        
        private int a;

        private TacticsIntercity(int i) {
            this.a = 0;
            this.a = i;
        }

        public int getInt() {
            return this.a;
        }
    }

    public enum TransTypeIntercity {
        ETRANS_TRAIN_FIRST(0),
        ETRANS_PLANE_FIRST(1),
        ETRANS_COACH_FIRST(2);
        
        private int a;

        private TransTypeIntercity(int i) {
            this.a = 0;
            this.a = i;
        }

        public int getInt() {
            return this.a;
        }
    }

    @Deprecated
    public MassTransitRoutePlanOption coordType(String str) {
        this.mCoordType = str;
        return this;
    }

    public MassTransitRoutePlanOption from(PlanNode planNode) {
        this.mFrom = planNode;
        return this;
    }

    public MassTransitRoutePlanOption pageIndex(int i) {
        if (i >= 0 && i <= 2147483646) {
            this.mPageIndex = i + 1;
        }
        return this;
    }

    public MassTransitRoutePlanOption pageSize(int i) {
        if (i >= 1 && i <= 10) {
            this.mPageSize = i;
        }
        return this;
    }

    public MassTransitRoutePlanOption tacticsIncity(TacticsIncity tacticsIncity) {
        this.mTacticsIncity = tacticsIncity;
        return this;
    }

    public MassTransitRoutePlanOption tacticsIntercity(TacticsIntercity tacticsIntercity) {
        this.mTacticsIntercity = tacticsIntercity;
        return this;
    }

    public MassTransitRoutePlanOption to(PlanNode planNode) {
        this.mTo = planNode;
        return this;
    }

    public MassTransitRoutePlanOption transtypeintercity(TransTypeIntercity transTypeIntercity) {
        this.mTransTypeIntercity = transTypeIntercity;
        return this;
    }
}
