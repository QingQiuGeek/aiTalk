package com.qingqiu.openchat.service;

import com.qingqiu.openchat.tools.ITool;
import java.util.List;

public interface ToolService {
    List<ITool> getAllTools();

    List<ITool> getOptionalTools();

    List<ITool> getFixedTools();
}
