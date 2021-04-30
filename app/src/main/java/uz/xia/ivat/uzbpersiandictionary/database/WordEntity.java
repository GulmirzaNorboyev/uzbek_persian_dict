package uz.xia.ivat.uzbpersiandictionary.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "uzb_persian_dictionary")
public class WordEntity implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id = 0L;
    @ColumnInfo(name = "word_uzb")
    @NonNull
    private String wordUzb = "";
    @ColumnInfo(name = "word_persian")
    private String wordPersian = "";
    @ColumnInfo(name = "word_transcription")
    private String wordTranscription = "";
    @Ignore
    private boolean isFavorite = false;
    @Ignore
    private boolean isTraining = false;

    public WordEntity() {
    }

    protected WordEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        wordUzb = in.readString();
        wordPersian = in.readString();
        wordTranscription = in.readString();
        isFavorite = in.readByte() != 0;
        isTraining = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(wordUzb);
        dest.writeString(wordPersian);
        dest.writeString(wordTranscription);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeByte((byte) (isTraining ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WordEntity> CREATOR = new Creator<WordEntity>() {
        @Override
        public WordEntity createFromParcel(Parcel in) {
            return new WordEntity(in);
        }

        @Override
        public WordEntity[] newArray(int size) {
            return new WordEntity[size];
        }
    };

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordEntity that = (WordEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(wordUzb, that.wordUzb) &&
                Objects.equals(wordPersian, that.wordPersian) &&
                Objects.equals(wordTranscription, that.wordTranscription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wordUzb, wordPersian, wordTranscription);
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isTraining() {
        return isTraining;
    }

    public void setTraining(boolean training) {
        isTraining = training;
    }
}
