package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.DominionDTO;

import java.util.List;
import java.util.UUID;

public class DominionController {
    public DominionDTO create(UUID owner, String name, String world,
                              Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        // todo 检查冲突
        return DominionDTO.insert(new DominionDTO(owner, name, world, x1, y1, z1, x2, y2, z2));
    }

    public List<DominionDTO> all(){
        return DominionDTO.selectAll();
    }
}
