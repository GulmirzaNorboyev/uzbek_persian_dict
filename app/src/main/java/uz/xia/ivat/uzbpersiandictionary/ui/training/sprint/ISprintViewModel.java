package uz.xia.ivat.uzbpersiandictionary.ui.training.sprint;

import androidx.lifecycle.LiveData;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.ui.training.sprint.adapter.Sprint;

public interface ISprintViewModel {
    LiveData<List<Sprint>> getLiveQuestions();

    LiveData<Boolean> getSavedResult();

    void loadQuestions();

    void saveResult(List<Sprint> correctQuestionList);
}
