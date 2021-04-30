package uz.xia.ivat.uzbpersiandictionary.ui.training.construct.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;

public class LetterAdapter extends RecyclerView.Adapter<LetterAdapter.LetterViewHolder> {
    private final AsyncListDiffer<String> asyncListDiffer = new AsyncListDiffer<>(this, new LetterDiffer());
    private final OnLetterCheckedListener letterCheckedListener;

    public LetterAdapter(OnLetterCheckedListener letterCheckedListener) {
        this.letterCheckedListener = letterCheckedListener;
    }

    @NonNull
    @Override
    public LetterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_letter, parent, false);
        return new LetterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LetterViewHolder holder, int position) {
        holder.onBind(asyncListDiffer.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<String> letters) {
        asyncListDiffer.submitList(letters);
    }

    interface OnLetterCheckedListener {
        void onLetterChecked(int position, String letter);
    }

    class LetterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final AppCompatCheckedTextView checkedLetter;
        private String letter = "";

        public LetterViewHolder(@NonNull View itemView) {
            super(itemView);
            checkedLetter = itemView.findViewById(R.id.tv_letter);
        }

        public void onBind(String letter) {
            this.letter = letter;
            checkedLetter.setText(letter);
            checkedLetter.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            checkedLetter.setChecked(true);
            checkedLetter.setEnabled(false);
            letterCheckedListener.onLetterChecked(getAdapterPosition(), letter);
        }
    }
}
