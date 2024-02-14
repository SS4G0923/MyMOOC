package com.mymooc.content.api;

import com.mymooc.content.model.dto.SaveTeachPlanDto;
import com.mymooc.content.model.dto.TeachPlanDto;
import com.mymooc.content.service.TeachPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeachPlanController {

    @Autowired
    TeachPlanService teachPlanService;

    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable Long courseId){
        return teachPlanService.findTeachPlanTree(courseId);
    }

    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachPlanDto saveTeachPlanDto){
        teachPlanService.saveTeachPlan(saveTeachPlanDto);
    }
}
