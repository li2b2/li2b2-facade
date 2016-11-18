package de.sekmi.li2b2.hive;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class HiveResponse extends HiveMessage{

	public HiveResponse(Document dom) {
		super(dom);
	}

	public Element getResponseHeader(){
		return (Element)getMessageHeader().getNextSibling();
	}
	public ResultStatus getResultStatus(){
		Element rh = getResponseHeader();
		NodeList nl = rh.getElementsByTagName("result_status");
		if( nl.getLength() == 0 ){
			return null;
		}else{
			return new ResultStatus((Element)nl.item(0).getFirstChild());
		}
	}
	public void setResultStatus(String type, String message){
		Element rh = getResponseHeader();
		NodeList nl = rh.getElementsByTagName("result_status");
		Element status = (Element)nl.item(0).getFirstChild();
		status.setAttribute("type", type);
		status.setTextContent(message);
	}
	public static class ResultStatus{
		private Element status;
		public ResultStatus(Element status){
			this.status = status;
		}
		public String getCode(){
			return status.getAttribute("type");
		}
		public String getMessage(){
			return status.getTextContent();
		}
	}
}
