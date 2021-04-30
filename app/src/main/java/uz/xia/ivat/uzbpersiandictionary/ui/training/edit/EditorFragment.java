package uz.xia.ivat.uzbpersiandictionary.ui.training.edit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;
import uz.xia.ivat.uzbpersiandictionary.ui.home.adapter.SwipeHelper;
import uz.xia.ivat.uzbpersiandictionary.ui.training.edit.adapter.EditorAdapter;

public class EditorFragment extends DialogFragment
        implements View.OnClickListener,
        EditorAdapter.OnAllCheckedChangeListener {

    private RecyclerView recyclerEditor;
    private EditorAdapter editorAdapter;
    private AppCompatCheckedTextView checkedTextUzb, checkedTextPersian, checkedTextSprint, checkedTextConstruct, checkedTextAll;
    private final Observer<List<TrainEntity>> observerTrainees = trainList -> {
        int listSize = trainList.size();
        int allUzbCounter = 0;
        int allPersianCounter = 0;
        int allSprintCounter = 0;
        int allConstructCounter = 0;
        for (TrainEntity trainEntity : trainList) {
            if (trainEntity.isCorrectUzb())
                allUzbCounter++;
            if (trainEntity.isCorrectPersian())
                allPersianCounter++;
            if (trainEntity.isCorrectSprint())
                allSprintCounter++;
            if (trainEntity.isCorrectConstruct())
                allConstructCounter++;
        }
        checkedTextUzb.setChecked(allUzbCounter == listSize - 1);
        checkedTextPersian.setChecked(allPersianCounter == listSize - 1);
        checkedTextSprint.setChecked(allSprintCounter == listSize - 1);
        checkedTextConstruct.setChecked(allConstructCounter == listSize - 1);
        checkedTextAll.setChecked(
                checkedTextUzb.isChecked() &&
                        checkedTextPersian.isChecked() &&
                        checkedTextSprint.isChecked() &&
                        checkedTextConstruct.isChecked()
        );
        editorAdapter.submitList(trainList);
    };
    private EditViewModel editViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editViewModel = new ViewModelProvider(this).get(EditViewModel.class);
        editViewModel.loadAll();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editor, container, false);
    }

    @Override
    public int getTheme() {
        return R.style.FullScreenDialogLeftRightDelayed;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupObservers();
    }

    private void setupObservers() {
        editViewModel.getLiveTrainees().observe(getViewLifecycleOwner(), observerTrainees);
    }

    private void setupViews(View view) {
        recyclerEditor = view.findViewById(R.id.rv_train);
        AppCompatImageView toolbarBack = view.findViewById(R.id.iv_back);
        checkedTextUzb = view.findViewById(R.id.tv_uzb_persian);
        checkedTextPersian = view.findViewById(R.id.tv_persian_uzb);
        checkedTextSprint = view.findViewById(R.id.tv_sprint);
        checkedTextConstruct = view.findViewById(R.id.tv_construct);
        checkedTextAll = view.findViewById(R.id.tv_all);
        toolbarBack.setOnClickListener(v -> dismiss());
        if (editorAdapter == null)
            editorAdapter = new EditorAdapter(this);
        recyclerEditor.setAdapter(editorAdapter);
        initializeSwipeHelper();
        recyclerEditor.setHasFixedSize(true);
        recyclerEditor.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        checkedTextUzb.setOnClickListener(this);
        checkedTextPersian.setOnClickListener(this);
        checkedTextSprint.setOnClickListener(this);
        checkedTextConstruct.setOnClickListener(this);
        checkedTextAll.setOnClickListener(this);
    }

    private void initializeSwipeHelper() {
        new SwipeHelper(requireContext(), recyclerEditor) {
            @Override
            public void instantiateUnderlayButton(
                    RecyclerView.ViewHolder viewHolder,
                    List<UnderlayButton> underlayButtons
            ) {
                underlayButtons.add(new UnderlayButton(
                        requireContext(),
                        "Delete",
                        R.drawable.ic_baseline_delete,
                        Color.parseColor("#FFF4511E"),
                        pos -> {
                            TrainEntity trainEntity = editorAdapter.getTrainEntity(pos);
                            editViewModel.removeTraining(trainEntity.getWordId());
                            Toast.makeText(requireContext(), R.string.removed_favorite, Toast.LENGTH_SHORT).show();
                            editorAdapter.removeTrain(pos);
                        }
                ));
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uzb_persian:
                checkedTextUzb.toggle();
                editorAdapter.checkedUzb(checkedTextUzb.isChecked());
                hasCheckedAll();
                break;
            case R.id.tv_persian_uzb:
                checkedTextPersian.toggle();
                editorAdapter.checkedPersian(checkedTextPersian.isChecked());
                hasCheckedAll();
                break;
            case R.id.tv_sprint:
                checkedTextSprint.toggle();
                editorAdapter.checkedSprint(checkedTextSprint.isChecked());
                hasCheckedAll();
                break;
            case R.id.tv_construct:
                checkedTextConstruct.toggle();
                editorAdapter.checkedConstruct(checkedTextConstruct.isChecked());
                hasCheckedAll();
                break;
            case R.id.tv_all:
                checkedTextAll.toggle();
                checkedTextUzb.setChecked(checkedTextAll.isChecked());
                checkedTextPersian.setChecked(checkedTextAll.isChecked());
                checkedTextSprint.setChecked(checkedTextAll.isChecked());
                checkedTextConstruct.setChecked(checkedTextAll.isChecked());
                editorAdapter.checkedAll(checkedTextAll.isChecked());
                break;
        }
    }

    private void hasCheckedAll() {
        checkedTextAll.setChecked(
                checkedTextUzb.isChecked() &&
                        checkedTextPersian.isChecked() &&
                        checkedTextSprint.isChecked() &&
                        checkedTextConstruct.isChecked()
        );
    }

    @Override
    public void onCheckedChanged(boolean isChecked) {
        checkedTextAll.setChecked(isChecked);
    }

    @Override
    public void onStop() {
        editViewModel.saveTrainees(editorAdapter.getAllItems());
        super.onStop();
    }
}
