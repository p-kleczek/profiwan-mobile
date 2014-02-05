package pkleczek.profiwan.utils;

import pkleczek.profiwan.R;

public enum Language {
	PL(R.drawable.flag_pl, "pl"), RUS(R.drawable.flag_rus, "rus");

	private final int flagIconId;
	private final String languageISOCode;
	// TODO: information about keyboard used
	
	Language(int flagIconId, String languageISOCode) {
		this.flagIconId = flagIconId;
		this.languageISOCode = languageISOCode;
	}

	public int getFlagIconId() {
		return flagIconId;
	}

	public String getLanguageISOCode() {
		return languageISOCode;
	}
}
