package pkleczek.profiwan.utils;

import pkleczek.profiwan.model.Timepoint;
import pkleczek.profiwan.model.Timepoint.TimepointType;

public class Logging {
	
	public static void logEvent(DatabaseHelper dbHelper, TimepointType type) {
		((DatabaseHelperImpl) dbHelper).createTimepoint(Timepoint.create(type));
	}
}
