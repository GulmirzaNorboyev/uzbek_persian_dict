package uz.xia.ivat.uzbpersiandictionary.ui.training.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.AnswerViewHolder> {
    private final AsyncListDiffer<Question> asyncListDiffer = new AsyncListDiffer<>(this, new TrainDiffer());
    private final boolean isUzbekPersian;
    private final OnAnswerListener answerListener;

    public QuestionAdapter(boolean isUzbekPersian, OnAnswerListener answerListener) {
        this.isUzbekPersian = isUzbekPersian;
        this.answerListener = answerListener;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        Question question = asyncListDiffer.getCurrentList().get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<Question> questions) {
        asyncListDiffer.submitList(questions);
    }

    public List<Question> getCorrectQuestions() {
        List<Question> correctQuestions = new ArrayList<>();
        for (Question question : asyncListDiffer.getCurrentList())
            if (question.isCorrect())
                correctQuestions.add(question);
        return correctQuestions;
    }

    public interface OnAnswerListener {
        void onNext(int position);

        default void onAnswered(int position, boolean isCorrect) {
        }
    }

    class AnswerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatTextView textAnswer;
        private final AppCompatCheckedTextView btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD;
        private final AppCompatButton btnNext;
        private Question question;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            textAnswer = itemView.findViewById(R.id.tv_word_persian);
            btnAnswerA = itemView.findViewById(R.id.rb_answer_a);
            btnAnswerB = itemView.findViewById(R.id.rb_answer_b);
            btnAnswerC = itemView.findViewById(R.id.rb_answer_c);
            btnAnswerD = itemView.findViewById(R.id.rb_answer_d);
            btnNext = itemView.findViewById(R.id.btn_next);
        }

        public void bind(Question question) {
            this.question = question;
            if (isUzbekPersian) {
                textAnswer.setText(question.getCorrectAnswer().getWordPersian());
                btnAnswerA.setText(question.getAnswerList().get(0).getWordUzb());
                btnAnswerB.setText(question.getAnswerList().get(1).getWordUzb());
                btnAnswerC.setText(question.getAnswerList().get(2).getWordUzb());
                btnAnswerD.setText(question.getAnswerList().get(3).getWordUzb());
            } else {
                textAnswer.setText(question.getCorrectAnswer().getWordUzb());
                btnAnswerA.setText(question.getAnswerList().get(0).getWordPersian());
                btnAnswerB.setText(question.getAnswerList().get(1).getWordPersian());
                btnAnswerC.setText(question.getAnswerList().get(2).getWordPersian());
                btnAnswerD.setText(question.getAnswerList().get(3).getWordPersian());
            }
            btnAnswerA.setEnabled(true);
            btnAnswerB.setEnabled(true);
            btnAnswerC.setEnabled(true);
            btnAnswerD.setEnabled(true);
            btnAnswerA.setBackgroundResource(R.drawable.shape_answer_correct);
            btnAnswerB.setBackgroundResource(R.drawable.shape_answer_correct);
            btnAnswerC.setBackgroundResource(R.drawable.shape_answer_correct);
            btnAnswerD.setBackgroundResource(R.drawable.shape_answer_correct);
            btnAnswerA.setChecked(false);
            btnAnswerB.setChecked(false);
            btnAnswerC.setChecked(false);
            btnAnswerD.setChecked(false);
            btnAnswerA.setOnClickListener(this);
            btnAnswerB.setOnClickListener(this);
            btnAnswerC.setOnClickListener(this);
            btnAnswerD.setOnClickListener(this);
            btnNext.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rb_answer_a:
                    if (question.getAnswerList().get(0).equals(question.getCorrectAnswer())) {
                        question.setCorrect(true);
                        checkedCorrectAnswer(btnAnswerA);
                    } else {
                        btnAnswerA.setBackgroundResource(R.drawable.shape_answer_incorrect);
                        findCorrectAnswer();
                    }
                    btnAnswerB.setEnabled(false);
                    btnAnswerC.setEnabled(false);
                    btnAnswerD.setEnabled(false);
                    break;

                case R.id.rb_answer_b:
                    if (question.getAnswerList().get(1).equals(question.getCorrectAnswer())) {
                        question.setCorrect(true);
                        checkedCorrectAnswer(btnAnswerB);
                    } else {
                        btnAnswerB.setBackgroundResource(R.drawable.shape_answer_incorrect);
                        findCorrectAnswer();
                    }
                    btnAnswerA.setEnabled(false);
                    btnAnswerC.setEnabled(false);
                    btnAnswerD.setEnabled(false);
                    break;
                case R.id.rb_answer_c:
                    if (question.getAnswerList().get(2).equals(question.getCorrectAnswer())) {
                        question.setCorrect(true);
                        checkedCorrectAnswer(btnAnswerC);
                    } else {
                        btnAnswerC.setBackgroundResource(R.drawable.shape_answer_incorrect);
                        findCorrectAnswer();
                    }
                    btnAnswerB.setEnabled(false);
                    btnAnswerA.setEnabled(false);
                    btnAnswerD.setEnabled(false);
                    break;
                case R.id.rb_answer_d:
                    if (question.getAnswerList().get(3).equals(question.getCorrectAnswer())) {
                        question.setCorrect(true);
                        checkedCorrectAnswer(btnAnswerD);
                    } else {
                        btnAnswerD.setBackgroundResource(R.drawable.shape_answer_incorrect);
                        findCorrectAnswer();
                    }
                    btnAnswerB.setEnabled(false);
                    btnAnswerC.setEnabled(false);
                    btnAnswerA.setEnabled(false);
                    break;
                default:
                    answerListener.onNext(getAdapterPosition());
                    break;
            }
            btnNext.setVisibility(View.VISIBLE);
        }

        private void checkedCorrectAnswer(AppCompatCheckedTextView btnAnswerCorrect) {
            btnAnswerCorrect.setChecked(true);
            btnAnswerCorrect.setEnabled(false);
            btnAnswerCorrect.setBackgroundResource(R.drawable.shape_answer_correctly);
        }

        private void findCorrectAnswer() {
            int correctIndex = question.getAnswerList().indexOf(question.getCorrectAnswer());
            switch (correctIndex) {
                case 0:
                    checkedCorrectAnswer(btnAnswerA);
                    break;
                case 1:
                    checkedCorrectAnswer(btnAnswerB);
                    break;
                case 2:
                    checkedCorrectAnswer(btnAnswerC);
                    break;
                default:
                    checkedCorrectAnswer(btnAnswerD);
                    break;
            }
        }
    }
}
