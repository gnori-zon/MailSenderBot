package org.gnori.send.mail.worker.config;

import org.gnori.data.EnableData;
import org.gnori.shared.EnableShared;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({EnableData.class, EnableShared.class})
public class ImportConfig {}
