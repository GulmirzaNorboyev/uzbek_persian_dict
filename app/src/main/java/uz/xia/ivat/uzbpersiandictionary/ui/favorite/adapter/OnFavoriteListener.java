package uz.xia.ivat.uzbpersiandictionary.ui.favorite.adapter;

import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;

public interface OnFavoriteListener {
    void onFavoriteClicked(int position, WordFavorite favorite);
}
