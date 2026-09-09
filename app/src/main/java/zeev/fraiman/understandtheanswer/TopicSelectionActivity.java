package zeev.fraiman.understandtheanswer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class TopicSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_TOPIC_ID = "extra_topic_id";
    public static final String TOPIC_POINT_COORDINATE = "point_coordinate";
    public static final String TOPIC_LINEAR_FUNCTION = "linear_function";
    public static final String TOPIC_QUADRATIC_FUNCTION = "quadratic_function";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topic_selection);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topic_selection_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialCardView cardTopicPoint = findViewById(R.id.cardTopicPoint);
        if (cardTopicPoint != null) {
            cardTopicPoint.setOnClickListener(v -> {
                Intent intent = new Intent(TopicSelectionActivity.this, MainActivity.class);
                intent.putExtra(EXTRA_TOPIC_ID, TOPIC_POINT_COORDINATE);
                startActivity(intent);
            });
        }

        MaterialCardView cardTopicLinear = findViewById(R.id.cardTopicLinear);
        if (cardTopicLinear != null) {
            cardTopicLinear.setOnClickListener(v -> {
                Intent intent = new Intent(TopicSelectionActivity.this, MainActivity.class);
                intent.putExtra(EXTRA_TOPIC_ID, TOPIC_LINEAR_FUNCTION);
                startActivity(intent);
            });
        }

        MaterialCardView cardTopicQuadratic = findViewById(R.id.cardTopicQuadratic);
        if (cardTopicQuadratic != null) {
            cardTopicQuadratic.setOnClickListener(v -> {
                Intent intent = new Intent(TopicSelectionActivity.this, MainActivity.class);
                intent.putExtra(EXTRA_TOPIC_ID, TOPIC_QUADRATIC_FUNCTION);
                startActivity(intent);
            });
        }
    }
}
