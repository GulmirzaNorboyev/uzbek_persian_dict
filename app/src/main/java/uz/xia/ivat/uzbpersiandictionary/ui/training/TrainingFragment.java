package uz.xia.ivat.uzbpersiandictionary.ui.training;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import uz.xia.ivat.uzbpersiandictionary.R;

import static uz.xia.ivat.uzbpersiandictionary.ui.training.test.TestFragment.KEY_HAS_UZB_PERSIAN;

public class TrainingFragment extends Fragment
        implements View.OnClickListener {

    private TrainingViewModel trainingViewModel;
    private LinearLayoutCompat containerUzbPersian, containerPersianUzb, containerSprint, containerConstructor, containerEditor;
    private String formatCountFromTotally = "";
    private AppCompatTextView textUzbPersianCount, textPersianUzbCount, textSprintCount, textConstructCount;
    private Integer countUzb = 0;
    private final Observer<Pair<Integer, Integer>> observerUzbCount = countPair -> {
        countUzb = countPair.second;
        textUzbPersianCount.setText(String.format(formatCountFromTotally, countPair.first, countPair.second));
    };
    private Integer countPersian = 0;
    private final Observer<Pair<Integer, Integer>> observerPersianCount = countPair -> {
        countPersian = countPair.second;
        textPersianUzbCount.setText(String.format(formatCountFromTotally, countPair.first, countPair.second));
    };
    private Integer countSprint = 0;
    private final Observer<Pair<Integer, Integer>> observerSprintCount = countPair -> {
        countSprint = countPair.second;
        textSprintCount.setText(String.format(formatCountFromTotally, countPair.first, countPair.second));
    };
    private Integer countConstruct = 0;
    private final Observer<Pair<Integer, Integer>> observerConstructCount = countPair -> {
        countConstruct = countPair.second;
        textConstructCount.setText(String.format(formatCountFromTotally, countPair.first, countPair.second));
    };
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trainingViewModel =
                new ViewModelProvider(this).get(TrainingViewModel.class);
        trainingViewModel.loadAvailableTraining();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupObservers();
    }

    private void setupObservers() {
        trainingViewModel.getLiveUzbPersianCount().observe(getViewLifecycleOwner(), observerUzbCount);
        trainingViewModel.getLivePersianUzbCount().observe(getViewLifecycleOwner(), observerPersianCount);
        trainingViewModel.getLiveSprintCount().observe(getViewLifecycleOwner(), observerSprintCount);
        trainingViewModel.getLiveConstructorCount().observe(getViewLifecycleOwner(), observerConstructCount);
    }

    private void setupViews(View view) {
        if (navController == null)
            navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        formatCountFromTotally = getString(R.string.count_from_totall);
        containerUzbPersian = view.findViewById(R.id.llc_uzb_persian_train);
        containerPersianUzb = view.findViewById(R.id.llc_persian_uzb_train);
        containerSprint = view.findViewById(R.id.llc_sprint_train);
        containerConstructor = view.findViewById(R.id.llc_construct_train);
        containerEditor = view.findViewById(R.id.llc_train_edit);
        textUzbPersianCount = view.findViewById(R.id.text_uzb_persian_count);
        textPersianUzbCount = view.findViewById(R.id.text_persian_uzb_count);
        textSprintCount = view.findViewById(R.id.text_sprint_count);
        textConstructCount = view.findViewById(R.id.text_construct_count);
        containerUzbPersian.setOnClickListener(this);
        containerPersianUzb.setOnClickListener(this);
        containerSprint.setOnClickListener(this);
        containerConstructor.setOnClickListener(this);
        containerEditor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        switch (v.getId()) {
            case R.id.llc_uzb_persian_train:
                if (countUzb <= 4)
                    showWarningDialog();
                else {
                    args.putBoolean(KEY_HAS_UZB_PERSIAN, true);
                    navController.navigate(R.id.nav_train_test, args);
                }
                break;
            case R.id.llc_persian_uzb_train:
                if (countPersian <= 4)
                    showWarningDialog();
                else {
                    args.putBoolean(KEY_HAS_UZB_PERSIAN, false);
                    navController.navigate(R.id.nav_train_test, args);
                }
                break;
            case R.id.llc_sprint_train:
                if (countSprint <= 4)
                    showWarningDialog();
                else
                    navController.navigate(R.id.nav_train_sprint);
                break;
            case R.id.llc_construct_train:
                if (countConstruct <= 4)
                    showWarningDialog();
                else
                    navController.navigate(R.id.nav_train_construct);
                break;
            default:
                navController.navigate(R.id.nav_train_editor);
                break;
        }
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setMessage(R.string.warning_count_min_five)
                .setPositiveButton(R.string.answer_ok, null);
        builder.create();
        builder.show();
    }
}