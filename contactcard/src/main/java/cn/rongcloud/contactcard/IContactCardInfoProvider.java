package cn.rongcloud.contactcard;

import java.util.List;

/**
 * Created by Beyond on 29/12/2016.
 */

public interface IContactCardInfoProvider {
	
	interface IContactCardInfoCallback {
		void getContactCardInfoCallback(List<?> list);
	}
	
	void getContactCardInfoProvider(IContactCardInfoCallback contactInfoCallback);
	
}
