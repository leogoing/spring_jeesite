package org.liwang.util;

import java.io.File;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * dom4j解析
 * @author liwang
 *
 */
public abstract class DomUtil {

	/**加载根节点*/
	public static Element loadElement(String path){
		InputStream is=ClassUtil.classLoader().getResourceAsStream(path);
		SAXReader reader=new SAXReader();
		try {
			Document dom=reader.read(is);
			return dom.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
