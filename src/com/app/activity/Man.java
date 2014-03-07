package com.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.ScrollView;

import com.app.adapter.ManListAdapter;
import com.app.man.R;
import com.app.model.ManItemImgsModel;
import com.app.model.ManItemModel;

/**
 * 男人帮
 * 
 * @author 王灵
 * 
 */
public class Man extends Activity {

	private ListView manListView;

	private ManListAdapter manListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.man);

		manListView = (ListView) findViewById(R.id.man_list);

		manListAdapter = new ManListAdapter(this, manListView);
		manListView.setAdapter(manListAdapter);

		initDatas();
	}

	/**
	 * 模拟数据
	 */
	private void initDatas() {
		List<ManItemModel> list = new ArrayList<ManItemModel>();
		
		final int size = 11;
		final int imgSize = 6;

		for (int i = 0; i < size; i++) {
			ManItemModel m = new ManItemModel();
			m.setArticleId(i);
			m.setAuthorName("王鹏");
			m.setAuthorImg("http://a.hiphotos.baidu.com/image/w%3D2048/sign=fc61311395dda144da096bb2868fd1a2/fcfaaf51f3deb48f7ce234def21f3a292df57823.jpg");
			m.setAuthorId(i);
			m.setDate("2014-3-3");
			m.setSummary("支付宝（alipay）最初作为淘宝网公司为了解决网络交易安全所设的一个功能，该功能为首先使用的“第三方担保交易模式”，由买家将货款打到支付宝账户，由支付宝向卖家通知发货，买家收到商品确认后指令支付宝将货款放于卖家，至此完成一笔网络交易。");
			
			List<ManItemImgsModel> imgs = new ArrayList<ManItemImgsModel>();
			for (int j = 0; j < imgSize; j++) {
				ManItemImgsModel imgModel = new ManItemImgsModel();
				imgModel.setId(j);
				imgModel.setUrl("http://img1.6544.cc/mn123/201201/1052074404.jpg");
				imgModel.setSummary("色情吧，呵呵~~~");
				imgs.add(imgModel);
			}
			
			m.setImages(imgs);
			
			list.add(m);
		}
		
		manListAdapter.setDatas(list);
		manListAdapter.notifyDataSetChanged();
	}

}
