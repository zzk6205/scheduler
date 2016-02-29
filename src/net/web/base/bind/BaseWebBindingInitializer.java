package net.web.base.bind;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

public class BaseWebBindingInitializer extends ConfigurableWebBindingInitializer {

	@Override
	public void initBinder(WebDataBinder binder, WebRequest request) {
		super.initBinder(binder, request);
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

}
