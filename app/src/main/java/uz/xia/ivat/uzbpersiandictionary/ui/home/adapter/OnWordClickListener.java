package uz.xia.ivat.uzbpersiandictionary.ui.home.adapter;

import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;

public interface OnWordClickListener {
    void onWordClicked(int position, WordEntity word);
}
