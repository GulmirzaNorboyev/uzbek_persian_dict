package uz.xia.ivat.uzbpersiandictionary.ui.favorite.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_FAVORITE = 0;
    private final int TYPE_EMPTY = 1;

    private final AsyncListDiffer<WordFavorite> asyncListDiffer = new AsyncListDiffer<>(this, new FavoriteDiffer());
    private final OnFavoriteListener favoriteListener;

    public FavoriteAdapter(OnFavoriteListener favoriteListener) {
        this.favoriteListener = favoriteListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == TYPE_EMPTY) {
            view = inflater.inflate(R.layout.item_empty, parent, false);
            return new EmptyViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_favorite, parent, false);
            return new FavoriteViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FavoriteViewHolder)
            ((FavoriteViewHolder) holder).bind(asyncListDiffer.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }

    @Override
    public int getItemViewType(int position) {
        return asyncListDiffer.getCurrentList().get(position).getId() == -1L ? TYPE_EMPTY : TYPE_FAVORITE;
    }

    public void submitList(List<WordFavorite> wordFavorites) {
        asyncListDiffer.submitList(wordFavorites);
    }

    public WordFavorite getFavoriteEntity(int pos) {
        return asyncListDiffer.getCurrentList().get(pos);
    }

    public void removeFavorite(int position) {
        List<WordFavorite> currentList = asyncListDiffer.getCurrentList();
        List<WordFavorite> removedList = new ArrayList<>();
        for (int i = 0; i < asyncListDiffer.getCurrentList().size(); i++) {
            if (i != position)
                removedList.add(currentList.get(i));
        }
        if (removedList.isEmpty()) {
            removedList.add(new WordFavorite(-1L, -1L, "", "", "", false, false));
        }
        asyncListDiffer.submitList(removedList);
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatTextView textViewUzb, textViewPersian;
        private WordFavorite favorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPersian = itemView.findViewById(R.id.tv_word_persian);
            textViewUzb = itemView.findViewById(R.id.tv_word_uzb);
            itemView.setOnClickListener(this);
        }

        void bind(WordFavorite favorite) {
            this.favorite = favorite;
            textViewUzb.setText(favorite.getWordUzb());
            textViewPersian.setText(favorite.getWordPersian());
        }

        @Override
        public void onClick(View v) {
            favoriteListener.onFavoriteClicked(getAdapterPosition(), favorite);
        }
    }
}
