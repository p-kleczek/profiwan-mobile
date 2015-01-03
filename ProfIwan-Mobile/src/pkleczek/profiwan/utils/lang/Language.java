package pkleczek.profiwan.utils.lang;

import pkleczek.profiwan.R;

public enum Language {
	PL(R.drawable.flag_pl, "pl", 0), RU(
			R.drawable.flag_rus,
			"ru",
			R.xml.kbd_rus);

	private final int flagIconId;
	private final String languageISOCode;
	private final int keyboardXml;

	Language(int flagIconId, String languageISOCode, int keyboard) {
		this.flagIconId = flagIconId;
		this.languageISOCode = languageISOCode;
		this.keyboardXml = keyboard;
	}

	public int getFlagIconId() {
		return flagIconId;
	}

	public String getLanguageISOCode() {
		return languageISOCode;
	}

	public int getKeyboard() {
		return keyboardXml;
	}

	public static Language getLanguageByCode(String code) {
		for (Language lang : Language.values()) {
			if (lang.getLanguageISOCode().equals(code)) {
				return lang;
			}
		}

		throw new IllegalArgumentException("No such language code: " + code);
	}
}
