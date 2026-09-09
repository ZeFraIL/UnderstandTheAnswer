package zeev.fraiman.understandtheanswer;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ParabolaFunctionFragment extends Fragment {

    private double targetA = 1.0;
    private double targetB = -2.0;
    private double targetC = -3.0;

    private ParabolaFunctionView parabolaFunctionView;
    private EditText etA;
    private EditText etB;
    private EditText etC;
    private TextView tvFeedback;
    private MaterialCardView cardTopCalcInfo;
    private TextView tvTopCalcText;
    private MaterialButton btnToggleTemplateA;
    private Random random;

    private final double[] availableA = {1.0, -1.0, 2.0, -2.0, 0.5, -0.5};
    private int templateAIndex = 0;

    public ParabolaFunctionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parabola_function, container, false);

        random = new Random();

        parabolaFunctionView = view.findViewById(R.id.parabolaFunctionView);
        etA = view.findViewById(R.id.etA);
        etB = view.findViewById(R.id.etB);
        etC = view.findViewById(R.id.etC);
        tvFeedback = view.findViewById(R.id.tvFeedback);

        cardTopCalcInfo = view.findViewById(R.id.cardTopCalcInfo);
        tvTopCalcText = view.findViewById(R.id.tvTopCalcText);
        btnToggleTemplateA = view.findViewById(R.id.btnToggleTemplateA);

        MaterialSwitch switchGrid = view.findViewById(R.id.switchGrid);
        MaterialSwitch switchAxisValues = view.findViewById(R.id.switchAxisValues);

        ChipGroup chipGroupMode = view.findViewById(R.id.chipGroupMode);

        Button btnCheck = view.findViewById(R.id.btnCheck);
        Button btnNewTask = view.findViewById(R.id.btnNewTask);

        if (cardTopCalcInfo != null) {
            cardTopCalcInfo.setOnClickListener(v -> {
                if (parabolaFunctionView == null) return;
                List<Point> points = parabolaFunctionView.getRevealedPoints();
                if (points.size() >= 2) {
                    showInteractiveSystemDialog(points);
                }
            });
        }

        if (btnToggleTemplateA != null) {
            btnToggleTemplateA.setOnClickListener(v -> {
                templateAIndex = (templateAIndex + 1) % availableA.length;
                double newA = availableA[templateAIndex];
                if (parabolaFunctionView != null) {
                    parabolaFunctionView.setTemplateA(newA);
                }
                btnToggleTemplateA.setText(String.format(Locale.US, "a = %.1f 🔄", newA));
            });
        }

        if (parabolaFunctionView != null) {
            parabolaFunctionView.setOnParabolaChangeListener(new ParabolaFunctionView.OnParabolaChangeListener() {
                @Override
                public void onTemplateChanged(double a, int x0, int y0) {
                    String msg = String.format(Locale.getDefault(), "📍 Шаблон вершины: (%d, %d), a = %.1f", x0, y0, a);
                    tvFeedback.setText(msg);
                }

                @Override
                public void onAxisMoved(int xAxis, boolean isMatched) {
                    String msg = "📍 Ось симметрии: x = " + xAxis;
                    tvFeedback.setText(msg);

                    if (cardTopCalcInfo != null && tvTopCalcText != null) {
                        if (isMatched) {
                            tvTopCalcText.setText(String.format(Locale.getDefault(),
                                    "🎯 Совпадение! Ось x = %d → x0 = -b/(2a) = %d", xAxis, xAxis));
                        } else {
                            tvTopCalcText.setText("📍 Передвигайте ось x = " + xAxis + " до совпадения с графиком");
                        }
                        cardTopCalcInfo.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPointRevealed(int count, int x, int y) {
                    String msg = String.format(Locale.getDefault(), "📍 Выявлена точка M%d(%d, %d) на параболе!", count, x, y);
                    tvFeedback.setText(msg);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }

                    List<Point> points = parabolaFunctionView.getRevealedPoints();
                    if (points.size() >= 2) {
                        if (cardTopCalcInfo != null && tvTopCalcText != null) {
                            tvTopCalcText.setText("📍 Выявлены " + points.size() + " точки на параболе\n💡 (Нажмите для вычисления систем уравнений)");
                            cardTopCalcInfo.setVisibility(View.VISIBLE);
                        }

                        if (points.size() == 3) {
                            showInteractiveSystemDialog(points);
                        }
                    }
                }

                @Override
                public void onForbiddenZoneTriggered() {
                    if (getContext() != null) {
                        String msg = getString(R.string.msg_parabola_vertex_too_close);
                        tvFeedback.setText(msg);
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (chipGroupMode != null) {
            chipGroupMode.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty() || parabolaFunctionView == null) return;

                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipParabolaTransformer) {
                    parabolaFunctionView.setMode(ParabolaFunctionView.Mode.TRANSFORMER);
                    if (btnToggleTemplateA != null) btnToggleTemplateA.setVisibility(View.VISIBLE);
                    if (cardTopCalcInfo != null) cardTopCalcInfo.setVisibility(View.GONE);
                } else if (checkedId == R.id.chipParabolaAxis) {
                    parabolaFunctionView.setMode(ParabolaFunctionView.Mode.AXIS_OF_SYMMETRY);
                    if (btnToggleTemplateA != null) btnToggleTemplateA.setVisibility(View.GONE);
                    if (cardTopCalcInfo != null) cardTopCalcInfo.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.chipParabolaPoints) {
                    parabolaFunctionView.setMode(ParabolaFunctionView.Mode.POINTS_ON_PARABOLA);
                    if (btnToggleTemplateA != null) btnToggleTemplateA.setVisibility(View.GONE);
                    if (cardTopCalcInfo != null) {
                        List<Point> points = parabolaFunctionView.getRevealedPoints();
                        if (points.size() >= 2) {
                            tvTopCalcText.setText("📍 Выявлены " + points.size() + " точки на параболе\n💡 (Нажмите для решения системы)");
                            cardTopCalcInfo.setVisibility(View.VISIBLE);
                        } else {
                            cardTopCalcInfo.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        if (switchGrid != null) {
            switchGrid.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (parabolaFunctionView != null) {
                    parabolaFunctionView.setGridVisible(isChecked);
                }
            });
        }

        if (switchAxisValues != null) {
            switchAxisValues.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (parabolaFunctionView != null) {
                    parabolaFunctionView.setAxisValuesVisible(isChecked);
                }
            });
        }

        btnCheck.setOnClickListener(v -> checkAnswer());
        btnNewTask.setOnClickListener(v -> generateNewTask());

        generateNewTask();

        return view;
    }

    private void generateNewTask() {
        targetA = availableA[random.nextInt(availableA.length)];
        int vX = random.nextInt(11) - 5; // Vertex X in [-5, 5]
        int vY = random.nextInt(11) - 5; // Vertex Y in [-5, 5]

        targetB = -2.0 * targetA * vX;
        targetC = vY + targetA * vX * vX;

        if (parabolaFunctionView != null) {
            parabolaFunctionView.setTargetFunction(targetA, targetB, targetC);
        }

        if (etA != null) etA.setText("");
        if (etB != null) etB.setText("");
        if (etC != null) etC.setText("");
        if (tvFeedback != null) tvFeedback.setText(R.string.hint_parabola_placeholder);

        if (cardTopCalcInfo != null) cardTopCalcInfo.setVisibility(View.GONE);
    }

    private void checkAnswer() {
        String aExpr = etA.getText().toString().trim();
        String bExpr = etB.getText().toString().trim();
        String cExpr = etC.getText().toString().trim();

        if (TextUtils.isEmpty(aExpr) || TextUtils.isEmpty(bExpr) || TextUtils.isEmpty(cExpr)) {
            String msg = getString(R.string.msg_enter_abc);
            tvFeedback.setText(msg);
            if (getContext() != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double inputA = MathExpressionEvaluator.evaluate(aExpr);
            double inputB = MathExpressionEvaluator.evaluate(bExpr);
            double inputC = MathExpressionEvaluator.evaluate(cExpr);

            String msg;
            if (Math.abs(inputA - targetA) < 0.01 && Math.abs(inputB - targetB) < 0.01 && Math.abs(inputC - targetC) < 0.01) {
                msg = getString(R.string.msg_parabola_correct);
            } else {
                msg = getString(R.string.msg_parabola_incorrect);
            }
            tvFeedback.setText(msg);
            if (getContext() != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            String msg = "⚠️ Ошибка вычисления: " + e.getMessage();
            tvFeedback.setText(msg);
            if (getContext() != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showInteractiveSystemDialog(List<Point> points) {
        if (getContext() == null || points == null || points.isEmpty()) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_parabola_calc, null);
        TextView tvPointsHeader = dialogView.findViewById(R.id.tvPointsHeader);
        TextView tvSystemHeader = dialogView.findViewById(R.id.tvSystemHeader);

        EditText etDialogA = dialogView.findViewById(R.id.etDialogA);
        EditText etDialogB = dialogView.findViewById(R.id.etDialogB);
        EditText etDialogC = dialogView.findViewById(R.id.etDialogC);

        TextView tvEvalA = dialogView.findViewById(R.id.tvEvalA);
        TextView tvEvalB = dialogView.findViewById(R.id.tvEvalB);
        TextView tvEvalC = dialogView.findViewById(R.id.tvEvalC);
        TextView tvDialogFeedback = dialogView.findViewById(R.id.tvDialogFeedback);

        StringBuilder sbPoints = new StringBuilder("Выявленные точки:\n");
        StringBuilder sbSystem = new StringBuilder("Система уравнений (a·x² + b·x + c = y):\n");

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            sbPoints.append(String.format(Locale.getDefault(), "M%d(%d, %d) ", i + 1, p.x, p.y));
            sbSystem.append(String.format(Locale.getDefault(), "%d) a·(%d)² + b·(%d) + c = %d\n", i + 1, p.x, p.x, p.y));
        }

        if (tvPointsHeader != null) tvPointsHeader.setText(sbPoints.toString());
        if (tvSystemHeader != null) tvSystemHeader.setText(sbSystem.toString().trim());

        if (etDialogA != null && tvEvalA != null) {
            etDialogA.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) { updateEvalPreview(s.toString(), tvEvalA); }
            });
        }

        if (etDialogB != null && tvEvalB != null) {
            etDialogB.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) { updateEvalPreview(s.toString(), tvEvalB); }
            });
        }

        if (etDialogC != null && tvEvalC != null) {
            etDialogC.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) { updateEvalPreview(s.toString(), tvEvalC); }
            });
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_parabola_calc_title)
                .setView(dialogView)
                .setPositiveButton(R.string.btn_check, null)
                .setNeutralButton(R.string.btn_show_solution, (dialogInterface, which) -> showParabolaSolutionDialog(points))
                .setNegativeButton(R.string.btn_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setOnClickListener(v -> {
                String aExpr = (etDialogA != null) ? etDialogA.getText().toString().trim() : "";
                String bExpr = (etDialogB != null) ? etDialogB.getText().toString().trim() : "";
                String cExpr = (etDialogC != null) ? etDialogC.getText().toString().trim() : "";

                if (TextUtils.isEmpty(aExpr) || TextUtils.isEmpty(bExpr) || TextUtils.isEmpty(cExpr)) {
                    if (tvDialogFeedback != null) tvDialogFeedback.setText(R.string.msg_enter_abc);
                    return;
                }

                try {
                    double inputA = MathExpressionEvaluator.evaluate(aExpr);
                    double inputB = MathExpressionEvaluator.evaluate(bExpr);
                    double inputC = MathExpressionEvaluator.evaluate(cExpr);

                    if (Math.abs(inputA - targetA) < 0.01 && Math.abs(inputB - targetB) < 0.01 && Math.abs(inputC - targetC) < 0.01) {
                        if (etA != null) etA.setText(String.format(Locale.US, "%.1f", targetA));
                        if (etB != null) etB.setText(String.format(Locale.US, "%.1f", targetB));
                        if (etC != null) etC.setText(String.format(Locale.US, "%.1f", targetC));

                        String msg = getString(R.string.msg_parabola_correct);
                        tvFeedback.setText(msg);
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        if (tvDialogFeedback != null) {
                            tvDialogFeedback.setText(R.string.msg_parabola_incorrect);
                        }
                    }
                } catch (Exception e) {
                    if (tvDialogFeedback != null) {
                        tvDialogFeedback.setText("⚠️ " + e.getMessage());
                    }
                }
            });
        });

        dialog.show();
    }

    private void updateEvalPreview(String expr, TextView targetTv) {
        if (TextUtils.isEmpty(expr)) {
            targetTv.setText("= ?");
            targetTv.setTextColor(Color.GRAY);
            return;
        }

        try {
            double res = MathExpressionEvaluator.evaluate(expr);
            targetTv.setText(String.format(Locale.US, "= %.2f", res));
            targetTv.setTextColor(Color.parseColor("#15803D"));
        } catch (Exception e) {
            targetTv.setText("⚠️ " + e.getMessage());
            targetTv.setTextColor(Color.parseColor("#D32F2F"));
        }
    }

    private void showParabolaSolutionDialog(List<Point> points) {
        if (getContext() == null) return;

        double vX = -targetB / (2.0 * targetA);
        double vY = targetC - (targetB * targetB) / (4.0 * targetA);

        String htmlContent = String.format(Locale.getDefault(),
                "<b>Параметры искомой параболы:</b><br>" +
                        "<b>y = %.1fx² %s %.1fx %s %.1f</b><br><br>" +
                        "<b>1. Координаты вершины (x₀, y₀):</b><br>" +
                        "x₀ = -b / (2a) = <b>%.1f</b><br>" +
                        "y₀ = c - b²/(4a) = <b>%.1f</b><br><br>" +
                        "<b>2. Выявленные точки:</b><br>" +
                        "M1(%d, %d), M2(%d, %d)...<br><br>" +
                        "<b>3. Подстановка в y = ax² + bx + c:</b><br>" +
                        "Каждая точка удовлетворяет уравнению параболы.",
                targetA, (targetB >= 0 ? "+" : "-"), Math.abs(targetB),
                (targetC >= 0 ? "+" : "-"), Math.abs(targetC),
                vX, vY,
                points.size() > 0 ? points.get(0).x : 0, points.size() > 0 ? points.get(0).y : 0,
                points.size() > 1 ? points.get(1).x : 0, points.size() > 1 ? points.get(1).y : 0);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_calc_title)
                .setMessage(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(R.string.btn_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .show();
    }
}
