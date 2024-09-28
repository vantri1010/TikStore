package im.bclpbkiauv.ui.components.paint;

import android.graphics.Matrix;
import android.view.MotionEvent;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.messenger.AndroidUtilities;
import java.util.Vector;

public class Input {
    private boolean beganDrawing;
    /* access modifiers changed from: private */
    public boolean clearBuffer;
    private boolean hasMoved;
    private Matrix invertMatrix;
    private boolean isFirst;
    private Point lastLocation;
    /* access modifiers changed from: private */
    public double lastRemainder;
    private Point[] points = new Point[3];
    private int pointsCount;
    private RenderView renderView;
    private float[] tempPoint = new float[2];

    public Input(RenderView render) {
        this.renderView = render;
    }

    public void setMatrix(Matrix m) {
        Matrix matrix = new Matrix();
        this.invertMatrix = matrix;
        m.invert(matrix);
    }

    public void process(MotionEvent event) {
        int action = event.getActionMasked();
        float x = event.getX();
        float y = ((float) this.renderView.getHeight()) - event.getY();
        float[] fArr = this.tempPoint;
        fArr[0] = x;
        fArr[1] = y;
        this.invertMatrix.mapPoints(fArr);
        float[] fArr2 = this.tempPoint;
        Point point = new Point((double) fArr2[0], (double) fArr2[1], 1.0d);
        if (action != 0) {
            if (action == 1) {
                if (!this.hasMoved) {
                    if (this.renderView.shouldDraw()) {
                        point.edge = true;
                        paintPath(new Path(point));
                    }
                    reset();
                } else if (this.pointsCount > 0) {
                    smoothenAndPaintPoints(true);
                }
                this.pointsCount = 0;
                this.renderView.getPainting().commitStroke(this.renderView.getCurrentColor());
                this.beganDrawing = false;
                this.renderView.onFinishedDrawing(this.hasMoved);
                return;
            } else if (action != 2) {
                return;
            }
        }
        if (!this.beganDrawing) {
            this.beganDrawing = true;
            this.hasMoved = false;
            this.isFirst = true;
            this.lastLocation = point;
            this.points[0] = point;
            this.pointsCount = 1;
            this.clearBuffer = true;
        } else if (point.getDistanceTo(this.lastLocation) >= ((float) AndroidUtilities.dp(5.0f))) {
            if (!this.hasMoved) {
                this.renderView.onBeganDrawing();
                this.hasMoved = true;
            }
            Point[] pointArr = this.points;
            int i = this.pointsCount;
            pointArr[i] = point;
            int i2 = i + 1;
            this.pointsCount = i2;
            if (i2 == 3) {
                smoothenAndPaintPoints(false);
            }
            this.lastLocation = point;
        }
    }

    private void reset() {
        this.pointsCount = 0;
    }

    private void smoothenAndPaintPoints(boolean ended) {
        int i = this.pointsCount;
        if (i > 2) {
            Vector<Point> points2 = new Vector<>();
            Point[] pointArr = this.points;
            Point prev2 = pointArr[0];
            Point prev1 = pointArr[1];
            Point cur = pointArr[2];
            if (cur == null || prev1 == null) {
                Vector<Point> vector = points2;
            } else if (prev2 == null) {
                Vector<Point> vector2 = points2;
            } else {
                Point midPoint1 = prev1.multiplySum(prev2, 0.5d);
                Point midPoint2 = cur.multiplySum(prev1, 0.5d);
                int numberOfSegments = (int) Math.min(48.0d, Math.max(Math.floor((double) (midPoint1.getDistanceTo(midPoint2) / ((float) 1))), 24.0d));
                float t = 0.0f;
                float step = 1.0f / ((float) numberOfSegments);
                for (int j = 0; j < numberOfSegments; j++) {
                    Point point = smoothPoint(midPoint1, midPoint2, prev1, t);
                    if (this.isFirst) {
                        point.edge = true;
                        this.isFirst = false;
                    }
                    points2.add(point);
                    t += step;
                }
                if (ended) {
                    midPoint2.edge = true;
                }
                points2.add(midPoint2);
                Point[] result = new Point[points2.size()];
                points2.toArray(result);
                paintPath(new Path(result));
                Point[] pointArr2 = this.points;
                Vector<Point> vector3 = points2;
                int i2 = numberOfSegments;
                System.arraycopy(pointArr2, 1, pointArr2, 0, 2);
                if (ended) {
                    this.pointsCount = 0;
                } else {
                    this.pointsCount = 2;
                }
            }
        } else {
            Point[] result2 = new Point[i];
            System.arraycopy(this.points, 0, result2, 0, i);
            paintPath(new Path(result2));
        }
    }

    private Point smoothPoint(Point midPoint1, Point midPoint2, Point prev1, float t) {
        Point point = midPoint1;
        Point point2 = midPoint2;
        Point point3 = prev1;
        double a1 = Math.pow((double) (1.0f - t), 2.0d);
        double a2 = (double) ((1.0f - t) * 2.0f * t);
        double a3 = (double) (t * t);
        double d = a1;
        return new Point((point.x * a1) + (point3.x * a2) + (point2.x * a3), (point.y * a1) + (point3.y * a2) + (point2.y * a3), 1.0d);
    }

    private void paintPath(final Path path) {
        path.setup(this.renderView.getCurrentColor(), this.renderView.getCurrentWeight(), this.renderView.getCurrentBrush());
        if (this.clearBuffer) {
            this.lastRemainder = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
        path.remainder = this.lastRemainder;
        this.renderView.getPainting().paintStroke(path, this.clearBuffer, new Runnable() {
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        double unused = Input.this.lastRemainder = path.remainder;
                        boolean unused2 = Input.this.clearBuffer = false;
                    }
                });
            }
        });
    }
}
