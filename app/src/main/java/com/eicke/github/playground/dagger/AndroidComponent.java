package com.eicke.github.playground.dagger;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, AndroidModule.class})
public interface AndroidComponent {

}

