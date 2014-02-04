package pkleczek.profiwan.dictionary;

class Flag {
	private final int flagIconId;
	private final String languageISOCode;

	public Flag(int flagIconId, String languageISOCode) {
		super();
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
