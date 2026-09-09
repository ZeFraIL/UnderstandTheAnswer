package zeev.fraiman.understandtheanswer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize saved language before layout inflating
        LanguageManager.initLanguage(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.start_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialButtonToggleGroup toggleGroupLanguage = findViewById(R.id.toggleGroupLanguage);
        if (toggleGroupLanguage != null) {
            String currentLang = LanguageManager.getLanguage(this);
            if (LanguageManager.LANG_RU.equals(currentLang)) {
                toggleGroupLanguage.check(R.id.btnLangRu);
            } else {
                toggleGroupLanguage.check(R.id.btnLangEn);
            }

            toggleGroupLanguage.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    if (checkedId == R.id.btnLangRu) {
                        if (!LanguageManager.LANG_RU.equals(LanguageManager.getLanguage(this))) {
                            LanguageManager.setLanguage(this, LanguageManager.LANG_RU);
                        }
                    } else if (checkedId == R.id.btnLangEn) {
                        if (!LanguageManager.LANG_EN.equals(LanguageManager.getLanguage(this))) {
                            LanguageManager.setLanguage(this, LanguageManager.LANG_EN);
                        }
                    }
                }
            });
        }

        Button btnStart = findViewById(R.id.btnStart);
        if (btnStart != null) {
            btnStart.setOnClickListener(v -> {
                Intent intent = new Intent(StartActivity.this, TopicSelectionActivity.class);
                startActivity(intent);
            });
        }
    }
}
