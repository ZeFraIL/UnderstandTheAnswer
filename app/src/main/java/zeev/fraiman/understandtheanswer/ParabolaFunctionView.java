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

public class ParabolaFunctionView extends View {

    public enum Mode {
        TRANSFORMER,
        AXIS_OF_SYMMETRY,
        POINTS_ON_PARABOLA
    }

    public interface OnParabolaChangeListener {
        void onTemplateChanged(double a, int x0, int y0);
        void onAxisMoved(int xAxis, boolean isMatched);
        void onPointRevealed(int count, int x, int y);
        void onForbiddenZoneTriggered();
    }

    private static final int MIN_VAL = -15;
    private static final int MAX_VAL = 15;
    private static final int TOTAL_UNITS = MAX_VAL - MIN_VAL; // 30 units
    private static final double MIN_VERTEX_DIST = 2.0;

    // Target Parabola: y = targetA * x^2 + targetB * x + targetC
    private double targetA = 1.0;
    private double targetB = -2.0;
    private double targetC = -3.0;

    // Target Vertex Coordinates
    private double targetVertexX = 1.0;
    private double targetVertexY = -4.0;

    private Mode currentMode = Mode.TRANSFORMER;

    // Mode 1: Template Parabola y = templateA * (x - tmplX0)^2 + tmplY0
    private double templateA = 1.0;
    private int tmplX0 = -4;
    private int tmplY0 = 3;
    private boolean isDraggingTemplateVertex = false;

    // Mode 2: Axis of symmetry
    private int axisX = -2;
    private boolean isDraggingAxis = false;

    // Mode 3: Revealed points on target parabola
    private final List<Point> revealedPoints = new ArrayList<>();

    private boolean isGridVisible = true;
    private boolean isAxisValuesVisible = true;

    private boolean showForbiddenWarning = false;
    private final Runnable hideWarningRunnable = () -> {
        showForbiddenWarning = false;
        invalidate();
    };

    private OnParabolaChangeListener onParabolaChangeListener;

    private Paint bgPaint;
    private Paint gridPaint;
    private Paint majorGridPaint;
    private Paint axisPaint;
    private Paint arrowPaint;
    private Paint textPaint;
    private Paint labelPaint;

    private Paint targetLinePaint;
    private Paint templateLinePaint;
    private Paint axisLinePaint;
    private Paint pointPaint;
    private Paint revealedPointPaint;
    private Paint pointBorderPaint;
    private Paint formulaTextPaint;
    private Paint forbiddenFillPaint;
    private Paint forbiddenStrokePaint;

    private Path arrowPath;
    private Path parabolaPath;
    private Path forbiddenPath;

    // Cached grid geometry
    private float cachedCenterX;
    private float cachedCenterY;
    private float cachedStep;

    private float startTouchX;
    private float startTouchY;

    public ParabolaFunctionView(Context context) {
        super(context);
        init();
    }

    public ParabolaFunctionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParabolaFunctionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        templateLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        templateLinePaint.setColor(Color.parseColor("#D97706")); // Amber/Orange
        templateLinePaint.setStrokeWidth(dpToPx(2.5f));
        templateLinePaint.setStyle(Paint.Style.STROKE);

        axisLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisLinePaint.setColor(Color.parseColor("#DB2777")); // Magenta/Pink
        axisLinePaint.setStrokeWidth(dpToPx(2.5f));
        axisLinePaint.setStyle(Paint.Style.STROKE);
        axisLinePaint.setPathEffect(new DashPathEffect(new float[]{dpToPx(8f), dpToPx(4f)}, 0));

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor("#D97706"));
        pointPaint.setStyle(Paint.Style.FILL);

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
        parabolaPath = new Path();
        forbiddenPath = new Path();
    }

    public void setTargetFunction(double a, double b, double c) {
        this.targetA = a;
        this.targetB = b;
        this.targetC = c;

        this.targetVertexX = -b / (2.0 * a);
        this.targetVertexY = c - (b * b) / (4.0 * a);

        // Reset template vertex away from target vertex
        this.tmplX0 = (int) Math.round(targetVertexX) + 5;
        if (this.tmplX0 > MAX_VAL) this.tmplX0 = (int) Math.round(targetVertexX) - 5;
        this.tmplY0 = (int) Math.round(targetVertexY) + 4;
        if (this.tmplY0 > MAX_VAL) this.tmplY0 = (int) Math.round(targetVertexY) - 4;

        this.templateA = a; // Same curvature for easier vertex matching

        this.axisX = -5;
        revealedPoints.clear();
        invalidate();
    }

    public void setTemplateA(double a) {
        this.templateA = a;
        invalidate();
        if (onParabolaChangeListener != null) {
            onParabolaChangeListener.onTemplateChanged(templateA, tmplX0, tmplY0);
        }
    }

    public double getTemplateA() { return templateA; }

    public void setMode(Mode mode) {
        this.currentMode = mode;
        invalidate();
    }

    public Mode getCurrentMode() { return currentMode; }

    public void setOnParabolaChangeListener(OnParabolaChangeListener listener) {
        this.onParabolaChangeListener = listener;
    }

    public List<Point> getRevealedPoints() { return revealedPoints; }

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

        if (onParabolaChangeListener != null) {
            onParabolaChangeListener.onForbiddenZoneTriggered();
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

                if (currentMode == Mode.TRANSFORMER) {
                    float vPx = cachedCenterX + tmplX0 * cachedStep;
                    float vPy = cachedCenterY - tmplY0 * cachedStep;
                    if (Math.hypot(touchX - vPx, touchY - vPy) <= dpToPx(32f)) {
                        isDraggingTemplateVertex = true;
                        if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                } else if (currentMode == Mode.AXIS_OF_SYMMETRY) {
                    float axisPx = cachedCenterX + axisX * cachedStep;
                    if (Math.abs(touchX - axisPx) <= dpToPx(28f)) {
                        isDraggingAxis = true;
                        if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (currentMode == Mode.TRANSFORMER && isDraggingTemplateVertex && cachedStep > 0) {
                    if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);

                    int reqX = Math.round((touchX - cachedCenterX) / cachedStep);
                    int reqY = Math.round((cachedCenterY - touchY) / cachedStep);

                    reqX = Math.max(MIN_VAL, Math.min(MAX_VAL, reqX));
                    reqY = Math.max(MIN_VAL, Math.min(MAX_VAL, reqY));

                    double dist = Math.hypot(reqX - targetVertexX, reqY - targetVertexY);
                    if (dist < MIN_VERTEX_DIST) {
                        triggerForbiddenWarning();
                    } else {
                        tmplX0 = reqX;
                        tmplY0 = reqY;
                        invalidate();

                        if (onParabolaChangeListener != null) {
                            onParabolaChangeListener.onTemplateChanged(templateA, tmplX0, tmplY0);
                        }
                    }
                    return true;
                } else if (currentMode == Mode.AXIS_OF_SYMMETRY && isDraggingAxis && cachedStep > 0) {
                    if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);

                    int reqX = Math.round((touchX - cachedCenterX) / cachedStep);
                    reqX = Math.max(MIN_VAL, Math.min(MAX_VAL, reqX));

                    if (axisX != reqX) {
                        axisX = reqX;
                        invalidate();

                        boolean isMatched = Math.abs(axisX - targetVertexX) < 0.1;
                        if (onParabolaChangeListener != null) {
                            onParabolaChangeListener.onAxisMoved(axisX, isMatched);
                        }
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(false);

                if (isDraggingTemplateVertex) {
                    isDraggingTemplateVertex = false;
                    return true;
                }
                if (isDraggingAxis) {
                    isDraggingAxis = false;
                    return true;
                }

                float distMoved = (float) Math.hypot(touchX - startTouchX, touchY - startTouchY);
                if (distMoved < dpToPx(10f) && currentMode == Mode.POINTS_ON_PARABOLA && cachedStep > 0) {
                    int gridX = Math.round((touchX - cachedCenterX) / cachedStep);
                    double lineY = targetA * gridX * gridX + targetB * gridX + targetC;

                    if (Math.abs(lineY - Math.round(lineY)) < 0.001 && Math.abs(lineY) <= MAX_VAL) {
                        int gridY = (int) Math.round(lineY);

                        boolean exists = false;
                        for (Point p : revealedPoints) {
                            if (p.x == gridX && p.y == gridY) { exists = true; break; }
                        }

                        if (!exists && revealedPoints.size() < 3) {
                            revealedPoints.add(new Point(gridX, gridY));
                            invalidate();

                            if (onParabolaChangeListener != null) {
                                onParabolaChangeListener.onPointRevealed(revealedPoints.size(), gridX, gridY);
                            }
                        }
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(false);
                isDraggingTemplateVertex = false;
                isDraggingAxis = false;
                break;
        }

        return super.onTouchEvent(event);
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

        // 6. Draw Target Parabola
        drawParabola(canvas, targetA, targetB, targetC, targetLinePaint, gridLeft, gridRight, gridTop, gridBottom);

        // 7. Draw Forbidden Warning Zone around Target Vertex if triggered
        if (showForbiddenWarning && currentMode == Mode.TRANSFORMER) {
            float vPx = cachedCenterX + (float) targetVertexX * cachedStep;
            float vPy = cachedCenterY - (float) targetVertexY * cachedStep;
            float forbiddenPxRadius = (float) (MIN_VERTEX_DIST * cachedStep);

            canvas.drawCircle(vPx, vPy, forbiddenPxRadius, forbiddenFillPaint);
            canvas.drawCircle(vPx, vPy, forbiddenPxRadius, forbiddenStrokePaint);
        }

        // 8. Mode Specific Drawing
        if (currentMode == Mode.TRANSFORMER) {
            // Draw Template Parabola y = templateA * (x - tmplX0)^2 + tmplY0
            // Convert vertex form to standard form: y = templateA*x^2 - 2*templateA*tmplX0*x + templateA*tmplX0^2 + tmplY0
            double tmplB = -2.0 * templateA * tmplX0;
            double tmplC = templateA * tmplX0 * tmplX0 + tmplY0;
            drawParabola(canvas, templateA, tmplB, tmplC, templateLinePaint, gridLeft, gridRight, gridTop, gridBottom);

            // Draw Template Vertex Point
            float tvPx = cachedCenterX + tmplX0 * cachedStep;
            float tvPy = cachedCenterY - tmplY0 * cachedStep;
            float radius = dpToPx(8f);

            canvas.drawCircle(tvPx, tvPy, radius, pointPaint);
            canvas.drawCircle(tvPx, tvPy, radius, pointBorderPaint);

            String formula = String.format(Locale.US, "y = %.1f(x %s %d)² %s %d",
                    templateA, (tmplX0 >= 0 ? "-" : "+"), Math.abs(tmplX0),
                    (tmplY0 >= 0 ? "+" : "-"), Math.abs(tmplY0));
            canvas.drawText(formula, tvPx + dpToPx(10f), tvPy - dpToPx(8f), formulaTextPaint);

        } else if (currentMode == Mode.AXIS_OF_SYMMETRY) {
            // Draw Vertical Axis of Symmetry
            float axisPx = cachedCenterX + axisX * cachedStep;
            canvas.save();
            canvas.clipRect(gridLeft, gridTop, gridRight, gridBottom);
            canvas.drawLine(axisPx, gridTop, axisPx, gridBottom, axisLinePaint);
            canvas.restore();

            String axisLabel = "x = " + axisX;
            canvas.drawText(axisLabel, axisPx + dpToPx(6f), gridTop + dpToPx(16f), formulaTextPaint);

        } else if (currentMode == Mode.POINTS_ON_PARABOLA) {
            // Draw Revealed Points
            for (int i = 0; i < revealedPoints.size(); i++) {
                Point p = revealedPoints.get(i);
                drawPoint(canvas, p.x, p.y, "M" + (i + 1) + "(" + p.x + "," + p.y + ")", revealedPointPaint);
            }
        }
    }

    private void drawParabola(Canvas canvas, double a, double b, double c, Paint paint,
                              float gridLeft, float gridRight, float gridTop, float gridBottom) {
        parabolaPath.reset();

        boolean first = true;
        float stepSize = 0.1f;

        for (float x = MIN_VAL; x <= MAX_VAL; x += stepSize) {
            float y = (float) (a * x * x + b * x + c);
            float px = cachedCenterX + x * cachedStep;
            float py = cachedCenterY - y * cachedStep;

            if (first) {
                parabolaPath.moveTo(px, py);
                first = false;
            } else {
                parabolaPath.lineTo(px, py);
            }
        }

        canvas.save();
        canvas.clipRect(gridLeft, gridTop, gridRight, gridBottom);
        canvas.drawPath(parabolaPath, paint);
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

    private float dpToPx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    private float spToPx(float sp) {
        return sp * getContext().getResources().getDisplayMetrics().scaledDensity;
    }
}
