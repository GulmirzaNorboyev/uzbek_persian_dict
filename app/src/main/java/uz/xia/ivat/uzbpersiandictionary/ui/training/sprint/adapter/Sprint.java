package uz.xia.ivat.uzbpersiandictionary.ui.training.sprint.adapter;

import java.util.Objects;

import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;

public class Sprint {
    private Long id;
    private Long correctTrainId;
    private Long inCorrectTrainId;
    private TrainEntity correctAnswer;
    private TrainEntity inCorrectAnswer;
    private boolean isCorrect;

    public Sprint(Long id,
                  Long correctTrainId,
                  Long inCorrectTrainId,
                  TrainEntity correctAnswer,
                  TrainEntity inCorrectAnswer,
                  boolean isCorrect
    ) {
        this.id = id;
        this.correctTrainId = correctTrainId;
        this.inCorrectTrainId = inCorrectTrainId;
        this.correctAnswer = correctAnswer;
        this.inCorrectAnswer = inCorrectAnswer;
        this.isCorrect = isCorrect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCorrectTrainId() {
        return correctTrainId;
    }

    public void setCorrectTrainId(Long correctTrainId) {
        this.correctTrainId = correctTrainId;
    }

    public Long getInCorrectTrainId() {
        return inCorrectTrainId;
    }

    public void setInCorrectTrainId(Long inCorrectTrainId) {
        this.inCorrectTrainId = inCorrectTrainId;
    }

    public TrainEntity getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(TrainEntity correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public TrainEntity getInCorrectAnswer() {
        return inCorrectAnswer;
    }

    public void setInCorrectAnswer(TrainEntity inCorrectAnswer) {
        this.inCorrectAnswer = inCorrectAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprint sprint = (Sprint) o;
        return isCorrect == sprint.isCorrect &&
                Objects.equals(id, sprint.id) &&
                Objects.equals(correctTrainId, sprint.correctTrainId) &&
                Objects.equals(inCorrectTrainId, sprint.inCorrectTrainId) &&
                Objects.equals(correctAnswer, sprint.correctAnswer) &&
                Objects.equals(inCorrectAnswer, sprint.inCorrectAnswer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, correctTrainId, inCorrectTrainId, correctAnswer, inCorrectAnswer, isCorrect);
    }
}
