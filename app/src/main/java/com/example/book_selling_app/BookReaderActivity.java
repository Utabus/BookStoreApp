package com.example.book_selling_app;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.book_selling_app.databinding.ActivityBookReaderBinding;
import com.example.book_selling_app.models.Book;

public class BookReaderActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "EXTRA_BOOK";

    private ActivityBookReaderBinding binding;
    private Book currentBook;
    private float currentFontSize = 16f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookReaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra(EXTRA_BOOK)) {
            currentBook = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);
        }

        if (currentBook == null) {
            Toast.makeText(this, "Không tìm thấy nội dung sách!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayBookInfo();
        setupEvents();
        applyTheme("SEPIA"); // default comfortable reading mode
    }

    private void displayBookInfo() {
        binding.tvReaderBookTitle.setText(currentBook.getTitle());
        binding.tvReaderAuthor.setText(currentBook.getAuthor() + " • Đọc thử miễn phí");

        // Construct realistic sample chapter text
        StringBuilder content = new StringBuilder();
        content.append("— LỜI ĐỀ TỪ —\n\n");
        content.append("Cuộc đời mỗi con người là một hành trình kỳ diệu. Những trang sách mở ra là lúc bạn bước vào một thế giới mới, nơi kiến thức, trải nghiệm và những xúc cảm chân thực nhất được đúc kết qua từng trang viết.\n\n");
        content.append("-----------------------------------------\n\n");
        content.append("TRÍCH ĐOẠN KHỞI ĐẦU:\n\n");

        if (currentBook.getDescription() != null && !currentBook.getDescription().isEmpty()) {
            content.append(currentBook.getDescription()).append("\n\n");
        }

        content.append("Mỗi buổi sáng khi thức dậy, chúng ta đều đứng trước vô vàn lựa chọn: Tiếp tục ngủ vùi trong những giấc mơ hay thức dậy và bắt tay vào hiện thực hóa những khát vọng của đời mình.\n\n");
        content.append("Trong tác phẩm \"").append(currentBook.getTitle()).append("\", tác giả ").append(currentBook.getAuthor())
                .append(" đã dẫn dắt người đọc qua những câu chuyện đầy cảm hứng, từ những vấp ngã đầu đời đến bài học sâu sắc về sự kiên định, thấu hiểu bản thân và cách nhìn nhận thế giới xung quanh với một tâm thế cởi mở.\n\n");
        content.append("Đôi khi, điều quan trọng nhất không phải là bạn đã đi được bao xa, mà là bạn đã học được những gì trên từng bước đường mình đã chọn. Những biến cố, thử thách hay thậm chí là những lần thất bại đau đớn nhất lại chính là những viên gạch vững chắc xây nên nhân cách và bản lĩnh của một con người thành công.\n\n");
        content.append("Hãy lật từng trang sách, chiêm nghiệm từng câu từ và để tâm trí bạn được tự do khám phá những góc nhìn hoàn toàn mới mẻ mà trước đây có thể bạn chưa từng nghĩ tới...");

        binding.tvReaderBookContent.setText(content.toString());
    }

    private void setupEvents() {
        binding.btnReaderBack.setOnClickListener(v -> finish());

        binding.btnFontIncrease.setOnClickListener(v -> {
            if (currentFontSize < 26f) {
                currentFontSize += 2f;
                updateFontSize();
            } else {
                Toast.makeText(this, "Đã đạt cỡ chữ tối đa", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnFontDecrease.setOnClickListener(v -> {
            if (currentFontSize > 12f) {
                currentFontSize -= 2f;
                updateFontSize();
            } else {
                Toast.makeText(this, "Đã đạt cỡ chữ tối thiểu", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnThemeLight.setOnClickListener(v -> applyTheme("LIGHT"));
        binding.btnThemeSepia.setOnClickListener(v -> applyTheme("SEPIA"));
        binding.btnThemeDark.setOnClickListener(v -> applyTheme("DARK"));

        binding.btnReaderBuyBook.setOnClickListener(v -> {
            // Finish reader and return to detail screen so user can buy
            finish();
        });
    }

    private void updateFontSize() {
        binding.tvReaderBookContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentFontSize);
        binding.tvCurrentFontSize.setText(String.valueOf((int) currentFontSize));
    }

    private void applyTheme(String theme) {
        int bgColor;
        int textColor;
        int subTextColor;

        if ("DARK".equalsIgnoreCase(theme)) {
            bgColor = Color.parseColor("#16181D");
            textColor = Color.parseColor("#E6EDF3");
            subTextColor = Color.parseColor("#8B949E");
        } else if ("LIGHT".equalsIgnoreCase(theme)) {
            bgColor = Color.parseColor("#FFFFFF");
            textColor = Color.parseColor("#1F2328");
            subTextColor = Color.parseColor("#656D76");
        } else { // SEPIA
            bgColor = Color.parseColor("#FBF0D9");
            textColor = Color.parseColor("#2F241E");
            subTextColor = Color.parseColor("#7D6B5A");
        }

        binding.layoutReaderRoot.setBackgroundColor(bgColor);
        binding.layoutReaderHeader.setBackgroundColor(bgColor);
        binding.layoutReaderToolbar.setBackgroundColor(bgColor);
        binding.tvReaderBookTitle.setTextColor(textColor);
        binding.tvReaderAuthor.setTextColor(subTextColor);
        binding.tvReaderChapterTitle.setTextColor(textColor);
        binding.tvReaderBookContent.setTextColor(textColor);
        binding.btnReaderBack.setColorFilter(textColor);
    }
}
