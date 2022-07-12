package gb.tda.tools;



public final class StringUtils {

    public static String replaceExtension(String filename, String newExt) {
	int indexOfDot = filename.lastIndexOf(".");
	String name = filename.substring(0, indexOfDot);
	return name+newExt;
    }

    public static boolean searchString(String stringToFind, String stringToSearch) {
	boolean matchFound = false;
	if ( stringToSearch.equals(stringToFind) ) return true;
	else if ( stringToSearch.length() < stringToFind.length() ) return false;
	else {
	    int maxNumOfTests = stringToSearch.length() - stringToFind.length();
	    String substring = null;
	    int i = 0;
	    while ( i < maxNumOfTests && matchFound == false ) {
		substring = stringToSearch.substring(0+i, stringToFind.length()+i);
		if ( substring.equals(stringToFind) ) matchFound = true;
		else i++;
	    }
	}
	return matchFound;
    }

    public static int findStringIndex(String stringToFind, String[] stringArrayToSearch) {
	int index = -1;
	int i=0;
	boolean found = false;
	while ( i < stringArrayToSearch.length && found == false ) {
	    index = i;
	    found = stringArrayToSearch[i].contains(stringToFind);
	    i++;
	}
	return index;
    }
    
    public static int getStringIndex(String stringToFind, String[] stringArrayToSearch) {
	int nMembers = stringArrayToSearch.length;
	String stringToSearch = null;
	boolean matchFound = false;
	int index = -1;
	int n=0;
	while ( n < nMembers && matchFound == false ) {
	    try {
		stringToSearch = stringArrayToSearch[n];
	    }
	    catch (ArrayIndexOutOfBoundsException e) {}

	    if ( stringToSearch.equals(stringToFind) ) {
		matchFound = true;
		index = n;
	    }
	    else if ( stringToSearch.length() < stringToFind.length() ) 
		matchFound = false;
	    else {
		int maxNumOfTests = stringToSearch.length() - stringToFind.length();
		String substring = null;
		int i = 0;
		while ( i < maxNumOfTests && matchFound == false ) {
		    substring = stringToSearch.substring(0+i, stringToFind.length()+i);
		    if ( substring.equals(stringToFind) ) {
			matchFound = true;
			index = n;
		    }
		    else i++;
		}
	    }
	    n++;
	}
	return index;
    }

    public static boolean findString(String stringToFind, String[] stringArrayToSearch) {
	int nMembers = stringArrayToSearch.length;
	String stringToSearch = null;
	boolean matchFound = false;
	int index = -1;
	int n=0;
	while ( n < nMembers && matchFound == false ) {
	    try {
		stringToSearch = stringArrayToSearch[n];
	    }
	    catch (ArrayIndexOutOfBoundsException e) {}

	    if ( stringToSearch.equals(stringToFind) ) {
		matchFound = true;
		index = n;
	    }
	    else if ( stringToSearch.length() < stringToFind.length() ) 
		matchFound = false;
	    else {
		int maxNumOfTests = stringToSearch.length() - stringToFind.length();
		String substring = null;
		int i = 0;
		while ( i < maxNumOfTests && matchFound == false ) {
		    substring = stringToSearch.substring(0+i, stringToFind.length()+i);
		    if ( substring.equals(stringToFind) ) {
			matchFound = true;
			index = n;
		    }
		    else i++;
		}
	    }
	    n++;
	}
	return matchFound;
    }

}
