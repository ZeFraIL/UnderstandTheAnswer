package zeev.fraiman.understandtheanswer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LinearFunctionView extends View {

    public enum Mode {
        PARALLEL_LINE,
        TWO_POINTS,
        POINTS_ON_LINE
    }

    public interface OnLineChangeListener {
        void onParallelLineShifted(double hintB);
        void onTwoPointsChanged(int x1, int y1, int x2, int y2, double k, double b);
        void onPointRevealed(int count, int x, int y);
        void onForbiddenZoneTriggered();
    }

    private static final int MIN_VAL = -15;
    private static final int MAX_VAL = 15;
    private static final int TOTAL_UNITS = MAX_VAL - MIN_VAL; // 30 units
    private static final double MIN_PARALLEL_DIST = 2.0;

    // Target Line Parameters: y = k0 * x + b0
    private double targetK = 2.0;
    private double targetB = -1.0;

    private Mode currentMode = Mode.PARALLEL_LINE;

    // Mode 1: Parallel Line shift
    private double parallelB = 4.0;
    private boolean isDraggingParallel = false;

    // Mode 2: Two user points
    private final Point userP1 = new Point(-3, -2);
    private final Point userP2 = new Point(4, 5);
    private int draggedUserPoint = -1;

    // Mode 3: Revealed points on target line
    private final List<Point> revealedPoints = new ArrayList<>();

    private boolean isGridVisible = true;
    private boolean isAxisValuesVisible = true;

    private boolean showForbiddenWarning = false;
    private final Runnable hideWarningRunnable = () -> {
        showForbiddenWarning = false;
        invalidate();
    };

    private OnLineChangeListener onLineChangeListener;

    private Paint bgPaint;
    private Paint gridPaint;
    private Paint majorGridPaint;
    private Paint axisPaint;
    private Paint arrowPaint;
    private Paint textPaint;
    private Paint labelPaint;

    private Paint targetLinePaint;
    private Paint parallelLinePaint;
    private Paint userLinePaint;
    private Paint userPointPaint;
    private Paint revealedPointPaint;
    private Paint pointBorderPaint;
    private Paint formulaTextPaint;
    private Paint forbiddenFillPaint;
    private Paint forbiddenStrokePaint;

    private Path arrowPath;
    private Path forbiddenPath;

    // Cached grid geometry
    private float cachedCenterX;
    private float cachedCenterY;
    private float cachedStep;

    private float startTouchX;
    private float startTouchY;

    public LinearFunctionView(Context context) {
        super(context);
        init();
    }

    public LinearFunctionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinearFunctionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);
        bgPaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#E2E8F0"));
        gridPaint.setStrokeWidth(dpToPx(1f));
        gridPaint.setStyle(Paint.Style.STROKE);

        majorGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        majorGridPaint.setColor(Color.parseColor("#CBD5E1"));
        majorGridPaint.setStrokeWidth(dpToPx(1.5f));
        majorGridPaint.setStyle(Paint.Style.STROKE);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(dpToPx(2.5f));
        axisPaint.setStyle(Paint.Style.STROKE);

        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setColor(Color.BLACK);
        arrowPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#334155"));
        textPaint.setTextSize(spToPx(11f));
        textPaint.setTextAlign(Paint.Align.CENTER);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(spToPx(15f));
        labelPaint.setFakeBoldText(true);

        targetLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetLinePaint.setColor(Color.parseColor("#1D4ED8")); // Vibrant Blue
        targetLinePaint.setStrokeWidth(dpToPx(3.5f));
        targetLinePaint.setStyle(Paint.Style.STROKE);

        parallelLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        parallelLinePaint.setColor(Color.parseColor("#D97706")); // Amber/Orange
        parallelLinePaint.setStrokeWidth(dpToPx(2.5f));
        parallelLinePaint.setStyle(Paint.Style.STROKE);

        userLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userLinePaint.setColor(Color.parseColor("#16A34A")); // Green
        userLinePaint.setStrokeWidth(dpToPx(2.5f));
        userLinePaint.setStyle(Paint.Style.STROKE);
        userLinePaint.setPathEffect(new DashPathEffect(new float[]{dpToPx(8f), dpToPx(4f)}, 0));

        userPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userPointPaint.setColor(Color.parseColor("#15803D"));
        userPointPaint.setStyle(Paint.Style.FILL);

        revealedPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        revealedPointPaint.setColor(Color.parseColor("#7C3AED")); // Purple
        revealedPointPaint.setStyle(Paint.Style.FILL);

        pointBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointBorderPaint.setColor(Color.WHITE);
        pointBorderPaint.setStrokeWidth(dpToPx(2f));
        pointBorderPaint.setStyle(Paint.Style.STROKE);

        formulaTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        formulaTextPaint.setColor(Color.parseColor("#B45309"));
        formulaTextPaint.setTextSize(spToPx(13f));
        formulaTextPaint.setFakeBoldText(true);

        forbiddenFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        forbiddenFillPaint.setColor(Color.parseColor("#33FF1744"));
        forbiddenFillPaint.setStyle(Paint.Style.FILL);

        forbiddenStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        forbiddenStrokePaint.setColor(Color.parseColor("#FF1744"));
        forbiddenStrokePaint.setStrokeWidth(dpToPx(1.5f));
        forbiddenStrokePaint.setStyle(Paint.Style.STROKE);

        arrowPath = new Path();
        forbiddenPath = new Path();
    }

    public void setTargetFunction(double k, double b) {
        this.targetK = k;
        this.targetB = b;

        if (Math.abs(parallelB - targetB) < MIN_PARALLEL_DIST) {
            parallelB = targetB + 4.0;
            if (parallelB > MAX_VAL) parallelB = targetB - 4.0;
        }

        revealedPoints.clear();
        invalidate();
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
        invalidate();
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    public void setOnLineChangeListener(OnLineChangeListener listener) {
        this.onLineChangeListener = listener;
    }

    public Point getUserP1() { return userP1; }
    public Point getUserP2() { return userP2; }
    public List<Point> getRevealedPoints() { return revealedPoints; }

    public double getTargetK() { return targetK; }
    public double getTargetB() { return targetB; }

    public void setGridVisible(boolean gridVisible) {
        this.isGridVisible = gridVisible;
        invalidate();
    }

    public void setAxisValuesVisible(boolean axisValuesVisible) {
        this.isAxisValuesVisible = axisValuesVisible;
        invalidate();
    }

    public void triggerForbiddenWarning() {
        showForbiddenWarning = true;
        removeCallbacks(hideWarningRunnable);
        postDelayed(hideWarningRunnable, 1200);
        invalidate();

        if (onLineChangeListener != null) {
            onLineChangeListener.onForbiddenZoneTriggered();
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                startTouchX = touchX;
                startTouchY = touchY;

                if (currentMode == Mode.PARALLEL_LINE) {
                    isDraggingParallel = isTouchNearParallelLine(touchX, touchY);
                    if (isDraggingParallel && getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                } else if (currentMode == Mode.TWO_POINTS) {
                    draggedUserPoint = findNearUserPoint(touchX, touchY);
                    if (draggedUserPoint != -1 && getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (currentMode == Mode.PARALLEL_LINE && isDraggingParallel && cachedStep > 0) {
                    if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);

                    double requestedB = (cachedCenterY - touchY) / cachedStep;
                    requestedB = Math.max(MIN_VAL, Math.min(MAX_VAL, Math.round(requestedB * 2.0) / 2.0));

                    if (Math.abs(requestedB - targetB) < MIN_PARALLEL_DIST) {
                        triggerForbiddenWarning();
                    } else {
                        parallelB = requestedB;
                        invalidate();
                        if (onLineChangeListener != null) {
                            onLineChangeListener.onParallelLineShifted(parallelB);
                        }
                    }
                    return true;
                } else if (currentMode == Mode.TWO_POINTS && draggedUserPoint != -1 && cachedStep > 0) {
                    if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);

                    int gridX = Math.round((touchX - cachedCenterX) / cachedStep);
                    int gridY = Math.round((cachedCenterY - touchY) / cachedStep);

                    gridX = Math.max(MIN_VAL, Math.min(MAX_VAL, gridX));
                    gridY = Math.max(MIN_VAL, Math.min(MAX_VAL, gridY));

                    Point pt = (draggedUserPoint == 1) ? userP1 : userP2;
                    Point other = (draggedUserPoint == 1) ? userP2 : userP1;

                    if (gridX != other.x && (pt.x != gridX || pt.y != gridY)) {
                        pt.x = gridX;
                        pt.y = gridY;
                        invalidate();

                        if (onLineChangeListener != null) {
                            double k = (double) (userP2.y - userP1.y) / (userP2.x - userP1.x);
                            double b = userP1.y - k * userP1.x;
                            onLineChangeListener.onTwoPointsChanged(userP1.x, userP1.y, userP2.x, userP2.y, k, b);
                        }
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(false);

                if (isDraggingParallel) {
                    isDraggingParallel = false;
                    return true;
                }
                if (draggedUserPoint != -1) {
                    draggedUserPoint = -1;
                    return true;
                }

                float distMoved = (float) Math.hypot(touchX - startTouchX, touchY - startTouchY);
                if (distMoved < dpToPx(10f) && currentMode == Mode.POINTS_ON_LINE && cachedStep > 0) {
                    int gridX = Math.round((touchX - cachedCenterX) / cachedStep);
                    double lineY = targetK * gridX + targetB;

                    if (Math.abs(lineY - Math.round(lineY)) < 0.001 && Math.abs(lineY) <= MAX_VAL) {
                        int gridY = (int) Math.round(lineY);

                        boolean exists = false;
                        for (Point p : revealedPoints) {
                            if (p.x == gridX && p.y == gridY) { exists = true; break; }
                        }

                        if (!exists && revealedPoints.size() < 4) {
                            revealedPoints.add(new Point(gridX, gridY));
                            invalidate();

                            if (onLineChangeListener != null) {
                                onLineChangeListener.onPointRevealed(revealedPoints.size(), gridX, gridY);
                            }
                        }
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(false);
                isDraggingParallel = false;
                draggedUserPoint = -1;
                break;
        }

        return super.onTouchEvent(event);
    }

    private boolean isTouchNearParallelLine(float touchX, float touchY) {
        if (cachedStep <= 0) return false;
        int gridX = Math.round((touchX - cachedCenterX) / cachedStep);
        double lineY = targetK * gridX + parallelB;
        float linePxY = cachedCenterY - (float) lineY * cachedStep;
        return Math.abs(touchY - linePxY) <= dpToPx(28f);
    }

    private int findNearUserPoint(float touchX, float touchY) {
        float radius = dpToPx(28f);

        float p1x = cachedCenterX + userP1.x * cachedStep;
        float p1y = cachedCenterY - userP1.y * cachedStep;
        if (Math.hypot(touchX - p1x, touchY - p1y) <= radius) return 1;

        float p2x = cachedCenterX + userP2.x * cachedStep;
        float p2y = cachedCenterY - userP2.y * cachedStep;
        if (Math.hypot(touchX - p2x, touchY - p2y) <= radius) return 2;

        return -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(width, height > 0 ? height : width);
        if (size == 0) size = (int) dpToPx(300);

        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // White background
        canvas.drawRect(0, 0, width, height, bgPaint);

        float padding = dpToPx(28f);
        float availableWidth = width - 2 * padding;
        float availableHeight = height - 2 * padding;

        cachedStep = Math.min(availableWidth, availableHeight) / TOTAL_UNITS;
        cachedCenterX = padding + (availableWidth / 2f);
        cachedCenterY = padding + (availableHeight / 2f);

        float gridLeft = cachedCenterX + MIN_VAL * cachedStep;
        float gridRight = cachedCenterX + MAX_VAL * cachedStep;
        float gridTop = cachedCenterY - MAX_VAL * cachedStep;
        float gridBottom = cachedCenterY - MIN_VAL * cachedStep;

        // 1. Draw Grid Lines
        if (isGridVisible) {
            for (int i = MIN_VAL; i <= MAX_VAL; i++) {
                float x = cachedCenterX + i * cachedStep;
                float y = cachedCenterY - i * cachedStep;

                Paint p = (i % 5 == 0) ? majorGridPaint : gridPaint;
                canvas.drawLine(x, gridTop, x, gridBottom, p);
                canvas.drawLine(gridLeft, y, gridRight, y, p);
            }
        }

        // 2. Draw Axes
        canvas.drawLine(gridLeft, cachedCenterY, gridRight, cachedCenterY, axisPaint);
        canvas.drawLine(cachedCenterX, gridBottom, cachedCenterX, gridTop, axisPaint);

        // 3. Draw Arrows
        float arrowSize = dpToPx(8f);
        arrowPath.reset();
        arrowPath.moveTo(gridRight + arrowSize, cachedCenterY);
        arrowPath.lineTo(gridRight - arrowSize / 2f, cachedCenterY - arrowSize / 1.5f);
        arrowPath.lineTo(gridRight - arrowSize / 2f, cachedCenterY + arrowSize / 1.5f);
        arrowPath.close();
        canvas.drawPath(arrowPath, arrowPaint);

        arrowPath.reset();
        arrowPath.moveTo(cachedCenterX, gridTop - arrowSize);
        arrowPath.lineTo(cachedCenterX - arrowSize / 1.5f, gridTop + arrowSize / 2f);
        arrowPath.lineTo(cachedCenterX + arrowSize / 1.5f, gridTop + arrowSize / 2f);
        arrowPath.close();
        canvas.drawPath(arrowPath, arrowPaint);

        // 4. Axis Labels 'X' and 'Y'
        canvas.drawText("X", gridRight + arrowSize + dpToPx(4f), cachedCenterY + dpToPx(5f), labelPaint);
        canvas.drawText("Y", cachedCenterX - dpToPx(5f), gridTop - arrowSize - dpToPx(4f), labelPaint);

        // 5. Number Marks
        if (isAxisValuesVisible) {
            for (int i = MIN_VAL; i <= MAX_VAL; i += 5) {
                if (i == 0) continue;

                float x = cachedCenterX + i * cachedStep;
                canvas.drawText(String.valueOf(i), x, cachedCenterY + dpToPx(16f), textPaint);

                float y = cachedCenterY - i * cachedStep;
                textPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(String.valueOf(i), cachedCenterX - dpToPx(6f), y + dpToPx(4f), textPaint);
                textPaint.setTextAlign(Paint.Align.CENTER);
            }

            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("0", cachedCenterX - dpToPx(5f), cachedCenterY + dpToPx(14f), textPaint);
            textPaint.setTextAlign(Paint.Align.CENTER);
        }

        // 6. Draw Target Line y = targetK * x + targetB
        drawLineFunction(canvas, targetK, targetB, targetLinePaint, gridLeft, gridRight, gridTop, gridBottom);

        // 7. Draw Forbidden Warning Corridor if triggered
        if (showForbiddenWarning && currentMode == Mode.PARALLEL_LINE) {
            double warnUpperB = targetB + MIN_PARALLEL_DIST;
            double warnLowerB = targetB - MIN_PARALLEL_DIST;

            forbiddenPath.reset();
            float xStart = MIN_VAL;
            float xEnd = MAX_VAL;

            float pxStart = cachedCenterX + xStart * cachedStep;
            float pxEnd = cachedCenterX + xEnd * cachedStep;

            float pyUpperStart = cachedCenterY - (float)(targetK * xStart + warnUpperB) * cachedStep;
            float pyUpperEnd = cachedCenterY - (float)(targetK * xEnd + warnUpperB) * cachedStep;

            float pyLowerStart = cachedCenterY - (float)(targetK * xStart + warnLowerB) * cachedStep;
            float pyLowerEnd = cachedCenterY - (float)(targetK * xEnd + warnLowerB) * cachedStep;

            forbiddenPath.moveTo(pxStart, pyUpperStart);
            forbiddenPath.lineTo(pxEnd, pyUpperEnd);
            forbiddenPath.lineTo(pxEnd, pyLowerEnd);
            forbiddenPath.lineTo(pxStart, pyLowerStart);
            forbiddenPath.close();

            canvas.drawPath(forbiddenPath, forbiddenFillPaint);
            canvas.drawLine(pxStart, pyUpperStart, pxEnd, pyUpperEnd, forbiddenStrokePaint);
            canvas.drawLine(pxStart, pyLowerStart, pxEnd, pyLowerEnd, forbiddenStrokePaint);
        }

        // 8. Mode Specific Drawing
        if (currentMode == Mode.PARALLEL_LINE) {
            // Draw Parallel Line: y = targetK * x + parallelB
            drawLineFunction(canvas, targetK, parallelB, parallelLinePaint, gridLeft, gridRight, gridTop, gridBottom);

            // Formula label above parallel line
            String formula = formatFunctionFormula(targetK, parallelB);
            float midX = cachedCenterX;
            float midY = cachedCenterY - (float)(targetK * 0 + parallelB) * cachedStep - dpToPx(8f);
            canvas.drawText(formula, midX, midY, formulaTextPaint);

        } else if (currentMode == Mode.TWO_POINTS) {
            // Draw User Line through P1 and P2
            if (userP1.x != userP2.x) {
                double uk = (double)(userP2.y - userP1.y) / (userP2.x - userP1.x);
                double ub = userP1.y - uk * userP1.x;
                drawLineFunction(canvas, uk, ub, userLinePaint, gridLeft, gridRight, gridTop, gridBottom);
            }

            // Draw P1 and P2
            drawPoint(canvas, userP1.x, userP1.y, "P1", userPointPaint);
            drawPoint(canvas, userP2.x, userP2.y, "P2", userPointPaint);

        } else if (currentMode == Mode.POINTS_ON_LINE) {
            // Draw Revealed Points
            for (int i = 0; i < revealedPoints.size(); i++) {
                Point p = revealedPoints.get(i);
                drawPoint(canvas, p.x, p.y, "M" + (i + 1) + "(" + p.x + "," + p.y + ")", revealedPointPaint);
            }
        }
    }

    private void drawLineFunction(Canvas canvas, double k, double b, Paint paint,
                                  float gridLeft, float gridRight, float gridTop, float gridBottom) {
        float x1 = MIN_VAL;
        float y1 = (float) (k * x1 + b);

        float x2 = MAX_VAL;
        float y2 = (float) (k * x2 + b);

        float px1 = cachedCenterX + x1 * cachedStep;
        float py1 = cachedCenterY - y1 * cachedStep;

        float px2 = cachedCenterX + x2 * cachedStep;
        float py2 = cachedCenterY - y2 * cachedStep;

        canvas.save();
        canvas.clipRect(gridLeft, gridTop, gridRight, gridBottom);
        canvas.drawLine(px1, py1, px2, py2, paint);
        canvas.restore();
    }

    private void drawPoint(Canvas canvas, int x, int y, String label, Paint paint) {
        float px = cachedCenterX + x * cachedStep;
        float py = cachedCenterY - y * cachedStep;
        float radius = dpToPx(7f);

        canvas.drawCircle(px, py, radius, paint);
        canvas.drawCircle(px, py, radius, pointBorderPaint);

        labelPaint.setTextSize(spToPx(12f));
        canvas.drawText(label, px + dpToPx(8f), py - dpToPx(6f), labelPaint);
    }

    private String formatFunctionFormula(double k, double b) {
        StringBuilder sb = new StringBuilder("y = ");
        if (k == 1.0) {
            sb.append("x");
        } else if (k == -1.0) {
            sb.append("-x");
        } else if (k == Math.floor(k)) {
            sb.append((int) k).append("x");
        } else {
            sb.append(String.format(Locale.US, "%.1f", k)).append("x");
        }

        if (b > 0) {
            sb.append(" + ");
            if (b == Math.floor(b)) sb.append((int) b);
            else sb.append(String.format(Locale.US, "%.1f", b));
        } else if (b < 0) {
            sb.append(" - ");
            double absB = Math.abs(b);
            if (absB == Math.floor(absB)) sb.append((int) absB);
            else sb.append(String.format(Locale.US, "%.1f", absB));
        }

        return sb.toString();
    }

    private float dpToPx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    private float spToPx(float sp) {
        return sp * getContext().getResources().getDisplayMetrics().scaledDensity;
    }
}
