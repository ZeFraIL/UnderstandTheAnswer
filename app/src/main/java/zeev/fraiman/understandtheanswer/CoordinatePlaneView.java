package zeev.fraiman.understandtheanswer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

public class CoordinatePlaneView extends View {

    public interface OnPointTapListener {
        void onPointTapped(int gridX, int gridY);
    }

    public interface OnPointDragListener {
        void onPointDragged(int index, int gridX, int gridY);
    }

    private static final int MIN_VAL = -15;
    private static final int MAX_VAL = 15;
    private static final int TOTAL_UNITS = MAX_VAL - MIN_VAL; // 30 units
    private static final double FORBIDDEN_RADIUS = 2.0;

    private int pointX = 3;
    private int pointY = -2;
    private boolean pointVisible = true;

    private boolean isGridVisible = true;
    private boolean isAxisValuesVisible = true;

    private boolean showForbiddenWarning = false;
    private final Runnable hideWarningRunnable = () -> {
        showForbiddenWarning = false;
        invalidate();
    };

    private final List<Point> hintPoints = new ArrayList<>();

    private OnPointTapListener onPointTapListener;
    private OnPointDragListener onPointDragListener;

    private int draggedHintIndex = -1;
    private float startTouchX;
    private float startTouchY;

    private Paint bgPaint;
    private Paint gridPaint;
    private Paint majorGridPaint;
    private Paint axisPaint;
    private Paint arrowPaint;
    private Paint textPaint;
    private Paint pointPaint;
    private Paint pointBorderPaint;
    private Paint hintPointPaint;
    private Paint hintTextPaint;
    private Paint labelPaint;
    private Paint forbiddenFillPaint;
    private Paint forbiddenStrokePaint;

    private Path arrowPath;

    // Cache calculation parameters
    private float cachedCenterX;
    private float cachedCenterY;
    private float cachedStep;

    public CoordinatePlaneView(Context context) {
        super(context);
        init();
    }

    public CoordinatePlaneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CoordinatePlaneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);
        bgPaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#E2E8F0")); // Light grey
        gridPaint.setStrokeWidth(dpToPx(1f));
        gridPaint.setStyle(Paint.Style.STROKE);

        majorGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        majorGridPaint.setColor(Color.parseColor("#CBD5E1")); // Darker grey for 5s
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

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor("#D32F2F")); // Bright red
        pointPaint.setStyle(Paint.Style.FILL);

        pointBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointBorderPaint.setColor(Color.WHITE);
        pointBorderPaint.setStrokeWidth(dpToPx(2f));
        pointBorderPaint.setStyle(Paint.Style.STROKE);

        hintPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hintPointPaint.setColor(Color.parseColor("#0284C7")); // Distinct blue for hints
        hintPointPaint.setStyle(Paint.Style.FILL);

        hintTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hintTextPaint.setColor(Color.parseColor("#0369A1"));
        hintTextPaint.setTextSize(spToPx(12f));
        hintTextPaint.setFakeBoldText(true);

        forbiddenFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        forbiddenFillPaint.setColor(Color.parseColor("#33FF1744")); // Translucent red
        forbiddenFillPaint.setStyle(Paint.Style.FILL);

        forbiddenStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        forbiddenStrokePaint.setColor(Color.parseColor("#FF1744")); // Solid red stroke
        forbiddenStrokePaint.setStrokeWidth(dpToPx(1.5f));
        forbiddenStrokePaint.setStyle(Paint.Style.STROKE);

        arrowPath = new Path();
    }

    public void setOnPointTapListener(OnPointTapListener listener) {
        this.onPointTapListener = listener;
    }

    public void setOnPointDragListener(OnPointDragListener listener) {
        this.onPointDragListener = listener;
    }

    public void setPoint(int x, int y) {
        this.pointX = Math.max(MIN_VAL, Math.min(MAX_VAL, x));
        this.pointY = Math.max(MIN_VAL, Math.min(MAX_VAL, y));
        this.pointVisible = true;
        clearHintPoints();
        invalidate();
    }

    public boolean addHintPoint(int x, int y) {
        double dist = Math.hypot(x - pointX, y - pointY);
        if (dist <= FORBIDDEN_RADIUS) {
            triggerForbiddenWarning();
            return false;
        }

        if (hintPoints.size() >= 2) {
            return false;
        }

        hintPoints.add(new Point(x, y));
        invalidate();
        return true;
    }

    public boolean isTooCloseToTarget(int x, int y) {
        boolean tooClose = Math.hypot(x - pointX, y - pointY) <= FORBIDDEN_RADIUS;
        if (tooClose) {
            triggerForbiddenWarning();
        }
        return tooClose;
    }

    public void triggerForbiddenWarning() {
        showForbiddenWarning = true;
        removeCallbacks(hideWarningRunnable);
        postDelayed(hideWarningRunnable, 1200);
        invalidate();
    }

    public void clearHintPoints() {
        hintPoints.clear();
        draggedHintIndex = -1;
        invalidate();
    }

    public int getHintPointsCount() {
        return hintPoints.size();
    }

    public boolean isGridVisible() {
        return isGridVisible;
    }

    public void setGridVisible(boolean gridVisible) {
        this.isGridVisible = gridVisible;
        invalidate();
    }

    public boolean isAxisValuesVisible() {
        return isAxisValuesVisible;
    }

    public void setAxisValuesVisible(boolean axisValuesVisible) {
        this.isAxisValuesVisible = axisValuesVisible;
        invalidate();
    }

    public int getPointX() {
        return pointX;
    }

    public int getPointY() {
        return pointY;
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
                draggedHintIndex = findHintPointNear(touchX, touchY);

                if (draggedHintIndex != -1) {
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    return true;
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (draggedHintIndex != -1 && cachedStep > 0) {
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                    int gridX = Math.round((touchX - cachedCenterX) / cachedStep);
                    int gridY = Math.round((cachedCenterY - touchY) / cachedStep);

                    gridX = Math.max(MIN_VAL, Math.min(MAX_VAL, gridX));
                    gridY = Math.max(MIN_VAL, Math.min(MAX_VAL, gridY));

                    if (isTooCloseToTarget(gridX, gridY)) {
                        triggerForbiddenWarning();
                    } else {
                        Point hp = hintPoints.get(draggedHintIndex);
                        if (hp.x != gridX || hp.y != gridY) {
                            hp.x = gridX;
                            hp.y = gridY;
                            invalidate();

                            if (onPointDragListener != null) {
                                onPointDragListener.onPointDragged(draggedHintIndex, gridX, gridY);
                            }
                        }
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                if (draggedHintIndex != -1) {
                    draggedHintIndex = -1;
                    return true;
                }

                // If wasn't dragging, check for tap gesture
                float distMoved = (float) Math.hypot(touchX - startTouchX, touchY - startTouchY);
                if (distMoved < dpToPx(10f) && cachedStep > 0 && onPointTapListener != null) {
                    int gridX = Math.round((touchX - cachedCenterX) / cachedStep);
                    int gridY = Math.round((cachedCenterY - touchY) / cachedStep);

                    if (gridX >= MIN_VAL && gridX <= MAX_VAL && gridY >= MIN_VAL && gridY <= MAX_VAL) {
                        onPointTapListener.onPointTapped(gridX, gridY);
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                draggedHintIndex = -1;
                break;
        }

        return super.onTouchEvent(event);
    }

    private int findHintPointNear(float touchX, float touchY) {
        float touchRadius = dpToPx(28f); // 28dp touch radius around point
        for (int i = 0; i < hintPoints.size(); i++) {
            Point hp = hintPoints.get(i);
            float hx = cachedCenterX + hp.x * cachedStep;
            float hy = cachedCenterY - hp.y * cachedStep;

            if (Math.hypot(touchX - hx, touchY - hy) <= touchRadius) {
                return i;
            }
        }
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

        // 1. Draw Grid Lines (-15 to +15)
        if (isGridVisible) {
            for (int i = MIN_VAL; i <= MAX_VAL; i++) {
                float x = cachedCenterX + i * cachedStep;
                float y = cachedCenterY - i * cachedStep;

                Paint p = (i % 5 == 0) ? majorGridPaint : gridPaint;

                // Vertical grid line
                canvas.drawLine(x, gridTop, x, gridBottom, p);

                // Horizontal grid line
                canvas.drawLine(gridLeft, y, gridRight, y, p);
            }
        }

        // 2. Draw Axes (Black)
        canvas.drawLine(gridLeft, cachedCenterY, gridRight, cachedCenterY, axisPaint);
        canvas.drawLine(cachedCenterX, gridBottom, cachedCenterX, gridTop, axisPaint);

        // 3. Draw Arrows
        float arrowSize = dpToPx(8f);

        // Right arrow (X axis end)
        arrowPath.reset();
        arrowPath.moveTo(gridRight + arrowSize, cachedCenterY);
        arrowPath.lineTo(gridRight - arrowSize / 2f, cachedCenterY - arrowSize / 1.5f);
        arrowPath.lineTo(gridRight - arrowSize / 2f, cachedCenterY + arrowSize / 1.5f);
        arrowPath.close();
        canvas.drawPath(arrowPath, arrowPaint);

        // Top arrow (Y axis end)
        arrowPath.reset();
        arrowPath.moveTo(cachedCenterX, gridTop - arrowSize);
        arrowPath.lineTo(cachedCenterX - arrowSize / 1.5f, gridTop + arrowSize / 2f);
        arrowPath.lineTo(cachedCenterX + arrowSize / 1.5f, gridTop + arrowSize / 2f);
        arrowPath.close();
        canvas.drawPath(arrowPath, arrowPaint);

        // 4. Axis Labels 'X' and 'Y'
        canvas.drawText("X", gridRight + arrowSize + dpToPx(4f), cachedCenterY + dpToPx(5f), labelPaint);
        canvas.drawText("Y", cachedCenterX - dpToPx(5f), gridTop - arrowSize - dpToPx(4f), labelPaint);

        // 5. Number Marks (-15, -10, -5, 5, 10, 15)
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

        // 6. Draw Forbidden Warning Zone (Translucent Red Circle) if triggered
        if (showForbiddenWarning && pointVisible) {
            float px = cachedCenterX + pointX * cachedStep;
            float py = cachedCenterY - pointY * cachedStep;
            float forbiddenPxRadius = (float) (FORBIDDEN_RADIUS * cachedStep);

            canvas.drawCircle(px, py, forbiddenPxRadius, forbiddenFillPaint);
            canvas.drawCircle(px, py, forbiddenPxRadius, forbiddenStrokePaint);
        }

        // 7. Draw Hint Points
        float hintRadius = dpToPx(6.5f);
        for (int i = 0; i < hintPoints.size(); i++) {
            Point hp = hintPoints.get(i);
            float hx = cachedCenterX + hp.x * cachedStep;
            float hy = cachedCenterY - hp.y * cachedStep;

            // Highlight dragged point slightly larger
            float curRadius = (i == draggedHintIndex) ? dpToPx(9f) : hintRadius;

            canvas.drawCircle(hx, hy, curRadius, hintPointPaint);
            canvas.drawCircle(hx, hy, curRadius, pointBorderPaint);

            String label = "H" + (i + 1) + "(" + hp.x + ", " + hp.y + ")";
            canvas.drawText(label, hx + dpToPx(8f), hy - dpToPx(5f), hintTextPaint);
        }

        // 8. Draw Target Point A
        if (pointVisible) {
            float px = cachedCenterX + pointX * cachedStep;
            float py = cachedCenterY - pointY * cachedStep;

            float pointRadius = dpToPx(7.5f);

            canvas.drawCircle(px, py, pointRadius, pointPaint);
            canvas.drawCircle(px, py, pointRadius, pointBorderPaint);

            labelPaint.setTextSize(spToPx(13f));
            canvas.drawText("A", px + dpToPx(9f), py - dpToPx(7f), labelPaint);
        }
    }

    private float dpToPx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    private float spToPx(float sp) {
        return sp * getContext().getResources().getDisplayMetrics().scaledDensity;
    }
}
