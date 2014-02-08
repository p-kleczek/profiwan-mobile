package pkleczek.profiwan.model;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

// Extra class to support parcelling.
public class AndroidPhraseEntry extends PhraseEntry implements Parcelable {

	public AndroidPhraseEntry(PhraseEntry pe) {
		this.setId(pe.getId());
		this.setLangA(pe.getLangA());
		this.setLangB(pe.getLangB());
		this.setLangAText(pe.getLangAText());
		this.setLangBText(pe.getLangBText());
		this.setLabel(pe.getLabel());
		this.setCreatedAt(pe.getCreatedAt());
		this.setInRevisions(pe.isInRevisions());
	}

	public static final Parcelable.Creator<AndroidPhraseEntry> CREATOR = new Parcelable.Creator<AndroidPhraseEntry>() {
		public AndroidPhraseEntry createFromParcel(Parcel in) {
			AndroidPhraseEntry pe = new AndroidPhraseEntry(new PhraseEntry());
			pe.setId(in.readLong());
			pe.setLangA(in.readString());
			pe.setLangB(in.readString());
			pe.setLangAText(in.readString());
			pe.setLangBText(in.readString());
			pe.setLabel(in.readString());
			pe.setCreatedAt(new DateTime(in.readLong()));
			pe.setInRevisions(Boolean.valueOf(in.readString()));

			return pe;
		}

		public AndroidPhraseEntry[] newArray(int size) {
			return new AndroidPhraseEntry[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeString(getLangA());
		dest.writeString(getLangB());
		dest.writeString(getLangAText());
		dest.writeString(getLangBText());
		dest.writeString(getLabel());
		dest.writeLong(getCreatedAt().getMillis());
		dest.writeString(Boolean.valueOf(isInRevisions()).toString());
	}

}
