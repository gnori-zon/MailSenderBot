package org.gnori.data;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("org.gnori.data.entity")
@ComponentScan
@EnableJpaRepositories("org.gnori.data.repository")
public class EnableData {
}
