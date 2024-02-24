package org.gnori.shared;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@ConfigurationPropertiesScan
@ComponentScan({"org.gnori.shared.service", "org.gnori.shared.crypto"})
public class EnableShared {
}
