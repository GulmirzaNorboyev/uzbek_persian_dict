package uz.xia.ivat.uzbpersiandictionary.util;

import android.util.Pair;

public class GlobalEvents {

    public static SingleLiveEvent<Pair<Long, Boolean>> liveFavoriteChanged = new SingleLiveEvent<>();
    public static SingleLiveEvent<Boolean> liveFavoriteDeleted = new SingleLiveEvent<>();
    public static SingleLiveEvent<Pair<Long, Boolean>> liveTrainChanged = new SingleLiveEvent<>();
    public static SingleLiveEvent<Boolean> liveFavoriteDetailsChanged = new SingleLiveEvent<>();
    public static SingleLiveEvent<Pair<Long, Boolean>> liveTrainDetailsChanged = new SingleLiveEvent<>();

}
