package de.imichelb.kodicmd.kodi;

/*
 * KODI API JSON Response
 */
@SuppressWarnings("unused")
public class KodiResponse {
	
	private String jsonrpc;
	private int id;
	private String result;
	private Error error;
	
	private class Error{
		
		private Integer code;
		private String message;
	}
}
