package uz.xia.ivat.uzbpersiandictionary.ui.training.construct;

import androidx.lifecycle.LiveData;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.ui.training.construct.adapter.Construct;

public interface IConstructViewModel {
    LiveData<List<Construct>> getLiveQuestions();

    LiveData<Boolean> getSavedResult();

    void loadQuestions();

    void saveResult(List<Construct> correctQuestionList);
}
