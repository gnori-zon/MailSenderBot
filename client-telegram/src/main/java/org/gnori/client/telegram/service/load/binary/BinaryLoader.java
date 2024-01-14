package org.gnori.client.telegram.service.load.binary;

import org.gnori.shared.flow.Result;
import org.gnori.shared.service.loader.url.LoadFailure;

public interface BinaryLoader {
    Result<byte[], LoadFailure> loadBy(String fileId);
}
