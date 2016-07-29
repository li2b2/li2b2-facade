package de.sekmi.li2b2.services;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class MyBinder extends AbstractBinder{

	@Override
	protected void configure() {
//		bind(Impl.class).to(Inter.class);
		// singleton
		bind(new Settings()).to(Settings.class);
	}

}
