package uz.xia.ivat.uzbpersiandictionary.ui.training.sprint.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uz.xia.ivat.uzbpersiandictionary.R;

public class SprintAdapter extends RecyclerView.Adapter<SprintAdapter.AnswerViewHolder> {
    private final AsyncListDiffer<Sprint> asyncListDiffer = new AsyncListDiffer<>(this, new SprintDiffer());
    private final OnAnswerListener answerListener;

    public SprintAdapter(OnAnswerListener answerListener) {
        this.answerListener = answerListener;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sprint, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        Sprint question = asyncListDiffer.getCurrentList().get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<Sprint> questions) {
        asyncListDiffer.submitList(questions);
    }

    public List<Sprint> getCorrectQuestions() {
        List<Sprint> correctQuestions = new ArrayList<>();
        for (Sprint question : asyncListDiffer.getCurrentList())
            if (question.isCorrect())
                correctQuestions.add(question);
        return correctQuestions;
    }

    public interface OnAnswerListener {
        default void onNext(int position) {
        }

        default void onAnswered(int position, boolean isCorrect) {
        }
    }

    class AnswerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatTextView textAnswer, textQuestion;
        private final AppCompatButton btnCorrect, btnInCorrect;
        private final Random random;
        private Sprint question;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            textQuestion = itemView.findViewById(R.id.tv_word_persian);
            textAnswer = itemView.findViewById(R.id.tv_select_word);
            btnCorrect = itemView.findViewById(R.id.btn_correct);
            btnInCorrect = itemView.findViewById(R.id.btn_incorrect);
            random = new Random();
        }

        public void bind(Sprint sprint) {
            this.question = sprint;
            textQuestion.setText(sprint.getCorrectAnswer().getWordPersian());
            if (random.nextInt() % 2 == 0)
                textAnswer.setText(sprint.getCorrectAnswer().getWordUzb());
            else
                textAnswer.setText(sprint.getInCorrectAnswer().getWordUzb());
            btnCorrect.setOnClickListener(this);
            btnInCorrect.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_correct:
                    if (question.getCorrectAnswer().getWordUzb().equals(textAnswer.getText().toString())) {
                        question.setCorrect(true);
                        answerListener.onAnswered(getAdapterPosition(), true);
                    } else {
                        answerListener.onAnswered(getAdapterPosition(), false);
                    }
                    break;
                case R.id.btn_incorrect:
                    if (!question.getCorrectAnswer().getWordUzb().equals(textAnswer.getText().toString())) {
                        question.setCorrect(true);
                        answerListener.onAnswered(getAdapterPosition(), true);
                    } else {
                        answerListener.onAnswered(getAdapterPosition(), false);
                    }
                    break;
            }
        }
    }
}
