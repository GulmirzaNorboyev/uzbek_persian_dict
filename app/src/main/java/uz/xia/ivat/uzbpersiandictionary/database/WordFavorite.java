package uz.xia.ivat.uzbpersiandictionary.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "uzb_persian_favorite")
public class WordFavorite implements Parcelable {
    public static final Creator<WordFavorite> CREATOR = new Creator<WordFavorite>() {
        @Override
        public WordFavorite createFromParcel(Parcel in) {
            return new WordFavorite(in);
        }

        @Override
        public WordFavorite[] newArray(int size) {
            return new WordFavorite[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id = 0L;
    @NonNull
    @ColumnInfo(name = "word_id")
    private Long wordId = 0L;
    @ColumnInfo(name = "word_uzb")
    @NonNull
    private String wordUzb = "";
    @ColumnInfo(name = "word_persian")
    private String wordPersian = "";
    @ColumnInfo(name = "word_transcription")
    private String wordTranscription = "";
    @ColumnInfo(name = "is_favorite")
    private Boolean isFavorite = false;
    @ColumnInfo(name = "is_training")
    private Boolean isTraining = false;

    public WordFavorite() {
    }

    public WordFavorite(
            @NonNull Long id,
            @NonNull Long wordId,
            @NonNull String wordUzb,
            String wordPersian,
            String wordTranscription,
            Boolean isFavorite,
            Boolean isTraining
    ) {
        this.id = id;
        this.wordId = wordId;
        this.wordUzb = wordUzb;
        this.wordPersian = wordPersian;
        this.wordTranscription = wordTranscription;
        this.isFavorite = isFavorite;
        this.isTraining = isTraining;
    }

    @Ignore
    protected WordFavorite(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            wordId = null;
        } else {
            wordId = in.readLong();
        }
        wordUzb = in.readString();
        wordPersian = in.readString();
        wordTranscription = in.readString();
        byte tmpIsFavorite = in.readByte();
        isFavorite = tmpIsFavorite == 0 ? null : tmpIsFavorite == 1;
        byte tmpIsTraining = in.readByte();
        isTraining = tmpIsTraining == 0 ? null : tmpIsTraining == 1;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    @NonNull
    public String getWordUzb() {
        return wordUzb;
    }

    public void setWordUzb(@NonNull String wordUzb) {
        this.wordUzb = wordUzb;
    }

    public String getWordPersian() {
        return wordPersian;
    }

    public void setWordPersian(String wordPersian) {
        this.wordPersian = wordPersian;
    }

    public String getWordTranscription() {
        return wordTranscription;
    }

    public void setWordTranscription(String wordTranscription) {
        this.wordTranscription = wordTranscription;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public Boolean getTraining() {
        return isTraining;
    }

    public void setTraining(Boolean training) {
        isTraining = training;
    }

    @NonNull
    public Long getWordId() {
        return wordId;
    }

    public void setWordId(@NonNull Long wordId) {
        this.wordId = wordId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        if (wordId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(wordId);
        }
        dest.writeString(wordUzb);
        dest.writeString(wordPersian);
        dest.writeString(wordTranscription);
        dest.writeByte((byte) (isFavorite == null ? 0 : isFavorite ? 1 : 2));
        dest.writeByte((byte) (isTraining == null ? 0 : isTraining ? 1 : 2));
    }
}
