package uz.xia.ivat.uzbpersiandictionary.ui.home.adapter;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;

public class WordViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    private final AppCompatTextView textViewUzb, textViewPersian;
    private final OnWordClickListener wordClickListener;
    private WordEntity wordEntity = null;

    public WordViewHolder(
            @NonNull View itemView,
            OnWordClickListener wordClickListener
    ) {
        super(itemView);
        this.wordClickListener = wordClickListener;
        textViewUzb = itemView.findViewById(R.id.tv_word_uzb);
        textViewPersian = itemView.findViewById(R.id.tv_word_persian);
        itemView.setOnClickListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bind(WordEntity wordEntity) {
        this.wordEntity = wordEntity;
        textViewUzb.setText(wordEntity.getWordUzb());
        textViewPersian.setText(wordEntity.getWordPersian());
    }

    @Override
    public void onClick(View v) {
        wordClickListener.onWordClicked(getAdapterPosition(), wordEntity);
    }
}
