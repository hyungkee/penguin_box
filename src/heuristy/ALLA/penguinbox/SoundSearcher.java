package heuristy.ALLA.penguinbox;


////////////////////////////ÃÊ¼º°Ë»öÀ» À§ÇÑ class/////////////////////////////////
public class SoundSearcher 
{ 
	private static final char HANGUL_BEGIN_UNICODE = 44032; // °¡ 
	private static final char HANGUL_LAST_UNICODE = 55203; // ÆR
	private static final char HANGUL_BASE_UNIT = 588;//°¢ÀÚÀ½ ¸¶´Ù °¡Áö´Â ±ÛÀÚ¼ö
	//ÀÚÀ½
	private static final char[] INITIAL_SOUND = { '¤¡', '¤¢', '¤¤', '¤§', '¤¨', '¤©', '¤±', '¤²', '¤³', '¤µ', '¤¶', '¤·', '¤¸', '¤¹', '¤º', '¤»', '¤¼', '¤½', '¤¾' }; 
	
	
	/**
	 * ÇØ´ç ¹®ÀÚ°¡ INITIAL_SOUNDÀÎÁö °Ë»ç.
	 * @param searchar
	 * @return
	 */
	public static boolean isInitialSound(char searchar){ 
		for(char c:INITIAL_SOUND){ 
			if(c == searchar){ 
				return true; 
			} 
		} 
		return false; 
	} 
	
	/**
	 * ÇØ´ç ¹®ÀÚÀÇ ÀÚÀ½À» ¾ò´Â´Ù.
	 *  
	 * @param c °Ë»çÇÒ ¹®ÀÚ
	 * @return
	 */
	public static char getInitialSound(char c) { 
		int hanBegin = (c - HANGUL_BEGIN_UNICODE); 
		int index = hanBegin / HANGUL_BASE_UNIT; 
		return INITIAL_SOUND[index]; 
	} 
	
	/**
	 * ÇØ´ç ¹®ÀÚ°¡ (¿ÏÀüÇÑÇüÅÂÀÇ)ÇÑ±ÛÀÎÁö °Ë»ç
	 * @param c ¹®ÀÚ ÇÏ³ª
	 * @return
	 */
	public static boolean isHangulSYLLABLES(char c) { 
		return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE; 
	} 
	
	// ÀÚÀ½ ¸ğÀ½À» Æ÷ÇÔÇÑ ¸ğµç°æ¿ì¿¡ ´ëÇØ ÇÑ±ÛÀÎÁö °Ë»ç
	public static Boolean isHangul(String string) {
		char ch = string.charAt(0);
		Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);

		if (block == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO || block == Character.UnicodeBlock.HANGUL_JAMO
				|| block == Character.UnicodeBlock.HANGUL_SYLLABLES)
			return true;
		return false;
	}

	
	/**
	 * »ı¼ºÀÚ.
	 * 
	 */
	public SoundSearcher() { } 
	
	/** * °Ë»öÀ» ÇÑ´Ù. ÃÊ¼º °Ë»ö ¿Ïº® Áö¿øÇÔ. 
	 * @param value : °Ë»ö ´ë»ó ex> ÃÊ¼º°Ë»öÇÕ´Ï´Ù 
	 * @param search : °Ë»ö¾î ex> ¤µ°Ë¤µÇÕ¤¤ 
	 * @return ¸ÅÄª µÇ´Â°Å Ã£À¸¸é true ¸øÃ£À¸¸é false. */ 
	public static boolean matchString(String value, String search){ 
		int t = 0; 
		int seof = value.length() - search.length(); 
		int slen = search.length(); 
		if(seof < 0) 
			return false; //°Ë»ö¾î°¡ ´õ ±æ¸é false¸¦ ¸®ÅÏÇÑ´Ù. 
		for(int i = 0;i <= seof;i++){ 
			t = 0; 
			while(t < slen){ 
				if(isInitialSound(search.charAt(t))==true && isHangulSYLLABLES(value.charAt(i+t))){ 
					//¸¸¾à ÇöÀç charÀÌ ÃÊ¼ºÀÌ°í value°¡ ÇÑ±ÛÀÌ¸é
					if(getInitialSound(value.charAt(i+t))==search.charAt(t)) 
						//°¢°¢ÀÇ ÃÊ¼º³¢¸® °°ÀºÁö ºñ±³ÇÑ´Ù
						t++; 
					else 
						break; 
				} else { 
					//charÀÌ ÃÊ¼ºÀÌ ¾Æ´Ï¶ó¸é
					if(value.charAt(i+t)==search.charAt(t)) 
						//±×³É °°ÀºÁö ºñ±³ÇÑ´Ù. 
						t++; 
					else 
						break; 
				} 
			} 
			if(t == slen) 
				return true; //¸ğµÎ ÀÏÄ¡ÇÑ °á°ú¸¦ Ã£À¸¸é true¸¦ ¸®ÅÏÇÑ´Ù. 
			} 
		return false; //ÀÏÄ¡ÇÏ´Â °ÍÀ» Ã£Áö ¸øÇßÀ¸¸é false¸¦ ¸®ÅÏÇÑ´Ù.
	}
}