package yomi;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class YomiServer extends NanoHTTPD {

	public static int port = 57380;
	public static int port2 = 443;
	
	public YomiServer(int pPort) throws IOException {
        super(pPort);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:" + pPort + "/ \n");
	}

	public static void main(String[] args) {
		
		try {
            new YomiServer(port);
            new YomiServer(port2);
            //new YomiServer(80);
            
            //here is to wake it up so the initial request does not need to load the dict
            YomiDecoder oDecoder = new YomiDecoder();
            oDecoder.MakeDictionary();
            oDecoder.GetYomi("yoroshiku");
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }  
	
	@Override
    public Response serve(IHTTPSession session) {
        Map<String, String> parms = session.getParms();
        
        if (parms.get("q") == null) {
            String msg = "<html><title>YOMI</title><body><h1>" + YomiUtils.TitleText + "</h1>\n";
            msg += "<style> *{ font-family: Tahoma;} </style>" ;
            msg += "<form action='?' method='get'>\n" + "  <p>" ;
            msg += YomiUtils.AskText + "<input type='text' name='q' style='width:400px;'></p>\n" + "";
            msg += "<div style='color:#888;'>eugene studies lol</div>";
            msg += "</form></body></html>\n";
            return newFixedLengthResponse(msg);
        } else {
        	
        	boolean isColored = false;
        	if (parms.get("color") != null){
        		isColored = (parms.get("color").toUpperCase().equals("Y"));
        	}

    		try
    		{
    			YomiDecoder oDecoder = new YomiDecoder();
    			oDecoder.isColored = isColored;
            	String decoded = oDecoder.GetYomi(parms.get("q"));
            	return newFixedLengthResponse(decoded);
    		}catch(Exception ex){
    			StringWriter sw = new StringWriter();
    			PrintWriter pw = new PrintWriter(sw);
    			ex.printStackTrace(pw);
    			StringBuilder sb = new StringBuilder();
    			sb.append("<html><title>YOMI</title>");
    			sb.append("<body>");
    			sb.append("<pre style='color:red;'>");
    			sb.append("ERROR:\n");
    			sb.append(sw.toString());
    			sb.append("</pre></body></html>");
    			return newFixedLengthResponse(sb.toString());
    		}
        	
        }

    }

}