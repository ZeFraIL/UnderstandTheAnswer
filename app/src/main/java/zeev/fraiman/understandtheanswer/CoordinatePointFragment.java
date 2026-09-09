package zeev.fraiman.understandtheanswer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Random;

public class CoordinatePointFragment extends Fragment {

    private int targetX = 3;
    private int targetY = -2;

    private CoordinatePlaneView coordinatePlaneView;
    private EditText etCoordX;
    private EditText etCoordY;
    private TextView tvFeedback;
    private Random random;

    public CoordinatePointFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinate_point, container, false);

        random = new Random();

        coordinatePlaneView = view.findViewById(R.id.coordinatePlaneView);
        etCoordX = view.findViewById(R.id.etCoordX);
        etCoordY = view.findViewById(R.id.etCoordY);
        tvFeedback = view.findViewById(R.id.tvFeedback);

        MaterialSwitch switchGrid = view.findViewById(R.id.switchGrid);
        MaterialSwitch switchAxisValues = view.findViewById(R.id.switchAxisValues);

        Button btnCheck = view.findViewById(R.id.btnCheck);
        Button btnHint = view.findViewById(R.id.btnHint);
        Button btnNewPoint = view.findViewById(R.id.btnNewPoint);

        if (coordinatePlaneView != null) {
            coordinatePlaneView.setOnPointTapListener(this::handlePointTap);
            coordinatePlaneView.setOnPointDragListener(this::handlePointDrag);
        }

        if (switchGrid != null) {
            switchGrid.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (coordinatePlaneView != null) {
                    coordinatePlaneView.setGridVisible(isChecked);
                }
            });
        }

        if (switchAxisValues != null) {
            switchAxisValues.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (coordinatePlaneView != null) {
                    coordinatePlaneView.setAxisValuesVisible(isChecked);
                }
            });
        }

        btnCheck.setOnClickListener(v -> checkAnswer());
        btnHint.setOnClickListener(v -> giveHint());
        btnNewPoint.setOnClickListener(v -> generateNewPoint());

        generateNewPoint();

        return view;
    }

    private void handlePointTap(int gridX, int gridY) {
        if (coordinatePlaneView == null || getContext() == null) return;

        if (coordinatePlaneView.isTooCloseToTarget(gridX, gridY)) {
            String msg = getString(R.string.msg_too_close);
            tvFeedback.setText(msg);
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean added = coordinatePlaneView.addHintPoint(gridX, gridY);
        if (added) {
            int usedCount = coordinatePlaneView.getHintPointsCount();
            int remaining = 2 - usedCount;
            String msg = getString(R.string.msg_hint_point_added, usedCount, gridX, gridY, remaining);
            tvFeedback.setText(msg);
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        } else {
            String msg = getString(R.string.msg_max_hints_reached);
            tvFeedback.setText(msg);
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePointDrag(int index, int gridX, int gridY) {
        String msg = getString(R.string.msg_hint_point_dragged, index + 1, gridX, gridY);
        tvFeedback.setText(msg);
    }

    private void generateNewPoint() {
        do {
            targetX = random.nextInt(29) - 14;
            targetY = random.nextInt(29) - 14;
        } while (targetX == 0 && targetY == 0);

        if (coordinatePlaneView != null) {
            coordinatePlaneView.setPoint(targetX, targetY);
        }

        if (etCoordX != null) etCoordX.setText("");
        if (etCoordY != null) etCoordY.setText("");
        if (tvFeedback != null) tvFeedback.setText(R.string.hint_placeholder);
    }

    private void checkAnswer() {
        String xStr = etCoordX.getText().toString().trim();
        String yStr = etCoordY.getText().toString().trim();

        if (TextUtils.isEmpty(xStr) || TextUtils.isEmpty(yStr)) {
            String msg = getString(R.string.msg_enter_both);
            tvFeedback.setText(msg);
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int inputX = Integer.parseInt(xStr);
            int inputY = Integer.parseInt(yStr);

            String msg;
            if (inputX == targetX && inputY == targetY) {
                msg = getString(R.string.msg_correct);
            } else {
                msg = getString(R.string.msg_incorrect);
            }
            tvFeedback.setText(msg);
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            String msg = getString(R.string.msg_invalid_number);
            tvFeedback.setText(msg);
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void giveHint() {
        String msg;
        if (targetX > 0 && targetY > 0) {
            msg = getString(R.string.hint_quadrant_1);
        } else if (targetX < 0 && targetY > 0) {
            msg = getString(R.string.hint_quadrant_2);
        } else if (targetX < 0 && targetY < 0) {
            msg = getString(R.string.hint_quadrant_3);
        } else if (targetX > 0 && targetY < 0) {
            msg = getString(R.string.hint_quadrant_4);
        } else if (targetX == 0) {
            msg = "Подсказка: точка лежит на оси Y (X = 0).";
        } else {
            msg = "Подсказка: точка лежит на оси X (Y = 0).";
        }
        tvFeedback.setText(msg);
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
