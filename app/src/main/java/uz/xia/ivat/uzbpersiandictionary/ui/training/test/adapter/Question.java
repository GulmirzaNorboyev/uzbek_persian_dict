package uz.xia.ivat.uzbpersiandictionary.ui.training.test.adapter;

import java.util.List;
import java.util.Objects;

import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;

public class Question {
    private Long id;
    private Long correctTrainId;
    private TrainEntity correctAnswer;
    private List<TrainEntity> answerList;
    private boolean isCorrect = false;

    public Question(
            Long id,
            Long correctTrainId,
            TrainEntity correctAnswer,
            List<TrainEntity> answerList
    ) {
        this.id = id;
        this.correctTrainId = correctTrainId;
        this.correctAnswer = correctAnswer;
        this.answerList = answerList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TrainEntity getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(TrainEntity correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<TrainEntity> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<TrainEntity> answerList) {
        this.answerList = answerList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return isCorrect == question.isCorrect &&
                Objects.equals(id, question.id) &&
                Objects.equals(correctTrainId, question.correctTrainId) &&
                Objects.equals(correctAnswer, question.correctAnswer) &&
                Objects.equals(answerList, question.answerList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, correctTrainId, correctAnswer, answerList, isCorrect);
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Long getCorrectTrainId() {
        return correctTrainId;
    }

    public void setCorrectTrainId(Long correctTrainId) {
        this.correctTrainId = correctTrainId;
    }
}
