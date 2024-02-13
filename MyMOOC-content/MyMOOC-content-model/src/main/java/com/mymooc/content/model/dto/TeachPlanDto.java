package com.mymooc.content.model.dto;

import com.mymooc.content.model.po.Teachplan;
import com.mymooc.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class TeachPlanDto extends Teachplan {

    private TeachplanMedia teachplanMedia;

    private List<TeachPlanDto> teachPlanTreeNodes;

}
