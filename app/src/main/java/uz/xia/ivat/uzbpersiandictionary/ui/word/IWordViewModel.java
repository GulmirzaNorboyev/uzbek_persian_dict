package uz.xia.ivat.uzbpersiandictionary.ui.word;

import androidx.lifecycle.LiveData;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;

public interface IWordViewModel {
    LiveData<List<WordEntity>> getLiveWordList();

    void loadLikedWords(WordEntity query);

    void checkFavorite(WordEntity wordEntity);

    void removeFavorite(Long id);

    void removeTraining(Long id);

    void saveFavorite(WordEntity wordEntity);

    void saveTraining(WordEntity wordEntity);
}
