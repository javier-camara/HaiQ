package org.jcm.haiq.parse;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.jcm.haiq.core.HQRelationCollection;


public class RelationCollectionParser {

	protected static boolean m_debug = true;
	protected static JSONObject m_jsonObject = null;
	
	public HQRelationCollection readRelationCollection(String id, String code) {
		
        JSONParser parser = new JSONParser();

        try (Reader reader = new StringReader(code)) {

        	//System.out.println(reader.toString());
            m_jsonObject = (JSONObject) parser.parse(reader);
            if (m_debug)
            	System.out.println(m_jsonObject);
           
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new HQRelationCollection(id, m_jsonObject);
	}
	
	
}
