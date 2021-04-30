package uz.xia.ivat.uzbpersiandictionary.ui.favorite;

import androidx.lifecycle.LiveData;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;

public interface IFavoriteViewModel {
    LiveData<List<WordFavorite>> getLiveFavorites();

    void loadAll();

    void removeFavorite(Long id);

    void removeTraining(Long id);

    void saveTraining(WordFavorite wordEntity);

    void removeAll();
}
