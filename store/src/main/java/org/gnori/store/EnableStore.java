package org.gnori.store;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("org.gnori.store.entity")
@ComponentScan("org.gnori.store.dao")
@EnableJpaRepositories("org.gnori.store.repository")
public class EnableStore {
}
