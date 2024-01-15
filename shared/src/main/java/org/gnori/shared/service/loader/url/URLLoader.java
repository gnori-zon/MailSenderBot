package org.gnori.shared.service.loader.url;

import org.gnori.shared.flow.Result;
import org.gnori.shared.service.loader.LoadFailure;

/**
 * Service for downloading files for url
 */
public interface URLLoader {

    Result<byte[], LoadFailure> load(String uri, int chunkSize);
}
