package uz.xia.ivat.uzbpersiandictionary.ui.favorite.detail;

import androidx.lifecycle.LiveData;

import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;

public interface IFavoriteDetailsViewModel {
    LiveData<WordFavorite> getLiveFavorite();

    void loadFavorite(Long id);

    void removeFavorite(Long id);

    void removeTraining(Long id);

    void saveFavorite(WordFavorite wordEntity);

    void saveTraining(WordFavorite wordEntity);
}
