package uz.xia.ivat.uzbpersiandictionary.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "uzb_persian_train")
public class TrainEntity implements Parcelable {
    public static final Creator<TrainEntity> CREATOR = new Creator<TrainEntity>() {
        @Override
        public TrainEntity createFromParcel(Parcel in) {
            return new TrainEntity(in);
        }

        @Override
        public TrainEntity[] newArray(int size) {
            return new TrainEntity[size];
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
    @ColumnInfo(name = "is_correct_uzb")
    private boolean isCorrectUzb = false;
    @ColumnInfo(name = "is_correct_persian")
    private boolean isCorrectPersian = false;
    @ColumnInfo(name = "is_correct_sprint")
    private boolean isCorrectSprint = false;
    @ColumnInfo(name = "is_correct_constructor")
    private boolean isCorrectConstruct = false;

    public TrainEntity() {
    }

    public TrainEntity(
            @NonNull Long id,
            @NonNull Long wordId,
            @NonNull String wordUzb,
            String wordPersian,
            String wordTranscription,
            boolean isCorrectUzb,
            boolean isCorrectPersian,
            boolean isCorrectSprint,
            boolean isCorrectConstruct
    ) {
        this.id = id;
        this.wordId = wordId;
        this.wordUzb = wordUzb;
        this.wordPersian = wordPersian;
        this.wordTranscription = wordTranscription;
        this.isCorrectUzb = isCorrectUzb;
        this.isCorrectPersian = isCorrectPersian;
        this.isCorrectSprint = isCorrectSprint;
        this.isCorrectConstruct = isCorrectConstruct;
    }

    @Ignore
    protected TrainEntity(Parcel in) {
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
        isCorrectUzb = in.readByte() != 0;
        isCorrectPersian = in.readByte() != 0;
        isCorrectSprint = in.readByte() != 0;
        isCorrectConstruct = in.readByte() != 0;
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
        dest.writeByte((byte) (isCorrectUzb ? 1 : 0));
        dest.writeByte((byte) (isCorrectPersian ? 1 : 0));
        dest.writeByte((byte) (isCorrectSprint ? 1 : 0));
        dest.writeByte((byte) (isCorrectConstruct ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
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

    public boolean isCorrectUzb() {
        return isCorrectUzb;
    }

    public void setCorrectUzb(boolean correctUzb) {
        isCorrectUzb = correctUzb;
    }

    @NonNull
    public Long getWordId() {
        return wordId;
    }

    public void setWordId(@NonNull Long wordId) {
        this.wordId = wordId;
    }

    public boolean isCorrectPersian() {
        return isCorrectPersian;
    }

    public void setCorrectPersian(boolean correctPersian) {
        isCorrectPersian = correctPersian;
    }

    public boolean isCorrectSprint() {
        return isCorrectSprint;
    }

    public void setCorrectSprint(boolean correctSprint) {
        isCorrectSprint = correctSprint;
    }

    public boolean isCorrectConstruct() {
        return isCorrectConstruct;
    }

    public void setCorrectConstruct(boolean correctConstruct) {
        isCorrectConstruct = correctConstruct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainEntity that = (TrainEntity) o;
        return isCorrectUzb == that.isCorrectUzb &&
                isCorrectPersian == that.isCorrectPersian &&
                isCorrectSprint == that.isCorrectSprint &&
                isCorrectConstruct == that.isCorrectConstruct &&
                id.equals(that.id) &&
                wordId.equals(that.wordId) &&
                wordUzb.equals(that.wordUzb) &&
                Objects.equals(wordPersian, that.wordPersian) &&
                Objects.equals(wordTranscription, that.wordTranscription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wordId, wordUzb, wordPersian, wordTranscription, isCorrectUzb, isCorrectPersian, isCorrectSprint, isCorrectConstruct);
    }
}
