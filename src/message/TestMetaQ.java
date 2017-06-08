package message;

import net.sf.json.JSONObject;

import com.ai.metaq.service.interfaces.IUpcMessageListener;

public class TestMetaQ implements IUpcMessageListener {

	@Override
	public Object doBusiness(Object obj) throws Exception {
		
		System.out.println("test-1223");
		System.out.println(obj.toString());
		
//		JSONObject ployInfo = (JSONObject) obj;
		
		JSONObject ployInfo = JSONObject.fromObject(obj);
		
		System.out.println(ployInfo);
		
		return true;
		
	}

}
