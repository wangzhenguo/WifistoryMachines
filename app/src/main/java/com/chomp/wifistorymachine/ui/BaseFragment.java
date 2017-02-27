package com.chomp.wifistorymachine.ui;

import android.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import java.lang.reflect.Field;

//基础fragment
public class BaseFragment extends Fragment implements OnClickListener {
	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onClick(View v) {
		
	}
}
