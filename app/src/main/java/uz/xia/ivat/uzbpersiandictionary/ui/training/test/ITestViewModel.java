package uz.xia.ivat.uzbpersiandictionary.ui.training.test;

import androidx.lifecycle.LiveData;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.ui.training.test.adapter.Question;

public interface ITestViewModel {
    LiveData<List<Question>> getLiveQuestions();

    LiveData<Boolean> getSavedResult();

    void loadQuestions(boolean isUzbPersian);

    void saveResult(List<Question> correctQuestionList, boolean isUzbekPersian);
}
