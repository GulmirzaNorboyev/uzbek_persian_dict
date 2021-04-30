package uz.xia.ivat.uzbpersiandictionary.ui.favorite.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;

public class FavoriteDiffer extends DiffUtil.ItemCallback<WordFavorite> {
    @Override
    public boolean areItemsTheSame(@NonNull WordFavorite oldItem, @NonNull WordFavorite newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull WordFavorite oldItem, @NonNull WordFavorite newItem) {
        return oldItem.equals(newItem);
    }
}
