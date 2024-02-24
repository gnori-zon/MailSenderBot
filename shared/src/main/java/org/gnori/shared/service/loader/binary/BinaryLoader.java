package org.gnori.shared.service.loader.binary;

import org.gnori.data.flow.Result;
import org.gnori.shared.service.loader.LoadFailure;

public interface BinaryLoader {
    Result<byte[], LoadFailure> loadBy(String fileId);
}
