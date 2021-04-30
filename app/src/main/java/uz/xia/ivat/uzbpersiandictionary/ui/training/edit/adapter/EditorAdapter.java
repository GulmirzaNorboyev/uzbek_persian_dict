package uz.xia.ivat.uzbpersiandictionary.ui.training.edit.adapter;

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
import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;
import uz.xia.ivat.uzbpersiandictionary.util.CheckedFrameLayout;

public class EditorAdapter extends RecyclerView.Adapter<EditorAdapter.EditorViewHolder> {
    private final AsyncListDiffer<TrainEntity> asyncListDiffer = new AsyncListDiffer<>(this, new TrainingDiffer());
    private final OnAllCheckedChangeListener checkedChangeListener;

    public EditorAdapter(OnAllCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    @NonNull
    @Override
    public EditorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit, parent, false);
        return new EditorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditorViewHolder holder, int position) {
        holder.bind(asyncListDiffer.getCurrentList().get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull EditorViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads);
        else if (payloads.get(0) instanceof PayloadEdit) {
            PayloadEdit payloadEdit = (PayloadEdit) payloads.get(0);
            switch (payloadEdit.payload) {
                case CHANGED_UZB:
                    holder.onCheckedUzb(payloadEdit.isChecked);
                    break;
                case CHANGED_PERSIAN:
                    holder.onCheckedPersian(payloadEdit.isChecked);
                    break;
                case CHANGED_SPRINT:
                    holder.onCheckedSprint(payloadEdit.isChecked);
                    break;
                case CHANGED_CONSTRUCT:
                    holder.onCheckedConstruct(payloadEdit.isChecked);
                    break;
                default:
                    holder.onCheckedAll(payloadEdit.isChecked);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }

    public TrainEntity getTrainEntity(int pos) {
        return asyncListDiffer.getCurrentList().get(pos);
    }

    public void submitList(List<TrainEntity> trainList) {
        asyncListDiffer.submitList(trainList);
    }

    public void removeTrain(int pos) {
        List<TrainEntity> newList = new ArrayList<>(asyncListDiffer.getCurrentList());
        newList.remove(pos);
        asyncListDiffer.submitList(newList);
    }

    public void checkedUzb(boolean checked) {
        for (TrainEntity trainEntity : asyncListDiffer.getCurrentList())
            trainEntity.setCorrectUzb(checked);
        notifyItemRangeChanged(0, asyncListDiffer.getCurrentList().size(), new PayloadEdit(PayloadEditor.CHANGED_UZB, checked));
    }

    public void checkedPersian(boolean checked) {
        for (TrainEntity trainEntity : asyncListDiffer.getCurrentList())
            trainEntity.setCorrectPersian(checked);
        notifyItemRangeChanged(0, asyncListDiffer.getCurrentList().size(), new PayloadEdit(PayloadEditor.CHANGED_PERSIAN, checked));
    }

    public void checkedSprint(boolean checked) {
        for (TrainEntity trainEntity : asyncListDiffer.getCurrentList())
            trainEntity.setCorrectSprint(checked);
        notifyItemRangeChanged(0, asyncListDiffer.getCurrentList().size(), new PayloadEdit(PayloadEditor.CHANGED_SPRINT, checked));
    }

    public void checkedConstruct(boolean checked) {
        for (TrainEntity trainEntity : asyncListDiffer.getCurrentList())
            trainEntity.setCorrectConstruct(checked);
        notifyItemRangeChanged(0, asyncListDiffer.getCurrentList().size(), new PayloadEdit(PayloadEditor.CHANGED_CONSTRUCT, checked));
    }

    public void checkedAll(boolean checked) {
        for (TrainEntity trainEntity : asyncListDiffer.getCurrentList()) {
            trainEntity.setCorrectUzb(checked);
            trainEntity.setCorrectPersian(checked);
            trainEntity.setCorrectSprint(checked);
            trainEntity.setCorrectConstruct(checked);
        }
        notifyItemRangeChanged(0, asyncListDiffer.getCurrentList().size(), new PayloadEdit(PayloadEditor.CHANGED_ALL, checked));
    }

    public List<TrainEntity> getAllItems() {
        return asyncListDiffer.getCurrentList();
    }

    enum PayloadEditor {
        CHANGED_UZB,
        CHANGED_PERSIAN,
        CHANGED_SPRINT,
        CHANGED_CONSTRUCT,
        CHANGED_ALL
    }

    public interface OnAllCheckedChangeListener {
        void onCheckedChanged(boolean isChecked);
    }

    class PayloadEdit {
        private final PayloadEditor payload;
        private final boolean isChecked;

        public PayloadEdit(PayloadEditor payload, boolean isChecked) {
            this.payload = payload;
            this.isChecked = isChecked;
        }
    }

    class EditorViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final CheckedFrameLayout checkedFrameUzb, checkedFramePersian, checkedFrameSprint, checkedFrameConstruct, checkedFrameAll;
        private final AppCompatTextView textUzb, textPersian;
        private TrainEntity trainEntity;

        public EditorViewHolder(@NonNull View itemView) {
            super(itemView);
            checkedFrameUzb = itemView.findViewById(R.id.ch_uzb_persian);
            checkedFramePersian = itemView.findViewById(R.id.ch_persian_uzb);
            checkedFrameSprint = itemView.findViewById(R.id.ch_sprint);
            checkedFrameConstruct = itemView.findViewById(R.id.ch_construct);
            checkedFrameAll = itemView.findViewById(R.id.ch_all);
            textPersian = itemView.findViewById(R.id.tv_word_persian);
            textUzb = itemView.findViewById(R.id.tv_word_uzb);
            checkedFrameUzb.setOnClickListener(this);
            checkedFramePersian.setOnClickListener(this);
            checkedFrameSprint.setOnClickListener(this);
            checkedFrameConstruct.setOnClickListener(this);
            checkedFrameAll.setOnClickListener(this);
        }

        public void bind(TrainEntity trainEntity) {
            this.trainEntity = trainEntity;
            textPersian.setText(trainEntity.getWordPersian());
            textUzb.setText(trainEntity.getWordUzb());
            checkedFrameUzb.setChecked(trainEntity.isCorrectUzb());
            checkedFramePersian.setChecked(trainEntity.isCorrectPersian());
            checkedFrameSprint.setChecked(trainEntity.isCorrectSprint());
            checkedFrameConstruct.setChecked(trainEntity.isCorrectConstruct());
            checkedFrameAll.setChecked(
                    trainEntity.isCorrectUzb() &&
                            trainEntity.isCorrectPersian() &&
                            trainEntity.isCorrectSprint() &&
                            trainEntity.isCorrectConstruct());
        }

        @Override
        public void onClick(View v) {
            TrainEntity trainEntity = asyncListDiffer.getCurrentList().get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.ch_uzb_persian:
                    trainEntity.setCorrectUzb(!trainEntity.isCorrectUzb());
                    checkedFrameUzb.setChecked(trainEntity.isCorrectUzb());
                    hasCheckedAll();
                    break;
                case R.id.ch_persian_uzb:
                    trainEntity.setCorrectPersian(!trainEntity.isCorrectPersian());
                    checkedFramePersian.setChecked(trainEntity.isCorrectPersian());
                    hasCheckedAll();
                    break;
                case R.id.ch_sprint:
                    trainEntity.setCorrectSprint(!trainEntity.isCorrectSprint());
                    checkedFrameSprint.setChecked(trainEntity.isCorrectSprint());
                    hasCheckedAll();
                    break;
                case R.id.ch_construct:
                    trainEntity.setCorrectConstruct(!trainEntity.isCorrectConstruct());
                    checkedFrameConstruct.setChecked(trainEntity.isCorrectConstruct());
                    hasCheckedAll();
                    break;
                case R.id.ch_all:
                    boolean isAllChecked = checkedFrameAll.isChecked();
                    trainEntity.setCorrectUzb(!isAllChecked);
                    trainEntity.setCorrectPersian(!isAllChecked);
                    trainEntity.setCorrectSprint(!isAllChecked);
                    trainEntity.setCorrectConstruct(!isAllChecked);
                    checkedFrameAll.setChecked(!isAllChecked);
                    checkedFrameUzb.setChecked(!isAllChecked);
                    checkedFramePersian.setChecked(!isAllChecked);
                    checkedFrameSprint.setChecked(!isAllChecked);
                    checkedFrameConstruct.setChecked(!isAllChecked);
                    checkedChangeListener.onCheckedChanged(checkedFrameAll.isChecked());
                    break;
            }
        }

        public void onCheckedUzb(boolean isChecked) {
            checkedFrameUzb.setChecked(isChecked);
            hasCheckedAll();
        }

        public void onCheckedPersian(boolean isChecked) {
            checkedFramePersian.setChecked(isChecked);
            hasCheckedAll();
        }

        public void onCheckedSprint(boolean isChecked) {
            checkedFrameSprint.setChecked(isChecked);
            hasCheckedAll();
        }

        public void onCheckedConstruct(boolean isChecked) {
            checkedFrameConstruct.setChecked(isChecked);
            hasCheckedAll();
        }

        public void onCheckedAll(boolean isChecked) {
            checkedFrameUzb.setChecked(isChecked);
            checkedFramePersian.setChecked(isChecked);
            checkedFrameSprint.setChecked(isChecked);
            checkedFrameConstruct.setChecked(isChecked);
            hasCheckedAll();
        }

        public void hasCheckedAll() {
            checkedFrameAll.setChecked(
                    checkedFrameUzb.isChecked() &&
                            checkedFramePersian.isChecked() &&
                            checkedFrameSprint.isChecked() &&
                            checkedFrameConstruct.isChecked());
            checkedChangeListener.onCheckedChanged(checkedFrameAll.isChecked());
        }
    }
}
