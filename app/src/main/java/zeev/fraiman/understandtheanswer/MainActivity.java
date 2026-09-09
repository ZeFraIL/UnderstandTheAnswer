package zeev.fraiman.understandtheanswer;

import android.os.Bundle;
import android.text.Html;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    private String currentTopicId = TopicSelectionActivity.TOPIC_POINT_COORDINATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra(TopicSelectionActivity.EXTRA_TOPIC_ID)) {
            String topic = getIntent().getStringExtra(TopicSelectionActivity.EXTRA_TOPIC_ID);
            if (topic != null) {
                currentTopicId = topic;
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            if (TopicSelectionActivity.TOPIC_QUADRATIC_FUNCTION.equals(currentTopicId)) {
                toolbar.setTitle(R.string.fragment_parabola_title);
            } else if (TopicSelectionActivity.TOPIC_LINEAR_FUNCTION.equals(currentTopicId)) {
                toolbar.setTitle(R.string.fragment_linear_title);
            } else {
                toolbar.setTitle(R.string.fragment_point_title);
            }

            toolbar.setNavigationOnClickListener(v -> finish());
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_help) {
                    showHelpDialog();
                    return true;
                }
                return false;
            });
        }

        if (savedInstanceState == null) {
            Fragment fragment;
            if (TopicSelectionActivity.TOPIC_QUADRATIC_FUNCTION.equals(currentTopicId)) {
                fragment = new ParabolaFunctionFragment();
            } else if (TopicSelectionActivity.TOPIC_LINEAR_FUNCTION.equals(currentTopicId)) {
                fragment = new LinearFunctionFragment();
            } else {
                fragment = new CoordinatePointFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public void showHelpDialog() {
        int contentResId = R.string.dialog_help_content;
        int titleResId = R.string.dialog_help_title;

        if (TopicSelectionActivity.TOPIC_QUADRATIC_FUNCTION.equals(currentTopicId)) {
            contentResId = R.string.dialog_parabola_help_content;
            titleResId = R.string.dialog_parabola_help_title;
        } else if (TopicSelectionActivity.TOPIC_LINEAR_FUNCTION.equals(currentTopicId)) {
            contentResId = R.string.dialog_linear_help_content;
            titleResId = R.string.dialog_linear_help_title;
        }

        CharSequence formattedText = Html.fromHtml(getString(contentResId), Html.FROM_HTML_MODE_COMPACT);

        new MaterialAlertDialogBuilder(this)
                .setTitle(titleResId)
                .setMessage(formattedText)
                .setPositiveButton(R.string.btn_close, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
