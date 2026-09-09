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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class LinearFunctionFragment extends Fragment {

    private double targetK = 2.0;
    private double targetB = -1.0;

    private LinearFunctionView linearFunctionView;
    private EditText etK;
    private EditText etB;
    private TextView tvFeedback;
    private MaterialCardView cardTopCalcInfo;
    private TextView tvTopCalcText;
    private Random random;

    public LinearFunctionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear_function, container, false);

        random = new Random();

        linearFunctionView = view.findViewById(R.id.linearFunctionView);
        etK = view.findViewById(R.id.etK);
        etB = view.findViewById(R.id.etB);
        tvFeedback = view.findViewById(R.id.tvFeedback);

        cardTopCalcInfo = view.findViewById(R.id.cardTopCalcInfo);
        tvTopCalcText = view.findViewById(R.id.tvTopCalcText);

        MaterialSwitch switchGrid = view.findViewById(R.id.switchGrid);
        MaterialSwitch switchAxisValues = view.findViewById(R.id.switchAxisValues);

        ChipGroup chipGroupMode = view.findViewById(R.id.chipGroupMode);

        Button btnCheck = view.findViewById(R.id.btnCheck);
        Button btnNewTask = view.findViewById(R.id.btnNewTask);

        if (cardTopCalcInfo != null) {
            cardTopCalcInfo.setOnClickListener(v -> {
                if (linearFunctionView == null) return;
                if (linearFunctionView.getCurrentMode() == LinearFunctionView.Mode.TWO_POINTS) {
                    showCalculationDialog();
                } else if (linearFunctionView.getCurrentMode() == LinearFunctionView.Mode.POINTS_ON_LINE) {
                    List<Point> points = linearFunctionView.getRevealedPoints();
                    if (points.size() >= 2) {
                        showInteractiveCalcDialog(points.get(0), points.get(1));
                    }
                }
            });
        }

        if (linearFunctionView != null) {
            linearFunctionView.setOnLineChangeListener(new LinearFunctionView.OnLineChangeListener() {
                @Override
                public void onParallelLineShifted(double hintB) {
                    String msg = String.format(Locale.getDefault(), "📍 Параллельная прямая: y = %.1fx %s %.1f",
                            targetK, (hintB >= 0 ? "+" : "-"), Math.abs(hintB));
                    tvFeedback.setText(msg);
                }

                @Override
                public void onTwoPointsChanged(int x1, int y1, int x2, int y2, double k, double b) {
                    String topMsg = String.format(Locale.getDefault(),
                            "📍 P1(%d, %d), P2(%d, %d) → y = %.2fx %s %.2f\n💡 (Нажмите для пошагового расчёта)",
                            x1, y1, x2, y2, k, (b >= 0 ? "+" : "-"), Math.abs(b));

                    if (tvTopCalcText != null) {
                        tvTopCalcText.setText(topMsg);
                    }

                    String feedbackMsg = String.format(Locale.getDefault(),
                            "📍 Линия по точкам: y = %.2fx %s %.2f", k, (b >= 0 ? "+" : "-"), Math.abs(b));
                    tvFeedback.setText(feedbackMsg);
                }

                @Override
                public void onPointRevealed(int count, int x, int y) {
                    String msg = String.format(Locale.getDefault(), "📍 Выявлена точка M%d(%d, %d) на искомом графике!", count, x, y);
                    tvFeedback.setText(msg);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }

                    List<Point> points = linearFunctionView.getRevealedPoints();
                    if (points.size() >= 2) {
                        Point p1 = points.get(0);
                        Point p2 = points.get(1);

                        if (cardTopCalcInfo != null && tvTopCalcText != null) {
                            String topMsg = String.format(Locale.getDefault(),
                                    "📍 Выявлены точки M1(%d, %d) и M2(%d, %d)\n💡 (Нажмите для расчёта k и b)",
                                    p1.x, p1.y, p2.x, p2.y);
                            tvTopCalcText.setText(topMsg);
                            cardTopCalcInfo.setVisibility(View.VISIBLE);
                        }

                        if (points.size() == 2) {
                            showInteractiveCalcDialog(p1, p2);
                        }
                    }
                }

                @Override
                public void onForbiddenZoneTriggered() {
                    if (getContext() != null) {
                        String msg = getString(R.string.msg_parallel_too_close);
                        tvFeedback.setText(msg);
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (chipGroupMode != null) {
            chipGroupMode.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty() || linearFunctionView == null) return;

                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipParallel) {
                    linearFunctionView.setMode(LinearFunctionView.Mode.PARALLEL_LINE);
                    if (cardTopCalcInfo != null) cardTopCalcInfo.setVisibility(View.GONE);
                } else if (checkedId == R.id.chipTwoPoints) {
                    linearFunctionView.setMode(LinearFunctionView.Mode.TWO_POINTS);
                    if (cardTopCalcInfo != null) {
                        cardTopCalcInfo.setVisibility(View.VISIBLE);
                        updateTopInfoForTwoPoints();
                    }
                } else if (checkedId == R.id.chipPointsOnLine) {
                    linearFunctionView.setMode(LinearFunctionView.Mode.POINTS_ON_LINE);
                    if (cardTopCalcInfo != null) {
                        List<Point> points = linearFunctionView.getRevealedPoints();
                        if (points.size() >= 2) {
                            Point p1 = points.get(0);
                            Point p2 = points.get(1);
                            String topMsg = String.format(Locale.getDefault(),
                                    "📍 Выявлены точки M1(%d, %d) и M2(%d, %d)\n💡 (Нажмите для расчёта k и b)",
                                    p1.x, p1.y, p2.x, p2.y);
                            tvTopCalcText.setText(topMsg);
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
                if (linearFunctionView != null) {
                    linearFunctionView.setGridVisible(isChecked);
                }
            });
        }

        if (switchAxisValues != null) {
            switchAxisValues.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (linearFunctionView != null) {
                    linearFunctionView.setAxisValuesVisible(isChecked);
                }
            });
        }

        btnCheck.setOnClickListener(v -> checkAnswer());
        btnNewTask.setOnClickListener(v -> generateNewTask());

        generateNewTask();

        return view;
    }

    private void updateTopInfoForTwoPoints() {
        if (linearFunctionView == null || tvTopCalcText == null) return;
        Point p1 = linearFunctionView.getUserP1();
        Point p2 = linearFunctionView.getUserP2();

        if (p1.x != p2.x) {
            double k = (double) (p2.y - p1.y) / (p2.x - p1.x);
            double b = p1.y - k * p1.x;
            String topMsg = String.format(Locale.getDefault(),
                    "📍 P1(%d, %d), P2(%d, %d) → y = %.2fx %s %.2f\n💡 (Нажмите для пошагового расчёта)",
                    p1.x, p1.y, p2.x, p2.y, k, (b >= 0 ? "+" : "-"), Math.abs(b));
            tvTopCalcText.setText(topMsg);
        }
    }

    private void generateNewTask() {
        double[] availableK = {-3.0, -2.0, -1.0, 1.0, 2.0, 3.0, 0.5, -0.5};
        targetK = availableK[random.nextInt(availableK.length)];
        targetB = random.nextInt(17) - 8; // Random B in [-8, 8]

        if (linearFunctionView != null) {
            linearFunctionView.setTargetFunction(targetK, targetB);
        }

        if (etK != null) etK.setText("");
        if (etB != null) etB.setText("");
        if (tvFeedback != null) tvFeedback.setText(R.string.hint_linear_placeholder);

        if (cardTopCalcInfo != null) {
            if (linearFunctionView != null && linearFunctionView.getCurrentMode() == LinearFunctionView.Mode.TWO_POINTS) {
                updateTopInfoForTwoPoints();
            } else {
                cardTopCalcInfo.setVisibility(View.GONE);
            }
        }
    }

    private void checkAnswer() {
        String kExpr = etK.getText().toString().trim();
        String bExpr = etB.getText().toString().trim();

        if (TextUtils.isEmpty(kExpr) || TextUtils.isEmpty(bExpr)) {
            String msg = getString(R.string.msg_enter_kb);
            tvFeedback.setText(msg);
            if (getContext() != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double inputK = MathExpressionEvaluator.evaluate(kExpr);
            double inputB = MathExpressionEvaluator.evaluate(bExpr);

            String msg;
            if (Math.abs(inputK - targetK) < 0.01 && Math.abs(inputB - targetB) < 0.01) {
                msg = getString(R.string.msg_linear_correct);
            } else {
                msg = getString(R.string.msg_linear_incorrect);
            }
            tvFeedback.setText(msg);
            if (getContext() != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            String msg = "⚠️ Ошибка вычисления: " + e.getMessage();
            tvFeedback.setText(msg);
            if (getContext() != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showInteractiveCalcDialog(Point p1, Point p2) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_points_calc, null);
        TextView tvPointsHeader = dialogView.findViewById(R.id.tvPointsHeader);
        EditText etDialogK = dialogView.findViewById(R.id.etDialogK);
        EditText etDialogB = dialogView.findViewById(R.id.etDialogB);
        TextView tvEvalK = dialogView.findViewById(R.id.tvEvalK);
        TextView tvEvalB = dialogView.findViewById(R.id.tvEvalB);
        TextView tvDialogFeedback = dialogView.findViewById(R.id.tvDialogFeedback);

        if (tvPointsHeader != null) {
            String header = String.format(Locale.getDefault(),
                    "Выявлены 2 точки на графике:\nM1(%d, %d)  и  M2(%d, %d)\nВведите выражения или значения:",
                    p1.x, p1.y, p2.x, p2.y);
            tvPointsHeader.setText(header);
        }

        if (etDialogK != null && tvEvalK != null) {
            etDialogK.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    updateEvalPreview(s.toString(), tvEvalK);
                }
            });
        }

        if (etDialogB != null && tvEvalB != null) {
            etDialogB.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    updateEvalPreview(s.toString(), tvEvalB);
                }
            });
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_interactive_calc_title)
                .setView(dialogView)
                .setPositiveButton(R.string.btn_check, null)
                .setNeutralButton(R.string.btn_show_solution, (dialogInterface, which) -> showCalculationDialogForPoints(p1, p2))
                .setNegativeButton(R.string.btn_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setOnClickListener(v -> {
                String kExpr = (etDialogK != null) ? etDialogK.getText().toString().trim() : "";
                String bExpr = (etDialogB != null) ? etDialogB.getText().toString().trim() : "";

                if (TextUtils.isEmpty(kExpr) || TextUtils.isEmpty(bExpr)) {
                    if (tvDialogFeedback != null) tvDialogFeedback.setText(R.string.msg_enter_kb);
                    return;
                }

                try {
                    double inputK = MathExpressionEvaluator.evaluate(kExpr);
                    double inputB = MathExpressionEvaluator.evaluate(bExpr);

                    if (Math.abs(inputK - targetK) < 0.01 && Math.abs(inputB - targetB) < 0.01) {
                        if (etK != null) etK.setText(String.format(Locale.US, "%.2f", targetK));
                        if (etB != null) etB.setText(String.format(Locale.US, "%.2f", targetB));

                        String msg = getString(R.string.msg_linear_correct);
                        tvFeedback.setText(msg);
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        if (tvDialogFeedback != null) {
                            tvDialogFeedback.setText(R.string.msg_linear_incorrect);
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
            targetTv.setTextColor(Color.parseColor("#15803D")); // Green
        } catch (Exception e) {
            targetTv.setText("⚠️ " + e.getMessage());
            targetTv.setTextColor(Color.parseColor("#D32F2F")); // Red error
        }
    }

    private void showCalculationDialog() {
        if (linearFunctionView == null) return;
        Point p1 = linearFunctionView.getUserP1();
        Point p2 = linearFunctionView.getUserP2();
        showCalculationDialogForPoints(p1, p2);
    }

    private void showCalculationDialogForPoints(Point p1, Point p2) {
        if (getContext() == null) return;

        if (p1.x == p2.x) {
            Toast.makeText(getContext(), "Разные X точки должны быть разными!", Toast.LENGTH_SHORT).show();
            return;
        }

        double k = (double) (p2.y - p1.y) / (p2.x - p1.x);
        double b = p1.y - k * p1.x;

        String htmlContent = String.format(Locale.getDefault(),
                "<b>Даны 2 точки:</b><br>" +
                        "P1(x1 = %d, y1 = %d)<br>" +
                        "P2(x2 = %d, y2 = %d)<br><br>" +
                        "<b>1. Расчет углового коэффициента k:</b><br>" +
                        "k = (y2 - y1) / (x2 - x1)<br>" +
                        "k = (%d - %d) / (%d - %d) = %d / %d = <b>%.2f</b><br><br>" +
                        "<b>2. Расчет свободного члена b:</b><br>" +
                        "b = y1 - k · x1<br>" +
                        "b = %d - (%.2f) · %d = <b>%.2f</b><br><br>" +
                        "<b>Итоговое уравнение:</b><br>" +
                        "<b>y = %.2fx %s %.2f</b>",
                p1.x, p1.y, p2.x, p2.y,
                p2.y, p1.y, p2.x, p1.x, (p2.y - p1.y), (p2.x - p1.x), k,
                p1.y, k, p1.x, b,
                k, (b >= 0 ? "+" : "-"), Math.abs(b));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_calc_title)
                .setMessage(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(R.string.btn_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .show();
    }
}
