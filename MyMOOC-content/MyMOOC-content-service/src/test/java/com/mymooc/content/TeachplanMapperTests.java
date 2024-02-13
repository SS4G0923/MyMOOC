package com.mymooc.content;

import com.mymooc.content.mapper.TeachplanMapper;
import com.mymooc.content.model.dto.TeachPlanDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TeachplanMapperTests {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Test
    public void testSelectTreeNodes(){
        List<TeachPlanDto> res =  teachplanMapper.selectTreeNodes(117L);
        System.out.println(res);
    }
}
