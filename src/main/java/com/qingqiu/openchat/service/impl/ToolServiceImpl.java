package com.qingqiu.openchat.service.impl;

import com.qingqiu.openchat.tools.ITool;
import com.qingqiu.openchat.tools.ToolType;
import com.qingqiu.openchat.service.ToolService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ToolServiceImpl implements ToolService {

    private final List<ITool> tools;

    @Override
    public List<ITool> getAllTools() {
        return tools;
    }

    @Override
    public List<ITool> getOptionalTools() {
        return getToolsByType(ToolType.OPTIONAL);
    }

    @Override
    public List<ITool> getFixedTools() {
        return getToolsByType(ToolType.FIXED);
    }

    private List<ITool> getToolsByType(ToolType type) {
        return tools.stream()
                .filter(tool -> tool.getType().equals(type))
                .toList();
    }
}
