package org.gnori.client.telegram.config;

import org.gnori.shared.EnableShared;
import org.gnori.store.EnableStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({EnableStore.class, EnableShared.class})
public class ImportConfig {}
