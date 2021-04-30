package uz.xia.ivat.uzbpersiandictionary.ui.training.construct.adapter;

import java.util.Objects;

import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;

public class Construct {
    private Long id;
    private TrainEntity correctAnswer;
    private boolean isCorrect = false;

    public Construct(Long id, TrainEntity correctAnswer, boolean isCorrect) {
        this.id = id;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Construct construct = (Construct) o;
        return isCorrect == construct.isCorrect &&
                Objects.equals(id, construct.id) &&
                Objects.equals(correctAnswer, construct.correctAnswer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, correctAnswer, isCorrect);
    }

    public TrainEntity getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(TrainEntity correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
