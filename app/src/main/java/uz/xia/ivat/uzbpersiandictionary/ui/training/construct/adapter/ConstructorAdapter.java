package uz.xia.ivat.uzbpersiandictionary.ui.training.construct.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;

public class ConstructorAdapter extends RecyclerView.Adapter<ConstructorAdapter.AnswerViewHolder> {
    private final AsyncListDiffer<Construct> asyncListDiffer = new AsyncListDiffer<>(this, new ConstructDiffer());
    private final OnAnswerListener answerListener;

    public ConstructorAdapter(OnAnswerListener answerListener) {
        this.answerListener = answerListener;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_construct, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        Construct question = asyncListDiffer.getCurrentList().get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<Construct> questions) {
        asyncListDiffer.submitList(questions);
    }

    public List<Construct> getCorrectQuestions() {
        List<Construct> correctQuestions = new ArrayList<>();
        for (Construct question : asyncListDiffer.getCurrentList())
            if (question.isCorrect())
                correctQuestions.add(question);
        return correctQuestions;
    }

    public interface OnAnswerListener {
        void onNext(int position);

        void onAnswered(boolean isCorrect);

        void onFinish();
    }

    class AnswerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
            LetterAdapter.OnLetterCheckedListener {
        private final AppCompatTextView textAnswer, textQuestion, textInputLetter;
        private final AppCompatButton btnNext;
        private final RecyclerView recyclerLetters;
        private final FlexboxLayoutManager flexboxLayoutManager;
        private final LetterAdapter letterAdapter;
        private final List<String> letterList;
        private Construct question;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            textAnswer = itemView.findViewById(R.id.tv_word_persian);
            textQuestion = itemView.findViewById(R.id.tv_word_uzb);
            textInputLetter = itemView.findViewById(R.id.tv_input_letter);
            btnNext = itemView.findViewById(R.id.btn_next);
            recyclerLetters = itemView.findViewById(R.id.rv_letters);
            flexboxLayoutManager = new FlexboxLayoutManager(itemView.getContext());
            flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
            flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
            flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
            recyclerLetters.setLayoutManager(flexboxLayoutManager);
            letterAdapter = new LetterAdapter(this);
            recyclerLetters.setAdapter(letterAdapter);
            letterList = new ArrayList<>();
        }

        public void bind(Construct question) {
            this.question = question;
            textQuestion.setText(question.getCorrectAnswer().getWordUzb());
            letterList.clear();
            for (char letter : question.getCorrectAnswer().getWordPersian().toCharArray())
                letterList.add(String.valueOf(letter));
            Collections.shuffle(letterList);
            letterAdapter.submitList(letterList);
            btnNext.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            answerListener.onNext(getAdapterPosition());
            textAnswer.setText(" ");
            textInputLetter.setText("");
            recyclerLetters.setVisibility(View.VISIBLE);
            letterAdapter.notifyItemRangeChanged(0, letterAdapter.getItemCount());
            btnNext.setVisibility(View.GONE);
        }

        @Override
        public void onLetterChecked(int position, String letter) {
            textInputLetter.setText(String.format("%s%s", textInputLetter.getText().toString(), letter));
            boolean isCheckedAll = textInputLetter.getText().toString().length() ==
                    question.getCorrectAnswer().getWordPersian().length();
            if (isCheckedAll) {
                question.setCorrect(textInputLetter.getText().toString().equals(question.getCorrectAnswer().getWordPersian()));
                textAnswer.setText(question.getCorrectAnswer().getWordPersian());
                recyclerLetters.setVisibility(View.GONE);
                btnNext.setVisibility(View.VISIBLE);
                answerListener.onAnswered(question.isCorrect());
            }
            if (getAdapterPosition() == getItemCount() - 1)
                onFinish();
        }

        public void onFinish() {
            btnNext.setText(R.string.title_finish);
            btnNext.setOnClickListener(v -> answerListener.onFinish());
        }
    }
}
