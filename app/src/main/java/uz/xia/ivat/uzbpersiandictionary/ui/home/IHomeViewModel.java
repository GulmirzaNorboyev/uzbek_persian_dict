package uz.xia.ivat.uzbpersiandictionary.ui.home;

import androidx.lifecycle.LiveData;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;

public interface IHomeViewModel {
    LiveData<List<WordEntity>> getLiveWordList();

    String getLastQuery();

    void loadAllWords();

    void removeFavorite(Long id);

    void removeTraining(Long id);

    void saveFavorite(WordEntity wordEntity);

    void saveTraining(WordEntity wordEntity);

    void search(String newText);

    void searchPersian(String newText);

    void searchCyrillic(String newText);

    void clearSearching();
}
