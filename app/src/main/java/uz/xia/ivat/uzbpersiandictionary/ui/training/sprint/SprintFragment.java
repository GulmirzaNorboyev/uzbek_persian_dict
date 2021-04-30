package uz.xia.ivat.uzbpersiandictionary.ui.training.sprint;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.ui.training.sprint.adapter.Sprint;
import uz.xia.ivat.uzbpersiandictionary.ui.training.sprint.adapter.SprintAdapter;

public class SprintFragment extends Fragment
        implements SprintAdapter.OnAnswerListener,
        Observer<Boolean>,
        Runnable {

    private final CompositeDisposable cd = new CompositeDisposable();
    private ViewPager2 viewPagerSprint;
    private AppCompatTextView textCorrectCounter, textTimer, textStartTimer;
    private SprintAdapter sprintAdapter;
    private final Observer<List<Sprint>> observerQuestionList = questions -> {
        sprintAdapter.submitList(questions);
    };
    private SprintViewModel sprintViewModel;
    private NavController navController;
    private Handler handler;
    private int position = 0;
    private LinearLayoutCompat llcRoot, llcCounterContainer;
    private CountDownTimer countDownTimer, startDownTimer;
    private boolean isTestFinished = false;
    private ObjectAnimator correctColorAnimator, inCorrectColorAnimator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sprintViewModel = new ViewModelProvider(this).get(SprintViewModel.class);
        sprintViewModel.loadQuestions();
        handler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sprint, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupObservers();
    }

    private void startTimer() {
        startDownTimer = new CountDownTimer(3_000L, 1_000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                textStartTimer.setText(String.valueOf(millisUntilFinished / 1_000L + 1L));
            }

            @Override
            public void onFinish() {
                showSprint();
            }
        };
        startDownTimer.start();
    }

    private void showSprint() {
        textStartTimer.setVisibility(View.GONE);
        llcCounterContainer.setVisibility(View.VISIBLE);
        viewPagerSprint.setVisibility(View.VISIBLE);
        setupTimer(sprintAdapter.getItemCount());
    }

    private void setupTimer(int size) {
        long millisFinish;
        if (size > 10 && size < 15)
            millisFinish = 45_000L;
        else if (size >= 15)
            millisFinish = 60_000L;
        else
            millisFinish = 30_000L;
        countDownTimer = new CountDownTimer(millisFinish, 1_000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                textTimer.setText(String.valueOf(millisUntilFinished / 1000L + 1L));
            }

            @Override
            public void onFinish() {
                if (!isTestFinished)
                    showTimeEndedDialog();
            }
        };
        countDownTimer.start();
    }

    private void showTimeEndedDialog() {
        List<Sprint> correctQuestionList = sprintAdapter.getCorrectQuestions();
        int correctCount = correctQuestionList.size();
        int testSize = sprintAdapter.getItemCount();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_result_timeout)
                .setMessage(String.format(getString(R.string.message_test_result), testSize, correctCount))
                .setPositiveButton(R.string.answer_ok, (dialog, which) -> {
                    if (testSize > 0)
                        sprintViewModel.saveResult(correctQuestionList);
                    else
                        navController.popBackStack();
                })
                .setCancelable(false);
        builder.create().show();
    }

    private void setupObservers() {
        sprintViewModel.getLiveQuestions().observe(getViewLifecycleOwner(), observerQuestionList);
        sprintViewModel.getSavedResult().observe(getViewLifecycleOwner(), this);
    }

    private void setupViews(View view) {
        textCorrectCounter = view.findViewById(R.id.tv_correct_count);
        textTimer = view.findViewById(R.id.tv_time);
        textStartTimer = view.findViewById(R.id.tv_start_counter);
        llcRoot = view.findViewById(R.id.llc_questions);
        viewPagerSprint = view.findViewById(R.id.vp_questions);
        llcCounterContainer = view.findViewById(R.id.llc_status);
        viewPagerSprint.setUserInputEnabled(false);
        if (sprintAdapter == null)
            sprintAdapter = new SprintAdapter(this);
        viewPagerSprint.setAdapter(sprintAdapter);
        if (navController == null)
            navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        textCorrectCounter.setText(String.format(getString(R.string.correct_counts), 0));
        startTimer();
        correctColorAnimator = ObjectAnimator.ofObject(
                llcRoot,
                "backgroundColor",
                new ArgbEvaluator(),
                0xFFFFFFFF,
                0xFF2196F3);
        inCorrectColorAnimator = ObjectAnimator.ofObject(
                llcRoot,
                "backgroundColor",
                new ArgbEvaluator(),
                0xFFFFFFFF,
                0xFFFF4747);
    }

    @Override
    public void onAnswered(int position, boolean isCorrect) {
        this.position = position;
        int correctCount = sprintAdapter.getCorrectQuestions().size();
        textCorrectCounter.setText(String.format(getString(R.string.correct_counts), correctCount));
        if (isCorrect)
            correctColorAnimator.start();
        else
            inCorrectColorAnimator.start();
        handler.postDelayed(this, 500);
    }

    @SuppressLint("StringFormatMatches")
    private void showResultDialog() {
        isTestFinished = true;
        List<Sprint> correctQuestionList = sprintAdapter.getCorrectQuestions();
        int correctCount = correctQuestionList.size();
        int testSize = sprintAdapter.getItemCount();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_result_sprint)
                .setMessage(String.format(getString(R.string.message_test_result), testSize, correctCount))
                .setPositiveButton(R.string.answer_ok, (dialog, which) -> {
                    if (testSize > 0)
                        sprintViewModel.saveResult(correctQuestionList);
                    else
                        navController.popBackStack();
                })
                .setCancelable(false);
        builder.create().show();
    }

    @Override
    public void onChanged(Boolean b) {
        navController.popBackStack();
    }

    @Override
    public void run() {
        llcRoot.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        if (position != sprintAdapter.getItemCount() - 1)
            viewPagerSprint.setCurrentItem(viewPagerSprint.getCurrentItem() + 1, false);
        else
            showResultDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cd.dispose();
        handler.removeCallbacks(this);
        startDownTimer.cancel();
        if (countDownTimer != null)
            countDownTimer.cancel();
    }
}
