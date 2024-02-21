package org.gnori.shared.service.loader.url;

import org.gnori.data.flow.Result;
import org.gnori.shared.service.loader.LoadFailure;

public interface URLLoader {
    Result<byte[], LoadFailure> load(String uri, int chunkSize);
}
