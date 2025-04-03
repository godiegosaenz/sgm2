package com.origami.config;

import com.origami.app.AppProps;

import jodd.props.Props;

public class SgmeeProps extends Props {
	
	@Override
	public String getValue(String key) {
		String fullname = "sgmee."+key;
		String appProp = AppProps.getString(fullname);
		return appProp!=null ? appProp : super.getValue(key);
	}
	
}
